package vk.music.downloader.data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import vk.music.downloader.R;
import vk.music.downloader.R.id;
import vk.music.downloader.R.layout;
import vk.music.downloader.activities.ActivityMain;
import vk.music.downloader.fragments.AllAudoisFragment;
import vk.music.downloader.logic.FileDownloadListener;
import vk.music.downloader.logic.FileDownloadedListener;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListAudiosAdapter extends BaseAdapter 
{
	
	private List<AudioFile> list;
	private LayoutInflater layoutInflater;
	private FileDownloadListener fileDownloadListener;
	private List<String> downloadedFiles;
	private Context context;

	public MainListAudiosAdapter(List<AudioFile> list,Context context,FileDownloadListener fileDownloadListener,List<String> downloadedFiles) 
	{
		this.list = list;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.fileDownloadListener = fileDownloadListener;
		this.downloadedFiles = downloadedFiles;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.music_list_layout, null);
		
		TextView name = (TextView) convertView.findViewById(R.id.txtName);
		TextView author = (TextView) convertView.findViewById(R.id.txtAuthor);
		TextView time = (TextView) convertView.findViewById(R.id.txtTime);
		final ImageView isDownloaded = (ImageView) convertView.findViewById(R.id.isDownloaded);
		
		name.setText(list.get(position).getName());
		author.setText(list.get(position).getAuthor());
		time.setText(timeConvert(list.get(position).getLength()));
		
		final String title = createTitle(list.get(position).getName(), list.get(position).getAuthor());
		if (downloadedFiles.contains(title)) 
		{
			isDownloaded.setVisibility(View.VISIBLE);
		}
		else
		{
			isDownloaded.setVisibility(View.INVISIBLE);
		}
			

		convertView.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				if (isDownloaded.getVisibility() != View.VISIBLE)//if (!downloadedFiles.contains(title)) 
				{

					fileDownloadListener.downloadFile(list.get(position).getUrl(),createTitle(list.get(position).getName(), list.get(position).getAuthor()),new FileDownloadedListener() 
					{					
						@Override
						public void FileWasDownloaded() 
						{
							isDownloaded.setVisibility(View.VISIBLE);							
						}
					});		

				} else
				{										
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/"+title+".mp3");
					intent.setDataAndType(Uri.fromFile(file), "audio/*");
					context.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}
	
	private String timeConvert(String sec)
	{
		String result = "";
		int seconds = Integer.parseInt(sec);
		int min = seconds / 60;
		result += Integer.toString(min) + ":" + Integer.toString(seconds - min*60);		
		return result;
	}
	
	private String createTitle(String name,String author)
	{
		return author + " - " + name;
	}

}
