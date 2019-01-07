package com.dfyl.service;

import java.util.HashMap;
import java.util.Map;

import weaver.hrm.company.DepartmentComInfo;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.conn.RecordSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.dfyl.base.GetToken;
import com.dfyl.base.SynDept;
import com.util.SendHttpUtil;

public class GetDeptInfo extends BaseCronJob{
	
	public void execute()  {
		getDept();
	}
	
	public String getDept(){
		RecordSet rs = new RecordSet();
		String url="http://hrapi.dfyl.com.cn/APIDFYL/org";
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
		DepartmentComInfo dci=new DepartmentComInfo();
		String code = json.getString("code");
		if("200".equals(code)){
			//String dept = json.getString("result"); 
			SynDept sd=new SynDept();
			JSONArray deptjsonarr = (JSONArray) json.get("result");
			for(int i=0;i<deptjsonarr.size();i++){
				String name=deptjsonarr.getJSONObject(i).get("name").toString();//部门名称
				String oid=deptjsonarr.getJSONObject(i).get("oid").toString();//部门编号
				String poid=deptjsonarr.getJSONObject(i).get("poid").toString();//上级部门
				String ehrstatus=deptjsonarr.getJSONObject(i).get("status").toString();//状态
				String leveloid=deptjsonarr.getJSONObject(i).get("leveloid").toString();//状态
				sd.synDept(oid, name, ehrstatus, poid, leveloid);
			}
			
			dci.removeCompanyCache();//清除缓存
		}else if("500".equals(code)){
			GetToken gettoken=new GetToken();
			token=gettoken.getToken();
			rs.execute("update uf_token set token='"+token+"' where id=1");
			getDept();
		}
		
		return "";
	}
	
/*public static void main(String[] args) {
		String url="http://hrapi.dfyl.com.cn/APIDFYL/org";
		String token="RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		String Authorization="Bearer"+" "+token;
		SendHttpUtil shu=new SendHttpUtil();
		
		Map<String, String> tokmap=new HashMap<String, String>();
		tokmap.put("username", "18112201");
		String result=shu.sendPostHeader(url, tokmap, "Authorization", Authorization);
		
		
		//String resultstr="{\"code\":200,\"data\":143,\"massege\":\"\",\"result\":[{\"code\":\"1DL000\",\"createTime\":\"1522058049000\",\"leveloid\":\"526049\",\"modifyTime\":\"1523257378000\",\"name\":\"东风裕隆\",\"oid\":\"526049\",\"poid\":\"526046\",\"status\":\"1\"}]}";
		JSONObject json = JSONObject.fromObject(result);
		String dept = json.getString("result"); 
		JSONArray deptjsonarr = (JSONArray) json.get("result");
		//System.out.println("===="+deptjsonarr.indexOf());
		for(int i=0;i<1;i++){
			System.out.println("===="+deptjsonarr.getJSONObject(0).get("name").toString());
		}
		
	}*/
	
	
	

}
