package weather.server.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class ApiResponse {
    private String data;
    private int status;
    private String message;

    public ApiResponse() {}

    public ApiResponse(String data, int status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}