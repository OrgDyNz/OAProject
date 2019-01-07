package com.dfyl.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.StaticObj;


public class SynDept {
	
	public void synDept(String deptcode,String name,String ehrstatus,String poid,String leveloid){ 
		
		
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();
		//同步部门
		try {
			
			String status="0";
			if("1".equals(ehrstatus)){//转换部门状态
				status="0";
			}else{
				status="1";
			}
			
			
			String supdepid = getParentid(poid);
			String subcompanyid1=getCompany(leveloid);
			if("824310".equals(deptcode)){
				subcompanyid1="3";
				supdepid="";
			}
			if("526114".equals(deptcode)||"526049".equals(deptcode)){
				
				supdepid="";
			}
			
			String sql="select * from hrmdepartment  where departmentcode='"+deptcode+"'";
			rs1.executeQuery(sql);
			if(rs1.next()){//存在则更新
				String upsql="update hrmdepartment set subcompanyid1='"+subcompanyid1+"',departmentmark='"+name+"',departmentname='"+name+"',canceled='"+status+"',supdepid='"+supdepid+"' where departmentcode='"+deptcode+"'";
				rs2.execute(upsql);
				new BaseBean().writeLog("更新部门的sql:"+upsql);
				

			}else{//不存在则新增
				String sql_t= "insert into hrmdepartment(subcompanyid1,departmentmark,departmentname,departmentcode,showorder,canceled,supdepid) values('"+subcompanyid1+"','" + name + "','" + name + "','" + deptcode + "','','"+status+"','"+supdepid+"')";
		    	rs2.execute(sql_t);
		    	new BaseBean().writeLog("新增部门的sql:"+sql_t);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private String getParentid(String parentcode){//查上级部门
		RecordSet rs = new RecordSet();
		String parentid="";
		String sql_t="select  id from hrmdepartment where departmentcode='"+parentcode+"'";//查上级部门ID
		rs.execute(sql_t);
		if(rs.next()){
			parentid=rs.getString("id");
		}
		return parentid;
	}
	
	private String getCompany(String leveloid){//查所属分部
		RecordSet rs = new RecordSet();
		String companyid="";
		String sql_t="select id from hrmsubcompany  where subcompanycode='"+leveloid+"'";//查所属分部
		rs.execute(sql_t);
		if(rs.next()){
			companyid=rs.getString("id");
		}
		return companyid;
	}

}
