package vk.music.downloader.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import vk.music.downloader.R;
import vk.music.downloader.R.drawable;
import vk.music.downloader.R.id;
import vk.music.downloader.R.layout;
import vk.music.downloader.R.string;
import vk.music.downloader.api.APIExeption;
import vk.music.downloader.api.AccessToken;
import vk.music.downloader.api.VKAudioAPI;
import vk.music.downloader.data.AudioFile;
import vk.music.downloader.data.MainListAudiosAdapter;
import vk.music.downloader.fragments.AllAudoisFragment;
import vk.music.downloader.logic.DownloadFileTask;
import vk.music.downloader.logic.FileDownloadListener;
import vk.music.downloader.logic.FileDownloadedListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends Activity 
{
	
	private Context context;
	private List<String> downloadedFiles;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		this.context = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.search_results);
		actionBar.setIcon(R.drawable.vkmusicwhite);
		
		if (!initializeDownloadedFiles())
		{
			Toast.makeText(context, "No SD card avaiable", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			Initializer initializer = new Initializer(errorsHandler);
			initializer.execute(query);
		}
	}
	
	private Handler errorsHandler = new Handler()
	{
		public void dispatchMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
				case AllAudoisFragment.API_EXCEPTION:
				{
					Toast.makeText(context, "API error, please notify developer", Toast.LENGTH_SHORT).show();	
					break;
				}
				case AllAudoisFragment.INTERNET_EXCEPTION:
				{
					Toast.makeText(context, "There is no internet connection", Toast.LENGTH_SHORT).show();			
					break;
				}
				case AllAudoisFragment.PROTOCOL_EXCEPTION:
				{
					Toast.makeText(context, "Protocol error, please notify developer", Toast.LENGTH_SHORT).show();					
					break;
				}
				case AllAudoisFragment.SD_CARD_EXCEPTION:
				{
					Toast.makeText(context, "Please insert SD card", Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
			}
			
		}
	};
	
	private void setListVisability(boolean bool)
	{
		ListView view = (ListView) findViewById(R.id.mainListVIewSearch);
		if (bool) view.setVisibility(View.VISIBLE);
		else view.setVisibility(View.INVISIBLE);
	}
	
	private class Initializer extends AsyncTask<String, Void, List<AudioFile>>
	{
		
		private Handler handler;
		
		public Initializer(Handler handler)
		{
			this.handler = handler;
		}
		
		@Override
		protected void onPreExecute() 
		{
			setListVisability(false);
		}
		
		@Override
		protected List<AudioFile> doInBackground(String... params)
		{
			if (!initializeDownloadedFiles())
			{
				handler.sendEmptyMessage(AllAudoisFragment.SD_CARD_EXCEPTION);
			}
			try 
			{
				
				AccessToken accessToken = new AccessToken(context);
				String [] info = accessToken.getAccessToken();				
				List<AudioFile> audioFiles = new VKAudioAPI(info[2],info[0]).searchAudios(params[0]);
				return audioFiles; 
			} catch (APIExeption e) 
			{
				e.printStackTrace();
				handler.sendEmptyMessage(AllAudoisFragment.API_EXCEPTION);
				return null;
			} catch (ClientProtocolException e) 
			{
				e.printStackTrace();
				handler.sendEmptyMessage(AllAudoisFragment.PROTOCOL_EXCEPTION);
				return null;
			} catch (IOException e) 
			{
				e.printStackTrace();
				handler.sendEmptyMessage(AllAudoisFragment.INTERNET_EXCEPTION);
				return null;
			}					
		}		

		@Override
		protected void onPostExecute(List<AudioFile> result) 
		{
			if (result == null)
			{
				setListVisability(true);
				return;
			}
			ListView view = (ListView) findViewById(R.id.mainListVIewSearch);
			view.setAdapter(new MainListAudiosAdapter(result, context,fileDownloadListener,downloadedFiles));
			setListVisability(true);	
		}
	}
	
	
	   @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	
	private FileDownloadListener fileDownloadListener= new FileDownloadListener()
	{
		@Override
		public void downloadFile(String url,String fileName,FileDownloadedListener fileDownloadedListener) 
		{
			ProgressDialog mProgressDialog;

			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setTitle("Downloading...");
			mProgressDialog.setMessage(fileName);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);

			final DownloadFileTask downloadTask = new DownloadFileTask(context,mProgressDialog,fileDownloadedListener);
			downloadTask.execute(url,fileName);

			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() 
			{
			    @Override
			    public void onCancel(DialogInterface dialog) 
			    {
			        downloadTask.cancel(true);
			    }
			});
		}
		
	};
	
	private boolean initializeDownloadedFiles()
	{
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (!isSDPresent) return false;
		this.downloadedFiles = new ArrayList<String>();
		File appFolder = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/");
		appFolder.mkdirs();
		String [] names = appFolder.list();
		for (String name:names) downloadedFiles.add(name.substring(0, name.length()-4));	
		return true;
	}

}
