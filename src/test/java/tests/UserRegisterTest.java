package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email",email);
        userData=DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();
        Assertions.assertResponceCodeEquals(responseCreateAuth,400);
        Assertions.assertResponceTextEquals(responseCreateAuth,"Users with email '"+email +"' already exists");

    }
    @Test
    public void testCreateUserSuccessfully(){
        Map<String, String> userData = DataGenerator.getRegistrationData( );

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponceCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");

    }

    @Test
    public void testCreateUserWithIncorrectEmail(){
        String email = "vinkotovexample.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email",email);
        userData=DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponceCodeEquals(responseCreateAuth,400);
        Assertions.assertResponceTextEquals(responseCreateAuth,"Invalid email format");

    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password","username","firstName","lastName"})
    public void testCreateUserWithoutOneField(String field){
        Map<String, String> userData = new HashMap<>();
        userData=DataGenerator.getRegistrationData(userData);
        userData.remove(field);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponceCodeEquals(responseCreateAuth,400);
        Assertions.assertResponceTextEquals(responseCreateAuth,"The following required params are missed: "+field);

    }

    @Test
    public void testCreateUserWithShortFirstName(){
        String firstName = "v";
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName",firstName);
        userData=DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponceCodeEquals(responseCreateAuth,400);
        Assertions.assertResponceTextEquals(responseCreateAuth,"The value of 'firstName' field is too short");

    }

    @Test
    public void testCreateUserWithLongFirstName(){
        String firstName = "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc.";
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName",firstName);
        userData=DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.
                makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        Assertions.assertResponceCodeEquals(responseCreateAuth,400);
        Assertions.assertResponceTextEquals(responseCreateAuth,"The value of 'firstName' field is too long");

    }
}
