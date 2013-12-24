package vk.music.downloader.ui;

public interface LogDoneListener 
{
	public void onResultOk(String url);
	public void onResultError(String description);
}
