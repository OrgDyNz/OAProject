package com.dfyl.base;

import java.util.List;

import com.util.DateUtil;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.resource.ResourceComInfo;

public class SynHrm {

	public void synHrm(List<weaver.soa.hrm.User> userlist){
	
		BaseBean base=new BaseBean();
		/*SubCompanyComInfo scc=new SubCompanyComInfo();
		DepartmentComInfo dci=new DepartmentComInfo();
		
		scc.removeCompanyCache();
		dci.removeCompanyCache();*/
		
		try {
			ResourceComInfo rci = new ResourceComInfo();
			
			for(int i=0;i<userlist.size();i++){//遍历list
				weaver.soa.hrm.User user=userlist.get(i);
				getUpOrIn(user.getTextfield1(),user.getSeclevel(),user.getPassword(),user.getJobtitle(),user.getJoblevel(),
						user.getWorkcode(),user.getSex(),
						user.getLastname(),user.getManagerid(),
						user.getDepartmentid(),user.getStatus(),
						user.getEmail(),user.getTelephone(),user.getMobile(),user.getCertificatenum());
			}	
			rci.removeResourceCache();//清除缓存
		} catch (Exception e) {
			// TODO: handle exception
			base.writeLog("捕捉"+e);
		}
	}
	/*
	 * textfield1 主键参数
	 */
	
	private void getUpOrIn(String textfield1,int seclevel,String pwd,
			int jobtitle,int joblevel,
			String workcode,String sex,String name,
			int managerid,int dept,int status,
			String email,String telephone,String mobile,
			String certificatenum){//执行插入或更新
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		RecordSet rs3 = new RecordSet();
		BaseBean base=new BaseBean();
		//String companyid="";
		String sql_t="select id from hrmresource  where workcode='"+workcode+"'";//查询主键是否存在
		rs.execute(sql_t);
		if(rs.next()){//存在则更新
			
			String upsql="update hrmresource set outkey='"+textfield1+"',loginid='"+email+"',password='"+pwd+"',seclevel='"+seclevel+"',jobtitle='"+jobtitle+"',joblevel='"+joblevel+"',sex='"+sex+"',status='"+status+"',email='"+email+"',lastname='"+name+"',telephone='"+telephone+"',mobile='"+mobile+"',managerid='"+managerid+"',departmentid='"+dept+"' where workcode='"+workcode+"'";
			rs1.execute(upsql);
			base.writeLog("更新"+upsql);
		}else{//不存在则判断状态是否为5离职
			if(!"5".equals(status+"")){//不为5插入
				rs3.executeProc("HrmResourceMaxId_Get", "");
				rs3.next(); 
				int maxid = rs3.getInt(1);  
				String insql="insert into hrmresource(id,loginid,password,workcode,sex,lastname,managerid,departmentid,outkey,status,email,telephone,mobile,certificatenum,joblevel,jobtitle,createrid,createdate,lastmodid,lastmoddate,systemlanguage)values"
						+ "('"+maxid+"','"+email+"','"+pwd+"','"+workcode+"','"+sex+"','"+name+"','"+managerid+"','"+dept+"','"+textfield1+"','"+status+"','"+email+"','"+telephone+"','"+mobile+"','"+certificatenum+"','"+joblevel+"','"+jobtitle+"',1,'"+DateUtil.getDate()+"',1,'"+DateUtil.getDate()+"',7)";
				rs1.execute(insql);
				base.writeLog("插入"+insql);
			}
		}
		
	}
	
}
