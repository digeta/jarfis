package jarfis.stocks.daomock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.mysql.jdbc.PreparedStatement;

import jarfis.stocks.model.StatsData;

public class StatsDao {
	static Connection sqlConn = null;
	//For MySQL
	//String url = "jdbc:mysql://10.200.24.95:3306/jarfis";
	String conStr = "jdbc:sqlserver://10.1.16.30:1433;databaseName=JARFIS;user=jarfis;password=147852369";
	String url = "jdbc:mysql://localhost/jarfis";
	String user = "jarfis";
	String pass = "sspspsfdp";
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	
	  public List<StatsData> getStatValues(String company) {
		  //http://www.vogella.com/articles/EclipseBIRT/article.html
		  List<StatsData> history = new ArrayList<StatsData>();
		  
	    	try
	    	{
	    		//sqlConn = DriverManager.getConnection(url, user, pass);
	    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    		sqlConn = DriverManager.getConnection(conStr);
	    		String sqlString = "SELECT L.ID, L.KARTID, P.AD, P.SOYAD, L.BASARI AS HAREKET, L.TARIH FROM PERSONEL AS P INNER JOIN KARTLAR AS K ON P.PERID = K.PERID"
	    				+ " RIGHT OUTER JOIN LOG AS L ON K.KARTID = L.KARTID WHERE P.TEKRARKART = 0 AND P.AKTIF = 1"
	    				+ " ORDER BY L.TARIH DESC";
	    		
	    		SQLServerStatement statement = null;
	    		//PreparedStatement statement = null;
	    		//statement = (PreparedStatement) sqlConn.prepareStatement(sqlString);
	    		statement = (SQLServerStatement) sqlConn.createStatement();	    		
	    		ResultSet results = statement.executeQuery(sqlString);
			    // Ignore the company and always return the data
			    // A real implementation would of course use the company string
			    //history = new ArrayList<StatsData>();
			    // We fake the values, we will return fake value for 01.01.2009 -
			    // 31.01.2009			    
			  	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  	
	    		while ( results.next() )
	    		{
	    			StatsData data = new StatsData();
				  	data.setAd(results.getString("AD"));
				    data.setSoyad(results.getString("SOYAD"));
				    data.setKartid(results.getString("KARTID"));
				    data.setHareket(results.getString("HAREKET"));
				    data.setTarih(results.getString("TARIH"));				    
				    history.add(data);				    
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
	    		System.out.println("Report Data Error");      		
				System.out.println( ex.getClass().getName() + ": " + ex.getMessage() + dateFormat.format(new Date()) + "\n");
			    //System.exit(0);    		
	    	}
		    return history;
		    
		    /*
		    double begin = 2.5;
		    for (int i = 1; i <= 31; i++) {
		      Calendar day = Calendar.getInstance();
		      day.set(Calendar.HOUR, 0);
		      day.set(Calendar.MINUTE, 0);
		      day.set(Calendar.SECOND, 0);
		      day.set(Calendar.MILLISECOND, 0);
		      day.set(Calendar.YEAR, 2009);
		      day.set(Calendar.MONTH, 0);
		      day.set(Calendar.DAY_OF_MONTH, i);
		      */

		      /*
		      data.setOpen(begin);		      
		      double close = Math.round(begin + Math.random() * begin * 0.1);		      
		      data.setClose(close);		      
		      data.setLow(Math.round(Math.min(begin, begin - Math.random() * begin * 0.1)));
		      data.setHigh(Math.round(Math.max(begin, close) + Math.random() * 2));
		      data.setVolume(1000 + (int) (Math.random() * 500));
		      
		      begin = close;
		      data.setDate(day.getTime());
		      */
		    //}
		  }
}
