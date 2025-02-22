import io.restassured.RestAssured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void getJsonHomework4() {
        String locationHeader = "https://playground.learnqa.ru/api/long_redirect";
        int code = 100;
        while (code!=200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(locationHeader)
                    .andReturn();
            locationHeader = response.getHeader("Location");
            code = response.getStatusCode();
            System.out.println(code);
        }
    }
    @Test
    public void longTimeJob() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        response.prettyPrint();
        String token = response.getHeader("token");
        System.out.println(token);
         Response response2 = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        response2.prettyPrint();
    }

    @Test
    public void longTimeJob2() throws InterruptedException {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String token = response.get("token");
        int seconds = response.get("seconds");
        System.out.println(token);
        JsonPath response2 = RestAssured
                .given()
                .queryParam("token",token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String status = response2.get("status");

        System.out.println(status);
        if (status.equals("Job is NOT ready")){
            System.out.println("Status is correct");
            Thread.sleep(seconds*1000);
            JsonPath response3 = RestAssured
                    .given()
                    .queryParam("token",token)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
            String status2 = response3.get("status");
            String result = response3.get("result");
            if (status2.equals("Job is ready") && result!=null){
                System.out.println("Done\n"+status2+"\n"+result);

            }
        }
    }

        private static final String BASE_URL = "https://playground.learnqa.ru/ajax/api/longtime_job";

        @Test
        public void testLongTimeJob() throws InterruptedException {
            // 1. Создание задачи
            JsonPath initialResponse = RestAssured.given().get(BASE_URL).jsonPath();
            String token = initialResponse.getString("token");
            int seconds = initialResponse.getInt("seconds");

            assertNotNull(token, "Token should not be null");
            assertTrue(seconds > 0, "Seconds should be greater than zero");
            System.out.println("Task created with token: " + token + ", will be ready in: " + seconds + " seconds");

            // 2. Запрос до завершения задачи
            JsonPath preCheckResponse = RestAssured.given()
                    .queryParam("token", token)
                    .get(BASE_URL)
                    .jsonPath();

            assertEquals("Job is NOT ready", preCheckResponse.getString("status"), "Status before completion should be 'Job is NOT ready'");
            System.out.println("Checked before completion: Job is NOT ready");

            // 3. Ожидание завершения задачи
            Thread.sleep(seconds * 1000L);

            // 4. Запрос после завершения задачи
            JsonPath finalResponse = RestAssured.given()
                    .queryParam("token", token)
                    .get(BASE_URL)
                    .jsonPath();

            assertEquals("Job is ready", finalResponse.getString("status"), "Status after completion should be 'Job is ready'");
            assertNotNull(finalResponse.getString("result"), "Result should not be null after completion");
            System.out.println("Job completed successfully: " + finalResponse.getString("result"));
        }


}