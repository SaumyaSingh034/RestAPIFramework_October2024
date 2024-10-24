package reqresAPI;

import api.base.BaseTest;
import constants.AuthType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReqResTest extends BaseTest {

    @Test
    public void getCall(){
        Response response = restClient.get(BASE_URL_REQ_RES, "/api/users?page=2",null,null
        , AuthType.NO_AUTH, ContentType.JSON);
        Assert.assertEquals(response.getStatusCode(),200);
    }
}
