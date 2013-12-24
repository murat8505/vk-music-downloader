package vk.music.downloader.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vk.music.downloader.activities.ActivityMain;
import vk.music.downloader.fragments.AllAudoisFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.widget.Toast;

public class DownloadFileTask extends AsyncTask<String, Integer, String> 
{
	private ProgressDialog progressDialog;
	private Context context;
	private FileDownloadedListener fileDownloadListener;
	private String fileName = null;

	public DownloadFileTask(Context context, ProgressDialog progressDialog, FileDownloadedListener fileDownloadedListener) 
	{
		this.context = context;
		this.progressDialog = progressDialog;
		this.fileDownloadListener = fileDownloadedListener;
	}
	
	@Override
	protected void onCancelled() 
	{
		File file = new File(fileName);
		file.delete();
	}

	@Override
	protected String doInBackground(String... sUrl) 
	{                
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try 
		{
			URL url = new URL(sUrl[0]);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

			int fileLength = connection.getContentLength();

			input = connection.getInputStream();			
			this.fileName = Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/"+correctFileName(sUrl[1])+".mp3";
			File appFolder = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/");
			appFolder.mkdirs();
			output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/"+correctFileName(sUrl[1])+".mp3");

			byte data[] = new byte[4096];
			int total = 0;
			int count;
			while ((count = input.read(data)) != -1) 
			{
				if (isCancelled()) return null;
				total += count;
				if (fileLength > 0) publishProgress(total,fileLength);
				output.write(data, 0, count);
			}
		} catch (Exception e) 
		{
			return null;
		} finally 
		{
			try 
			{
				if (output != null)	output.close();
				if (input != null) input.close();
			} 
			catch (IOException ignored) {}
			
			if (connection != null) connection.disconnect();
		}

		return correctFileName(sUrl[1]);
	}
	
	private String correctFileName(String old)
	{
		if (old.length() >= 250)
		{
			old = old.substring(0, 249);
		}
		old = old.replace("/", "");
		return old;
	}
	
	@Override
    protected void onPreExecute() 
	{
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) 
    {
        super.onProgressUpdate(progress);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(progress[1]/1024);
        progressDialog.setProgress(progress[0]/1024);
    }

    @Override
    protected void onPostExecute(String result) 
    {
    	progressDialog.dismiss();
        if (result != null)
        {
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            fileDownloadListener.FileWasDownloaded();
        }
    }

}
