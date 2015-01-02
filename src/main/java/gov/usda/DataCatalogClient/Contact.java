package gov.usda.DataCatalogClient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONObject;
/**
 * The Contact class is based on Project Open Data Metadata specification 1.1.
 * More details can be found here: https://project-open-data.cio.gov/v1.1/schema/#contactPoint
 * 
 * @author bbrotsos
 *
 */
public class Contact {

	private String type;
	private String fullName;
	private String emailAddress;
	
	private ContactException contactException = new ContactException();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	
	/**
	 * Sometimes email address will come in as mailto:  This is removed
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress) {
		if (emailAddress != null)
		{
			//remove the 'mailto' in mailto:jane.doe@us.gov if it exists
			emailAddress = removeMailto(emailAddress);
			EmailValidator emailValidator = EmailValidator.getInstance();
			if (!emailValidator.isValid(emailAddress))
			{
				contactException.addError("Email Address: " + emailAddress + " is not a valid address.");
			}
		}
		this.emailAddress = emailAddress;
	}
	
	/**
	 * Sometimes email address will come in as mailto:  This is removed
	 * @param emailAddress
	 */
	private String removeMailto(String emailAddress)
	{
		//remove the 'mailto' in mailto:jane.doe@us.gov if it exists
		if (emailAddress.contains(":"))
		{
			final String[] parseEmailAddress = emailAddress.split(":");
			emailAddress = parseEmailAddress[1];
		}
		return emailAddress;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toProjectOpenDataJSON()
	{
		JSONObject contactPointMap = new JSONObject();
		contactPointMap.put("fn", getFullName());
		contactPointMap.put("hasEmail", getEmailAddress());
		return contactPointMap;
	}
	
	/**
	 * This method takes in a Project Open Data contactPoint json file.
	 * 
	 * {
	 *    "@type": "vcard:Contact",
	 *    "fn": "Jane Doe",
	 *    "hasEmail": "mailto:jane.doe@us.gov"
     * }
	 * @param contactProjectOpenDataJSON
	 * @throws ContactException
	 */
	public void loadDatasetFromPOD_JSON(JSONObject contactProjectOpenDataJSON) throws ContactException
	{
		if (contactProjectOpenDataJSON == null)
		{
			throw new ContactException("contact cannot be empty");
		}
		setType((String) contactProjectOpenDataJSON.get("@type"));
		setFullName((String) contactProjectOpenDataJSON.get("fn"));
		setEmailAddress((String) contactProjectOpenDataJSON.get("hasEmail"));
		validateContact();
	}
	
	/**
	 * Business rules for valid Contact.  fullName and email address are required.
	 * @return
	 * @throws ContactException
	 */
	public Boolean validateContact() throws ContactException
	{
		Boolean validIndicator = true;
		if (fullName == null)
		{
			contactException.addError("Full Name is required");
			validIndicator = false;
		}
		if (emailAddress == null)
		{
			contactException.addError("Email Address is required");
			validIndicator = false;
		}
		if (contactException.exceptionSize() > 0)
		{
			throw (contactException);
		}
		
		return validIndicator;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Contact))
		{
			return false;
		}
		Contact contact_other = (Contact)o;
		
		return new EqualsBuilder()
         .append(emailAddress, contact_other.emailAddress)
         .append(fullName, contact_other.fullName)
         .append(type,contact_other.type)
         .isEquals();
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(21, 27).
				append(emailAddress).
				append(fullName).
				append(type).
				toHashCode();
	}
	@Override
	public String toString() {
		return "Contact [type=" + type + ", fullName=" + fullName
				+ ", emailAddress=" + emailAddress + "]";
	}
	
	
	
}
