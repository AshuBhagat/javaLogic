package com.service;
import java.sql.*;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
public class Myconnection 
{
    private static Connection conn;
    static
    {
            try
            {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/databaseName?useUnicode=true&characterEncoding=UTF-8","root","root");
            }
             catch(CommunicationsException comm)
            {
            	System.out.println("Communications link failure Check MySql Service To Start Again CONTACT OT CREATOWEB");
            }
            catch(NullPointerException n)
	    	{
	    		System.out.println("Communications link get NULLPointerExceptions failure Check MySql Service To Start Again CONTACT OT CREATOWEB");
	    	}
            catch(Exception e)
            {
                    e.printStackTrace();
            }
    }
    public static Connection getConnection() throws SQLException
    {
        if(con.isClosed())
        {
        	conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/databaseName?useUnicode=true&characterEncoding=UTF-8","root","root");
        }
        return con;
    }
}
