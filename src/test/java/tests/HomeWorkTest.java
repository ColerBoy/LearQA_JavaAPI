package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HomeWorkTest extends BaseTestCase {
    String cookie;
    String header;

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
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0###Web###Chrome###No",
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
