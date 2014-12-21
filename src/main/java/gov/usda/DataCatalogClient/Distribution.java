package gov.usda.DataCatalogClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Distribution {

	//Common DCAT & POD metadata fields
	private String title;
	private String description;
	private URL accessURL;
	private URL downloadURL;
	private String mediaType;
	private String format;
	
	//Additional DCAT fields http://www.w3.org/TR/vocab-dcat/
	private Integer byteSize;
	private Date issued;
	private Date modified;
	private String license;
	private String rights;
	
	//Additional POD Fields: https://project-open-data.cio.gov/v1.1/schema/#accessLevel
	private String describedBy;
	private String describedByType;
	private String conformsTo;
	private String type;
	
	public void loadDistributionFromCKAN_JSON(JSONObject resourceCKAN_JSON)
	{
		setAccessURL((String) resourceCKAN_JSON.get("url"));
    	format = (String) resourceCKAN_JSON.get("format");
    	title = (String) resourceCKAN_JSON.get("name");
    	description = (String) resourceCKAN_JSON.get("description");	
	}
	
	public JSONObject toCKAN_JSON()
	{
		JSONObject distributionCKAN_JSON = new JSONObject();
		
		distributionCKAN_JSON.put("name", title);
		distributionCKAN_JSON.put("description", description);
		distributionCKAN_JSON.put("format", format);
		distributionCKAN_JSON.put("url", accessURL);
		
		return distributionCKAN_JSON;
	}
	
	public Map toProjectOpenDataJSON()
	{
		Map distributionJSON = new LinkedHashMap();
		//JSONObject dataSetJSON = new JSONObject();
		distributionJSON .put("title", title);
		distributionJSON .put("description", description);
		
		return distributionJSON ;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public URL getAccessURL() {
		return accessURL;
	}
	public void setAccessURL(URL accessURL) {
		this.accessURL = accessURL;
	}
	private void setAccessURL(String accessURL_String) 
	{
		try
		{
			this.accessURL =  new URL(accessURL_String);
		}
		catch(MalformedURLException ex)
		{
			System.out.println(ex.toString());
		}
	}
	public URL getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(URL downloadURL) {
		this.downloadURL = downloadURL;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Integer getByteSize() {
		return byteSize;
	}
	public void setByteSize(Integer byteSize) {
		this.byteSize = byteSize;
	}
	public Date getIssued() {
		return issued;
	}
	public void setIssued(Date issued) {
		this.issued = issued;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getDescribedBy() {
		return describedBy;
	}
	public void setDescribedBy(String describedBy) {
		this.describedBy = describedBy;
	}
	public String getDescribedByType() {
		return describedByType;
	}
	public void setDescribedByType(String describedByType) {
		this.describedByType = describedByType;
	}
	public String getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
