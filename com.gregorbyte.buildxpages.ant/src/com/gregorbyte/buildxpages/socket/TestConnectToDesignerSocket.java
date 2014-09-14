package com.gregorbyte.buildxpages.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TestConnectToDesignerSocket {

	private static final String MSG_TERM = "END.";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			Socket s = new Socket(InetAddress.getLocalHost(), 8098);
			//Socket s = new Socket("localhost", 8098);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			
			for (int i = 0; i < 3; i++) {
				System.out.println(input.readLine());
			}
			
			//out.println("Will you BUILDMEANNSF please?");
			out.println("will you tell me your PROBLEMS?");
			
			String response;
			response = input.readLine();
			
			while (!response.equals(MSG_TERM)) {
				System.out.println(response);			
				response = input.readLine();
			}
				
//			PrintStream output;
//			try {
//				output = new PrintStream(s.getOutputStream());
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			try {
				input.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
