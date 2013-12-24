package vk.music.downloader.logic;


public interface FileDownloadListener 
{
	public void downloadFile(String url,String fileName,FileDownloadedListener fileDownloadedListener);
}
