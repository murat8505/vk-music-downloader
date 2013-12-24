package vk.music.downloader.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import vk.music.downloader.R;
import vk.music.downloader.activities.ActivityMain;
import vk.music.downloader.api.APIExeption;
import vk.music.downloader.api.AccessToken;
import vk.music.downloader.api.ReloginExeption;
import vk.music.downloader.api.VKAudioAPI;
import vk.music.downloader.data.AudioFile;
import vk.music.downloader.data.MainListAudiosAdapter;
import vk.music.downloader.logic.DownloadFileTask;
import vk.music.downloader.logic.FileDownloadListener;
import vk.music.downloader.logic.FileDownloadedListener;
import vk.music.downloader.ui.LogDoneListener;
import vk.music.downloader.ui.LogInDialog;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ListView;
import android.widget.Toast;

public class AllAudoisFragment extends Fragment 
{
	public static final String appFolderPath = "VKMusic";
	public static final int INTERNET_EXCEPTION = 0;
	public static final int PROTOCOL_EXCEPTION = 1;
	public static final int API_EXCEPTION = 2;
	public static final int SD_CARD_EXCEPTION = 3;
	public static final int RELOGIN_EXCEPTION = 4;
	
	private Context context;
	
	private List<String> downloadedFiles;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		
		this.context = this.getActivity();
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.main_title);		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		super.onViewCreated(view, savedInstanceState);
		if (! new AccessToken(context).isAccessToken())
		{
			new LogInDialog(context, logDoneListener,true).show();
		}
		else
		{			
			Initializer init = new Initializer(this.errorsHandler);
			init.execute();
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }
	
	private void setListVisability(boolean bool)
	{
		ListView view = (ListView) getView().findViewById(R.id.mainListVIew);
		if (bool) view.setVisibility(View.VISIBLE);
		else view.setVisibility(View.INVISIBLE);
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
					getActivity().finish();
					break;
				}
				case AllAudoisFragment.RELOGIN_EXCEPTION:
				{
					new LogInDialog(context, logDoneListener, false).show();
					break;
				}
			}
			
		}
	};
	
	

	private class Initializer extends AsyncTask<Void, Void, List<AudioFile>>
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
		protected List<AudioFile> doInBackground(Void... params)
		{
			if (!initializeDownloadedFiles())
			{
				handler.sendEmptyMessage(AllAudoisFragment.SD_CARD_EXCEPTION);
			}
			try 
			{
				AccessToken accessToken = new AccessToken(context);
				String [] info = accessToken.getAccessToken();
				List<AudioFile> audioFiles = new VKAudioAPI(info[2],info[0]).getAudios();
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
			} catch (ReloginExeption e) 
			{
				e.printStackTrace();
				handler.sendEmptyMessage(AllAudoisFragment.RELOGIN_EXCEPTION);
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
			ListView view = (ListView) getView().findViewById(R.id.mainListVIew);
			view.setAdapter(new MainListAudiosAdapter(result, context,fileDownloadListener,downloadedFiles));
			setListVisability(true);	
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
	
	private LogDoneListener logDoneListener = new LogDoneListener() 
	{		
		@Override
		public void onResultOk(String url) 
		{
			new AccessToken(context).saveAccessToken(url);
			Initializer init = new Initializer(errorsHandler);
			init.execute();
		}
		
		@Override
		public void onResultError(String description) 
		{
			Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
		}
	};
	
	public void changeUser()
	{
		CookieSyncManager.createInstance(context);         
		CookieManager cookieManager = CookieManager.getInstance();        
		cookieManager.removeAllCookie();
		new LogInDialog(context, logDoneListener, true).show();
	}
	
	public void refreshList()
	{
		Initializer init = new Initializer(errorsHandler);
		init.execute();
	}
	
	
}
