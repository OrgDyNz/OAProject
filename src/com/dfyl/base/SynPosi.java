package com.dfyl.base;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;

public class SynPosi {

	
	public void synPosi(String name,String code,String outkey){
		
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		RecordSet rs3 = new RecordSet();
		//同步部门
		try {
			
			String sql="select * from hrmjobtitles where outkey='"+outkey+"'";
			rs1.executeQuery(sql);
			if(rs1.next()){//存在则更新
				String upsql="update hrmjobtitles  set jobtitlename='"+name+"',jobtitlemark='"+name+"',jobtitlecode='"+code+"' where outkey='"+outkey+"'";
				rs2.execute(upsql);
				new BaseBean().writeLog("更新岗位的sql:"+upsql);
				

			}else{//不存在则新增
				rs3.executeProc("HrmResourceMaxId_Get", "");
				rs3.next(); 
				int maxid = rs3.getInt(1);  
				
				String sql_t= "insert into hrmjobtitles(id,jobtitlename,jobtitlemark,jobtitlecode,outkey) values('"+maxid+"','" + name + "','" + name + "','" + code + "','"+outkey+"')";
		    	rs2.execute(sql_t);
		    	new BaseBean().writeLog("新增岗位的sql:"+sql_t);
		    	
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
