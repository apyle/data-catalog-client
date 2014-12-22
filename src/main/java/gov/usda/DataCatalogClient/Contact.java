package gov.usda.DataCatalogClient;

import java.util.LinkedHashMap;
import java.util.Map;

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
	
}
