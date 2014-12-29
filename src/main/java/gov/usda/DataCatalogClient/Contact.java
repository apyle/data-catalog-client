package gov.usda.DataCatalogClient;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Contact {

	private String type;
	private String fullName;
	private String emailAddress;
	
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
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public Map toProjectOpenDataJSON()
	{
		Map contactPointMap = new LinkedHashMap();
		contactPointMap.put("fn", getFullName());
		contactPointMap.put("hasEmail", getEmailAddress());
		return contactPointMap;
	}
	
	public void loadDatasetFromPOD_JSON(JSONObject contactProjectOpenDataJSON) throws ContactException
	{
		if (contactProjectOpenDataJSON== null)
		{
			throw new ContactException("contact cannot be empty");
		}
		type = (String) contactProjectOpenDataJSON.get("@type");
		fullName = (String) contactProjectOpenDataJSON.get("fn");
		emailAddress = (String) contactProjectOpenDataJSON.get("hasEmail");
	}
	
	public Boolean validateContact()
	{
		Boolean validIndicator = true;
		
		if (fullName == null)
		{
			System.out.println("Contact Point invalid: Full Name is required");
			validIndicator = false;
		}
		if (emailAddress == null)
		{
			System.out.println("Contact Point invalid: Email Address is required");
			validIndicator = false;
		}
		
		return validIndicator;
	}

	
}
