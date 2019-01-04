package jarfis;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class jarfisHttp extends Thread{
	static final String HTML_START =
			"<html>" +
			"<title>HTTP Server in java</title>" +
			"<body>";

			static final String HTML_END =
			"</body>" +
			"</html>";

			Socket connectedClient = null;
			BufferedReader inFromClient = null;
			DataOutputStream outToClient = null;

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			public jarfisHttp(Socket client) {
			connectedClient = client;
			}

			public void run() {

			try {

			System.out.println( "The Client "+
			connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");

			inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream()));
			outToClient = new DataOutputStream(connectedClient.getOutputStream());

			String requestString = inFromClient.readLine();
			String headerLine = requestString;

			StringTokenizer tokenizer = new StringTokenizer(headerLine);
			String httpMethod = tokenizer.nextToken();
			String httpQueryString = tokenizer.nextToken();

			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append("<b> Welcome to Jarfis Control Panel.... </b><BR>");
			//responseBuffer.append("The HTTP Client request is ....<BR>");

			System.out.println("The HTTP request string is ....");
			
			/*
			while (inFromClient.ready())
			  {
			    // Read the HTTP complete HTTP Query				
			    responseBuffer.append(requestString + "<BR>");
			    System.out.println(requestString);			    
			    requestString = inFromClient.readLine();
			  }
			*/
			
			if (httpMethod.equals("GET")) {
				while (inFromClient.ready())
				  {
				    // Read the HTTP complete HTTP Query				
				    responseBuffer.append(requestString + "<BR>");
				    System.out.println(requestString);			    
				    requestString = inFromClient.readLine();
				  }
				
				if (httpQueryString.equals("/")) {
				 // The default home page
					
					responseBuffer.delete(0, responseBuffer.length());
					responseBuffer.append("<div align=\"center\"><h1><b>Jarfis Control Panel</b></h1><br><br>");
					responseBuffer.append("<form action=\"verify\" enctype=\"multipart/form-data\" method=\"post\">");
					responseBuffer.append("User Name:<br>");
					responseBuffer.append("<input type=\"text\" name=\"username\"><br><br>");
					responseBuffer.append("Password:<br>");
					responseBuffer.append("<input type=\"password\" name=\"password\"><br><br>");
					responseBuffer.append("<input type=\"submit\" name=\"submit\" value=\"Login\">");
					responseBuffer.append("</form></div>");
				sendResponse(200, responseBuffer.toString(), false, false);
				} else {
					System.out.println(">>>>" + httpQueryString.substring(0, 6));
					if ((httpQueryString.substring(0, 6)).equals("verify")) {						
						responseBuffer.delete(0, responseBuffer.length());
						responseBuffer.append("Login Successful");
						sendResponse(200, responseBuffer.toString(), false, false);
					} else {
						//This is interpreted as a file name
						String fileName = httpQueryString.replaceFirst("/", "");					
						fileName = URLDecoder.decode(fileName,"UTF-8");
						
						if (new File(fileName).isFile()){
							sendResponse(200, fileName, true, false);
						}
						else {
							sendResponse(404, "<b>The Requested resource not found ...." +
									"Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>", false, false);
						}
					}
				}
			}
			else {				
				if (httpMethod.equals("POST")) {
					while (inFromClient.ready())
					  {
					    // Read the HTTP complete HTTP Query				
					    responseBuffer.append(requestString + "<BR>");
					    System.out.println(requestString);			    
					    requestString = inFromClient.readLine();
					  }
					
					if ( httpQueryString.substring(0, 7).trim().equals("/verify"))
					{
						int start = -1;
						int end = -1;
						String orjStr = "";
						String str = "";
						String remains = "";
						String _username = "";
						String _password = "";
						
						orjStr = responseBuffer.toString();
						str = responseBuffer.toString().toLowerCase();					
							start = str.indexOf("name=\"username\"<br><br>");					
							remains = str.substring(start+23);
							end = remains.indexOf("<br>-");
						
						if (start != -1 && end != -1)
						{
							_username = orjStr.substring(start+23,start+23+end).trim();
							System.out.println(_username);						
						}
						
							start = str.indexOf("name=\"password\"<br><br>");					
							remains = str.substring(start+23);
							end = remains.indexOf("<br>-");

						if (start != -1 && end != -1)
						{
							_password = orjStr.substring(start+23,start+23+end).trim();
							System.out.println(_password);						
						}					
						
						if (_username.equals("admin") && _password.equals("m1f4r3"))
						{
							sendResponse(200, "", false, true);
							/*
							String fileName = "rapor.pdf";					
							fileName = URLDecoder.decode(fileName,"UTF-8");
							
							if (new File(fileName).isFile()){
								sendResponse(200, fileName, true, false);
							}
							else {
								sendResponse(404, "<b>The Requested resource not found ....</b>", false, false);
							}
							*/						
						} else {
							sendResponse(401, "<div align=\"center\"><h1><b>Unauthorized!</b></h1></div>", false, false);
						}
					}
					
					System.out.println(">>>>" + httpQueryString.substring(0, 7));
					
					if ( httpQueryString.substring(0, 7).trim().equals("/cpanel"))
					{
						(new jarfisReport()).buildReport();
						
						responseBuffer.delete(0, responseBuffer.length());
						responseBuffer.append("<html><head><title></title></head><body>");
						responseBuffer.append("<p><span style=\"font-size:18px;\"><strong>");
						responseBuffer.append("<span style=\"font-family:tahoma,geneva,sans-serif;\">");
						responseBuffer.append("Jarfis Control Panel</span></strong></span></p><hr />");
						responseBuffer.append("<div style=\"clear:both\">");
						responseBuffer.append("<div style=\"float:left\">");
						responseBuffer.append("<form action=\"cpanel\" enctype=\"multipart/form-data\" method=\"post\">");
						responseBuffer.append("<input name=\"btnCreateRpt\" type=\"submit\" value=\"Rapor Olustur\" />");
						responseBuffer.append("</form>");
						responseBuffer.append("</div>");
						responseBuffer.append("<div style=\"float:left; margin-left:10px;\">");
						responseBuffer.append("<a href=\"rapor.pdf\">rapor.pdf" + " | " + dateFormat.format(new Date()) +  "</a>");
						responseBuffer.append("</div>");
						responseBuffer.append("</div>");
						responseBuffer.append("</body></html>");
						
						sendResponse(200, responseBuffer.toString(), false, false);
					}
					
				} else {
					sendResponse(404, "<b>The Requested resource not found ...." +
					"Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>", false, false);
				}
			}
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
			}

			public void sendResponse (int statusCode, String responseString, boolean isFile, boolean isCpl) throws Exception {
			String cpl_htmlHeader = "<html><head><title></title></head><body>";
			
			String cpl_htmlBodyWelcome = "<p><span style=\"font-size:18px;\"><strong>"
					+ "<span style=\"font-family:tahoma,geneva,sans-serif;\">Jarfis Control Panel</span></strong></span></p><hr />";
			
			String cpl_htmlBody = "<div style=\"clear:both\">"
					+ "<div style=\"float:left\">"
					+ "<form action=\"cpanel\" enctype=\"multipart/form-data\" method=\"post\">"
					+ "<input name=\"btnCreateRpt\" type=\"submit\" value=\"Rapor Olustur\" />"
					+ "</form>"
					+ "</div>"
					+ "<div style=\"float:left; margin-left:10px;\">"
					+ "<a href=\"#\">test...</a>"
					+ "</div>"
					+ "</div>";
			
			String cpl_htmlFooter = "</body></html>";
				
			String statusLine = null;
			String serverdetails = "Server: Jarfis HTTPServer";
			String contentLengthLine = null;
			String fileName = null;
			String contentTypeLine = "Content-Type: text/html" + "\r\n";
			FileInputStream fin = null;

			if (statusCode == 200)
				statusLine = "HTTP/1.1 200 OK" + "\r\n";
			else
				statusLine = "HTTP/1.1 404 Not Found" + "\r\n";

			if (isFile) {
				fileName = responseString;
				fin = new FileInputStream(fileName);
				contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
					if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
						//contentTypeLine = "Content-Type: \r\n";
						contentTypeLine = "Content-Type: application/pdf\r\n";
			}
			else {
				if (isCpl)
				{
					responseString = cpl_htmlHeader + cpl_htmlBodyWelcome + cpl_htmlBody + cpl_htmlFooter;
				}
				
				responseString = jarfisHttp.HTML_START + responseString + jarfisHttp.HTML_END;
				contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";
			}

			outToClient.writeBytes(statusLine);
			outToClient.writeBytes(serverdetails);
			outToClient.writeBytes(contentTypeLine);
			outToClient.writeBytes(contentLengthLine);
			outToClient.writeBytes("Cache-Control: no-cache");
			outToClient.writeBytes("Connection: close\r\n");
			outToClient.writeBytes("\r\n");

			if (isFile) sendFile(fin, outToClient);
			else outToClient.writeBytes(responseString);

			outToClient.close();
			}

			public void sendFile (FileInputStream fin, DataOutputStream out) throws Exception {
			byte[] buffer = new byte[1024] ;
			int bytesRead;

			while ((bytesRead = fin.read(buffer)) != -1 ) {
			out.write(buffer, 0, bytesRead);
			}
			fin.close();
			}

			/*
			public static void main (String args[]) throws Exception {

			ServerSocket Server = new ServerSocket (5000, 10, InetAddress.getByName("127.0.0.1"));
			System.out.println ("TCPServer Waiting for client on port 5000");

			while(true) {
			Socket connected = Server.accept();
			    (new jarfisHttp(connected)).start();
			}
			}
			*/
}
