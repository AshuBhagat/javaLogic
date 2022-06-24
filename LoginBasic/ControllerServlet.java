package com.servlets.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.service.authorise.Authorise;

@SuppressWarnings("serial")
public class ControllerServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
        	Authorise auth = new Authorise(request, response);
        	if (!auth.isValid()) {
                response.sendRedirect("login.jsp");
                return;
            }
            String userPath=null; 
            userPath = request.getServletPath();
            if(userPath.equals("/index")&& auth.isAdminHome())
           {
        	   userPath="indexHomePage";
           }
           if(userPath.equals("/EmployeeHome") && auth.isEmpHome())
           {
        	   userPath="EmployeeHomePage";
           }
           RequestDispatcher rd = request.getRequestDispatcher("/"+userPath+".jsp");
            System.out.println("/"+userPath +".jsp");
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

}
