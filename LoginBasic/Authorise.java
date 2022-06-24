package com.service.authorise;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.service.Myconnection;
import com.model.EmployeeBean;
import java.io.IOException;
import java.sql.*;

public class Authorise {
	private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
	private static Connection con=null;
	private static PreparedStatement ps=null;
	private static ResultSet rs;
	public Authorise(HttpServletRequest request, HttpServletResponse response) 
	{
        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
    }
	public static EmployeeBean checkLogin(String username, String password) throws Exception
	{
		conn=Myconnection.getConnection();
		String qry="SELECT * FROM employeetab WHERE mailid = ? AND password = ?";
		ps=conn.prepareStatement(qry);
		ps.setString(1, username);
		ps.setString(2, password);
		rs=ps.executeQuery();
        if(rs.next())
        {
        	 EmployeeBean employeeName=new EmployeeBean(rs.getInt("employeeId"),rs.getString("firstName"),rs.getString("lastName"),rs.getString("emailId"),
         			rs.getString("department"), rs.getString("picPath"),rs.getInt("salary"), rs.getString("password"));
             return employeeName;
        }
        return null;
	}
	public boolean isValid() throws Exception
	{
		  if (session == null)
		  {
	            return false;
		  }else{
	            return true;
		  }
	}
	public boolean isAdminHome() 
    {
            return session.getAttribute("department").toString().equals("admin");
    }
    public boolean isEmpHome()
    {
            return session.getAttribute("department").toString().equals("EmployeeHome");
    }
    public void logout() throws IOException {

        session.invalidate();
        session = null;
        response.sendRedirect("login.jsp");

    }
}
