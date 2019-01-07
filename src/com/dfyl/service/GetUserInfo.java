package com.dfyl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import weaver.conn.RecordSet;



import weaver.general.BaseBean;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.soa.hrm.User;

import com.dfyl.base.GetToken;
import com.dfyl.base.SynHrm;
import com.util.SendHttpUtil;

public class GetUserInfo extends BaseCronJob{
	
	
	public void execute()  {
		getUser();
	}
	
	public String getUser(){//同步人员
		RecordSet rs = new RecordSet();
		BaseBean base=new BaseBean();
		String url="http://hrapi.dfyl.com.cn/APIDFYL/emp";
		String sql="select token from uf_token where id=1";
		rs.execute(sql);
		String token="RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";//"RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		if(rs.next()){//获取token
			token=rs.getString("token");
		}
		//String token="RFlOWjIwMTgtMTEtMjIgMDk6NTI6MDg=";
		String Authorization="Bearer"+" "+token;
		SendHttpUtil shu=new SendHttpUtil();
		List<User> userlist=new ArrayList<User>();
		//User user=new User();
		Map<String, String> tokmap=new HashMap<String, String>();
		tokmap.put("username", "18112201");
		String result=shu.sendPostHeader(url, tokmap, "Authorization", Authorization);//发送post请求url,Map,Header,HeaderValue
		JSONObject json = JSONObject.fromObject(result);
		
		String code = json.getString("code");
		//base.writeLog("code"+code);
		if("200".equals(code)){
			//String dept = json.getString("result"); 
			
			JSONArray deptjsonarr = (JSONArray) json.get("result");
			//base.writeLog("deptjsonarr"+deptjsonarr);
			for(int i=0;i<deptjsonarr.size();i++){
				
				User user=new User();
				user.setLastname(deptjsonarr.getJSONObject(i).get("username").toString());//姓名
				user.setEmail(deptjsonarr.getJSONObject(i).get("email").toString());//邮箱
				user.setDepartmentid(getDeptid(deptjsonarr.getJSONObject(i).get("oiddeparment").toString()));//部门
				user.setWorkcode(deptjsonarr.getJSONObject(i).get("employeeno").toString());////工号
				user.setStatus(getStatus(deptjsonarr.getJSONObject(i).get("statustranslate").toString()));//状态
				user.setManagerid(getManager(deptjsonarr.getJSONObject(i).get("pouserid").toString()));//上级
				user.setSex(getSex(deptjsonarr.getJSONObject(i).get("sex").toString()));//性别
				user.setTextfield1(deptjsonarr.getJSONObject(i).get("userid").toString());//设置主键
				user.setTelephone(deptjsonarr.getJSONObject(i).get("tel").toString());//座机
				user.setMobile(deptjsonarr.getJSONObject(i).get("telephone").toString());//电话
				user.setCertificatenum(deptjsonarr.getJSONObject(i).get("idnumber").toString());//身份证
				user.setJoblevel(getJoblevel(deptjsonarr.getJSONObject(i).get("oidjoblevel").toString()));//职级
				user.setJobtitle(getJobtitle(deptjsonarr.getJSONObject(i).get("jobpositionid").toString()));//岗位 
				user.setSeclevel(getSeclevel(deptjsonarr.getJSONObject(i).get("oidjoblevel").toString()));//安全级别
			    user.setPassword("670B14728AD9902AECBA32E22FA4F6BD");//密码，初始值000000
				userlist.add(user);
				
			}
			//base.writeLog("userlist"+userlist.size());
			SynHrm syh=new SynHrm();
			syh.synHrm(userlist);
			
		}else if("500".equals(code)){//500token过期
			GetToken gettoken=new GetToken();
			token=gettoken.getToken();
			rs.execute("update uf_token set token='"+token+"' where id=1");//更新token中间表
			getUser();//递归调用方法
		}
		
		return "";
	}
	
	private int getDeptid(String deptcode){
		RecordSet rs = new RecordSet();
		int deptid=0;
		String sql_t="select  id from hrmdepartment where departmentcode='"+deptcode+"'";//查部门ID
		rs.execute(sql_t);
		if(rs.next()){
			deptid=rs.getInt("id");
		}
		return deptid;
	}
	private int getJoblevel(String levelcode){//转化职级
		RecordSet rs = new RecordSet();
		int level=0;
		String sql_t="select dj from uf_joblevel where oid='"+levelcode+"'";//
		rs.execute(sql_t);
		if(rs.next()){
			level=rs.getInt("dj");
		}
		return level;
	}
	private int getSeclevel(String levelcode){//转化安全级别
		RecordSet rs = new RecordSet();
		int level=0;
		String sql_t="select anjb from uf_joblevel where oid='"+levelcode+"'";//
		rs.execute(sql_t);
		if(rs.next()){
			level=rs.getInt("anjb");
		}
		return level;
	}
	
	private int getJobtitle(String posi){//转化岗位
		RecordSet rs = new RecordSet();
		int jobtitle=0;
		String sql_t="select id from hrmjobtitles  where outkey='"+posi+"'";//
		rs.execute(sql_t);
		if(rs.next()){
			jobtitle=rs.getInt("id");
		}
		return jobtitle;
	}
	
	
	private int getManager(String managerid){//上级领导
		RecordSet rs = new RecordSet();
		int hrid=-1;
		String sql_t="select  id from hrmresource where  status=1 and outkey='"+managerid+"'";//
		rs.execute(sql_t);
		if(rs.next()){
			hrid=rs.getInt("id");
		}
		return hrid;
	}
	private String getSex(String sex){//
		//RecordSet rs = new RecordSet();
		String sexval="男";
		if("0".equals(sex)){
			sexval="男";
		}else{
			sexval="女";
		}
		
		return sexval;
	}
	
	private int getStatus(String status){
		//RecordSet rs = new RecordSet();
		int statusval=0;
		if("试用".equals(status)){
			statusval=0;
		}else if("正式".equals(status)){
			statusval=1;
		}else if("临时".equals(status)){
			statusval=2;
		}else if("试用延期".equals(status)){
			statusval=3;
		}else if("解聘".equals(status)){
			statusval=4;
		}else if("离职".equals(status)){
			statusval=5;
		}else if("退休".equals(status)){
			statusval=6;
		}else{
			statusval=7;
		}
		return statusval;
	}
}
