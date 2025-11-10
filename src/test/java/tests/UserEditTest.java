package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test successfully create new user, login and changed first name")
    @DisplayName("Test positive changed userData")
     public void testEditJustCreatedTest(){
    //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",userData);


        String userId = responseCreateAuth.jsonPath().get("id");

        //LOGIN

        Map<String,String>authData= new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);


        //EDIT
        String newName = "Changed Name";
        Map<String,String>editData=new HashMap<>();
        editData.put("firstName",newName);
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithParams("https://playground.learnqa.ru/api/user/"+userId,token,cookie,editData);


        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/"+userId,token, cookie);
       Assertions.assertJsonByName(responseUserData,"firstName",newName);
    }

    @Test
    @Description("This test checks error edit data without auth")
    @DisplayName("Test negative changed userData")
    public void testEditWithoutAuth(){
        String newName = "Changed Name";
        Map<String,String>editData=new HashMap<>();
        editData.put("firstName",newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequestWithoutParams("https://playground.learnqa.ru/api/user/2",editData);
        Assertions.assertJsonByName(responseEditUser,"error","Auth token not supplied");
    }

    @Test
    @Description("This test checks error edit data to incorrect user id")
    @DisplayName("Test negative changed userData")
    public void testEditIncorrectUser(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",userData);

        //LOGIN

        Map<String,String>authData= new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);


        //EDIT
        String newName = "Changed Name";
        Map<String,String>editData=new HashMap<>();
        editData.put("firstName",newName);
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithParams("https://playground.learnqa.ru/api/user/23",token,cookie,editData);
        Assertions.assertJsonByName(responseEditUser,"error","This user can only edit their own data.");

    }

    @Test
    @Description("This test checks error edit data with invalid email")
    @DisplayName("Test negative changed userData")
    public void testEditWithIncorrectEmail(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",userData);
        String userId = responseCreateAuth.jsonPath().get("id");
        //LOGIN

        Map<String,String>authData= new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);


        //EDIT
        String newEmail = "testmail.ru";
        Map<String,String>editData=new HashMap<>();
        editData.put("email",newEmail);
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithParams("https://playground.learnqa.ru/api/user/" + userId,token,cookie,editData);
        Assertions.assertJsonByName(responseEditUser,"error","Invalid email format");

    }

    @Test
    @Description("This test checks error edit data with too short first name")
    @DisplayName("Test negative changed userData")
    public void testEditWithIncorrectFirstName(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",userData);


        String userId = responseCreateAuth.jsonPath().get("id");

        //LOGIN

        Map<String,String>authData= new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",authData);


        //EDIT
        String newName = "C";
        Map<String,String>editData=new HashMap<>();
        editData.put("firstName",newName);
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithParams("https://playground.learnqa.ru/api/user/"+userId,token,cookie,editData);
        Assertions.assertJsonByName(responseEditUser,"error","The value for field `firstName` is too short");
    }

}
