package vk.music.downloader.api;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;

	public class VKPostAsynkTask extends AsyncTask<String, Void, String> 
	{
	    protected String doInBackground(String... urls)
	    {
	        HttpClient client = new DefaultHttpClient();
	        HttpGet request = new HttpGet(urls[0]);	 
	        String responseText = null;
	        try 
	        {
	            HttpResponse response = client.execute(request);
	            HttpEntity entity = response.getEntity();

	            responseText = EntityUtils.toString(entity);
	            	            	            
	        }
	        catch(ClientProtocolException cexc)
	        {
	        	cexc.printStackTrace();
	        }
	        catch(IOException ioex){
	        	ioex.printStackTrace();
	        }	
	        return responseText;
	    }	    
	}

