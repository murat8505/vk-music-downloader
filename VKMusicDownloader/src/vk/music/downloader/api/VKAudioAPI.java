package vk.music.downloader.api;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import vk.music.downloader.data.AudioFile;

import android.text.Html;
import android.util.Log;

public class VKAudioAPI 
{

	public static final String VK_API_URL = "https://api.vkontakte.ru/method/";
	public static final String VK_API_GET_AUDIO = VK_API_URL + "audio.get.xml?";
	public static final String VK_API_SEARCH_AUDIO = VK_API_URL + "audio.search.xml?";
	
	
	private String accessToken;
	private String userId;
	
	public VKAudioAPI(String userId,String accessToken)
	{
		this.accessToken = accessToken;
		this.userId = userId;
	}
	
	public List<AudioFile> getAudios() throws APIExeption, ClientProtocolException, IOException, ReloginExeption
	{
		String requst = VKAudioAPI.VK_API_GET_AUDIO + "owner_id="+userId +"&need_user="+"0"+"&access_token="+accessToken;
		
		String result = null;

		HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(requst);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();

        result = EntityUtils.toString(entity);   
		
		if (result == null) throw new APIExeption("Error in AsyncTask: response == null");		
				
		List<AudioFile> audioFiles = new ArrayList<AudioFile>();
		
		
		//Log.v("SUN", result);
		try
		{
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);			
			XmlPullParser xpp;
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader (result));

			int eventType = xpp.getEventType();
			AudioFile audioFile = null;
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if(eventType == XmlPullParser.START_TAG) 
				{
					String tagName = xpp.getName();
					if (tagName.equals("audio")) audioFile = new AudioFile();
					if (tagName.equals("title")) audioFile.setName(xpp.nextText());
					if (tagName.equals("artist")) audioFile.setAuthor(xpp.nextText());
					if (tagName.equals("duration")) audioFile.setLength(xpp.nextText());
					if (tagName.equals("url")) audioFile.setUrl(xpp.nextText());
					if (tagName.equals("error_code")) throw new APIExeption(xpp.nextText());
				}
				if(eventType == XmlPullParser.END_TAG) 
				{
					String tagName = xpp.getName();
					if (tagName.equals("audio")) audioFiles.add(audioFile);
				}
				eventType = xpp.next();
			}		
		}
		catch (APIExeption e)
		{
			if (e.getMessage().equals("5")) throw new ReloginExeption("Different IP Adress, need relogin");
			throw e;
		}
		catch (Exception e1) 
		{
			e1.printStackTrace();
			throw new APIExeption("Data Changed");
		}
	
		return audioFiles;
	}
	
	public List<AudioFile> searchAudios(String search) throws APIExeption, ParseException, IOException
	{
		search = search.replace(" ", "+");
		String requst = VKAudioAPI.VK_API_SEARCH_AUDIO + "q="+search +"&auto_complete="+"1"+"&lyrics"+"=0"+"&sort"+"=2"+"&count="+"100"+"&access_token="+accessToken;
		
		String result = null;
		
		HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(requst);

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();

        result = EntityUtils.toString(entity);            	            	            
		
		if (result == null) throw new APIExeption("Error in AsyncTask: response == null");
		
		List<AudioFile> audioFiles = new ArrayList<AudioFile>();
		
		try
		{
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);			
			XmlPullParser xpp;
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader (result));

			int eventType = xpp.getEventType();
			AudioFile audioFile = null;
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				if(eventType == XmlPullParser.START_TAG) 
				{
					String tagName = xpp.getName();
					if (tagName.equals("audio")) audioFile = new AudioFile();
					if (tagName.equals("title")) audioFile.setName(xpp.nextText());
					if (tagName.equals("artist")) audioFile.setAuthor(xpp.nextText());
					if (tagName.equals("duration")) audioFile.setLength(xpp.nextText());
					if (tagName.equals("url")) audioFile.setUrl(xpp.nextText());
					if (tagName.equals("error")) throw new APIExeption(xpp.nextText());
				}
				if(eventType == XmlPullParser.END_TAG) 
				{
					String tagName = xpp.getName();
					if (tagName.equals("audio")) audioFiles.add(audioFile);
				}
				eventType = xpp.next();
			}		
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new APIExeption("Data Changed");
		}
	
		return audioFiles;
	}
}
