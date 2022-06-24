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
		
		//String qry = "select * from enquirytable where enquiryid=?";
		String qry="SELECT tab.expiryTabRs,followup.followupTabRs,freet.freeTabRs,cli.*,emp.`employeefirstname`,emp.`employeelastname`,sour.sourcename,service.serviceinterestedinname,wortm.workouttimename,center.centername,center.centerlocation,enqType.enquirytype,fitGole.fitnessgoalname,mrktp.markettingpreferencename,pers.personalinterestname,eth.ethncityname,occ.occupationname,sta.statusname,memtype.membershiptype "
				+ "FROM clienttable cli "
				+ "JOIN employeetable emp ON emp.`employeeid`=cli.employeeid "
				+ "LEFT JOIN (SELECT sourceid,sourcename FROM sourcetable) sour ON sour.sourceid=cli.sourceid "
				+"LEFT JOIN (SELECT CONCAT('{"+"''myExpDetailsArray''"+":[',GROUP_CONCAT(JSON_OBJECT('serialNumber',tab.serialnumber,'clientId',tab.clientid,'packageId',tab.packageid,'activatedDate',tab.activateddate,'expiryDate',tab.expirydate,'countPackage',tab.countpackage,'discountPrice',IF(tab.discountPrice IS NOT NULL,tab.discountPrice,0),'billNumberId',IF(tab.billnumberid!=NULL,tab.billnumberid,''),'packageName',tab.`packagename`,'packagePrice',tab.`packageprice`,'packageDays',tab.`packagedays`,'packageUsableDays',tab.`packageusabledays`,'memberShipTypeId',tab.`membershiptypeid`,'statusName',tab.statusname,'memberShipType',tab.membershiptype)),']}') AS expiryTabRs,tab.clientid FROM (SELECT cliExp.serialnumber,cliExp.clientid,cliExp.packageid,cliExp.activateddate,cliExp.expirydate,cliExp.countpackage,cliExp.discountPrice,cliExp.billnumberid,pk.`packagename`,pk.`packageprice`,pk.`packagedays`,pk.`packageusabledays`,pk.`membershiptypeid`,st.statusname,memType.membershiptype FROM clientexpirypackagedetailtable cliExp " 
				+"LEFT JOIN(SELECT CONCAT('{"+"''followupArray''"+":[',GROUP_CONCAT(JSON_OBJECT('followupId',followup.followupid,'followupTypeId',followup.followuptypeid,'callresponseId',followup.callresponseid,'convertibilityId',followup.convertibilityid,'followupDate',followup.followupdate,'followupTime',TIME_FORMAT(followup.followuptime,'%H:%i:%s'),'nextFollowupDate',followup.nextfollowupdate,'nextFollowupTime',TIME_FORMAT(followup.nextfollowuptime,'%H:%i:%s'),'followupByEmployeeId',followup.followupbyemployeeid,'followUpStatus',followup.followupstatus,'followupComment',followup.followupcomment,'followupType',followup.followuptype,'callresponseName',followup.callresponsename,'convertibilityName',followup.convertibilityname,'followupEmployeeFirstLastName',CONCAT(followup.employeefirstname,' ',followup.employeelastname))ORDER BY followup.followupId DESC),']}') AS followupTabRs,followup.enquiryidorclientid FROM " 
				+"(SELECT f.*,ft.`followuptype`,c.`callresponsename`,cv.`convertibilityname`,emp.`employeefirstname`,emp.`employeelastname` FROM followuptable f JOIN followuptypetable ft ON ft.`followupid`=f.`followuptypeid` JOIN callresponsetable c ON c.`callresponseid`=f.`callresponseid` JOIN convertibilitytable cv ON cv.`convertibilityid`=f.`convertibilityid` JOIN employeetable emp ON emp.`employeeid`=f.`followupbyemployeeid` " 
				+"WHERE enquiryidorclientid = "+clientId+" ORDER BY followupid DESC) AS followup) AS followup ON followup.enquiryidorclientid=cli.clientid "
				+"LEFT JOIN(SELECT CONCAT('{"+"''freeTrialArray''"+":[',GROUP_CONCAT(JSON_OBJECT('freetrialId',freet.freetrialid,'trialStartDate',freet.trialstartdate,'trialPeriod',freet.trialperiod,'trialDaysAllowed',freet.trialdaysallowed,'maximumTrialAmount',freet.maximumtrialamount,'trialPackageId',freet.packageid,'takeTrialCount',IF(freet.taketrial IS NOT NULL,freet.taketrial,''),'trialEmployeeFirstLastName',CONCAT(freet.employeefirstname,' ',freet.employeelastname),'trialPackageName',freet.packagename) ORDER BY freet.freetrialid DESC),']}') AS freeTabRs,freet.enquiryid FROM (SELECT free.*,ct.taketrial,emp.employeefirstname,emp.employeelastname,pk.packagename FROM freetrialtable free " 
				+"WHERE free.enquiryid="+clientId+" ORDER BY free.freetrialid DESC) AS freet) AS freet ON freet.enquiryid=cli.clientid "
				+ "where cli.clientid="+clientId+"";
		ps1 = conn.prepareStatement(qry);
		rs = ps1.executeQuery();
		int sizeodata=0;
		if (rs.next())
		{
			sizeodata=1;
			 jobj=new JSONObject();
			 	//long enquiryIddb=0;
				String firstName="";
				String middleName="";
				String lastName="";
				String gender="";
				String enquiryDate="";
				int employeeId=0;
				int sourceId=0;
				int serviceInterestedInId=0;
				String clientLanguage="";
				String country="";
				String state="";
				String city="";
				int workOutTime=0;
				long mobileNumber1=0;
				long mobileNumber2=0;
				long homeNumber1=0;
				long homeNumber2=0;
				long officeNumber1=0;
				long officeNumber2=0;
				String emailId="";
				int centerLocationId=0;
				int enquiryTypeId=0;
				int fitnessGoalId=0;
				int markettingPreferenceId=0;
				String dateOfBirth="";
				String anniversaryDate="";
				String nationality="";
				String nationalIn="";
				String spouseName="";
				String weight="0";
				String profilePicture="";
				String medicalAlert="";
				int personalInterestId=0;
				String refferredBy="";
				int pinCode=0;
				String address="";
				String emergencyContactPerson="";
				long emergencyContactNumber=0;
				String accessCardIssueDate="";
				String occupation="";// this is id hold
				String occupationName="";
				String organisation="";
				String facebookId="";
				String linkedinId="";
				int ethnicityId=0;//cast
				String generalInformation="";
				String personalisedMessage="";
				String bloodGroup="";
				int callResponseId=0;
				int convertibilityId=0;
				String followUpIdString="";
				String comment="";
				String freeTrialIdString="";
				String refferredBy1="";
				
				String employeefirstname="";
				String employeelastname="";
				String sourcename="";
				String serviceinterestedinname="";
				String workouttimename="";
				String centername="";
				String centerlocation="";
				String enquirytype="";
				String fitnessgoalname="";
				String markettingpreferencename="";
				String personalinterestname="";
				String ethncityname="";
				
				String expiryDetails="";// join table details package expiry
				String followupDetails="";// join table details of followups
				String freeTrialDetails=""; // join table details free Trials
				
				
				
				//------------------------------------------------------------------
				//long clientId;
			    //String firstName;
			    //String middleName;
			   // String lastName;
				//String gender;
				//java.sql.Date enquiryDate;
				//int employeeId;
				//int sourceId;
				//int serviceInterestedInId;
				//String clientLanguage;
				//String country;
				//String state;
			//	String city;
			//	int workOutTime;
				//long mobileNumber1;
				//long mobileNumber2;
				//long homeNumber1;
				//long homeNumber2;
				//long officeNumber1;
				//long officeNumber2;
				//String emailId;
				//int centerLocationId;
			//	int enquiryTypeId;
			//	int fitnessGoalId;
				//int markettingPreferenceId;
			//	java.sql.Date dateOfBirth;
			//	java.sql.Date anniversaryDate;
			//	String nationality;
				//String nationalIn;
				//String spouseName;
				//float weight;
				//String profilePicture;
				//String medicalAlert;
				//int personalInterestId;
				//String refferredBy;
				//int pinCode;
				//String address;
				//String emergencyContactPerson;
				//long emergencyContactNumber;
				//java.sql.Date accessCardIssueDate;
				//String occupation;
				//String organisation;
				//String facebookId;
				//String linkedinId;
				//int ethnicityId;//cast
				//String generalInformation;
				//String personalisedMessage;
				//String bloodGroup;
				//int callResponseId;
				//int convertibilityId;
				//String followUpIdString;
				//String comment;
				//String freeTrialIdString;
				
				int memberShipStatusId=0;
				String memberShipStatusName="";
				int memberShipTypeId=0;
				String memberShipTypeName="";
				String packageIdString="";
				String packageExpiryDateString="";
				String billIdString="";
				String totalBillAmount="0";
				String paidAmount="0";
				String totalBalance="0";
				String currentBalance="0";
				//String refferredBy1;
				
			 
				if(!Strings.isNullOrEmpty(rs.getString("firstName")))
				{
					firstName=rs.getString("firstName").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("middleName")))
				{
					middleName=rs.getString("middleName").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("lastName")))
				{
					lastName=rs.getString("lastName").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("gender")))
				{
					gender=rs.getString("gender");
				}
				if(!Strings.isNullOrEmpty(rs.getString("enquiryDate")))
				{
					enquiryDate=rs.getString("enquiryDate");
				}
				if(rs.getInt("employeeid")!=0)
				{
					employeeId=rs.getInt("employeeid");
				}
				if(!Strings.isNullOrEmpty(rs.getString("sourceId")))
				{
					sourceId=rs.getInt("sourceId");
				}
				if(!Strings.isNullOrEmpty(rs.getString("serviceInterestedInId")))
				{
					serviceInterestedInId=rs.getInt("serviceInterestedInId");
				}
				if(!Strings.isNullOrEmpty(rs.getString("clientLanguage")))
				{
					clientLanguage=rs.getString("clientLanguage");
				}
				if(!Strings.isNullOrEmpty(rs.getString("country")))
				{
					country=rs.getString("country").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("state")))
				{
					state=rs.getString("state").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("city")))
				{
					city=rs.getString("city").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("workOutTime")))
				{
					workOutTime=rs.getInt("workOutTime");
				}
				if(!Strings.isNullOrEmpty(rs.getString("mobileNumber1")))
				{
					mobileNumber1= rs.getLong("mobileNumber1");
				}
				if(!Strings.isNullOrEmpty(rs.getString("mobileNumber2")))
				{
					mobileNumber2=rs.getLong("mobileNumber2");
				}
				if(!Strings.isNullOrEmpty(rs.getString("homeNumber1"))){
					homeNumber1=rs.getLong("homeNumber1");
				}
				if(!Strings.isNullOrEmpty(rs.getString("homeNumber2"))){
					homeNumber2=rs.getLong("homeNumber2");
				}
				if(!Strings.isNullOrEmpty(rs.getString("officeNumber1"))){
					officeNumber1=rs.getLong("officeNumber1");
				}
				if(!Strings.isNullOrEmpty(rs.getString("officeNumber2"))){
					officeNumber2=	rs.getLong("officeNumber2");
				}
				if(!Strings.isNullOrEmpty(rs.getString("emailId"))){
					emailId=rs.getString("emailId");
				}
				if(!Strings.isNullOrEmpty(rs.getString("centerLocationId"))){
					centerLocationId=rs.getInt("centerLocationId");
				}
				
				if(!Strings.isNullOrEmpty(rs.getString("enquiryTypeId")))
				{
					enquiryTypeId=rs.getInt("enquiryTypeId");
				}
				if(!Strings.isNullOrEmpty(rs.getString("fitnessGoalId")))
				{
					fitnessGoalId=rs.getInt("fitnessGoalId");
				}
				if(!Strings.isNullOrEmpty(rs.getString("markettingPreferenceId")))
				{
					markettingPreferenceId=rs.getInt("markettingPreferenceId");
				}
				if(!Strings.isNullOrEmpty(rs.getString("dateOfBirth")))
				{
					dateOfBirth=rs.getString("dateOfBirth");
				}
				if(!Strings.isNullOrEmpty(rs.getString("anniversaryDate")))
				{
					anniversaryDate=rs.getString("anniversaryDate");
				}
				if(!Strings.isNullOrEmpty( rs.getString("nationality")))
				{
					nationality=rs.getString("nationality").toUpperCase();
				}
				if(!Strings.isNullOrEmpty(rs.getString("nationalIn")))
				{
					nationalIn=rs.getString("nationalIn");
				}
				if(!Strings.isNullOrEmpty(rs.getString("spouseName")))
				{
					spouseName=rs.getString("spouseName");
				}
				if(!Strings.isNullOrEmpty(rs.getString("weight")))
				{
					weight=rs.getString("weight");
				}
				if(!Strings.isNullOrEmpty(rs.getString("profilePicture")))
				{
					profilePicture=rs.getString("profilePicture");
				}
		        if(!Strings.isNullOrEmpty(rs.getString("medicalAlert"))){
		        	medicalAlert=rs.getString("medicalAlert");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("personalInterestId"))){
		        	personalInterestId=rs.getInt("personalInterestId");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("refferredBy"))){
		        	refferredBy=rs.getString("refferredBy");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("pinCode"))){
		        	pinCode=rs.getInt("pinCode");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("address"))){
		        	address=rs.getString("address");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("emergencyContactPerson"))){
		        	emergencyContactPerson=rs.getString("emergencyContactPerson");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("emergencyContactNumber"))){
		        	emergencyContactNumber=rs.getLong("emergencyContactNumber");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("accessCardIssueDate"))){
		        	accessCardIssueDate=rs.getString("accessCardIssueDate");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("occupation"))) {
		        	occupation=rs.getString("occupation");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("organisation"))){
		        	organisation=rs.getString("organisation");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("facebookId"))){
		        	facebookId=rs.getString("facebookId");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("linkedinId"))){
		        	linkedinId=rs.getString("linkedinId");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("ethnicityId"))){
		        	ethnicityId=rs.getInt("ethnicityId");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("generalInformation"))){
		        	generalInformation=rs.getString("generalInformation");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("personalisedMessage"))){
		        	personalisedMessage=rs.getString("personalisedMessage");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("bloodgroup"))){
		        	bloodGroup=rs.getString("bloodgroup");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("callResponseId"))){
		        	callResponseId=rs.getInt("callResponseId");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("convertibilityId"))){
		        	convertibilityId=rs.getInt("convertibilityId");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("followUpIdString"))){
		        	followUpIdString=	rs.getString("followUpIdString");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("comment"))){
		        	comment=rs.getString("comment");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("freeTrialIdString"))){
		        	freeTrialIdString=rs.getString("freeTrialIdString");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("refferredBy1"))){
		        	refferredBy1=rs.getString("refferredBy1");
		        }
		        
		        //extra in client table
		        if(!Strings.isNullOrEmpty(rs.getString("membershipstatusid")))
		        {
		        	memberShipStatusId=rs.getInt("membershipstatusid");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("statusname")))
		        {
		        	memberShipStatusName=rs.getString("statusname");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("membershiptypeid")))
		        {
		        	memberShipTypeId=rs.getInt("membershiptypeid");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("membershiptype")))
		        {
		        	memberShipTypeName=rs.getString("membershiptype").toUpperCase();
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("membershiptypeid")))
		        {
		        	packageIdString=rs.getString("packageidstring");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("membershiptypeid")))
		        {
		        	packageExpiryDateString=rs.getString("packageexpirydatestring");
		        }
		        if(!Strings.isNullOrEmpty(rs.getString("billidstring")))
		        {
		        	billIdString=rs.getString("billidstring");
		        }
		       
				if(!Strings.isNullOrEmpty(rs.getString("totalbillamount")))
				{
					totalBillAmount=rs.getString("totalbillamount");
				}
				if(!Strings.isNullOrEmpty(rs.getString("paidamount")))
				{
					paidAmount=rs.getString("paidamount");
				}
				if(!Strings.isNullOrEmpty(rs.getString("totalbalance")))
				{
					totalBalance=rs.getString("totalbalance");
				}
				if(!Strings.isNullOrEmpty(rs.getString("currentbalance")))
				{
					currentBalance=rs.getString("currentbalance");
				}
				//float totalBillAmount;
				//float paidAmount;
				//float totalBalance;
				//float currentBalance;
		        
		        
				//join record
		        if(!Strings.isNullOrEmpty(rs.getString("employeefirstname")))
				{
					employeefirstname=rs.getString("employeefirstname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("employeelastname")))
				{
					employeelastname=rs.getString("employeelastname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("sourcename")))
				{
					sourcename=rs.getString("sourcename");
				}
				if(!Strings.isNullOrEmpty(rs.getString("serviceinterestedinname")))
				{
					serviceinterestedinname=rs.getString("serviceinterestedinname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("workouttimename")))
				{
					workouttimename=rs.getString("workouttimename");
				}
				if(!Strings.isNullOrEmpty(rs.getString("centername")))
				{
					centername=rs.getString("centername");
				}
				if(!Strings.isNullOrEmpty(rs.getString("centerlocation")))
				{
					centerlocation=rs.getString("centerlocation");
				}
				if(!Strings.isNullOrEmpty(rs.getString("enquirytype")))
				{
					enquirytype=rs.getString("enquirytype");
				}
				if(!Strings.isNullOrEmpty(rs.getString("fitnessgoalname")))
				{
					fitnessgoalname=rs.getString("fitnessgoalname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("markettingpreferencename")))
				{
					markettingpreferencename=rs.getString("markettingpreferencename");
				}
				if(!Strings.isNullOrEmpty(rs.getString("personalinterestname")))
				{
					personalinterestname=rs.getString("personalinterestname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("ethncityname")))
				{
					ethncityname=rs.getString("ethncityname");
				}
				if(!Strings.isNullOrEmpty(rs.getString("occupationname")))
				{
					occupationName=rs.getString("occupationname");
				}
				
				if(!Strings.isNullOrEmpty(rs.getString("expiryTabRs")))
				{
					expiryDetails=rs.getString("expiryTabRs");
				}
				if(!Strings.isNullOrEmpty(rs.getString("followupTabRs")))
				{
					followupDetails=rs.getString("followupTabRs");
				}
				if(!Strings.isNullOrEmpty(rs.getString("freeTabRs")))
				{
					freeTrialDetails = rs.getString("freeTabRs");
				}
				
				/*org.json.JSONObject jexpObj = new org.json.JSONObject(expiryDetails);
				org.json.JSONArray expJrr = jexpObj.getJSONArray("myExpDetailsArray");
				org.json.JSONObject followupObj = new org.json.JSONObject(followupDetails);
				org.json.JSONArray folladminowupJrr = followupObj.getJSONArray("followupArray");
				org.json.JSONObject freeTrialObj = new org.json.JSONObject(freeTrialDetails);
				org.json.JSONArray freeTrialjrr = freeTrialObj .getJSONArray("freeTrialArray");*/
				
				//System.out.println(" json and array "+jexpObj);
				//System.out.println("lenght of jobje array in client table "+jexpObj.length()+" jrr length "+expJrr.length());
				
				jobj.put("expiryDetails", expiryDetails);
				jobj.put("followupDetails", followupDetails);
				jobj.put("freeTrialDetails", freeTrialDetails);
				jobj.put("clientId", rs.getLong("clientId"));
				jobj.put("firstName",firstName);
				jobj.put("middleName",middleName);
				jobj.put("lastName",lastName);
				jobj.put("gender",gender);
				jobj.put("enquiryDate",enquiryDate);
				jobj.put("employeeId",employeeId);
				jobj.put("sourceId",sourceId);
				jobj.put("serviceInterestedInId",serviceInterestedInId);
				jobj.put("clientLanguage",clientLanguage);
				jobj.put("country",country);
				jobj.put("state",state);
				jobj.put("city",city);
				jobj.put("workOutTime",workOutTime);
				jobj.put("mobileNumber1",mobileNumber1);
				jobj.put("mobileNumber2",mobileNumber2);
				jobj.put("homeNumber1",homeNumber1);
				jobj.put("homeNumber2",homeNumber2);
				jobj.put("officeNumber1",officeNumber1);
				jobj.put("officeNumber2",officeNumber2);
				jobj.put("emailId",emailId);
				jobj.put("centerLocationId",centerLocationId);
				jobj.put("enquiryTypeId",enquiryTypeId);
				jobj.put("fitnessGoalId",fitnessGoalId);
				jobj.put("markettingPreferenceId",markettingPreferenceId);
				jobj.put("dateOfBirth",dateOfBirth);
				jobj.put("anniversaryDate",anniversaryDate);
				jobj.put("nationality",nationality);
				jobj.put("nationalIn",nationalIn);
				jobj.put("spouseName",spouseName);
				jobj.put("weight",weight);
				jobj.put("profilePicture",profilePicture);
				jobj.put("medicalAlert",medicalAlert);
				jobj.put("personalInterestId",personalInterestId);
				jobj.put("refferredBy",refferredBy);
				jobj.put("pinCode",pinCode);
				jobj.put("address",address);
				jobj.put("emergencyContactPerson",emergencyContactPerson);
				jobj.put("emergencyContactNumber",emergencyContactNumber);
				jobj.put("accessCardIssueDate",accessCardIssueDate);
				jobj.put("occupation",occupation);
				jobj.put("organisation",organisation);
				jobj.put("facebookId",facebookId);
				jobj.put("linkedinId",linkedinId);
				jobj.put("ethnicityId",ethnicityId);
				jobj.put("generalInformation",generalInformation);
				jobj.put("personalisedMessage",personalisedMessage);
				jobj.put("bloodGroup",bloodGroup);
				jobj.put("callResponseId",callResponseId);
				jobj.put("convertibilityId",convertibilityId);
				jobj.put("followUpIdString",followUpIdString);
				jobj.put("comment",comment);
				jobj.put("freeTrialIdString",freeTrialIdString);
				jobj.put("refferredBy1",refferredBy1);
				jobj.put("employeeFirstLastName", employeefirstname+" "+employeelastname);
				jobj.put("sourceName", sourcename);
				jobj.put("serviceInterestedInName", serviceinterestedinname);
				jobj.put("workoutTimeName", workouttimename);
				jobj.put("centerName", centername);
				jobj.put("centerLocation", centerlocation);
				jobj.put("enquiryType", enquirytype);
				jobj.put("fitnessGoalName", fitnessgoalname);
				jobj.put("markettingPreferenceName", markettingpreferencename);
				jobj.put("personalInterestName", personalinterestname);
				jobj.put("ethncityName", ethncityname);
				jobj.put("occupationName", occupationName);
				
				jobj.put("memberShipStatusId",memberShipStatusId );
				jobj.put("memberShipStatusName", memberShipStatusName);
				jobj.put("memberShipTypeId", memberShipTypeId);
				jobj.put("memberShipTypeName", memberShipTypeName);
				jobj.put("packageIdString",packageIdString );
				jobj.put("packageExpiryDateString", packageExpiryDateString);
				jobj.put("billIdString",billIdString);
				jobj.put("totalBillAmount", totalBillAmount);
				jobj.put("paidAmount", paidAmount);
				jobj.put("totalBalance",totalBalance );
				jobj.put("currentBalance", currentBalance);
				
				
			
				 //jarray.put(jobj);
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

	
	
	
	}