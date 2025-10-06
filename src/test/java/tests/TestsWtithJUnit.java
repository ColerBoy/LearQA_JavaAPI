package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.restassured.http.Headers;
import lib.Assertions;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestsWtithJUnit extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    public void testAuthUser() {
        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();
        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        } else {
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }
        Response responseForCheck = spec.get().andReturn();
        Assertions.assertJsonByName(responseForCheck, "user_id", 0);
    }

    @Test
    public void testHomeworkCookie() {
        Response response = RestAssured
                .given()
                .post("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        System.out.println(response.getCookies());
        this.cookie = this.getCookie(response, "HomeWork");
        assertEquals(200, response.statusCode(), "Expected status code 200");
        assertNotNull(cookie, "cookie=null");
        System.out.println("Значение cookie: " + cookie);
    }

    @Test
    public void testHomeworkHeader() {
        Response response = RestAssured
                .given()
                .post("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        System.out.println(response.getHeaders());
        this.header = this.getHeader(response, "x-secret-homework-header");
        assertEquals(200, response.statusCode(), "Expected status code 200");
        assertNotNull(header, "header=null");
        System.out.println("Значение header: " + header);
    }

    @Test
    public void hw() {
        Response response = RestAssured
                .given()
                .post("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        System.out.println(response.getHeaders());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Формат: UserAgent###platform###browser###device
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30###Mobile###No###Android",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1###Mobile###Chrome###iOS",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)###Googlebot###Unknown###Unknown",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0###Web###Chrome###No",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1###Mobile###No###iPhone"
    })
    public void testUserAgent(String testData) {
        // Разделяем строку на части
        String[] parts = testData.split("###");
        String userAgent = parts[0];
        String expectedPlatform = parts[1];
        String expectedBrowser = parts[2];
        String expectedDevice = parts[3];

        // Отправляем запрос
        Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();

        String actualPlatform = response.jsonPath().getString("platform");
        String actualBrowser = response.jsonPath().getString("browser");
        String actualDevice = response.jsonPath().getString("device");

        // Проверки
        if (!expectedPlatform.equals(actualPlatform) ||
                !expectedBrowser.equals(actualBrowser) ||
                !expectedDevice.equals(actualDevice)) {
            System.out.println("❌ Incorrect result for User-Agent:\n" + userAgent);
            if (!expectedPlatform.equals(actualPlatform)) {
                System.out.println("   → platform: expected=" + expectedPlatform + ", actual=" + actualPlatform);
            }
            if (!expectedBrowser.equals(actualBrowser)) {
                System.out.println("   → browser: expected=" + expectedBrowser + ", actual=" + actualBrowser);
            }
            if (!expectedDevice.equals(actualDevice)) {
                System.out.println("   → device: expected=" + expectedDevice + ", actual=" + actualDevice);
            }
            System.out.println();
        }

        // Для JUnit отчета
        assertEquals(expectedPlatform, actualPlatform, "Platform mismatch");
        assertEquals(expectedBrowser, actualBrowser, "Browser mismatch");
        assertEquals(expectedDevice, actualDevice, "Device mismatch");
    }
}
