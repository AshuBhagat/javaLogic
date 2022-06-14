package com.abc.servlets.login;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InterruptedIOException;
import java.util.Calendar;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import com.abcMain.dao.attendancedao.AttendanceSystemDao;
import com.abcMain.dao.centerlocation.CenterLocationDAO;
import com.abcMain.dao.clientdao.ClientDao;
import com.abcMain.dao.logdao.LogEntryDao;
import com.abcMain.dao.loginoutdao.LogInCheckDao;
import com.abcMain.dao.membershipstatus.MembershipStatusDAO;
import com.abcMain.dao.smsdao.SentSmsDetailDAO;
import com.abcMain.dao.smsdao.SmsPanelSettingDao;
import com.abcMain.dao.smsdao.SmsTemplateDAO;
import com.abcMain.dao.statusdao.StatusDAO;
import com.abcMain.model.employee.bean.EmployeeBean;
import com.abcMain.model.error.ErrorBean;
import com.abcMain.model.log.bean.LogEntryBean;
import com.abcMain.model.loginout.bean.LogInCheckBean;
import com.abcMain.model.sms.bean.SmsPanelSettingBean;
import com.abcMain.model.sms.bean.SmsTemplateBean;
import com.abcMain.service.AutoMySqlBackUp;
import com.abcMain.service.authorise.Authorise;
import com.google.common.base.Strings;

//@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
    static String compareDate=null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		response.setContentType("text/html;charset=UTF-8");
		EmployeeBean employee=null;
		try
        {
		java.sql.Date logToDayDate=null;
    	DateFormat formatter;
    	//SimpleDateFormat timeFormatter;
    	formatter = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar cal = Calendar.getInstance();
    	java.util.Date dt = cal.getTime(); 
    	String newDt=formatter.format(dt);
    	logToDayDate=java.sql.Date.valueOf(newDt);
    	LogEntryBean logEntryb = null;
    	String birthDayWish="";
    		List<SmsTempBean> smsTempList = new SmsTempDao().getSmsTemp();
        	employee = Authorise.checkLogin(userName, password);// check password veryfication
			LogEntryBean logbean=new LogEntryBean();
        	if(smsTempList!=null)
        	{
        		for(SmsTempBean stm:smsTempList)
        		{
        			if(!Strings.isNullOrEmpty(stm.getNameOfTemp()) && stm.getNameOfTemp()!=null)
        			{
        				if(stm.getNameOfTemp().equals("BirthDay Wish"))
        				{
        					birthDayWish=stm.getDescription();
        				}
        			}else
        			{
        				System.out.println(" Null Name of temple ");
        			}
        		}
        	}
        	 String callDescriptionBirthDay = null;		// "Wish You Many Many Happy Retruns Of Bith Day From Gym";
        	 SmsPanelSettingBean sp = new SmsPanelSettingDao().getFirstSmsPanel();
        	String panelUrlBirthDay="";
        	if(sp!=null)
        	{
        		panelUrlBirthDay=sp.getUrl();
        	}
        	logEntryb=new LogEntryDao().getLogDetailsByLogDate(logToDayDate);
        	if(compareDate==null)
        	{
        		compareDate=String.valueOf(logToDayDate);
        		System.out.println("Date Compare this part Compare Null at First Time ......"+compareDate);
        		//If Attendance Finger ScanMachine Not Have To Owner so comment Belowed
        		 /****************Attendance Finger ScanMachine**************/
        		//new AttendanceFingerSystemDao().insertAttendanceFingerGetFromMsAccess(); // if have ScanMachine to open line and check also table have Ms-Access entry
        		/*********************/
        		if(logEntryb==null)
        		{
				/****************DATA BASE BACKUP**************/
        		//new AutoMySqlBackUpForAbc().backupDatabaseAbc("localhost", "3306", "root", "root", "abcDbName", "D:/AbcDataBaseBackUp/", "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump.exe");
        		// localhost,Port Number,User,Password,DataBaseName,Destination Path,Source Path Of mysqldump.exe	
				/****************End BackUp**************/
        		       	String mobileBirthDay=(String)new SentSmsDao().getClientTodayBirthDayMobileString();
        				logbean.setLogDate(logToDayDate);
                		logbean.setLogStatus(null);
                		logbean.setLogType(null);
                		boolean b = new LogEntryDao().insertLogEntryDate(logbean);
                		/****************SMS Birtday**************/
                		if(!Strings.isNullOrEmpty(mobileBirthDay))
                		{
                			if(!Strings.isNullOrEmpty(birthDayWish))
                			{
                				callDescriptionBirthDay = null;
                				callDescriptionBirthDay =birthDayWish;
                				callDescriptionBirthDay=callDescriptionBirthDay.replaceAll(" ", "%20");
	                			callDescriptionBirthDay=callDescriptionBirthDay.replaceAll("[\n\r]", " . ");
	                			callDescriptionBirthDay=callDescriptionBirthDay.replaceAll(" ", "");
	                			panelUrlBirthDay=panelUrlBirthDay.replaceAll("mobileMsgList",mobileBirthDay);
	                			panelUrlBirthDay=panelUrlBirthDay.replaceAll("Description",callDescriptionBirthDay);
		            		 //String urls="http://103.255.100.77/api/send_transactional_sms.php?username=812y16&msg_token=Mh7&sender_id=MKFITC&message="+Description+"&mobile="+mobileNumbers;
	                			String urls=panelUrlBirthDay; //"http://103.255.100.77/api/send_transactional_sms.php?username=812y16&msg_token=Mh7&sender_id=MKFITC&message=Description&mobile=mobileMsgList";
	                			System.out.println("BirthDayUrl: "+urls);  
	                			URL url = new URL(urls);
		            		   InputStream is = url.openStream();
                			}
                		}
                		/****end*****/
                	}
        	}
        	else
        	{
        		if(compareDate.equals(logToDayDate+""))
        		{
        				
        		}
        		else
        		{
        		compareDate=String.valueOf(logToDayDate);
        		 /****************Attendance Finger ScanMachine**************/
        		//new AttendanceFingerSystemDao().insertAttendanceFingerGetFromMsAccess(); // if have ScanMachine to open line and check also table have Ms-Access entry
        		/*********************/
        		
        			if(logEntryb==null)
                	{
        				System.out.println(" Second Time No Record Found Then Check");
				/****************DATA BASE BACKUP**************/
        		//new AutoMySqlBackUpForAbc().backupDatabaseAbc("localhost", "3306", "root", "root", "abcDbName", "D:/AbcDataBaseBackUp/", "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump.exe");
        		// localhost,Port Number,User,Password,DataBaseName,Destination Path,Source Path Of mysqldump.exe	
				/****************End BackUp**************/
        				String mobileBirthDay=(String)new SentSmsDao().getClientTodayBirthDayMobileString();
                		
                		logbean.setLogDate(logToDayDate);
                		logbean.setLogStatus(null);
                		logbean.setLogType(null);
                		boolean b = new LogEntryDao().insertLogEntry(logbean);
 		        		/****************SMS Birtday**************/
                		if(!Strings.isNullOrEmpty(mobileBirthDay))
                		{
                			if(!Strings.isNullOrEmpty(birthDayWish))
                			{callDescriptionBirthDay = null;
                				callDescriptionBirthDay =birthDayWish;
                				callDescriptionBirthDay=callDescriptionBirthDay.replaceAll(" ", "%20");
	                			callDescriptionBirthDay=callDescriptionBirthDay.replaceAll("[\n\r]", " . ");
	                			callDescriptionBirthDay=callDescriptionBirthDay.replaceAll(" ", "");
	                			panelUrlBirthDay=panelUrlBirthDay.replaceAll("mobileMsgList",mobileBirthDay);
	                			panelUrlBirthDay=panelUrlBirthDay.replaceAll("Description",callDescriptionBirthDay);
		            		 //String urls="http://103.255.100.77/api/send_transactional_sms.php?username=812y16&msg_token=Mh7&sender_id=MKFITC&message="+Description+"&mobile="+mobileNumbers;
	                			String urls=panelUrlBirthDay; //"http://103.255.100.77/api/send_transactional_sms.php?username=812y16&msg_token=Mh7&sender_id=MKFITC&message=Description&mobile=mobileMsgList";
	                			System.out.println("BirthDayUrl: "+urls);  
	                			URL url = new URL(urls);
		            		   InputStream is = url.openStream();
                			}
                		}
                		/****end*****/
                	}else
                	{
                		//System.out.println(" record found"+logEntryb.getLogDate());
                	}
        		}
        	}
            if(employee == null)
			{
	        	response.sendRedirect("login.jsp");
			}
	        else
	        {	if(employee.getEmployeeDepartment().equals("admin"))
	        	{
	        		HttpSession session = request.getSession(true);
					session.setAttribute("employeeId", employee.getEmployeeId());
					session.setAttribute("department", employee.getEmployeeDepartment());
					session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
					RequestDispatcher rd=request.getRequestDispatcher("index");
		            rd.forward(request, response);
		        	//response.sendRedirect("index.jsp");
	        	}
	        	if(employee.getEmployeeDepartment().equals("demo"))
	        	{
	        		HttpSession session = request.getSession(true);
					session.setAttribute("employeeId", employee.getEmployeeId());
					session.setAttribute("department", "admin");///*employee.getEmployeeDepartment()*/
					session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
					RequestDispatcher rd=request.getRequestDispatcher("index");
		            rd.forward(request, response);
		        }
	        }
	    }catch(MalformedURLException malUrl)
        {		System.out.println(" MalformedURLException Msg Url Wrong must for mail ");
        	malUrl.printStackTrace();
        	try {
				if (employee == null) {
					response.sendRedirect("login.jsp");
				} else {
					if (employee.getEmployeeDepartment().equals("admin")) {
						HttpSession session = request.getSession(true);
						session.setAttribute("employeeId",employee.getEmployeeId());
						session.setAttribute("department",employee.getEmployeeDepartment());
						session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
						RequestDispatcher rd = request.getRequestDispatcher("index");
						rd.forward(request, response);
					}
					if(employee.getEmployeeDepartment().equals("demo"))
		        	{
		        		HttpSession session = request.getSession(true);
						session.setAttribute("employeeId", employee.getEmployeeId());
						session.setAttribute("department", "admin");///*employee.getEmployeeDepartment()*/
						session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
			        	RequestDispatcher rd=request.getRequestDispatcher("index");
			            rd.forward(request, response);
			    	}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        catch(SocketException st)
        {
				System.out.println(" SocketException:When Url Wrong");
        	st.printStackTrace();
			try {
				if (employee == null) {
					response.sendRedirect("login.jsp");
				} else {
					if (employee.getEmployeeDepartment().equals("admin")) {
						HttpSession session = request.getSession(true);
						session.setAttribute("employeeId",employee.getEmployeeId());
						session.setAttribute("department",employee.getEmployeeDepartment());
						session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
						RequestDispatcher rd = request.getRequestDispatcher("index");
						rd.forward(request, response);
					}
					if(employee.getEmployeeDepartment().equals("demo"))
		        	{
		        		HttpSession session = request.getSession(true);
						session.setAttribute("employeeId", employee.getEmployeeId());
						session.setAttribute("department", "admin");///*employee.getEmployeeDepartment()*/
						session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
			        	RequestDispatcher rd=request.getRequestDispatcher("index");
			            rd.forward(request, response);
			    	}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        catch(UnknownHostException uhe)
		{
				System.out.println(" UnknownHostException When Net Speed very slow ");
        	uhe.printStackTrace();
        	try {
        	if (employee == null) 
			{
				response.sendRedirect("login.jsp");
			} 
			else 
			{
				if (employee.getEmployeeDepartment().equals("admin")) 
				{
					HttpSession session = request.getSession(true);
					session.setAttribute("employeeId",employee.getEmployeeId());
					session.setAttribute("department",employee.getEmployeeDepartment());
					session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
					RequestDispatcher rd = request.getRequestDispatcher("index");
					rd.forward(request, response);
				}
				if(employee.getEmployeeDepartment().equals("demo"))
	        	{
	        		HttpSession session = request.getSession(true);
					session.setAttribute("employeeId", employee.getEmployeeId());
					session.setAttribute("department", "admin");///*employee.getEmployeeDepartment()*/
					session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
		        	RequestDispatcher rd=request.getRequestDispatcher("index");
		            rd.forward(request, response);
		    	}
			}
		}
        catch (Exception e) 
        {
			e.printStackTrace();
		}
		}
        catch(InterruptedIOException ioe)
        {
			System.out.println("InterruptedIOException:=> Remote Host Time Out During read Operation");
			ioe.printStackTrace();
			try {
        		
				if (employee == null) 
				{
					response.sendRedirect("login.jsp");
				} 
				else 
				{
					if (employee.getEmployeeDepartment().equals("admin")) 
					{
						HttpSession session = request.getSession(true);
						session.setAttribute("employeeId",employee.getEmployeeId());
						session.setAttribute("department",employee.getEmployeeDepartment());
						session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
						RequestDispatcher rd = request.getRequestDispatcher("index");
						rd.forward(request, response);
					}
					if(employee.getEmployeeDepartment().equals("demo"))
		        	{
		        		HttpSession session = request.getSession(true);
						session.setAttribute("employeeId", employee.getEmployeeId());
						session.setAttribute("department", "admin");///*employee.getEmployeeDepartment()*/
						session.setAttribute("empSessionFirstLastName", employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName());
			        	RequestDispatcher rd=request.getRequestDispatcher("index");
			            rd.forward(request, response);
			    	}
				}
			}
	        catch (Exception e) 
	        {
				e.printStackTrace();
			}
        }
        catch (Exception e)
        {
        	System.out.println("Login Exception From Login Servlet Due To Wrong Entry And Details ");
	        e.printStackTrace();
	        response.sendRedirect("login.jsp");
        }
	}
}
