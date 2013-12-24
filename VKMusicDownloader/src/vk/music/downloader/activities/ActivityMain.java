package vk.music.downloader.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import vk.music.downloader.R;
import vk.music.downloader.R.drawable;
import vk.music.downloader.R.id;
import vk.music.downloader.R.layout;
import vk.music.downloader.R.menu;
import vk.music.downloader.R.string;
import vk.music.downloader.api.APIExeption;
import vk.music.downloader.api.AccessToken;
import vk.music.downloader.api.ReloginExeption;
import vk.music.downloader.api.VKAudioAPI;
import vk.music.downloader.data.AudioFile;
import vk.music.downloader.data.MainListAudiosAdapter;
import vk.music.downloader.data.SideListAdapter;
import vk.music.downloader.fragments.AllAudoisFragment;
import vk.music.downloader.fragments.SavedAudiosFragment;
import vk.music.downloader.logic.DownloadFileTask;
import vk.music.downloader.logic.FileDownloadListener;
import vk.music.downloader.logic.FileDownloadedListener;
import vk.music.downloader.ui.LogDoneListener;
import vk.music.downloader.ui.LogInDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMain extends Activity 
{
	public static final String USER_TAG = "user";
	public static final String REFRESH_TAG = "refresh";
	public static final String ABOUT_TAG = "about";
	
	public static final String ALL_AUDIOS_TAG = "all_audios";
	public static final String SAVED_AUDIOS_TAG = "saved_audios";
	
		
	private Context context;	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout mDrawerLayout;
	private ListView drawerList;
	private String curentFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setTitle(R.string.main_title);
		actionBar.setIcon(R.drawable.vkmusicwhite);		
		
		this.context = this;
		
		this.drawerList = (ListView) findViewById(R.id.mainSideList);
		this.drawerList.setAdapter(new SideListAdapter(context, new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				if (v.getId() == R.id.allAudios)
				{
					mDrawerLayout.closeDrawers();
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					AllAudoisFragment fragment = new AllAudoisFragment();
					fragmentTransaction.add(R.id.fragmen_conteiner, fragment, ActivityMain.ALL_AUDIOS_TAG);
					curentFragment = ActivityMain.ALL_AUDIOS_TAG;
					fragmentTransaction.commit();
				}
				if (v.getId() == R.id.savedAudios)
				{
					mDrawerLayout.closeDrawers();
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					SavedAudiosFragment fragment = new SavedAudiosFragment();
					fragmentTransaction.add(R.id.fragmen_conteiner, fragment, ActivityMain.SAVED_AUDIOS_TAG);
					curentFragment = ActivityMain.SAVED_AUDIOS_TAG;
					fragmentTransaction.commit();
				}
			}
		}));
		this.drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				mDrawerLayout.closeDrawers();
				if (arg1.getTag() == USER_TAG)
				{
					if (curentFragment != null && curentFragment == ActivityMain.ALL_AUDIOS_TAG)
					{
						FragmentManager fragmentManager = getFragmentManager();
						AllAudoisFragment fragment = (AllAudoisFragment) fragmentManager.findFragmentByTag(ALL_AUDIOS_TAG);
						fragment.changeUser();
					}
				}
				if (arg1.getTag() == REFRESH_TAG)
				{
					if (curentFragment != null && curentFragment == ActivityMain.ALL_AUDIOS_TAG)
					{
						FragmentManager fragmentManager = getFragmentManager();
						AllAudoisFragment fragment = (AllAudoisFragment) fragmentManager.findFragmentByTag(ALL_AUDIOS_TAG);
						fragment.refreshList();
					}					
				}
				if (arg1.getTag() == ABOUT_TAG)
				{
					Toast.makeText(context, "Developed by Oleh Fitsyk", Toast.LENGTH_LONG).show();
				}		
			}
		});
		
		this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_opened, R.string.drawer_opened)
		{
			@Override
			public void onDrawerClosed(View drawerView) 
			{
				getActionBar().setTitle(R.string.main_title);
				invalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerOpened(View drawerView) 
			{
				getActionBar().setTitle(R.string.drawer_options);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(drawerToggle);			
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
					
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); 
	    searchView.setSubmitButtonEnabled(true);
	    
	    int searchTextViewId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = (TextView) searchView.findViewById(searchTextViewId);
        searchTextView.setHintTextColor(Color.WHITE);
	    
		return true;
	}	
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) 
	{       
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{		
		if (drawerToggle.onOptionsItemSelected(item)) 
		{
	          return true;
	    }
		
		return super.onOptionsItemSelected(item);
	}
	
}
