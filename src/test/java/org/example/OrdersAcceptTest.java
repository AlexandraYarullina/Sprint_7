//Принять заказ
//Проверь:
//успешный запрос возвращает ok: true;
//если не передать id курьера, запрос вернёт ошибку;
//если передать неверный id курьера, запрос вернёт ошибку;
//если не передать номер заказа, запрос вернёт ошибку;
//если передать неверный номер заказа, запрос вернёт ошибку.

package org.example;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static Utils.Constants.*;
import static Utils.Constants.OK_STATUS_COD;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrdersAcceptTest extends APIBaseTest{
    DataСourier dataСourier;
    private Integer loginId ; //id курьера
    private Integer track;    //тело ответа содержит track
    private Integer orderNumberId; //id заказа

    @BeforeEach
    @DisplayName("Создание курьера и авторизация cо всеми обязательными полями")
    @Description("Проверка успешного создания курьера возвращает код 201 и ok: true и успешная авторизация возвращает код 200")
    public void checkCreateAndAutorize() {
        dataСourier = new DataСourier("dimar5846", "4rfe984d16","Дмитрий");
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

    @DisplayName("Создание заказа, проверить код ответа и номер заказа(track)")
    @Description("Проверка создания заказа, тело ответа содержит track")
    public void checkCreateOrder() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(new DataOrders("Анастасия","Семенова","Новорижская, д.15","Комсомольская","+7831535392",FOUR_DAYS,TOMORROW_STRING,"Позвонить за час",new String[]{"BLACK"}))
                        .when()
                        .post(CREATE_ENDPOINT_ORDERS);
        response.then().assertThat().body("track", notNullValue());
        response.then().statusCode(CREATE_STATUS_COD);
        track = response.body().path("track");
    }
//    Получить заказ по его номеру
//    Проверь:
//    успешный запрос возвращает объект с заказом;
    @DisplayName("Получить заказ по его track")
    @Description("Проверка успешного запроса для получения id заказа, возвращает код 200")
    public void getOrderNumber() {

        Response responseGetOrderNumber =
                given()
                        .get(GET_ENDPOINT_ORDERS_TRACK + "{track}", track.toString())
                        .then()
                        .statusCode(OK_STATUS_COD)
                        .extract().response();

        orderNumberId = responseGetOrderNumber.body().path("order.id");
    }

    @Test
    @DisplayName("Принять заказ курьером с корректныи номером заказа и id курьера")
    @Description("Проверка, что заказ принят курьером, должен возвращаться ok: true")
    public void checkOrderAccept() {
        checkCreateOrder();
        getOrderNumber();
        Response responseOrderAccept =
                given()
                        .put(PUT_ENDPOINT_ORDER_ACCEPT + "/{orderNumberId}?courierId={courierId}", orderNumberId.toString(), loginId.toString());

        responseOrderAccept.then().assertThat().body("ok", is(true));
    }

    @Test
    @DisplayName("Принять заказ курьером, не передавать id курьера")
    @Description("Проверка, если не передать id курьера должен возвращаться код 400")
    public void checkOrderAcceptWithoutCourierId() {
        checkCreateOrder();
        getOrderNumber();
        Response responseOrderAccept =
                given()
                        .put(PUT_ENDPOINT_ORDER_ACCEPT + "/{orderNumberId}?courierId={courierId}", orderNumberId.toString(),"");

        responseOrderAccept.then().statusCode(BAD_REQUEST_STATUS_COD);
        responseOrderAccept.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принять заказ курьером, передать неверный id курьера")
    @Description("Проверка, если передать неверный id курьера, должен возвращаться код 404")
    public void checkOrderAcceptIncorrectCourierId() {
        checkCreateOrder();
        getOrderNumber();
        Response responseOrderAccept =
                given()
                        .put(PUT_ENDPOINT_ORDER_ACCEPT + "/{orderNumberId}?courierId={courierId}", orderNumberId.toString(),"749698");

        responseOrderAccept.then().statusCode(NOT_FOUND_STATUS_COD);
        responseOrderAccept.then().assertThat().body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принять заказ курьером, не передавать номер заказа")
    @Description("Bug. Неверное описание в документации https://qa-scooter.praktikum-services.ru/docs/#api-Orders-AcceptOrder, если отправить запрос без номера заказа, возвращается тело ответа\n" +
            "    HTTP/1.1 400 Bad Request\n" +
            "    {\n" +
            "        \"message\":  \"Недостаточно данных для поиска\"\n" +
            "    },\n" +
            "    но в итоге при отравке запроса получаем ответ\n" +
            "    {\n" +
            "        \"code\":404,\"message\":\"Not Found.\"\n" +
            "    } ")
    public void checkOrderAcceptWithoutOrderNumberId() {
        checkCreateOrder();
        getOrderNumber();
        Response responseOrderAccept =
                given()
                        .put(PUT_ENDPOINT_ORDER_ACCEPT + "/{orderNumberId}?courierId={courierId}", "", loginId.toString());

        responseOrderAccept.then().statusCode(BAD_REQUEST_STATUS_COD);
        responseOrderAccept.then().assertThat().body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принять заказ курьером, передать неверный номер заказа")
    @Description("Проверка, если передать неверный номерзаказа, должен возвращаться код 400")
    public void checkOrderAcceptIncorrectOrderNumberId() {
        checkCreateOrder();
        getOrderNumber();
        Response responseOrderAccept =
                given()
                        .put(PUT_ENDPOINT_ORDER_ACCEPT + "/{orderNumberId}?courierId={courierId}", "989965", loginId.toString());

        responseOrderAccept.then().statusCode(NOT_FOUND_STATUS_COD);
        responseOrderAccept.then().assertThat().body("message", equalTo("Заказа с таким id не существует"));
    }

    @AfterEach
    @DisplayName("Удаление созданной записи по id, после каждого теста, если id не равен нулю")
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
