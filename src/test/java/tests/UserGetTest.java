package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test checks get information user without auth")
    @DisplayName("Test negative get information user")
    public void testGetUserDataNotAuth(){

        Response responseUserData = apiCoreRequests.
                makeGetRequestWithoutParams("https://playground.learnqa.ru/api/user/2");//пользователь не авторизован
        String[] expectedNotFields={"firstName", "lastName","email"};
        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasNotFields(responseUserData,expectedNotFields);
    }
    @Test
    @Description("This test checks get information after auth")
    @DisplayName("Test positive get information user")
    public void testGetUserDetailsAuthAsSameUser(){ //авторизуемся и проверяем наличие полей в ответе
         Map<String,String> authData = new HashMap<>();
         authData.put("email","vinkotov@example.com");
         authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
         String header = this.getHeader(responseGetAuth,"x-csrf-token");
         String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseUserData = apiCoreRequests.
                makeGetRequest("https://playground.learnqa.ru/api/user/2",header,cookie);
        String[] expectedFields={"username", "firstName", "lastName","email"};
        Assertions.asserJsonHasFields(responseUserData,expectedFields);

    }

    @Test
    @Description("This test checks get information user with different id")
    @DisplayName("Test negative get information user")
    public void testGetUserDetailsAuthAsNotSameUser(){ //авторизуемся и пытаемся получить данные другого пользователя
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");
        Response responseGetAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/login",authData);
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseUserData = apiCoreRequests.
                makeGetRequest("https://playground.learnqa.ru/api/user/3",header,cookie);
        String[] expectedNotFields={"firstName", "lastName","email"};
        Assertions.assertJsonHasNotFields(responseUserData,expectedNotFields);

    }
}
