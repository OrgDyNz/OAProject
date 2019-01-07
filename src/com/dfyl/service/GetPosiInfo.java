package com.dfyl.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import weaver.conn.RecordSet;

import weaver.hrm.job.JobTitlesComInfo;
import weaver.interfaces.schedule.BaseCronJob;

import com.dfyl.base.GetToken;
import com.dfyl.base.SynPosi;
import com.util.SendHttpUtil;

public class GetPosiInfo extends BaseCronJob{
	
	public void execute()  {
		getPositions();
	}
	
	
	public String getPositions(){
		RecordSet rs = new RecordSet();
		RecordSet rs3 = new RecordSet();
		String url="http://hrapi.dfyl.com.cn/APIDFYL/pos";
		String sql="select token from uf_token where id=1";
		rs.execute(sql);
		String token="";//"RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		if(rs.next()){//获取token
			token=rs.getString("token");
		}
		//String token="RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		String Authorization="Bearer"+" "+token;
		SendHttpUtil shu=new SendHttpUtil();
		
		Map<String, String> tokmap=new HashMap<String, String>();
		tokmap.put("username", "18112201");
		String result=shu.sendPostHeader(url, tokmap, "Authorization", Authorization);//发送post请求url,Map,Header,HeaderValue
		JSONObject json = JSONObject.fromObject(result);
		JobTitlesComInfo jtc=new JobTitlesComInfo();
		String code = json.getString("code");
		if("200".equals(code)){
			//String dept = json.getString("result"); 
			SynPosi sd=new SynPosi();
			JSONArray deptjsonarr = (JSONArray) json.get("result");
			rs3.execute("set identity_insert HrmJobTitles ON");
			for(int i=0;i<deptjsonarr.size();i++){
				String name=deptjsonarr.getJSONObject(i).get("name").toString();//岗位名称
				String posicode=deptjsonarr.getJSONObject(i).get("code").toString();//岗位编号
				String outkey=deptjsonarr.getJSONObject(i).get("oid").toString();//岗位主键
			
				sd.synPosi(name,posicode,outkey); 
			}
			
			rs3.execute("set identity_insert HrmJobTitles OFF");
			jtc.removeJobTitlesCache();//清除缓存
		}else if("500".equals(code)){
			GetToken gettoken=new GetToken();
			token=gettoken.getToken();
			rs.execute("update uf_token set token='"+token+"' where id=1");
			getPositions();
		}
		
		return "";
	}

}
