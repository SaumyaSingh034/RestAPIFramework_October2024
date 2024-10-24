package client;

import constants.AuthType;
import custom_exception.FrameworkException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestClient {

    private RequestSpecification setUpRequest(String baseUrl, AuthType authType, ContentType contentType){
        RequestSpecification requestSpecification = RestAssured.given().log().all()
                .baseUri(baseUrl).contentType(contentType).accept(contentType);

        switch(authType){
            case BEARER_TOKEN:
                requestSpecification.header("Authorization", "Bearer ");
                break;
            case NO_AUTH:
                break;
            case OAUTH2:
                break;
            case API_KEY:
                break;
            case CONTACTS_BEARER_TOKEN:
                break;
            default:
                System.out.println("The Auth is not supported. Please pass the right AUTH Type");
                throw new FrameworkException("NO_AUTH_SUPPORTED");

        }
        return requestSpecification;

    }
}
