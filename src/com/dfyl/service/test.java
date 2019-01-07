package com.dfyl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.soa.hrm.User;

import com.dfyl.base.GetToken;
import com.dfyl.base.SynHrm;
import com.util.SendHttpUtil;

public class test {

	
	public static void main(String[] args) {
		////RecordSet rs = new RecordSet();
		BaseBean base=new BaseBean();
		String url="http://hrapi.dfyl.com.cn/APIDFYL/emp";
		String sql="select token from uf_token where id=1";
		//rs.execute(sql);
		String token="RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";//"RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		//if(rs.next()){//获取token
		//	token=rs.getString("token");
		//}
		//String token="RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		String Authorization="Bearer"+" "+token;
		SendHttpUtil shu=new SendHttpUtil();
		//List<User> userlist=new ArrayList<User>();
		//User user=new User();
		Map<String, String> tokmap=new HashMap<String, String>();
		tokmap.put("username", "18112201");
		String result=shu.sendPostHeader(url, tokmap, "Authorization", Authorization);//发送post请求url,Map,Header,HeaderValue
		JSONObject json = JSONObject.fromObject(result);
		
		String code = json.getString("code");
		//base.writeLog("code"+code);
		if("200".equals(code)){
			//String dept = json.getString("result"); 
			System.out.println("code"+code);
			JSONArray deptjsonarr = (JSONArray) json.get("result");
			System.out.println("deptjsonarr"+deptjsonarr.size());
			//for(int i=0;i<deptjsonarr.size();i++){
			
				
			//}
			//base.writeLog("userlist"+userlist.size());
			//SynHrm syh=new SynHrm();
			//syh.synHrm(userlist);
			
		}else if("500".equals(code)){//500token过期
			GetToken gettoken=new GetToken();
			//token=gettoken.getToken();
			//rs.execute("update uf_token set token='"+token+"' where id=1");//更新token中间表
			//getUser();//递归调用方法
		}
		
		//return "";
	}
}
