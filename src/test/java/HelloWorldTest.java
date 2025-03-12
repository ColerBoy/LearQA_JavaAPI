import io.restassured.RestAssured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.LinkedHashMap;
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



        @Test
        public void testLongTimeJob() throws InterruptedException {
            String BASE_URL = "https://playground.learnqa.ru/ajax/api/longtime_job";
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

    @Test
    public void testGetSecretPass() {
        Map<String, String> data = new LinkedHashMap<>();
        String result = "You are NOT authorized";
        String[] passwords = {
                "password", "123456", "123456789", "12345678", "12345", "qwerty", "abc123", "football", "1234567",
                "monkey", "111111", "1234", "1234567890", "letmein", "dragon", "baseball", "sunshine", "iloveyou",
                "trustno1", "princess", "adobe123", "welcome", "login", "admin", "qwerty123", "1q2w3e4r", "master",
                "photoshop", "1qaz2wsx", "qwertyuiop", "ashley", "mustang", "121212", "starwars", "654321", "bailey",
                "access", "flower", "555555", "passw0rd", "shadow", "lovely", "7777777", "michael", "!@#$%^&*",
                "jesus", "password1", "superman", "hello", "charlie", "888888", "696969", "hottie", "freedom",
                "aa123456", "qazwsx", "ninja", "azerty", "loveme", "whatever", "donald", "batman", "zaq1zaq1",
                "000000", "123qwe"
        };

        // Map для хранения паролей (ключ - ID, значение - пароль)
        Map<Integer, String> passwordsMap = new LinkedHashMap<>();

        // Заполняем Map через forEach (с индексами)
        int id=0; // Используем массив, чтобы передавать изменяемую переменную
        for (String password : passwords) {
            passwordsMap.put(id, password);
            id++;
        }
        int i=0;
        while (result.equals("You are NOT authorized") && passwordsMap.get(i) !=null){

            data.put("login", "super_admin");
            data.put("password", passwordsMap.get(i));
            System.out.println(passwordsMap.get(i));
            i++;
            Response checkPassword = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            Map<String, String> cookies = new HashMap<>();
            String responseCookie = checkPassword.getCookie("auth_cookie");
            System.out.println(responseCookie);
            cookies.put("auth_cookie", responseCookie);
            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();
            result = responseForCheck.print();

        }
    }

    @Test
    public void testGetSecretPassHelp() {
        Map<String, String> data = new LinkedHashMap<>();
        String result = "You are NOT authorized";
        String[] passwords = {
                "password", "123456", "123456789", "12345678", "12345", "qwerty", "abc123", "football", "1234567",
                "monkey", "111111", "1234", "1234567890", "letmein", "dragon", "baseball", "sunshine", "iloveyou",
                "trustno1", "princess", "adobe123", "welcome", "login", "admin", "qwerty123", "1q2w3e4r", "master",
                "photoshop", "1qaz2wsx", "qwertyuiop", "ashley", "mustang", "121212", "starwars", "654321", "bailey",
                "access", "flower", "555555", "passw0rd", "shadow", "lovely", "7777777", "michael", "!@#$%^&*",
                "jesus", "password1", "superman", "hello", "charlie", "888888", "696969", "hottie", "freedom",
                "aa123456", "qazwsx", "ninja", "azerty", "loveme", "whatever", "donald", "batman", "zaq1zaq1",
                "000000", "123qwe"
        };

        int i = 0;
        while (result.equals("You are NOT authorized") && i < passwords.length) {
            data.put("login", "super_admin");
            data.put("password", passwords[i]);

            System.out.println("Пробуем пароль: " + passwords[i]);

            Response checkPassword = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = checkPassword.getCookie("auth_cookie");

            if (responseCookie != null) {
                Map<String, String> cookies = new HashMap<>();
                cookies.put("auth_cookie", responseCookie);

                Response responseForCheck = RestAssured
                        .given()
                        .cookies(cookies)
                        .when()
                        .post("https://playground.learnqa.ru/api/check_auth_cookie")
                        .andReturn();

                result = responseForCheck.asString();
            }

            i++;
        }

        System.out.println("Результат: " + result);
    }


}