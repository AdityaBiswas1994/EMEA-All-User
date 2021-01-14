package com.main;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.json.JSONObject;

import com.transform.Country;
import com.user.User;

public class GetUser 
{
	public List<LinkedHashMap<String, String>> getuserlist(String session, String URL, Map<String, String> propData) throws Exception
	{
		String session_id = null;
		User user = new User();
		Country countryAdd = new Country();
		JSONObject jb = new JSONObject(session);
		if(jb.getString("responseStatus").equalsIgnoreCase("SUCCESS"))
		{
			session_id = jb.getString("sessionId");
		}
		if(session_id != null)
		{
			List<JSONObject> userlist =	user.getUsers(URL,session_id);
			//All Users Retrieved
			String[] label = {"Name","User Name","Title","Status","Last Login Date","Vaults","Country","WWID","Base Location"};
			String[] keyName = {" ","username__sys","business_group__c","status__v","last_login__sys"," ","region__c","wwid__c","business_location__cr.name__v"};
			List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String,String>>();
			for(JSONObject ob : userlist)
			{
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				for(int i=0;i<label.length;i++)
				{
					String val = (ob.has(keyName[i])?ob.get(keyName[i]):" ").toString();
					if(label[i].equalsIgnoreCase("Status")) 
					{
						val=val.toString().replace("[", "").replace("]", "");
						
						if (val.contains("inactive__v"))
							val = val.replaceAll("inactive__v", "Inactive");							    
						else
							val = val.replaceAll("active__v", "Active");
						val=val.substring(1, val.length()-1);						
					}
					if(label[i].equalsIgnoreCase("Country")) 
					{
						val=val.toString().replace("[", "").replace("]", "");
						if (val.contains("emea__c"))
							val = val.replace("emea__c", "EMEA");
						val=val.substring(1, val.length()-1);
					}
					if(label[i].equalsIgnoreCase("Last Login Date"))
					{
						val = val.replaceAll("T", " : ");
						val = val.replaceAll(".000Z", " ");
					}
					
					val = val.replace(",",";");
					map.put(label[i],val);
				}
				String fname = (ob.has("first_name__sys")?ob.get("first_name__sys"):" ").toString();
				String lname = (ob.has("last_name__sys")?ob.get("last_name__sys"):" ").toString();
				String name = "\""+lname+","+fname+"\"";
				map.put(label[0],name);
				list.add(map);
			}
			List<LinkedHashMap<String,String>> user_modified = countryAdd.titlemanipulate(URL, session_id, list, propData);
			return user_modified;
		}
		return null;
	}
}
