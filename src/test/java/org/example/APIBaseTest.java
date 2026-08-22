package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class APIBaseTest {
    public Response response;
    Integer loginId;
    DataCourier dataCourier = new DataCourier(LOGIN, PASSWORD, FIRSTNAME);

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Создаём тестового курьера")
    protected void createTestCourier() {
        response = given()
                .header("Content-type", "application/json")
                .body(dataCourier)
                .when()
                .post(CREATE_ENDPOINT_COURIER);
    }

    @Step("Авторизация курьера")
    public void logInTestCourier() {
        response = given()
                .header("Content-type", "application/json")
                .body(dataCourier) // заполни body
                .when()
                .post(LOGIN_ENDPOINT_COURIER)
                .then()
                .statusCode(OK_STATUS_COD)
                .extract().response();
        loginId = response.body().path("id");
    }

    // удаление созданной записи по id, после каждог теста, если id не равен нулю
    @Step
    public void deleteTestCourier() {
        Integer id =
                given()
                        .header("Content-type", "application/json")
                        .body(dataCourier)
                        .when()
                        .post(LOGIN_ENDPOINT_COURIER)
                        .then().extract().body().path("id");
        if (id != null) {
            given()
                    .delete(CREATE_ENDPOINT_COURIER + "/{id}", id.toString());
        }
    }
}
