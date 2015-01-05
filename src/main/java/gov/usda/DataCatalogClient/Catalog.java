package gov.usda.DataCatalogClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * The Catalog class is based on Project Open Data metadata specification 1.1 https://project-open-data.cio.gov/v1.1/schema/
 * and W3C Data Catalog Vocabulary (DCAT)  http://www.w3.org/TR/vocab-dcat/
 * 
 * There are a couple of ways to load data into this object.
 * 
 * 1.  Load data from a Project Open Data compliant json file.  @see loadCatalogFromCKAN(String catalogFileName)
 * An example would be:
 * Catalog catalog = new Catalog();
 * catalog.loadCatalogFromCKAN("ckan.json") 
 *  
 * 2.  Load data from a CKAN compliant JSON file.  @see 
 * 
 * The Catalog class has 3 ways to output it's member variables.
 * 
 * 1.  To output to Project Open Data compliant JSON use toProjectOpenDataJSON(String, Boolean).
 * The second parameter is for creating either the Enterprise Data Inventory or the Public Data Listing
 * 
 * 2.  This class has helper method for creating CSV for distribution in Excel and potentially importing
 * to other 
 * 
 * @author bbrotsos
 *
 */

public class Catalog {
	
	//Project Open Data json field constants
	public final static String PROJECT_OPEN_DATA_CATALOG_CONFORMS_TO = "conformsTo";
	public final static String PROJECT_OPEN_DATA_CATALOG_CONTEXT = "@context";
	public final static String PROJECT_OPEN_DATA_CATALOG_DESCRIBED_BY = "describedBy";
	public final static String PROJECT_OPEN_DATA_CATALOG_IDENTIFIER = "@id";
	public final static String PROJECT_OPEN_DATA_CATALOG_TYPE = "@type";
	
	//Documentation on DCAT here: http://www.w3.org/TR/vocab-dcat/
	private String description;
	private String homepage;
	private Date issued;
	private String language;
	private String license;
	private String rights;
	private String spatial;
	private String title;

	
	//Project Open Data additional fields: https://project-open-data.cio.gov/v1.1/schema/#accessLevel
	private String conformsTo;
	private String context;
	private String describedBy;
	private String id;
	private String type;
	
	private List<Dataset> dataSetList;
	
	private CatalogException catalogException;
	
	public Catalog()
	{
		dataSetList = new ArrayList<Dataset>();
		catalogException = new CatalogException();
	}
	
	/**
	 * Populates this class' member variables from CKAN JSON Object
	 * <p>
	 * When doing a search on CKAN, the result is an array of packages(in this program datasets).
	 * This method begins the process of marshaling the JSON into Java Objects by looping through
	 * the packages and calling the loadDataset methods at the dataset level.
	 * @param catalogCKAN_JSON JSONObject The results from a CKAN query.
	 */
	public void loadCatalogFromCKAN_JSON(JSONObject catalogCKAN_JSON) throws CatalogException
	{
		if (catalogCKAN_JSON == null)
		{
			throw (new NullPointerException("JSONObject catalogCKAN_JSON cannot be null"));
		}
		final JSONObject resultObject= (JSONObject) catalogCKAN_JSON.get("result");
		final JSONArray packageList = (JSONArray) resultObject.get("packages");				
		for(int i = 0; i < packageList.size(); i++)
		{
			final JSONObject packageObject = (JSONObject) packageList.get(i);
			final Dataset ds = new Dataset();
			try{
				ds.loadDatasetFromCKAN_JSON(packageObject);
				dataSetList.add(ds);
			}
			catch(DatasetException e)
			{
				//TODO: Capture entire error
				catalogException.addError(e.toString());
			}
		}
		if (!validateCatalog() || catalogException.exceptionSize() > 0)
		{
			throw (catalogException);
		}
	}
	
	
	/**
	 * Adds datasets from another catalog to this catalog.
	 * 
	 * @param otherCatalog Catalog Another catalog who's datasets will be combined with this object.
	 */
	public void addFromOtherCatalog(Catalog otherCatalog)
	{
		if (otherCatalog == null)
		{
			throw (new NullPointerException("Catalog otherCatalog cannot be null"));
		}
		final List<Dataset> otherDatasetList = otherCatalog.dataSetList;
		
		for (Dataset ds: otherDatasetList)
		{
			dataSetList.add(ds);
		}
	}
	
	/**
	 * Outputs a Catalog into tab delimited format.
	 * <p>
	 * Begins the process by listing out the header and calling all datasets to create
	 * tab delimitted lines.
	 * @param filePath String The output file for the catalog tab delimitted file.
	 */
	public void outputCSV(String filePath) throws IOException
	{
		if (filePath == null)
		{
			throw (new NullPointerException("filepath cannot be null"));
		}
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(filePath);

			String headerLine = "Agency Name\tTitle\tDescription\tFormat\tAccess URL\tFrequency\tABureau Code\tContact Email\tContactName\t";
			headerLine = headerLine + "Landing Page\tProgram Code\tPublisher\tPublic Access Level\tAccess Level Comment\tTags\tLast Update\tRelease Date\tUnique Identifier\t";
			headerLine = headerLine + "Data Dictionary\tLicense\tSpatial\tTemporal\tSystem Of Records\tData Quality\tLangauge\t";
			headerLine = headerLine + "Program Code\tTheme\tReference\t";
			
			out.println(headerLine);		
			
			for (int i=0; i < dataSetList.size(); i++)
			{
				if (!dataSetList.get(i).getAccessLevel().equals("non-public"))
				{
					out.println(dataSetList.get(i).toCSV());
				}
			}
		}
		catch(IOException e)
		{
			throw (e);
		}
		finally{
			out.close();
		}
	}
	
	/**
	 * Populates catalog from Project Open Data compliant json object
	 * 
	 * @param catalogObject
	 */
	public void loadFromProjectOpenDataJSON(JSONObject catalogObject) throws CatalogException
	{
		if (catalogObject == null)
		{
			throw (new NullPointerException("catalogObject cannot be null"));
		}
		setConformsTo((String) catalogObject.get(PROJECT_OPEN_DATA_CATALOG_CONFORMS_TO));
		setDescribedBy((String) catalogObject.get(PROJECT_OPEN_DATA_CATALOG_DESCRIBED_BY));
		setContext ((String) catalogObject.get(PROJECT_OPEN_DATA_CATALOG_CONTEXT));
		setType ((String) catalogObject.get(PROJECT_OPEN_DATA_CATALOG_TYPE));
		
		final JSONArray dataSetArray = (JSONArray) catalogObject.get(Dataset.PROJECT_OPEN_DATA_DATASET);
		for (int i = 0; i < dataSetArray.size(); i++)
		{
			final Dataset ds = new Dataset();
			final JSONObject dataSetObject = (JSONObject) dataSetArray.get(i);
			try
			{
				ds.loadFromProjectOpenDataJSON(dataSetObject);
				dataSetList.add(ds);
			}
			catch(DatasetException e)
			{
				catalogException.addError(e.toString());
			}
		}		
		
		if (!validateCatalog() || catalogException.exceptionSize() > 0)
		{
			throw (catalogException);
		}
	}
	
	/**
	 * Populates catalog object from CKAN compliant results string.
	 * @param catalogJSONString String CKAN search results string
	 */
	public void loadCatalogFromJSONString(String catalogJSONString) throws CatalogException
	{
		if (catalogJSONString == null)
		{
			throw (new NullPointerException("catalogJSONString cannot be null"));
		}
		
		JSONObject resourceCKAN_JSON = null; 
		try{
			resourceCKAN_JSON = Utils.loadJsonObjectFromString(catalogJSONString);
		}
		catch (ParseException e) 
		{
			catalogException.addError("Error parsing string: " + e.toString());
		}
			
		loadCatalogFromCKAN_JSON(resourceCKAN_JSON);
	}
	

	/**
	 * Populates catalog from CKAN compliant json file.
	 * @param catalogFileName
	 */
	public void loadCatalogFromCKAN(String catalogFileName) throws CatalogException
	{
		if (catalogFileName == null)
		{
			throw (new NullPointerException("catalogFileName cannot be null"));
		}
		JSONObject resourceCKAN_JSON = new JSONObject();
		try{
			resourceCKAN_JSON = Utils.loadJsonObjectFile(catalogFileName);
		}
		catch (IOException | ParseException e) 
		{
			catalogException.addError(e.toString());
			throw (catalogException);
		}
				
		loadCatalogFromCKAN_JSON(resourceCKAN_JSON);
	}
	
	/**
	 * Outputs catalog object to Project Open Data v1.1 compliant json file for example data.json.
	 * <p>
	 * 
	 * @param podFilePath
	 * @param privateIndicator
	 */
	@SuppressWarnings("unchecked")
	public void toProjectOpenDataJSON(String podFilePath, Boolean privateIndicator) throws IOException
	{	
		if (podFilePath == null || privateIndicator == null)
		{
			throw (new NullPointerException("podFilePath or privateIndicator cannot be null."));
		}
		final JSONObject catalogJSON = new JSONObject();
		final JSONArray dataSetArray = new JSONArray();
		
		catalogJSON.put(PROJECT_OPEN_DATA_CATALOG_CONFORMS_TO, "https://project-open-data.cio.gov/v1.1/schema");
		catalogJSON.put(PROJECT_OPEN_DATA_CATALOG_DESCRIBED_BY, "https://project-open-data.cio.gov/v1.1/schema/catalog.json");
		catalogJSON.put(PROJECT_OPEN_DATA_CATALOG_CONTEXT, "https://project-open-data.cio.gov/v1.1/schema/data.jsonld");
		catalogJSON.put(PROJECT_OPEN_DATA_CATALOG_TYPE, "dcat:Catalog");
	
		for(Dataset ds: dataSetList)
		{
			if (privateIndicator)
			{
				dataSetArray.add(ds.toProjectOpenDataJSON());
			}
			else
			{
				String publicAccessLevel = ds.getAccessLevel();
				if (publicAccessLevel.equals("public") || publicAccessLevel.equals("restricted"))
				{
					dataSetArray.add(ds.toProjectOpenDataJSON());
				}
			}
		}
	
		catalogJSON.put(Dataset.PROJECT_OPEN_DATA_DATASET, dataSetArray);
		Utils.printJSON(podFilePath, catalogJSON); 
	}
	
	/**
	 * Skeleton method
	 * @return
	 */
	public JSONObject toCKAN_JSON()
	{
		JSONObject catalogCKAN_JSON = new JSONObject();
		return catalogCKAN_JSON;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
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
	public String getSpatial() {
		return spatial;
	}
	public void setSpatial(String spatial) {
		this.spatial = spatial;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
	}
	public String getDescribedBy() {
		return describedBy;
	}
	public void setDescribedBy(String describedBy) {
		this.describedBy = describedBy;
	}
	
	public int size()
	{
		return dataSetList.size();
	}
	
	/**
	 * Validates catalog is Project Open Data 1.1 compliant
	 * <p>
	 * This method checks business rules that identifiers are unique.  Other business rules
	 * will be added in the future.
	 * @return
	 */
	public Boolean validateCatalog()
	{
		Boolean validIndicator = true;
		validIndicator = validateUniqueIdentifiers();
		
		return validIndicator;
	}
	
	/**
	 * Search through datasetList looking for identifiers that are equal.  All identifiers
	 * should be unique.
	 * @return
	 */
	public Boolean validateUniqueIdentifiers()
	{
		Boolean validIndicator = true;
		//optimize this
		for (int i = 0; i < dataSetList.size(); i++)
		{
			String identifier = dataSetList.get(i).getUniqueIdentifier();
			for (int k=i+1; k < dataSetList.size(); k++)
			{
				String otherIdentifier = dataSetList.get(k).getUniqueIdentifier();
				if (identifier.equals(otherIdentifier))
				{
					catalogException.addError("Invalid catalog: non-unique identifier: " + otherIdentifier);
					validIndicator=false;
				}
			}
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
		if (!(o instanceof Catalog))
		{
			return false;
		}
		Catalog catalog_other = (Catalog)o;
		
		return new EqualsBuilder()
         .append(conformsTo, catalog_other.conformsTo)
         .append(context, catalog_other.context)
         .append(dataSetList, catalog_other.dataSetList)
         .append(describedBy, catalog_other.describedBy)
         .append(description, catalog_other.description)
         .append(homepage, catalog_other.homepage)
         .append(id, catalog_other.id)
         .append(issued, catalog_other.issued)
         .append(language, catalog_other.language)
         .append(license, catalog_other.license)
         .append(rights, catalog_other.rights)
         .append(spatial, catalog_other.spatial)
         .append(title,  catalog_other.title)
         .append(type, catalog_other.type)
         .isEquals();
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(19, 29).
				append(conformsTo).
				append(context).
				append(dataSetList).
				append(describedBy).
				append(description). 
				append(homepage). 
				append(id). 
				append(issued). 
				append(language).
				append(license).
				append(rights).
				append(spatial).
				append(title).
				append(type).
				toHashCode();
	}
	
	
	
	@Override
	public String toString() {
		return "Catalog [title=" + title + ", description=" + description
				+ ", issued=" + issued + ", language=" + language
				+ ", license=" + license + ", rights=" + rights + ", spatial="
				+ spatial + ", homepage=" + homepage + ", context=" + context
				+ ", id=" + id + ", type=" + type + ", conformsTo="
				+ conformsTo + ", describedBy=" + describedBy
				+ ", dataSetList=" + dataSetList + "]";
	}

	//This is for mulitple organization catalogs
	public void loadMulitpleCatalogsFromCKAN(String catalogFileName)
		{
			//TODO: testing skeleton for loadMultipleCatalogsFromCKAN
		}
		
		public void produceQuarterlyReport (String quarterReportFileName)
		{
			//TODO: testing skeleton for produceQuarterlyReport
		}
		
		public void produceBureauMetrics(String bureauMetricsFileName)
		{
			//TODO: testing skeleton for produceBureauMetrics
		}
}
