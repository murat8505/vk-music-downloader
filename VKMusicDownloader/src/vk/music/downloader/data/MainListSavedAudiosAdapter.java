package vk.music.downloader.data;

import java.io.File;
import java.util.List;

import vk.music.downloader.R;
import vk.music.downloader.fragments.AllAudoisFragment;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainListSavedAudiosAdapter extends BaseAdapter
{
	
	private List<String> list;
	private Context context;
	private LayoutInflater layoutInflater;

	public MainListSavedAudiosAdapter(List<String> audios, Context context) 
	{
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);		
		this.list = audios;
	}
	
	@Override
	public int getCount() 
	{		
		return list.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.music_list_layout, null);
		
		TextView name = (TextView) convertView.findViewById(R.id.txtName);
		TextView author = (TextView) convertView.findViewById(R.id.txtAuthor);
		TextView time = (TextView) convertView.findViewById(R.id.txtTime);	
		
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/"+list.get(position));

		String albumAutor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		String songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		String songLength = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		if (songName == null) songName = list.get(position).split("-")[1];
		if (albumAutor == null) albumAutor = list.get(position).split("-")[0];
		Long sl = Long.parseLong(songLength);
		sl /= 1000;
		name.setText(songName);
		author.setText(albumAutor);
		time.setText(Long.toString(sl/60)+":"+Long.toString(sl%60));
		
		
		convertView.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/"+list.get(position));
				intent.setDataAndType(Uri.fromFile(file), "audio/*");
				context.startActivity(intent);				
			}
		});
		
		return convertView;
	}
	
}
