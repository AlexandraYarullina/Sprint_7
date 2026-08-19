//Логин курьера
//        Проверь:
//        курьер может авторизоваться;
//        для авторизации нужно передать все обязательные поля;
//        система вернёт ошибку, если неправильно указать логин или пароль;
//        если какого-то поля нет, запрос возвращает ошибку;
//        если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
//        успешный запрос возвращает id.
package org.example;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class LoginCourierTest extends APIBaseTest {
    DataСourier dataСourier;
    String login ="victor";
    String password="ge5rt634ry";

    @BeforeEach
    @DisplayName("Создание курьера cо всеми обязательными полями")
    @Description("Проверка успешного создания курьера должен возвращаться код 201 и ok: true")
    public void checkCreateCourier() {
        dataСourier = new DataСourier(login, password);
        given()
                .header("Content-type", "application/json") // заполни header
                .body(dataСourier) // заполни body
                .when()
                .post(CREATE_ENDPOINT_COURIER) // отправь запрос на ручку
                .then()
                .statusCode(CREATE_STATUS_COD)
                .and()
                .assertThat().body("ok", is(true));
    }

    @Test
    @DisplayName("Авторизация курьера cо всеми обязательными полями")
    @Description("Проверяется, что при корректных логине и пароле система возвращает id курьера и код 200")
    public void loginCourierSuccess() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataСourier(login, password)) // заполни body
                .when()
                .post(LOGIN_ENDPOINT_COURIER)
                .then()
                .statusCode(OK_STATUS_COD)
                .extract().response();

        Integer id = response.body().path("id");
        Assertions.assertNotNull(id, "В успешном ответе должен присутствовать id");
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    @Description("При попытке авторизации без логина, должен возвращаться код 400")
    void checkAuthorizationCourierWithoutLogin() {
        Response response =given()
                .header("Content-type", "application/json")
                .body(new DataСourier("", password))
                .when()
                .post(LOGIN_ENDPOINT_COURIER);
        response.then().statusCode(BAD_REQUEST_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    @Description("При попытке авторизации без пароля, должен возвращаться код 400")
    void checkAuthorizationCourierWithoutPassword() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataСourier(login, ""))
                .when()
                .post(LOGIN_ENDPOINT_COURIER);
        response.then().statusCode(BAD_REQUEST_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неверным логином")
    @Description("При попытке авторизации с неверным логином, должен возвращаться код 404")
    public void checkAuthorizationCourierIncorrectLogin() {
        Response response = given()
                .header("Content-type", "application/json") // заполни header
                .body(new DataСourier("victor1856", password))
                .when()
                .post(LOGIN_ENDPOINT_COURIER);
        response.then().statusCode(NOT_FOUND_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неверным паролем")
    @Description("При попытке авторизации с неверным паролем, должен возвращаться код 404")

    public void checkAuthorizationCourierIncorrectPassword() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataСourier(login, "ds3465r6953f"))
                .when()
                .post(LOGIN_ENDPOINT_COURIER);
        response.then().statusCode(NOT_FOUND_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }


    @Test
    @DisplayName("Нельзя авторизоваться под несуществующим пользователем")
    @Description("При попытке авторизации с неверным логином и паролем, должен возвращаться код 404")
    public void checkNonExistentLogin() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataСourier("v6876ita3", "f8eo453w06"))
                .when()
                .post(LOGIN_ENDPOINT_COURIER);
        response.then().statusCode(NOT_FOUND_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @AfterEach
    @DisplayName("Удаление созданной записи по id, если id не равен нулю")
    public void deleteCourier() {
        Integer id =
                given()
                        .header("Content-type", "application/json")
                        .body(dataСourier)
                        .when()
                        .post(LOGIN_ENDPOINT_COURIER)
                        .then().extract().body().path("id");
        if (id != null) {
            given()
                    .delete(CREATE_ENDPOINT_COURIER + "/{id}", id.toString());
        }
    }
}
