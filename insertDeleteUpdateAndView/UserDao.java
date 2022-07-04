package com.indoreAweb.gymmanagement.dao.clientdao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbutils.DbUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.service.Myconnection;
import com.google.common.base.Strings;

public class ClientDao implements ClientImpl 
{
	private static Connection conn;
	

	public long insertClientDetails(ClientBean client) throws Exception
	{
		PreparedStatement ps=null;
		
		try{
		conn=Myconnection.getConnection();
	    ps=conn.prepareStatement("insert into clienttab values(?,?,?,?,?,?)");
		long id=getNextClientId();
	    ps.setLong(1,null);// auto generated in sql so set null other wise set max id which is geted nextclientid
	    ps.setString(2,client.getFirstName());
    	ps.setDate(3,client.getDOBDate());
     	int employeeId=client.getEmployeeId();
     	if(employeeId==0)
		{
    	ps.setString(4,null);
		}	
		else
		{
    	ps.setInt(4, client.getEmployeeId());
		}
		float weight=client.getWeight();
		if(weight==0)
		{
			ps.setString(5,null);
		}
		else
		{
			ps.setFloat(5, client.getWeight());
		}
		ps.setString(6, client.getProfilePicture());
        return ps.executeUpdate()>0 ? /*getMaxClientId(client.getEnquiryDate(),mobileNumber1)*/ id : 0;
		}finally
		{
			DbUtils.closeQuietly(ps);
		}
	}
	 public long getNextClientId() throws Exception
    {	
		ResultSet rs=null;
    	PreparedStatement ps=null;
		try
		{
    	long z = 1l;
		conn = Myconnection.getConnection();
		String qry = "select max(clientid) as clientid from clienttab";
		ps = conn.prepareStatement(qry);
		rs = ps.executeQuery();
		String nextId = "0";
		if (rs.next())
		{
			z = rs.getLong("clientId");
			if (z == 0)
			{ z = 1;
				nextId = "1430" + z;
			}
			else
			{
				nextId = "" + (z);
				nextId = nextId.substring(2);
				z = Long.parseLong(nextId);
				nextId = "1430" + (++z);
			}
		}
		return Long.parseLong(nextId);
    }finally
	{
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(ps);
	}
    }
	public boolean updateClientDetails(ClientBean enqB)throws Exception
	{
		PreparedStatement ps=null;
		
		try{
		conn = Myconnection.getConnection();
		String qry = "update clienttab set firstName=? where clientid=?";
		ps = conn.prepareStatement(qry);
		ps.setInt(1, enqB.getFirstName());
		ps.setLong(2, enqB.getClientId());
		return ps.executeUpdate() > 0 ? true : false;
		}finally
		{
		
			DbUtils.closeQuietly(ps);
		}
	}
	public boolean deleteClient(long clientId) throws Exception
	{
		PreparedStatement ps=null;
		try
		{
		conn=Myconnection.getConnection();
        ps=conn.prepareStatement("delete from clienttab where clientid=?");
        ps.setLong(1,clientId);
        return ps.executeUpdate()>0 ? true : false;
		}finally
		{
			DbUtils.closeQuietly(ps);
		}
	}
	
	public ClientBean getClientById(long clientId) throws Exception
	{
		PreparedStatement ps=null;
		ResultSet rs=null;
		try
		{
		conn=Myconnection.getConnection();
		String qry="select * from clienttab where clientid=?";
		ps=conn.prepareStatement(qry);
		ps.setLong(1, clientId);
        rs=ps.executeQuery();
        if(rs.next())
        {
            ClientBean client=new ClientBean(rs.getLong("clientId"), rs.getString("firstName"),rs.getDate("dobDate"), rs.getInt("employeeId"),rs.getFloat("weight"),rs.getString("profilePicPath"));
            return client;
        }
        else
        {
             return null;
        }
	}finally
	{
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(ps);
	}
	}
	
	// client Details set in JSON 
	public String getJsonByClientId(long clientId)
	{
		PreparedStatement ps=null,ps1=null;
		ResultSet rs=null;
		JSONObject jobj=null;
		JSONObject jobj1=null;
		JSONArray jarray = new JSONArray();
		try
		{
		conn = Myconnection.getConnection();
		ps = conn.prepareStatement("SET SESSION group_concat_max_len = 1000000");
		//ps.execute();
		int x = ps.executeUpdate();
		ps.close();
		ps=conn.prepareStatement("SET GLOBAL group_concat_max_len = 1000000");
		//ps.execute();
		int y = ps.executeUpdate();
		ps.close();
		String qry="SELECT tab1.expiryTabRs,flup.fowupTabRs,cli.clientid,cli.`firstname`,emp.`employeefirstname` FROM clienttable cli" 
+"JOIN employeetable emp ON emp.`employeeid`=cli.employeeid"
+"LEFT JOIN (SELECT CONCAT('{"+"''myExpDetailsArray''"+":[',GROUP_CONCAT(JSON_OBJECT('serialNumber',tab.serialnumber,'clientId',tab.clientid,'packageId',"
+"tab.packageid,'activatedDate',tab.activateddate,'expiryDate',tab.expirydate,'discountPrice',"
+"IF(tab.discountPrice IS NOT NULL,tab.discountPrice,0),'billNumberId',IF(tab.billnumberid!=NULL,tab.billnumberid,''))),']}') AS expiryTabRs,tab.clientid "
+"FROM (SELECT cliExp.serialnumber,cliExp.clientid,cliExp.packageid,cliExp.activateddate,cliExp.expirydate,cliExp.discountPrice,"
+"cliExp.billnumberid FROM clientexpirypackagedetailtable cliExp WHERE cliExp.`clientid`="+clientId+") AS tab) AS tab1 ON tab1.clientid=cli.clientid "

+"LEFT JOIN(SELECT CONCAT('{"+"''followupArray''"+":[',GROUP_CONCAT(JSON_OBJECT('fId',fow.followupid,'fupDate',fow.followupdate,'fTime',TIME_FORMAT(fow.followuptime,'%H:%i:%s'),'nextFupDate',fow.nextfollowupdate,'fByEmpId',fow.followupbyemployeeid,'fStatus',fow.followupstatus,'fComment',fow.followupcomment,'fEmpFLName',CONCAT(fow.employeefirstname,' ',fow.employeelastname))ORDER BY fow.followupId DESC),']}') AS fowupTabRs,fow.enquiryidorclientid "
+"FROM (SELECT f.*,emp.`employeefirstname`,emp.`employeelastname` FROM followuptable f JOIN employeetable emp ON emp.`employeeid`=f.`followupbyemployeeid`  "
+"WHERE enquiryidorclientid = "+clientId+" ORDER BY followupid DESC) AS fow) AS flup ON flup.enquiryidorclientid=cli.clientid "
+ "where cli.clientid="+clientId+"";
		ps1 = conn.prepareStatement(qry);
		rs = ps1.executeQuery();
		int sizeodata=0;
		if (rs.next())
		{
			sizeodata=1;
			 jobj=new JSONObject();
			 	String firstName="";
				String employeefirstname="";
				String expiryDetails="";// join table details packag expiry
				String followupDetails="";// join table details of followups
				if(!Strings.isNullOrEmpty(rs.getString("firstName")))
				{
					firstName=rs.getString("firstName").toUpperCase();
				}
				//join record fetch
		        if(!Strings.isNullOrEmpty(rs.getString("employeefirstname")))
				{
					employeefirstname=rs.getString("employeefirstname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("expiryTabRs")))
				{
					expiryDetails=rs.getString("expiryTabRs");
				}
				if(!Strings.isNullOrEmpty(rs.getString("followupTabRs")))
				{
					followupDetails=rs.getString("followupTabRs");
				}
				jobj.put("expiryDetails", expiryDetails);
				jobj.put("followupDetails", followupDetails);
				jobj.put("clientId", rs.getLong("clientId"));
				jobj.put("firstName",firstName);
				jobj.put("employeeFirstLastName", employeefirstname);
		} 
			return sizeodata > 0 ? jobj.toString() : null;
		}
		catch(SQLException sq)
		{
			sq.printStackTrace();
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps1);
		}
	}

	public org.json.JSONObject  getInActiveMCount() throws SQLException, JSONException 
	{
		PreparedStatement ps=null;
		ResultSet rs=null;
		int membCount=0;
		conn=Myconnection.getConnection();
		org.json.JSONObject ob=null;
		try {
	      String query="select count(userId) AS memberCount from usertable where memberid=2";		
		  ps=conn.prepareStatement(query);
		  rs=ps.executeQuery();
		  while(rs.next())
		  {
			ob=new org.json.JSONObject();
			membCount=rs.getInt("memberCount");  
			ob.put("InActiveMemCount",membCount); 
		  }
		  return ob;
		}finally 
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		} 
	}
	public String  getMemberCount() throws SQLException, JSONException 
	{
		PreparedStatement ps=null;
		ResultSet rs=null;
		long membCount=0;
		con=MyCon.getConnection();
		JSONObject ob=null,nob=null;
		JSONArray arr= new JSONArray();
	  try {
		String qry="select count(userid) AS memberCounts from usertable where memberstatusid=7 "+
					"union (select count(userid) AS memberCounts from followuptable where userid like '12%' AND followuptypeid=1 AND nextfollowupdate BETWEEN CURDATE() AND ADDDATE(CURDATE(),7)) union (select count(usersId) AS memberCounts from usertable where memberstatusid=9) union (select sum(totbalance) AS memberCounts from usertable) union (select sum(totbillamount) AS memberCounts from usertable) union(select sum(paidamt)as memberCounts from billtable)";
		ps=con.prepareStatement(qry);
		rs=ps.executeQuery();
		int i=0;
		while(rs.next())
		{
			 ob=new JSONObject();
			 membCount=rs.getLong("memberCounts");
			if(i==0)
			{	 
					 ob.put("activeMembCount",membCount);
			}else if(i==1)
			{
					 ob.put("weeklyJoinCount",membCount);
			}else if(i==2)
			{
					 ob.put("InActiveMembCount",membCount); 
			}else if(i==3)
			{
					 ob.put("memberDueAmt",membCount);
			}else if(i==4)
			{
					 ob.put("totBillamount",membCount);
			}else if(i==5)
			{
					 ob.put("pmtReceived",membCount);
			}
			arr.add(ob);
			i++;
		}
		nob = new JSONObject();
		  nob.put("ResultArry", arr);
		  return nob.toString();
		}catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
		finally 
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		} 
	}
	public static byte[] decodeImage(String imageDataString) {		
			return Base64.decodeBase64(imageDataString);
		}
	
}