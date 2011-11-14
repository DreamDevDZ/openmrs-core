/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.web.filter.initialization;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.dv.util.Base64;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.module.ModuleConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * Contains static methods to be used by the installation wizard when creating a testing
 * installation
 */
public class TestInstallUtil {
	
	private static final Log log = LogFactory.getLog(TestInstallUtil.class);
	
	/**
	 * Adds data to the test database from a sql dump file
	 * 
	 * @param host
	 * @param port
	 * @param databaseName
	 * @param user
	 * @param pwd
	 * @return
	 */
	protected static boolean addTestData(String host, int port, String databaseName, String user, String pwd, String filePath) {
		Process proc = null;
		BufferedReader br = null;
		String errorMsg = null;
		String[] command = new String[] { "mysql", "--host=" + host, "--port=" + port, "--user=" + user,
		        "--password=" + pwd, "--database=" + databaseName, "-e", "source " + filePath };
		
		try {
			proc = Runtime.getRuntime().exec(command);
			try {
				br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				String line;
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(System.getProperty("line.separator"));
					sb.append(line);
				}
				errorMsg = sb.toString();
			}
			catch (IOException e) {
				log.error("Failed to add test data:", e);
			}
			finally {
				if (br != null) {
					try {
						br.close();
					}
					catch (Exception e) {
						log.error("Failed to close the inputstream:", e);
					}
				}
			}
			
			//print out the error messages from the process
			if (StringUtils.isNotBlank(errorMsg))
				log.error(errorMsg);
			
			if (proc.waitFor() == 0) {
				if (log.isDebugEnabled())
					log.debug("Added test data successfully");
				return true;
			}
			
			log.error("The process terminated abnormally while adding test data");
			
		}
		catch (IOException e) {
			log.error("Failed to create the sql dump", e);
		}
		catch (InterruptedException e) {
			log.error("The back up was interrupted while adding test data", e);
		}
		
		return false;
	}
	
	/**
	 * Extracts .omod files from the specified {@link InputStream} and copies them to the module
	 * repository of the test application data directory, the method always closes the InputStream
	 * before returning
	 * 
	 * @param in the {@link InputStream} for the zip file
	 */
	@SuppressWarnings("rawtypes")
	protected static boolean addZippedTestModules(InputStream in) {
		ZipFile zipFile = null;
		FileOutputStream out = null;
		File tempFile = null;
		boolean successfullyAdded = true;
		
		try {
			File moduleRepository = OpenmrsUtil
			        .getDirectoryInApplicationDataDirectory(ModuleConstants.REPOSITORY_FOLDER_PROPERTY_DEFAULT);
			tempFile = File.createTempFile("modules", null);
			out = new FileOutputStream(tempFile);
			IOUtils.copy(in, out);
			zipFile = new ZipFile(tempFile);
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
					if (log.isDebugEnabled())
						log.debug("Skipping directory: " + entry.getName());
					continue;
				}
				
				String fileName = entry.getName();
				if (fileName.endsWith(".omod")) {
					//Convert the names of .omod files located in nested directories so that they get
					//created under the module repo directory when being copied
					if (fileName.contains(System.getProperty("file.separator")))
						fileName = new File(entry.getName()).getName();
					
					if (log.isDebugEnabled())
						log.debug("Extracting module file: " + fileName);
					OpenmrsUtil.copyFile(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(
					        new File(moduleRepository, fileName))));
				} else {
					if (log.isDebugEnabled())
						log.debug("Ignoring file that is not a .omod '" + fileName);
				}
			}
		}
		catch (IOException e) {
			log.error("An error occured while copying modules to the test system:", e);
			successfullyAdded = false;
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			if (zipFile != null) {
				try {
					zipFile.close();
				}
				catch (IOException e) {
					log.error("Failed to close zip file: ", e);
				}
			}
			if (tempFile != null) {
				tempFile.deleteOnExit();
			}
		}
		
		return successfullyAdded;
	}
	
	/**
	 * Tests the connection to the specified URL
	 * 
	 * @param urlString the url to test
	 * @return true if a connection a established otherwise false
	 */
	protected static boolean testConnection(String urlString) {
		try {
			HttpURLConnection urlConnect = (HttpURLConnection) new URL(urlString).openConnection();
			//wait for 15sec
			urlConnect.setConnectTimeout(15000);
			urlConnect.setUseCaches(false);
			//trying to retrieve data from the source. If there
			//is no connection, this line will fail
			urlConnect.getContent();
			return true;
		}
		catch (UnknownHostException e) {
			log.error("Error generated:", e);
		}
		catch (IOException e) {
			log.error("Error generated:", e);
		}
		
		return false;
	}
	
	/**
	 * @param url
	 * @param openmrsUsername
	 * @param openmrsPassword
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	protected static InputStream getResourceInputStream(String urlString, String openmrsUsername, String openmrsPassword)
	        throws MalformedURLException, IOException, APIException {
		
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlString).openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setConnectTimeout(15000);
		urlConnection.setUseCaches(false);
		urlConnection.setDoOutput(true);
		
		String requestParams = "username=" + new String(Base64.encode(openmrsUsername.getBytes(Charset.forName("UTF-8"))))
		        + "&password=" + new String(Base64.encode(openmrsPassword.getBytes(Charset.forName("UTF-8"))));
		
		OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
		out.write(requestParams);
		out.flush();
		out.close();
		
		if (log.isInfoEnabled())
			log.info("Http response message:" + urlConnection.getResponseMessage() + ", Code:"
			        + urlConnection.getResponseCode());
		
		if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
			throw new APIAuthenticationException("Invalid username or password");
		else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
			throw new APIException("An error occurred on the production server");
		
		return urlConnection.getInputStream();
	}
}