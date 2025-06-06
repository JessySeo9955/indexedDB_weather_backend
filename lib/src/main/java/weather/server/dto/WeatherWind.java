package weather.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherWind {
	  private float speed;
	  private float deg;


	 // Getter Methods 

	  public float getSpeed() {
	    return speed;
	  }

	  public float getDeg() {
	    return deg;
	  }

	 // Setter Methods 

	  public void setSpeed( float speed ) {
	    this.speed = speed;
	  }

	  public void setDeg( float deg ) {
	    this.deg = deg;
	  }
}
