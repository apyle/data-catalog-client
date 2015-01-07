package gov.usda.DataCatalogClient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {

	private static final Logger log = Logger.getLogger(ClientTest.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadOrganizations() {
		Catalog catalog = new Catalog();
    	Client odpClient = new Client();
    	
    	try{
    		catalog = odpClient.loadOrganizationsIntoCatalog("edi_2014-01-06");
    		catalog.toCSV("sample_data/test/catalog_full.txt", Catalog.DataListingCode.ENTERPRISE_DATA_INVENTORY);
    		catalog.toProjectOpenDataJSON("sample_data/test/data.json", Catalog.DataListingCode.PUBLIC_DATA_LISTING);
    	}
    	catch(CatalogException | IOException e)
    	{
			log.log(Level.SEVERE, e.toString());
    	}
    	
    
	}

}
