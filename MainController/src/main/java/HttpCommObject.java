import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Map;

public class HttpCommObject {
    private String webURL;
    public HttpCommObject(String webURL) {
        this.webURL = webURL;
    }

    JsonNode GetData(String endPoint){
        HttpResponse<JsonNode> jsonResponse;

        try {
            jsonResponse
                    = Unirest.get(webURL + endPoint)
                    .asJson();
        }catch (UnirestException e){
            return new JsonNode("{\"Error\" : \"" + e.getMessage() +"\"}");
        }

        return jsonResponse.getBody();
    }

    JsonNode GetData(String endPoint, Map<String, Object> query){
        HttpResponse<JsonNode> jsonResponse;

        try {
            jsonResponse
                    = Unirest.get(webURL + endPoint)
                    .queryString(query)
                    .asJson();
        }catch (UnirestException e){
            return new JsonNode("{\"Error\" : \"" + e.getMessage() +"\"}");
        }

        return jsonResponse.getBody();
    }

    JsonNode GetData(String endPoint, Map<String, Object> query, String pathParamKey, String pathParamValue) {
        HttpResponse<JsonNode> jsonResponse;

        try {
            jsonResponse
                    = Unirest.get(webURL + endPoint)
                    .routeParam(pathParamKey, pathParamValue)
                    .queryString(query)
                    .asJson();
        } catch (UnirestException e) {
            return new JsonNode("{\"Error\" : \"" + e.getMessage() + "\"}");
        }

        return jsonResponse.getBody();
    }

    JsonNode PostData(String endPoint, Map<String, Object> query){
        HttpResponse<JsonNode> jsonResponse;

        try {
            jsonResponse
                    = Unirest.post(webURL + endPoint)
                    .queryString(query)
                    .asJson();
        } catch (UnirestException e) {
            return new JsonNode("{\"Error\" : \"" + e.getMessage() + "\"}");
        }

        return jsonResponse.getBody();
    }

    JsonNode DeleteData(String endPoint, Map<String, Object> query, String pathParamKey, String pathParamValue){
        HttpResponse<JsonNode> jsonResponse;
        try {
            jsonResponse
                    = Unirest.delete(webURL + endPoint)
                    .routeParam(pathParamKey, pathParamValue)
                    .queryString(query)
                    .asJson();
        } catch (UnirestException e) {
            return new JsonNode("{\"Error\" : \"" + e.getMessage() + "\"}");
        }
        return jsonResponse.getBody();
    }

}
