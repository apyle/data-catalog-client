package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NetworkRequestTest {

	private static final Logger log = Logger.getLogger(NetworkRequestTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test this manually so not to hit the server every time.
	 * @throws ParseException
	 */
	@Test
	public void testGetOrganization() throws ParseException {
		
		//manually check this test
		if (false)
		{
		String response = "";
		try{
			NetworkRequest nr = new NetworkRequest();
			response = nr.getOrganizationCatalog("usda-gov");
		}
		catch(IOException | ParseException e){
			log.log(Level.SEVERE, e.toString());
		}
		
		Catalog testCatalog = new Catalog();
		CatalogException catalogException = null;
		try{
			testCatalog.loadCatalogFromJSONString(response);
		}
		catch(CatalogException e)
		{
			log.log(Level.SEVERE, e.toString());
			catalogException = e;
		}
		assertNull(catalogException);
		}
	}
	
	/**
	 * Added a filename parameter to NetworkRequest to the config file, this makes for easier testing.
	 * @throws ParseException
	 */
	@Test
	public void testCKANGetOrganization() throws ParseException {
		String response = "";
		try{
			NetworkRequest nr = new NetworkRequest("sample_data/config-ckan-demo.json");
			response = nr.getOrganizationCatalog("corporation-test");
		}
		catch(IOException | ParseException e){
			log.log(Level.SEVERE, e.toString());
		}
		
		Catalog testCatalog = new Catalog();
		CatalogException catalogException = null;
		try{
			testCatalog.loadCatalogFromJSONString(response);
		}
		catch(CatalogException e)
		{
			log.log(Level.SEVERE, e.toString());
			catalogException = e;
		}
		try{
			testCatalog.toProjectOpenDataJSON("ckan_data.json", true);
		}
		catch(IOException e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		assertNull(catalogException);
	}
	
	/**
	 * Needs to create unique name every time it send in new dataset request for testing.
	 */
	@Test
	public void testCreateDataset()
	{
		Dataset createDS = new Dataset();
		final String datasetTestProjectOpenDataFile = "sample_data/test/datasetTestProjectOpenData.json";

		createDS = getProjectOpenDataDataset();
		
		try
		{
			NetworkRequest nr = new NetworkRequest("sample_data/config-ckan-demo.json");
			nr.createDataset(createDS.toCKAN_JSON());
		}
		catch (IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
			//if http_reponse = 409 most likely conflict with "name"  This must be unique.
			//possible random number collision
			Assert.fail();
		}
	}
	
	/**
	 * Test updating CKAN for a given dataset.
	 */
	@Test
	public void testUpdateDataset()
	{
		//first create the dataset
		Dataset updateDS = getProjectOpenDataDataset();
		
		try{
			NetworkRequest nr = new NetworkRequest("sample_data/config-ckan-demo.json");
			nr.createDataset(updateDS.toCKAN_JSON());
		} 
		catch(IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail();
		}
		
		//update the ds
		updateDS.setDescription("UPDATED:" + updateDS.getDescription());
		
		try
		{
			NetworkRequest nr = new NetworkRequest("sample_data/config-ckan-demo.json");
			nr.updateDataset(updateDS.getName(), updateDS.toCKAN_JSON());
		}
		catch (IOException | ParseException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail();
		}
	}
	
	private Dataset getProjectOpenDataDataset()
	{
		Dataset ds = new Dataset();
		final String datasetTestProjectOpenDataFile = "sample_data/test/datasetTestProjectOpenData.json";

		try{
			ds.loadDatasetFromFile(datasetTestProjectOpenDataFile);
		}
		catch(IOException | DatasetException e)
		{
			log.log(Level.SEVERE, e.toString());
			Assert.fail();
		}
		
		int rand = generateSimpleRandom();
		
		ds.setTitle(ds.getTitle() + "_" + rand);
		ds.setUniqueIdentifier(ds.getUniqueIdentifier() + rand);
		
		
		return ds;
	}
	
	/**
	 * real simple random, don't use for anything else.
	 * @return
	 */
	private int generateSimpleRandom()
	{
		Random random = new Random();
	    int randomNumber = random.nextInt((1000 - 10) + 1) + 10;
	    return randomNumber;
	}

}
