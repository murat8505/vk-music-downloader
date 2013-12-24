package vk.music.downloader.api;

public class ReloginExeption extends Exception 
{
	private static final long serialVersionUID = 1L;

	public ReloginExeption(String message)
	{
		super(message);
	}
}
