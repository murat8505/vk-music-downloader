package vk.music.downloader.data;

import android.R.bool;

public class AudioFile 
{
	private String name;
	private String author;
	private String length;
	private String url;
	private boolean downloaded;
	
	public AudioFile()
	{}
	
	private AudioFile(String name,String author,String length,String url)
	{
		this.setName(name);
		this.setAuthor(author);
		this.setLength(length);
		this.setUrl(url);
	}
	
	public boolean isDownloaded()
	{
		return this.downloaded;
	}
	
	public void setDownloaded(boolean downloaded)
	{
		this.downloaded = downloaded;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		author = author.replace("amp;", "");
		this.name = name;
	}

	public String getAuthor() 
	{
		return author;		
	}

	public void setAuthor(String author) 
	{
		author = author.replace("amp;", "");
		this.author = author;
	}

	public String getLength() 
	{
		return length;
	}

	public void setLength(String length) 
	{
		this.length = length;
	}

	public String getUrl() 
	{
		return url;
	}

	public void setUrl(String url) 
	{
		this.url = url;
	}
	
}
