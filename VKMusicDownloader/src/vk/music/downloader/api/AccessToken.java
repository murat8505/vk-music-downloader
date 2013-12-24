package vk.music.downloader.api;

import android.content.Context;
import android.content.SharedPreferences;

public class AccessToken 
{
	private SharedPreferences sharedPreferences;
    private final String PREFS_NAME = "vk_Access_Token";
    private SharedPreferences.Editor editor;
       
    public AccessToken(Context context) 
    {
    	this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        this.editor = this.sharedPreferences.edit();
    }
    
    public void saveAccessToken(String url)
    {
    	String[] query = url.split("#");
		String[] params = query[1].split("&");		
    	editor.putString("VkAccessToken", params[0].split("=")[1]);
    	editor.putString("VkExpiresIn", params[1].split("=")[1]);
    	editor.putString("VkUserId", params[2].split("=")[1]);
    	editor.putLong("VkAccessTime", System.currentTimeMillis());
    	editor.commit();
    }
    
    public String[] getAccessToken()
    {
    	String[] token = new String[4];
    	token[0] = this.sharedPreferences.getString("VkAccessToken", "");
    	token[1] = this.sharedPreferences.getString("VkExpiresIn", "");
    	token[2] = this.sharedPreferences.getString("VkUserId", "");
    	token[3] =  String.valueOf(this.sharedPreferences.getLong("VkAccessTime",0));
    	return token;
    }
    
    public void resetAccessToken()
    {
    	this.editor.putString("VkAccessToken", "");
    	this.editor.putString("VkExpiresIn", "");
    	this.editor.putString("VkUserId", "");
    	this.editor.putLong("VkAccessTime", 0);
    	this.editor.commit();
    } 
    
    public boolean isAccessToken()
    {
    	if (this.sharedPreferences.getString("VkAccessToken", "").equals(""))
    		return false;
    	else
    	{
    		long startTime = this.sharedPreferences.getLong("VkAccessTime",0);
    		long time = Long.parseLong(this.sharedPreferences.getString("VkExpiresIn", ""));
    		if (startTime + time > System.currentTimeMillis() )
    			return false;
    		else return true;
		}
    }
}
