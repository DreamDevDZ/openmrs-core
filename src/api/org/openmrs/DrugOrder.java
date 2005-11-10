package org.openmrs;

/**
 * DrugOrder 
 * 
 * @author Ben Wolfe
 * @version 1.0
 */
public class DrugOrder extends Order implements java.io.Serializable {

	public static final long serialVersionUID = 72232L;

	// Fields

	private Integer dose;
	private String units;
	private String frequency;
	private Boolean prn;
	private Boolean complex;
	private Integer quantity;
	private Drug drug;
	
	// Constructors

	/** default constructor */
	public DrugOrder() {
	}

	/** constructor with id */
	public DrugOrder(Integer orderId) {
		this.setOrderId(orderId);
	}

	/** 
	 * Compares two DrugOrder objects for similarity
	 * 
	 * @param obj
	 * @return boolean true/false whether or not they are the same objects
	 */
	public boolean equals(Object obj) {
		if (obj instanceof DrugOrder) {
			//DrugOrder d = (DrugOrder)obj;
			return (super.equals((Order)obj)); /* &&
				this.getDrug().equals(d.getDrug()) &&
				this.getDose().equals(d.getDose())); */
		}
		return false;
	}
	
	public int hashCode() {
		if (this.getOrderId() == null) return super.hashCode();
		return this.getOrderId().hashCode();
	}

	public boolean isDrugOrder() {
		return true;
	}
	
	// Property accessors

	/**
	 * Gets the dosage for this drug order
	 * @return dose
	 */
	public Integer getDose() {
		return this.dose;
	}

	/**
	 * Sets the dosage for this drug order
	 * @param dose
	 */
	public void setDose(Integer dose) {
		this.dose = dose;
	}

	/**
	 * Gets the units of this drug order
	 * @return units
	 */
	public String getUnits() {
		return this.units;
	}

	/**
	 * Sets the units of this drug order
	 * @param units
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * Gets the frequency
	 * @return frequency
	 */
	public String getFrequency() {
		return this.frequency;
	}

	/**
	 * Sets the frequency
	 * @param frequency
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * Returns true/false whether the drug is a "pro re nata" (as needed) drug
	 * @return Boolean
	 */
	public Boolean isPrn() {
		return this.prn;
	}

	/**
	 * Sets the prn 
	 * @param prn
	 */
	public void setPrn(Boolean prn) {
		this.prn = prn;
	}

	/**
	 * Gets whether this drug is complex
	 * @return Boolean
	 */
	public Boolean isComplex() {
		return this.complex;
	}

	/**
	 * Sets whether this drug is complex
	 * @param complex
	 */
	public void setComplex(Boolean complex) {
		this.complex = complex;
	}

	/**
	 * Gets the quantity
	 * @return quantity
	 */
	public Integer getQuantity() {
		return this.quantity;
	}

	/**
	 * Sets the quantity
	 * @param quantity
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the drug
	 * @return drug
	 */
	public Drug getDrug() {
		return this.drug;
	}

	/**
	 * Sets the drug
	 * @param drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
	}
}