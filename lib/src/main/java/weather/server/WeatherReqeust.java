package weather.server;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import weather.server.dto.ApiResponse;
import weather.server.dto.WeatherTemperature;
import weather.server.dto.WeatherWind;

public class WeatherReqeust implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(event.getBody());

			HttpResponse<String> response = requestWeatherData(json);
			String sendData = "";
			
			if ("hours".equals(getTextFromJson("type", json))) {
				sendData = transformHourlyData(response);
			} else {
				sendData = transformWeatherData(response);
			}

			ApiResponse apiResonse = new ApiResponse(sendData, 200, "success");
	
			return new APIGatewayProxyResponseEvent()
				    .withStatusCode(200)
				    .withBody(mapper.writeValueAsString(apiResonse));
		} catch (Exception e) {
			context.getLogger().log("Error: " + e.getMessage());
			return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody(e.getMessage());
		}
	}

	public HttpResponse<String> requestWeatherData(JsonNode json) throws IOException, InterruptedException {

		// Get API key and URL from the event body
		String api = getTextFromJson("api", json);
		String secretKey = System.getenv("SECRET_KEY");

		api = api + "&appid=" + secretKey;

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(api)).GET().build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	public String transformWeatherData(HttpResponse<String> response) throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode json = objectMapper.readTree(response.body());
		
		return createWeatherSummary(json);
	}
	
	public String createWeatherSummary(JsonNode json) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Extract and parse temperature data
		String mainString = getTextFromJson("main", json);
		String description = json.get("weather").get(0).get("description").asText();

		WeatherTemperature temperature = objectMapper.readValue(mainString, WeatherTemperature.class);
		temperature.setDescription(description);
		temperature.setDt(json.get("dt").asDouble());
		temperature.setTimezone(json.get("timezone").asDouble());

		// Extract and parse wind data
		String windString = getTextFromJson("wind", json);
		WeatherWind wind = objectMapper.readValue(windString, WeatherWind.class);

		// Combine both objects into one response
		ObjectNode combined = objectMapper.createObjectNode();
		combined.set("temperature", objectMapper.valueToTree(temperature)); // Use the temperature object directly
		combined.set("wind", objectMapper.valueToTree(wind)); // Use the wind object directly

		return objectMapper.writeValueAsString(combined);
	}
	
	
	public String transformHourlyData(HttpResponse<String> response) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode listNode = objectMapper.readTree(response.body()).get("list");
		
	    ArrayNode arrayNode = (ArrayNode) listNode;
	    ArrayNode resultArray = objectMapper.createArrayNode(); // JSON array result

	    for (JsonNode item : arrayNode) {
	        String summary = createWeatherSummary(item);
	        JsonNode summaryNode = objectMapper.readTree(summary); // convert String back to JsonNode
	        resultArray.add(summaryNode); // add JSON object to array
	    }

	    return objectMapper.writeValueAsString(resultArray);
	}

	private static String getTextFromJson(String key, JsonNode json) {
		return json.get(key).toString();
	}

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {

	}

}
