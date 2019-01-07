package com.dfyl.base;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.util.cc;

public class GetToken {
	/*
	 * 获取token
	 */
	public String getToken(){
		//String username="18112201";
		//String userroot="20181122084028";
		cc c=new cc();
		String url="http://hrapi.dfyl.com.cn/APIDFYL/resettoken";
		Map<String, String> tokmap=new HashMap<String, String>();
		tokmap.put("username", "18112201");
		tokmap.put("userroot", "20181122084028");
		String result=c.sendPost(url, tokmap).toString();
		JSONObject json = JSONObject.fromObject(result);
		String token = json.getString("result"); 
		
		return token;
	}
	
	/*public static void main(String[] args) {
		cc c=new cc();
		String url="http://hrapi.dfyl.com.cn/APIDFYL/resettoken";
		Map<String, String> tokmap=new HashMap<String, String>();
		tokmap.put("username", "18112201");
		tokmap.put("userroot", "20181122084028");
		String result=c.sendPost(url, tokmap).toString();
		JSONObject json = JSONObject.fromObject(result);
		String token = json.getString("result"); 
		System.out.println("token="+token);
	}*/
}
