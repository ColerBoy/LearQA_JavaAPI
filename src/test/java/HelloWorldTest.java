import io.restassured.RestAssured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void HelloWorldName() {
        System.out.println("Hello World, Максим");
    }

    @Test
    public void testRestAssured() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();
        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }
        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();
        responseForCheck.print();
    }

    @Test
    public void getText() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void getJsonHomework() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String message = response.get("messages[1].message");
        System.out.println(message);
    }


    @Test
    public void getJsonHomework2() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .andReturn();
        response.print();
    }


    @Test
    public void getJsonHomework3() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);

    }
}