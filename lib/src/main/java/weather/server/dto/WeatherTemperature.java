package weather.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherTemperature {
	  private double temp;
	  private double feels_like;
	  private double humidity;
	  private String description;
	  private int dt;
	  private int timezone;



	  public double getTemp() {
	    return temp;
	  }

	    // Getters and setters for all fields
	    public double getFeels_like() {
	        return feels_like;
	    }


	  public int getDt() {
	    return dt;
	  }

	  public int getTimezone() {
	    return timezone;
	  }

	  public String getDescription() {
	    return description;
	  }
	  

	  public double getHumidity() {
	    return humidity;
	  }


	  public void setTemp( double temp ) {
	    this.temp = temp;
	  }


	    public void setFeels_like(double feels_like) {
	        this.feels_like = feels_like;
	    }

	  public void setDt( int  dt ) {
	    this.dt = dt;
	  }

	  public void setTimezone( int timezone ) {
	    this.timezone = timezone;
	  }

	  public void setDescription( String description ) {
	    this.description = description;
	  }
	  
		
	  public void setHumidity( double humidity ) {
	    this.humidity = humidity;
	  }

}
