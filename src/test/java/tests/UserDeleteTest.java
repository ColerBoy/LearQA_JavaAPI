package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import lib.BaseTestCase;
import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    String cookie;
    String token;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test login and tries delete user 2")
    @DisplayName("Test negative delete user")
    public void loginUserAndDelete() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.token = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2",token,cookie);
        Assertions.assertResponceTextEquals(responseDeleteUser,"{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
        Assertions.assertResponceCodeEquals(responseDeleteUser,400);
    }

    @Test
    @Description("This test checks create new user, login and delete user")
    @DisplayName("Test positive changed userData")
    public void createAndDeleteUser(){
        Map<String, String> userData = DataGenerator.getRegistrationData( );

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        String userId = responseCreateAuth.jsonPath().get("id");
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.token = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/"+userId,token,cookie);
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/"+userId,token, cookie);
        Assertions.assertResponceTextEquals(responseUserData,"User not found");
    }
    @Test
    @Description("This test cheks create new user, login for another and delete new user")
    @DisplayName("Test negative delete user")
    public void createAndDeleteOtherUser(){
        Map<String, String> userData = DataGenerator.getRegistrationData( );

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        String userId = responseCreateAuth.jsonPath().get("id");
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.token = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/"+userId,token,cookie);
        Assertions.assertResponceTextEquals(responseDeleteUser,"{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
        Assertions.assertResponceCodeEquals(responseDeleteUser,400);
    }

}
