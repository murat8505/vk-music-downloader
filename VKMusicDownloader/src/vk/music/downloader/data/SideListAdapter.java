package vk.music.downloader.data;

import vk.music.downloader.R;
import vk.music.downloader.activities.ActivityMain;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SideListAdapter extends BaseAdapter 
{
	
	
	private Context context;
	private LayoutInflater layoutInflater;
	private OnClickListener radioListener;
	
	public SideListAdapter(Context context, OnClickListener radioListener) 
	{
		this.context = context;		
		this.layoutInflater = LayoutInflater.from(context);
		this.radioListener = radioListener;
	}	
	
	@Override
	public int getCount() {
		return 6;
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
		switch(position)
		{
		case 0:
		{
			if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.list_item_title, null);
			
			TextView textView = (TextView) convertView.findViewById(R.id.list_item_title);
			textView.setText("Main settings");
			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
			textView.setTypeface(font);
			
			return convertView;
		}
		case 1:
		{
			if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.list_item_simple_text, null);

			RadioButton allAudiosRb = (RadioButton) convertView.findViewById(R.id.allAudios);
			RadioButton savedAudiosRb = (RadioButton) convertView.findViewById(R.id.savedAudios);
			allAudiosRb.setOnClickListener(radioListener);
			savedAudiosRb.setOnClickListener(radioListener);
			
			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
			TextView allAudios = (TextView) convertView.findViewById(R.id.allAudios);
			allAudios.setTypeface(font);
			TextView savedAusios = (TextView) convertView.findViewById(R.id.savedAudios);
			savedAusios.setTypeface(font);
			
			return convertView;
		}		
		case 2:
		{
			if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.list_item_title, null);
			
			TextView textView = (TextView) convertView.findViewById(R.id.list_item_title);
			textView.setText("Advanced settings");
			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
			textView.setTypeface(font);
			
			return convertView;
		}
		case 3:
		{
			if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.sidelist_icons, null);
			ImageView refresh = (ImageView) convertView.findViewById(R.id.list_item_pic_icon);		
			refresh.setImageResource(R.drawable.refresh);
			TextView text = (TextView) convertView.findViewById(R.id.list_item_pic_text);
			text.setText("Refresh list");		
			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
			text.setTypeface(font);
			convertView.setTag(ActivityMain.REFRESH_TAG);
			return convertView;
		}
		case 4:
		{
			if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.sidelist_icons, null);
			ImageView change = (ImageView) convertView.findViewById(R.id.list_item_pic_icon);		
			change.setImageResource(R.drawable.user);
			TextView text = (TextView) convertView.findViewById(R.id.list_item_pic_text);
			text.setText("Change user");	
			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
			text.setTypeface(font);
			convertView.setTag(ActivityMain.USER_TAG);				
			return convertView;
		}
		case 5:
		{
			if (convertView == null)  convertView = this.layoutInflater.inflate(R.layout.sidelist_icons, null);
			ImageView about = (ImageView) convertView.findViewById(R.id.list_item_pic_icon);		
			about.setImageResource(R.drawable.about);
			TextView text = (TextView) convertView.findViewById(R.id.list_item_pic_text);
			text.setText("About");	
			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
			text.setTypeface(font);
			convertView.setTag(ActivityMain.ABOUT_TAG);
			return convertView;
		}
		}
		
				
		return convertView;
	}
	
	@Override
	public boolean isEnabled(int position) 
	{
		if (getItemViewType(position)==0) return false; else return true;
	}
	
	@Override
	public int getViewTypeCount() 
	{
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) 
	{
		switch(position)
		{
		case 0:return 0;
		case 1:return 1;
		case 2:return 0;
		case 3:return 2;
		case 4:return 2;
		case 5:return 2;
		}
		return -1;
	}
	
}
