package org.openmrs;

import java.util.Date;

/**
 * ConceptName
 * 
 * @author Burke Mamlin
 */
public class ConceptName implements java.io.Serializable {

	public static final long serialVersionUID = 33226787L;

	// Fields

	private Concept concept;
	private String name;
	private String shortName;
	private String locale;
	private User creator;
	private Date dateCreated;

	// Constructors

	/** default constructor */
	public ConceptName() {
	}

	public boolean equals(Object obj) {
		if (obj instanceof ConceptName) {
			ConceptName c = (ConceptName) obj;
			return (this.concept.equals(c.getConcept())
					&& this.name.equals(c.getName()) && this.locale.equals(c
					.getLocale()));
		}
		return false;
	}

	public int hashCode() {
		if (this.getConcept() == null || this.getName() == null
				|| this.getLocale() == null)
			return super.hashCode();
		return this.getConcept().hashCode() + this.getName().hashCode() + this.getLocale().hashCode();
	}

	// Property accessors

	/**
	 * 
	 */
	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * 
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName
	 *            The shortName to set.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return Returns the creator.
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            The creator to set.
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return Returns the dateCreated.
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            The dateCreated to set.
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

}