package gov.usda.DataCatalogClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This is a helper class for the networking calls to a CKAN installation.
 * @author bbrotsos
 *
 */
public class NetworkRequest 
{
	HttpURLConnection connection;
	private String server;
	private String apiKey;
	
	/**
	 * The default constructor will load sample_data/config.json
	 * @throws IOException
	 * @throws ParseException
	 */
	public NetworkRequest () throws IOException, ParseException
	{
		String config_path = "sample_data/config.json";
		loadServerAndAPI_Key(config_path);
	}
	
	/**
	 * Allows user to enter in custom config path.  This is helpful for testing on multiple servers
	 * @param config_path
	 * @throws IOException
	 * @throws ParseException
	 */
	public NetworkRequest (String config_path) throws IOException, ParseException{
		loadServerAndAPI_Key(config_path);
	}
	
	/**
	 * Sample config file looks like:
	 * {
	 *    "server":"server.com",
	 *    "api_key":"My API Key here"
	 * }
	 * @param config_path
	 * @throws IOException
	 * @throws ParseException
	 */
	private void loadServerAndAPI_Key(String config_path) throws IOException, ParseException
	{
		JSONObject configJSON = new JSONObject();
		
		configJSON = Utils.loadJsonObjectFile(config_path);
		server = (String)configJSON.get("server");
		apiKey = (String)configJSON.get("api_key");
	}
	
	/**
	 * Common connection setup.  For certain CKAN installations a cookie also needs to be sent with auth_tkt.
	 * @param dataAPIURL
	 * @throws IOException
	 */
	private void setupConnection(URL dataAPIURL) throws IOException
	{
		if (dataAPIURL.getProtocol().toLowerCase().equals("https"))
		{
			connection = (HttpsURLConnection)dataAPIURL.openConnection();
		}
		else
		{
			connection = (HttpURLConnection)dataAPIURL.openConnection();
		}
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Authorization", apiKey);
		connection.setRequestProperty("Cookie", "auth_tkt=hello_world");
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(20000);
	}	
	
	/**
	 * Returns a CKAN compliant string of an organization's Datasets also known as Packages in CKAN.
	 * @param organization
	 * @return
	 * @throws IOException
	 */
	public String getOrganizationCatalog(String organization) throws IOException
	{
		URL dataAPIURL = new URL(server + "/api/3/action/organization_show?id=" + organization);
		setupConnection(dataAPIURL);
		connection.setRequestProperty("Content-Type", "application/json");
		return getHttpResponse();
	}
	
	/**
	 * Gets a single dataset from CKAN based on the unique name.
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public String getDataset(String name) throws IOException
	{
		URL dataAPIURL = new URL(server + "/api/3/action/package_show?id=" + name);
		
		setupConnection(dataAPIURL);
		connection.setRequestProperty("Content-Type", "application/json");
		return getHttpResponse();
	}
	
	/**
	 * Takes in a CKAN formated JSON Object (Packages and Resources) and adds it to the CKAN repository.
	 * @param postJSON
	 * @return
	 * @throws IOException
	 */
	public String createDataset(JSONObject postJSON) throws IOException
	{
		URL dataAPIURL = new URL(server + "/api/3/action/package_create");
		setupConnection(dataAPIURL);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);

		postObject(postJSON);
		return getHttpResponse();
	}
	
	/**
	 * Takes a single Dataset and updates it on CKAN based on the unique ckan name.
	 * @param name
	 * @param postJSON
	 * @return
	 * @throws IOException
	 */
	public String updateDataset(String name, JSONObject postJSON) throws IOException
	{
		URL dataAPIURL = new URL(server + "/api/3/action/package_update");
		setupConnection(dataAPIURL);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);

		postObject(postJSON);
		return getHttpResponse();
	}
	
	/**
	 * Deletes a dataset and returns back the result to the user.
	 * @param name
	 * @param postJSON
	 * @return
	 * @throws IOException
	 */
	public String  deleteDataset(String name, JSONObject postJSON) throws IOException
	{
		//get rid of connection_public...using it for connecting to non-ssl
		URL dataAPIURL = new URL(server + "/api/3/action/package_delete");
		setupConnection(dataAPIURL);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);

		postObject(postJSON);
		return getHttpResponse();
	}
	
	/**
	 * Common method for posting objects.
	 * @param object
	 * @throws IOException
	 */
	private void postObject(JSONObject object) throws IOException
	{
		OutputStreamWriter out = null;
		try
		{
		    out = new OutputStreamWriter(connection.getOutputStream());	 
			out.write(object.toJSONString());
		}
		catch(IOException e)
		{
			throw (e);
		}
		finally
		{
			out.close();
		}
	}
		
	/**
	 * Common method for getting HTTP response.
	 * @return
	 * @throws IOException
	 */
	String getHttpResponse () throws IOException
	{
		BufferedReader in = null;
		String response="";
		int responseCode = connection.getResponseCode();
		
		try
		{
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			response = "";
			while ((inputLine = in.readLine()) != null) 
			{
				response = response + inputLine;
			}	
			System.out.println();
			System.out.println(responseCode);
		}
		catch (IOException e)
		{
			throw (e);
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
			connection.disconnect();
		}
		
		return response;
	}
	
}
