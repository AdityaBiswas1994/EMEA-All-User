package com.user;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.sling.commons.json.*;

import com.session.Session;

public class User {
	public List<JSONObject> getUsers(String URL,String session_id) throws MalformedURLException, IOException, JSONException
	{
		int offset = 0;
		int limit = 1000;
		//String actual_URL = base_url.concat(" LIMIT " + limit + " OFFSET " + start);
		List<JSONObject> users = new ArrayList<JSONObject>();		
		while (true) 
		{
			String base_url = URL+"/query?q="+URLEncoder.encode("select first_name__sys,last_login__sys,last_name__sys,region__c,status__v,business_location__cr.name__v,username__sys,business_group__c,wwid__c from user__sys LIMIT "+limit+" OFFSET "+offset,"UTF-8");
			JSONObject user = new JSONObject();
			user.put("data", new JSONArray());
			JSONObject obj = Session.getRequest(base_url, session_id);
			if (obj.getString("responseStatus").equalsIgnoreCase("success")) 
			{
				if(obj.has("data")){
					JSONArray array=obj.getJSONArray("data");
					for(int i=0;i<array.length();i++)
					{
						user = array.getJSONObject(i);
						users.add(user);
					}					
				}
				if (obj.has("responseDetails")) 
				{
					JSONObject respD = obj.getJSONObject("responseDetails");
					int size = respD.getInt("size");
					int total = respD.getInt("total");
					if (size + offset >= total)
						break;
					offset += size;
				} 
				else 
				{
					break;
				}
			}
			else 
			{
				break;
			}
		}
		return users;
	}
}
