package client;

import configReader.ConfigManager;
import constants.AuthType;
import custom_exception.FrameworkException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class RestClient {

    private ResponseSpecification responseSpec200 = expect().statusCode(200);
    private ResponseSpecification responseSpec200or201 = expect().statusCode(anyOf(equalTo(200), equalTo(201)));
    private ResponseSpecification responseSpec200or404 = expect().statusCode(anyOf(equalTo(200), equalTo(404)));
    private ResponseSpecification responseSpec201 = expect().statusCode(201);
    private ResponseSpecification responseSpec204 = expect().statusCode(204);
    private ResponseSpecification responseSpec400 = expect().statusCode(400);
    private ResponseSpecification responseSpec401 = expect().statusCode(401);
    private ResponseSpecification responseSpec404 = expect().statusCode(404);
    private ResponseSpecification responseSpec422 = expect().statusCode(422);
    private ResponseSpecification responseSpec500 = expect().statusCode(500);

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

    //All CURD Operations

    public Response get(String baseUrl, String endpoint, Map<String, String> queryParams, Map<String, String> pathParams,
                        AuthType authType, ContentType contentType){
        RequestSpecification requestSpecification = setUpAuthAndContentType(baseUrl, authType, contentType);
        applyParams(requestSpecification,queryParams, pathParams);
       Response response =  requestSpecification.get(endpoint).then().spec(responseSpec200).extract().response();
       return response;


    }

    private RequestSpecification setUpAuthAndContentType(String baseUrl, AuthType authType, ContentType contentType){
        return setUpRequest(baseUrl, authType, contentType);
    }

    private void applyParams(RequestSpecification request, Map<String, String> queryParams, Map<String, String> pathParams) {
        if(queryParams!=null) {
            request.queryParams(queryParams);
        }
        if(pathParams!=null) {
            request.pathParams(pathParams);
        }
    }
}
