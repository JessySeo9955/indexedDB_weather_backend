package weather.server;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import weather.server.dto.ApiResponse;

@ExtendWith(MockitoExtension.class)
public class WeatherReqeustTest {
	
	@Mock
	HttpResponse response;
	
	@Test
	public void transformWeatherDataTest() throws JsonMappingException, JsonProcessingException {
	    WeatherReqeust weather = new WeatherReqeust();
	    
	    // Mock HttpResponse
	    HttpResponse<String> response = mock(HttpResponse.class);
	    
	    // Define the mock response body as the JSON string
	    String jsonbody = "{\"coord\":{\"lon\":-75.7286,\"lat\":45.3359},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\",\"main\":{\"temp\":293.41,\"feels_like\":292.54,\"temp_min\":292.06,\"temp_max\":295.18,\"pressure\":1011,\"humidity\":40,\"sea_level\":1011,\"grnd_level\":1000},\"visibility\":10000,\"wind\":{\"speed\":5.14,\"deg\":310,\"gust\":9.77},\"clouds\":{\"all\":75},\"dt\":1748893896,\"sys\":{\"type\":1,\"id\":872,\"country\":\"CA\",\"sunrise\":1748855864,\"sunset\":1748911470},\"timezone\":-14400,\"id\":5909024,\"name\":\"Britannia\",\"cod\":200}";
	    
	    // Mock response to return the JSON body
	    when(response.body()).thenReturn(jsonbody);
	    
	    // Call the method that processes the JSON
	    String result = weather.transformWeatherData(response);
	    
	    // Validate the result (check if temperature and wind data are present in the response)
	    assertTrue(result.contains("temperature"), "Item should have temperature");
	    assertTrue(result.contains("wind"), "Item should have wind");
	}
	
	@Test
	public void transformHourlyDataTest() throws JsonMappingException, JsonProcessingException {
	    WeatherReqeust weather = new WeatherReqeust();
	    
	    // Mock HttpResponse
	    HttpResponse<String> response = mock(HttpResponse.class);
	    
	    // Define the mock response body as the JSON string
	    // Define the mock response body as the JSON string
	    String jsonbody = "{\"list\":[{\"coord\":{\"lon\":-75.7286,\"lat\":45.3359},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\",\"main\":{\"temp\":293.41,\"feels_like\":292.54,\"temp_min\":292.06,\"temp_max\":295.18,\"pressure\":1011,\"humidity\":40,\"sea_level\":1011,\"grnd_level\":1000},\"visibility\":10000,\"wind\":{\"speed\":5.14,\"deg\":310,\"gust\":9.77},\"clouds\":{\"all\":75},\"dt\":1748893896,\"sys\":{\"type\":1,\"id\":872,\"country\":\"CA\",\"sunrise\":1748855864,\"sunset\":1748911470},\"timezone\":-14400,\"id\":5909024,\"name\":\"Britannia\",\"cod\":200}]}";

	    // Mock response to return the JSON body
	    when(response.body()).thenReturn(jsonbody);
	    
	    // Call the method that processes the JSON
	    String result = weather.transformHourlyData(response);
	    
	    // Optional: Validate JSON structure
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode resultNode = mapper.readTree(result);
	    assertTrue(resultNode.isArray(), "Result should be a JSON array");
	    assertTrue(resultNode.get(0).has("temperature"), "Each item should have temperature");
	    assertTrue(resultNode.get(0).has("wind"), "Each item should have wind");
	    
	    // Validate the result (check if temperature and wind data are present in the response)
	    assertTrue(result.contains("temperature"));
	    assertTrue(result.contains("wind"));
	}
	
	
//	@Test
//	public void testHandleRequestReturnsValidApiResponseFormat() throws Exception {
//	    // Arrange
//	    WeatherReqeust weather = spy(new WeatherReqeust());
//
//	    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
//	    event.setBody("{\"type\":\"hours\", \"api\":\"test\"}");  // Make sure 'type' is "hours"
//	    HttpResponse<String> mockResponse = mock(HttpResponse.class);
//	    when(mockResponse.body()).thenReturn("{}");
//	    doReturn(mockResponse).when(weather).requestWeatherData(any());
//
//	    doReturn("dummyHourlyData").when(weather).transformHourlyData(any());  // Mocking the expected return value for hourly data
//	    doReturn("dummyWeatherData").when(weather).transformWeatherData(any());  // Mocking the expected return value for weather data
//
////	    // Act
//	    weather.handleRequest(event, null);  // The method we're testing
////
////	    // Assert
//	    verify(weather, times(1)).transformHourlyData(any());  // This should be called
////	    verify(weather).transformWeatherData(any());  // This should NOT be called
////	}
//	}
}
