//С методом DELETE можно работать так же, как с другими методами.
//Проверь:
//неуспешный запрос возвращает соответствующую ошибку;
//успешный запрос возвращает ok: true;
//если отправить запрос без id, вернётся ошибка;
//если отправить запрос с несуществующим id, вернётся ошибка.
package org.example;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeleteCourierTest extends APIBaseTest{
    DataСourier dataСourier;
    private Integer loginId;

    @BeforeEach
    @DisplayName("Создание курьера и авторизация cо всеми обязательными полями")
    @Description("Проверка успешного создания курьера возвращает код 201 и ok: true и успешная авторизация возвращает код 200")
    public void checkCreateAndAutorize() {
        dataСourier = new DataСourier("dima36", "4rfe48d1","Дмитрий");
        Response createResponse  = given()
                .header("Content-type", "application/json") // заполни header
                .body(dataСourier) // заполни body
                .when()
                .post(CREATE_ENDPOINT_COURIER) // отправь запрос на ручку
                .then()
                .statusCode(CREATE_STATUS_COD)
                .body("ok", is(true))
                .extract().response();

        Response loginResponse  = given()
                .header("Content-type", "application/json") // заполни header
                .body(dataСourier) // заполни body
                .when()
                .post(LOGIN_ENDPOINT_COURIER) // отправь запрос на ручку
                .then()
                .statusCode(OK_STATUS_COD)
                .extract().response();

        loginId = loginResponse.body().path("id");
    }

    @Test
    @DisplayName("Удалении несуществующего курьера")
    @Description("При передаче несуществующего id возвращается ошибка 404")
    public void deleteNotExistCourier() {
        int nonExistentId = 865965; // заведомо несуществующий ID
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .delete(CREATE_ENDPOINT_COURIER + "/{id}", nonExistentId)
                .then()
                .assertThat()
                .statusCode(NOT_FOUND_STATUS_COD)
                .body("message", equalTo("Курьера с таким id нет."))
                .extract().response();
    }
    @Test
    @DisplayName("Удалении существующего курьера")
    @Description("Проверка, что при удалении существующего курьера возвращаться код 200 и ok: true")
    public void deleteExistCourier() {
        Response response = given()
                .delete(CREATE_ENDPOINT_COURIER + "/{id}", loginId.toString())
                .then()
                .statusCode(OK_STATUS_COD)
                .body("ok", is(true))
                .extract().response();
    }
    @Test
    @DisplayName("Удаление: ошибка при запросе к коллекции без id")
    @Description("Запрос DELETE на /courier (без id) должен возвращать ошибку 404")
    void deleteWithoutId() {
        Response response =given()
                .header("Content-Type", "application/json")
                .when()
                .delete(CREATE_ENDPOINT_COURIER) // без /{id}
                .then()
                .assertThat()
                .statusCode(NOT_FOUND_STATUS_COD)
                .body("message", equalTo("Not Found."))
                .extract().response();
    }
    @Test
    @DisplayName("Удаление: ошибка при запросе, ввод символов")
    @Description("Запрос DELETE должен возвращать ошибку 500")
    void deletesdWithoutId() {
        String nonexistentId = "gert9";
        Response response =given()
                .header("Content-Type", "application/json")
                .when()
                .delete(CREATE_ENDPOINT_COURIER+ "/{id}", nonexistentId)
                .then()
                .assertThat()
                .statusCode(INTERNAL_SERVER_ERROR_STATUS_COD)
                .body("message", equalTo("invalid input syntax for type integer: \""+nonexistentId+"\""))
                .extract().response();
    }

    @AfterEach
    @DisplayName("Удаление созданной записи по id, после каждог теста, если id не нулевой")
    public void deleteCourierLogin() {
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
