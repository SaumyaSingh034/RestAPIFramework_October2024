package client;

import configReader.ConfigManager;
import constants.AuthType;
import custom_exception.FrameworkException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;

public class RestClient {

    private RequestSpecification setUpRequest(String baseUrl, AuthType authType, ContentType contentType){
        RequestSpecification requestSpecification = RestAssured.given().log().all()
                .baseUri(baseUrl).contentType(contentType).accept(contentType);

        switch(authType){
            case BEARER_TOKEN:
                requestSpecification.header("Authorization", "Bearer "+ ConfigManager.get("bearerToken"));
                break;
            case NO_AUTH:
                System.out.println("Auth is not required .....");
                break;
            case OAUTH2:
                requestSpecification.header("Authorization", generateOAuth2Token());
                break;
            case BASIC_AUTH:
                requestSpecification.header("Authorization", generateBasicAuthToken());
                break;
            case API_KEY:
                requestSpecification.header("x-api-key", ConfigManager.get("apiKey"));
                break;
            case CONTACTS_BEARER_TOKEN:
                requestSpecification.header("Authorization", "Bearer "+ConfigManager.get("contacts_bearer_Token"));
                break;
            default:
                System.out.println("The Auth is not supported. Please pass the right AUTH Type");
                throw new FrameworkException("NO_AUTH_SUPPORTED");

        }
        return requestSpecification;

    }

    private String generateBasicAuthToken() {
        String credentials = ConfigManager.get("basicUsername")+ ":" +ConfigManager.get("basicPassword");
        return Base64.getEncoder().encodeToString(credentials.getBytes());

    }

    private String generateOAuth2Token() {
        return RestAssured.given()
                .formParam("client_id", ConfigManager.get("clientId"))
                .formParam("client_password", ConfigManager.get("clientSecret"))
                .formParam("grant_type", ConfigManager.get("grantType"))
                .post(ConfigManager.get("tokenUrl"))
                .then()
                .extract()
                .path("access_token");
    }
}
