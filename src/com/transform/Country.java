package com.transform;

import com.session.Session;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.sling.commons.json.*;



public class Country 
{
	public List<LinkedHashMap<String, String>> titlemanipulate(String URL,String session_ID, List<LinkedHashMap<String, String>> userlist, Map<String, String> propData) throws Exception
	{
		List<LinkedHashMap<String, String>> countryList = new ArrayList<LinkedHashMap<String,String>>();
		List<LinkedHashMap<String, String>> updatedlist = new ArrayList<LinkedHashMap<String,String>>();
		try
		{
		if(session_ID != null)
		{
		List<JSONObject> countryListAll =	getcountry(URL,session_ID);
		String[] label = {"Country","Region"};
		String[] keyName = {"name__v","regions__c"};
		for(JSONObject ob : countryListAll)
		{
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			for(int i=0;i<label.length;i++)
			{
				String val = (ob.has(keyName[i])?ob.get(keyName[i]):" ").toString();
				if(label[i].equalsIgnoreCase("Region")) 
				{
					val=val.toString().replace("[", "").replace("]", "");
					if (val.contains("emea__c"))
						val = val.replace("emea__c", "EMEA");
					val=val.substring(1, val.length()-1);
				}
				map.put(label[i],val);
			}
			countryList.add(map);
		}		
		for(LinkedHashMap<String, String> obj : userlist)
		{
			int count = 0;
			String result = "";
			for(LinkedHashMap<String, String> obj1 : countryList )
			{
				String country = obj1.get("Country").toLowerCase();
				if(obj.get("Title").toLowerCase().contains(country))
				{
					count ++;
					result = result + obj1.get("Country") + ";";
				}
			}
			if(obj.get("Title").toLowerCase().contains("romania"))
			{
				count ++;
				result = result + "Romania" + ";";
			}
			if(obj.get("Title").toLowerCase().contains("croatia"))
			{
				count ++;
				result = result + "Croatia" + ";";
			}
			if(obj.get("Title").toLowerCase().contains("estonia"))
			{
				count ++;
				result = result + "Estonia" + ";";
			}
			if(obj.get("Title").toLowerCase().contains("latvia"))
			{
				count ++;
				result = result + "Latvia" + ";";
			}
			if(obj.get("Title").toLowerCase().contains("lithuania"))
			{
				count ++;
				result = result + "Lithuania" + ";";
			}
			if(count > 0)
			{
				obj.put("Country", result);
				updatedlist.add(obj);
			}
		}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		VaultsName vault = new VaultsName();
		List<LinkedHashMap<String,String>> vaultAddition = vault.getvaults(updatedlist, propData);
		return vaultAddition;		
	}
	public static List<JSONObject> getcountry(String URL,String session_id) throws MalformedURLException, IOException, JSONException
	{
		List<JSONObject> countryFinal = new ArrayList<JSONObject>();		
		while (true) 
		{
			int offset = 0;
			int limit = 1000;
			String base_url = URL+"/query?q="+URLEncoder.encode("Your SQL query for country LIMIT "+limit+" OFFSET "+offset,"UTF-8");
			JSONObject country = new JSONObject();
			country.put("data", new JSONArray());
			JSONObject obj = Session.getRequest(base_url, session_id);
			if (obj.getString("responseStatus").equalsIgnoreCase("success")) 
			{
				if(obj.has("data")){
					JSONArray array = obj.getJSONArray("data");
					for(int i=0;i<array.length();i++)
					{
						country = array.getJSONObject(i);
						countryFinal.add(country);
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
		return countryFinal;
	}
}

