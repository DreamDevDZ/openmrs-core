package org.openmrs.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.context.Context;
import org.openmrs.context.ContextAuthenticationException;

public class LoginServlet extends HttpServlet {

	public static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("uname");
		String password = request.getParameter("pw");
		String redirect = request.getParameter("redirect");
		if (redirect == null || request.equals(""))
			redirect = request.getContextPath();
		
		HttpSession httpSession = request.getSession();

		Context context = (Context)httpSession.getAttribute(Constants.OPENMRS_CONTEXT_HTTPSESSION_ATTR);
		if (context == null) {
			httpSession.setAttribute(Constants.OPENMRS_ERROR_ATTR, "auth.session.expired");
			response.sendRedirect(request.getContextPath() + "/logout");
			return;
		}
		
		try {
			context.authenticate(username, password);
			if (context.isAuthenticated()) {
				response.sendRedirect(redirect);
				httpSession.removeAttribute("login_redirect");
				return;
			}
		} catch (ContextAuthenticationException e) {
			httpSession.setAttribute(Constants.OPENMRS_ERROR_ATTR, "auth.invalid");
			response.sendRedirect(request.getContextPath() + "/login.htm");
		}
	}

}
