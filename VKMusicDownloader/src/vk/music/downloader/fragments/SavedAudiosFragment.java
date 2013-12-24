package vk.music.downloader.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vk.music.downloader.R;
import vk.music.downloader.data.MainListSavedAudiosAdapter;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SavedAudiosFragment extends Fragment
{
	
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		
		this.context = this.getActivity();	
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		super.onViewCreated(view, savedInstanceState);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.savedMusic_title);	
		ListView listView = (ListView) getView().findViewById(R.id.mainListVIew);
		List<String> audios = downloadedFiles();
		if (audios == null) getActivity().finish();
		listView.setAdapter(new MainListSavedAudiosAdapter(audios, context));
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }
	
	private List<String> downloadedFiles()
	{
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (!isSDPresent) return null;
		List<String> downloadedFiles = new ArrayList<String>();
		File appFolder = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AllAudoisFragment.appFolderPath+"/");
		appFolder.mkdirs();
		String [] names = appFolder.list();
		for (String name:names) if (name.contains(".mp3"))downloadedFiles.add(name);//.substring(0, name.length()-4));	
		return downloadedFiles;
	}
}
