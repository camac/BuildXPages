package com.gregorbyte.designer.headless.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DesignerBuildServlet extends HttpServlet {

	private static final long serialVersionUID = -3018649421819814658L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("text/plain");
		resp.getWriter().write("Hello from Designer");		
		
	}

	
	
}
