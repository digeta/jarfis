/**
 * 
 */
package jarfis;

import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.PreparedStatement;


/**
 * @author I
 *
 */
public class jarfisMain {
	
	static boolean dbHata = false;	
	static ServerSocket srvSocket;	
	static ServerSocket httpSocket;
	static Connection sqlConn = null;	
	String conStr = "jdbc:sqlserver://10.1.16.30:1433;databaseName=JARFIS;user=jarfis;password=147852369";
	//For MySQL
	//String url = "jdbc:mysql://10.200.24.95:3306/jarfis";
	String url = "jdbc:mysql://localhost/jarfis";
	String user = "jarfis";
	String pass = "sspspsfdp";
	
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void Run() {
				System.out.println("la noli");
				onDestroy();
			}
		});
		(new jarfisMain()).Initialize(args);
		try
		{
			//(new jarfisReport()).buildReport();
			/*
	           DECreateDynamicTable de = new DECreateDynamicTable();
	           List al = new ArrayList();
	           al.add("OFFICECODE");
	           al.add("CITY");
	           al.add("COUNTRY");
	           de.buildReport((ArrayList) al, "From Offices" );
	        */		
		}		
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	
	private void Initialize(String[] args)
	{
		InitializeDatabase();
		InitializeSocket(Integer.parseInt(args[0]));
	}
	
	
	private void InitializeDatabase()
	{
		try
		{
			sqlConn = DriverManager.getConnection(url, user, pass);
			dbHata = false;
			
			/*// For SQLite
			File directory =  new File ("database");
			File dbFile = null;
			boolean fileFound = false;
			
			for ( File file :  directory.listFiles() )
			{
				if ( file.getName().equals("jarfis.sqlite") )
				{
					fileFound = true;
					dbFile = file;
					break;
				}
			}
			
			if ( fileFound )
			{
				System.out.println("Database file found. Proceeding..");
				Class.forName("org.sqlite.JDBC");
				sqlConn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
				sqlConn.setAutoCommit(false);				
			}
			else
			{
				System.out.println("Database file not found. Terminating..");
				System.exit(0);
			}
			*/
		}
		catch (Exception ex)
		{
			System.out.println("Database Init Error!");
			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
			dbHata = true;
		}
	}
	
	
	private void InitializeSocket(int port)
	{
		try
		{
			srvSocket = new ServerSocket(port);						
			httpSocket = new ServerSocket(5000, 0, InetAddress.getByName("0.0.0.0"));
			
			Date today = new Date();
			System.out.println(String.format("%tc | Socket Opened on %s:%s \nJarfis is Online :)\n", 
					today, srvSocket.getInetAddress().getHostName(), String.valueOf(srvSocket.getLocalPort())
			));
			
			//System.out.print("Socket Opened on > " + srvSocket.getInetAddress().getHostName() + ":" + srvSocket.getLocalPort() + " |  Listening for Clients.\n");
			(new Thread(new ListenForClients())).start();
			(new Thread(new ListenForHttp())).start();
		}
		catch (Exception ex)
		{
			System.out.println("Socket Init Error: " + port);
			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
		    System.exit(0);			
		}		
	}
	
	
	private static void onDestroy() { 
		try
		{
			srvSocket.close();
			sqlConn.close();
			System.out.println("Terminating..");
		}
		catch (Exception ex)
		{
			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + "\n");
		    System.exit(0);			
		}
	}
	

	private static class ListenForHttp implements Runnable
	{
		private ListenForHttp()
		{
			
		}

		@Override
		public void run() 
		{
            try
            {
                while ( true )
                {
                	Socket connected = httpSocket.accept();
                    System.out.print("Http Connection Established. Starting Client Thread.\n");                	
                	(new jarfisHttp(connected)).start();
                }
            }
            catch ( Exception ex )
            {
                System.out.println("Socket Error : " + ex.getMessage());
    			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
    		    System.exit(0);                
            }
		}
		
	}

	
    private static class ListenForClients implements Runnable 
    {
        //InputStream in;
        
        private ListenForClients ()
        {
            //this.in = in;
        }
        
        public void run ()
        {
            try
            {
                while ( true )
                {
                	Socket connSocket = srvSocket.accept();
                    System.out.print("Connection Established. Starting Client Thread.\n");                	
                	(new Thread(new HandleClientComm(connSocket))).start();
                }
            }
            catch ( Exception ex )
            {
                System.out.println("Socket Error : " + ex.getMessage());
    			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
    		    System.exit(0);                
            }            
        }
    }
    

    private static class HandleClientComm implements Runnable 
    {
        Socket socket;
        
        //int dataReceived;
        String dataSend;         
        
        //byte[] message = new byte[4096];
        //char[] charBuffer = new char[4096];
        //int bytesRead;
        
        String deviceId = "";
        String kartId = "";
        
        private HandleClientComm (Socket socket)
        {
            this.socket = socket;            
        }
        
        public void run ()
        {        	
            try
            {
            	/*
        		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        		dataReceived  = inFromClient.read(charBuffer, 0, 4096);
        		System.out.println(new String(charBuffer));
        		String[] dataStr = (new String(charBuffer)).split(",");
        
        		kartBul(dataStr);
        		*/

            	CharBuffer cbuff = CharBuffer.allocate(4096);
            	while (true)
            	{
            		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            		/*
            		dataReceived  = inFromClient.read(charBuffer, 0, 4096);
            		System.out.println("Gelen Veri : " + new String(charBuffer));
            		String[] dataStr = (new String(charBuffer)).split(",");
            		*/
            		//dataReceived  = inFromClient.read(cbuff);
            		
            		inFromClient.read(cbuff);
            		cbuff.flip();
            		if ( cbuff.length() > 1 )
            		{	            		
	            		String gelenVeri = cbuff.toString();
	            		//System.out.println(cbuff.length());
	            		System.out.println(gelenVeri + " | " + dateFormat.format(new Date()));
	            		String[] dataStr = gelenVeri.split(",");            		            		
	            		
	            		// Gelen data for example : MCR02-3EAB01,UID=xxxxxx , substring retrieves the number after 'UID='
	            		if ( dataStr.length >= 2 )
	            		{
	            		
		            		deviceId = dataStr[0];
		            		kartId = dataStr[1].trim();
		            		
		            		if ( kartId.length() > 4 )
		            		{
			            		kartId = kartId.substring(4);
			            	
			                    if ( kartId.matches("[0-9]+") && ( kartId.length() <= String.valueOf(Long.MAX_VALUE).length() ) )
			                    {
				            		Long kartLong = Long.parseLong(kartId);
				            		
				            		String kartHex = Long.toHexString(kartLong).toUpperCase();
				            		String kartKimlik = "";
				            		
				            		//System.out.println("KARTID : " + kartId);
				  
				                    for (int i = 6; i > -1; i -= 2){               
				                        kartKimlik += kartHex.substring(i, i+2);                    
				                    }                    
				                    
				                    Long kartId2 = Long.parseLong(kartKimlik,16);
				                    
				                	if ( dbHata ) 
				                	{
				                		poke(deviceId + ",BUZZER;200;2");
				                		System.out.println("Database Error! | " + dateFormat.format(new Date()));
				                	}
				                	else
				                	{            		
					            		if ( kartBul(kartId2) )
					            		{
					            			poke(deviceId + ",BUZZER;50;2,ROLE1=200,LCDCLR,LCDSET;30;30;1;TESTING..");
					                		Log(kartId2, true);
					                		System.out.println("Card Id is exist : " + kartId2 + " | " + dateFormat.format(new Date()) + "\n");
					            		}
					            		else
					            		{
					            			poke(deviceId + ",BUZZER;300;1");
					            			Log(kartId2, false);
					            			System.out.println("Card Id is not exist : " + kartId2 + " | " + dateFormat.format(new Date()) + "\n");
					            		}
				                	}
			                    }
			                    else
			                    {
			                    	System.out.println("Invalid Card\n");
			                    }
		            		}
		            		
	            		}
            		}
            		cbuff.clear();
            	}            	
            }
            catch ( Exception ex )
            {
                System.out.println("Error in da Thread : " + ex.getMessage());
    			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + " | " + dateFormat.format(new Date()) + "\n");
    		    System.exit(0);                
            }            
        }
        
        
        private void poke(String data) 
        {
        	try
        	{
        		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        		
        		dataSend = data;
        		
                byte[] buffer = dataSend.getBytes();
                outToClient.write(buffer);
        	}        	
        	catch (Exception ex)
        	{
                System.out.println("Device Error : " + data);
    			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
        	}
        }                
    }    
    
    
    private static boolean kartBul(Long data)
    {
    	ResultSet results = null;
		boolean relayOn = false;

    	try
    	{
    		String sqlString = "SELECT 1 FROM USERS WHERE KARTID = ?";
    		
    		PreparedStatement statement = null;
    		statement = (PreparedStatement) sqlConn.prepareStatement(sqlString);
    		statement.setString(1, String.valueOf(data));
    		results = statement.executeQuery();
    		/*
    		String _ad = "";
    		String _soyad = "";
    		String _kartId = "";
    		String _birim = "";
    		*/      		
    		while ( results.next() )
    		{
    			relayOn = true;      			
    			/*
    			_kartId = results.getString("KARTID");
    			_ad = results.getString("AD");
    			_soyad = results.getString("SOYAD");
    			_birim = results.getString("BIRIM");
    			*/
    		}
    		/*
    		System.out.println(_kartId);
    		System.out.println(_ad);
    		System.out.println(_soyad);
    		System.out.println(_birim);
    		*/
    		results.close();
    		statement.close();
    	}
    	catch (Exception ex)
    	{
    		relayOn = false;
            System.out.println("Card Searcher Error : " + data);        		
			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
		    System.exit(0);    		
    	}
    	return relayOn;
    }
    
    
    private static void Log(Long kartId, boolean success)
    {
    	try
    	{
    		String sqlString = "INSERT INTO LOG (KARTID, BASARI) VALUES (?, ?)";
    		    		
    		PreparedStatement statement = null;
    		statement = (PreparedStatement) sqlConn.prepareStatement(sqlString);
    		statement.setString(1, String.valueOf(kartId));
    		
    		int suxxess = 0;
    		if ( success )
    		{
    			suxxess = 1;
    		}
    		
    		statement.setString(2, String.valueOf(suxxess));
    		statement.executeUpdate();
    		statement.close();
    	}
    	catch (Exception ex)
    	{
            System.out.println("Log Error : " + kartId);        		
			System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
		    System.exit(0);    		
    	}        	
    }
}

