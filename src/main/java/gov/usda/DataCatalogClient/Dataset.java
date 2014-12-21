package gov.usda.DataCatalogClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Dataset {
	
	//documentation is at http://www.w3.org/TR/vocab-dcat/
	private String title;
	private String description;
	private Date issued;
	private Date modified;
	private String identifier;
	//These three are lists for Project Open Data compliance
	private List<String> keywordList;
	private List<String> languageList;
	private List<String> themeList;
	private String contactPoint;
	private String temporal;
	private String spatial;
	private String accrualPeriodicity;
	private URL landingPage;
	
	//federal government project open data extension documentation here: https://project-open-data.cio.gov/v1.1/schema/
	private String uniqueIdentifier;
	private List<String> bureauCodeList;
	private List<String> programCodeList;
	private String primaryITInvestmentUII;
	private String contactEmail;  //Project Open Data = hasEmail
	private String accessLevel;
	private String rights;
	private String systemOfRecords;
	private Boolean dataQuality;
	private List<String> referenceList;
	private String publisher;
	private String describedBy;
	private String describedByType;
	
	//Agency specific (legacy)
	private String comments;
	private String webService;
	private String ownerOrganization;
	
	private DatasetException dsEx;
	
	public Dataset()
	{
		dsEx = new DatasetException("Dataset Error");
		bureauCodeList = new ArrayList<String>();
		programCodeList = new ArrayList<String>();
		keywordList = new ArrayList<String>();
		languageList = new ArrayList<String>();
		themeList = new ArrayList<String>();
		referenceList = new ArrayList<String>();
	}
	
	public void loadDatasetFromCKAN_JSON(JSONObject datasetCKAN_JSON)
	{	
		//probably shoud use GSON, but I ran into problems on android in past.
		//optimize in the future
		JSONArray extraList = (JSONArray) datasetCKAN_JSON.get("extras");
		for (int i = 0; i < extraList.size(); i++)
		{
			JSONObject extraObject = (JSONObject) extraList.get(i);
			String key = (String) extraObject.get("key");
			String value = (String) extraObject.get("value");
			if (key.equals("data_quality"))
	    	{
	    		setDataQuality(value);	    		
	    	}
	    	else if (key.equals("accrual_periodicity"))
	    	{
	    		setAccrualPeriodicity(value);
	    	}
	    	else if (key.equals("bureau_code"))
	    	{
	    		setBureauCodeList(value);
	    	}
	    	else if (key.equals("unique_id"))
	    	{
	    		setUniqueIdentifier(value);
	    	}
	    	else if (key.equals("contact_email"))
	    	{
	    		setContactEmail(value);
	    	}
	    	else if (key.equals("contact_name"))
	    	{
	    		setContactPoint(value);
	    	}
	    	else if (key.equals("homepage_url"))
	    	{
	    		setLandingPage(value);
	    	}
	    	//TODO: Fix ckan feed for misspelled program_ocde
	    	else if (key.equals("program_code") || key.equals("program_ocde"))
	    	{
	    		programCodeList.add(value);
	    	}
	    	else if (key.equals("publisher"))
	    	{
	    		setPublisher(value);
	    	}
	    	else if (key.equals("related_documents"))
	    	{
	    		setReferenceList(value);
	    	}
	    	else if (key.equals("release_date"))
	    	{
	    		setIssued(value);
	    	}
	    	else if (key.equals("spatial"))
	    	{
	    		setSpatial(value);
	    	}
	    	else if (key.equals("temporal"))
	    	{
	    		setTemporal(value);
	    	}
	    	else if (key.equals("public_access_level"))
	    	{
	    		setAccessLevel(value);
	    	}
	    	else if (key.equals("access_level_comment"))
	    	{
	    		setRights(value);
	    	}
	    	else if (key.equals("title"))
	    	{
	    		setTitle(value.trim());	    		
	    	}
	    	else if (key.equals("revision_timestamp"))
	    	{
	    		setModified(value);
	    	}
	    	else if (key.equals("notes"))
	    	{
	    		setComments(value);
	    	}
	    	else if (key.equals("category"))
	    	{
	    		setThemeList(value);	
	    	}
	    	else if (key.equals("modified"))
	    	{
	    		setModified(value);
	    	}
	    	else if (key.equals("system_of_records"))
	    	{
	    		setSystemOfRecords(value);
	    	}
	    	else if (key.equals("data_dictionary") || key.equals("data_dict"))
	    	{
	    		setDescribedBy(value);
	    	}
	    	else if (key.equals("language"))
	    	{
	    		setLanguageList(value);
	    	}
	    	else if (key.equals("webservice"))
	    	{
	    		setWebService(value);
	    	}
	    	else if (key.equals("owner_org") || key.equals("ow"))
	    	{
	    		setOwnerOrganization(value);
	    	}
	    	else
	    	{
	    		System.out.println("Unaccounted for CKAN Element:" + key + " value: " + value);
	    	}
		}
		
	}
	
	public JSONObject toCKAN_JSON()
	{
		JSONObject datasetCKAN_JSON = new JSONObject();
		datasetCKAN_JSON.put("title", this.title);
		datasetCKAN_JSON.put("unique_id", uniqueIdentifier);
		datasetCKAN_JSON.put("contact_name", contactPoint);
		datasetCKAN_JSON.put("contact_email", contactEmail);
		datasetCKAN_JSON.put("public_access_level", accessLevel);
	
		return datasetCKAN_JSON;
	}
	
	public Map toProjectOpenDataJSON()
	{
		Map dataSetJSON = new LinkedHashMap();
		//JSONObject dataSetJSON = new JSONObject();
		dataSetJSON.put("title", title);
		dataSetJSON.put("description", description);
		dataSetJSON.put("keyword", keywordList);
		dataSetJSON.put("modified", modified);
		
		return dataSetJSON;
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

	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
	}
	
	private void setIssued(String issued)
	{
		this.issued = convertISOStringToDate(issued);
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public void setModified(String modified){
		this.modified = convertISOStringToDate(modified);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}

	public List<String> getLanguageList() {
		return languageList;
	}

	public void setLanguageList(List<String> languageList) {
		this.languageList = languageList;
	}
	
	public void setLanguageList(String languageListString){
		this.languageList.add(languageListString);
	}

	public List<String> getThemeList() {
		return themeList;
	}

	public void setThemeList(List<String> themeList) {
		this.themeList = themeList;
	}
	
	//CKAN will send this data element as comma separated values
	private void setThemeList(String themeListString)
	{
		String[] categoryArray = themeListString.split(",");
		if (categoryArray.length == 1)
		{
			themeList.add(themeListString);
		}
		else
		{
			for (int r = 0; r < categoryArray.length; r++)
			{
				themeList.add(categoryArray[r].trim());
			}
		}
	}

	public String getContactPoint() {
		return contactPoint;
	}

	public void setContactPoint(String contactPoint) {
		this.contactPoint = contactPoint;
	}

	public String getTemporal() {
		return temporal;
	}

	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}

	public String getSpatial() {
		return spatial;
	}

	public void setSpatial(String spatial) {
		this.spatial = spatial;
	}

	public String getAccrualPeriodicity() {
		return accrualPeriodicity;
	}

	public void setAccrualPeriodicity(String accrualPeriodicity) {
		this.accrualPeriodicity = accrualPeriodicity;
	}

	public URL getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(URL landingPage) {
		this.landingPage = landingPage;
	}
	
	private void setLandingPage(String landingPage){
		try 
		{
			this.landingPage = new URL(landingPage);
		}
		catch(MalformedURLException ex)
		{
			dsEx.addError("Landing Page must be valid URL: " + landingPage);
		}
	}

	public List<String> getBureauCodeList() {
		return bureauCodeList;
	}

	public void setBureauCodeList(String bureauCode){
		if (Pattern.matches("\\d{3}:\\d{2}", bureauCode)){
			bureauCodeList.add(bureauCode);
		}
		else{
			dsEx.addError("Bureau Code must be \\d{3}:\\d{2}: " + bureauCode);
			//throw dsEx;
		}
	}

	public List<String> getProgramCodeList() {
		return programCodeList;
	}

	public void setProgramCode(String programCode) {
		programCodeList.add(programCode);
	}

	public String getPrimaryITInvestmentUII() {
		return primaryITInvestmentUII;
	}

	public void setPrimaryITInvestmentUII(String primaryITInvestmentUII) {
		this.primaryITInvestmentUII = primaryITInvestmentUII;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getSystemOfRecords() {
		return systemOfRecords;
	}

	public void setSystemOfRecords(String systemOfRecords) {
		this.systemOfRecords = systemOfRecords;
	}

	public Boolean getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(Boolean dataQuality) {
		this.dataQuality = dataQuality;
	}
	
	//handle string case
	private void setDataQuality(String dataQuality){
		if (dataQuality.equals("true"))
		{
			this.dataQuality = true;
		}
		else
		{
			this.dataQuality = false;
		}
	}

	public List<String> getReferenceList() {
		return referenceList;
	}

	public void setReferenceList(List<String> referenceList) {
		this.referenceList = referenceList;
	}
	
	//CKAN can send this as a comma delimited field
	private void setReferenceList(String referenceListString) {
		String[] referenceArray = referenceListString.split(",");
		
		if (referenceArray.length == 1)
		{
			referenceList.add(referenceListString);
		}
		else
		{
			for (int r = 0; r < referenceArray.length; r++)
			{
				referenceList.add(referenceArray[r].trim());
			}
		}
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public Date convertISOStringToDate(String isoDateString)
	{
		DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date isoFormattedDate = new Date();
		try
		{
			isoDateFormat.parse(isoDateString);
		}
		catch(ParseException ex)
		{
			dsEx.addError("Date Parse Exception: " + isoDateString);
		}
		return isoFormattedDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getWebService() {
		return webService;
	}

	public void setWebService(String webService) {
		this.webService = webService;
	}

	public String getOwnerOrganization() {
		return ownerOrganization;
	}

	public void setOwnerOrganization(String ownerOrganization) {
		this.ownerOrganization = ownerOrganization;
	}

}
