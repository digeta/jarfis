package jarfis.stocks.model;

import java.util.Date;

public class StatsData {

	  //private Date tarih;
	  private String tarih;
	  private String ad;
	  private String soyad;
	  private String hareket;
	  private String kartid;
	  
	  
/*
	  public Date getTarih() 
	  {
	    return tarih;
	  }
  
	  public void setTarih(Date date) 
	  {
	    this.tarih = date;
	  }	
*/

	  public String getTarih() 
	  {
	    return tarih;
	  }
  
	  public void setTarih(String date) 
	  {
	    this.tarih = date;
	  }
	  
	  
	  
	  public String getAd() 
	  {
	    return ad;
	  }
	  
	  public void setAd(String name) 
	  {
	    this.ad = name;
	  }	  

	  
	  
	  public String getSoyad() 
	  {
	    return soyad;
	  }
	  
	  public void setSoyad(String surname) 
	  {
	    this.soyad = surname;
	  }
	  
	  
	  
	  public String getKartid() 
	  {
	    return kartid;
	  }
	  
	  public void setKartid(String cardid) 
	  {
	    this.kartid = cardid;
	  }
	  
	  
	  
	  public String getHareket() 
	  {
	    return hareket;
	  }

	  public void setHareket(String movement) 
	  {
	    this.hareket = movement;
	  }	  
}
