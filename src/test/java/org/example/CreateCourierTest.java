//Создание курьера
//        Проверь:
//        курьера можно создать;
//        нельзя создать двух одинаковых курьеров;
//        чтобы создать курьера, нужно передать в ручку все обязательные поля;
//        запрос возвращает правильный код ответа;
//        успешный запрос возвращает ok: true;
//        если одного из полей нет, запрос возвращает ошибку;
//        если создать пользователя с логином, который уже есть, возвращается ошибка.
package org.example;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateCourierTest extends APIBaseTest {
    DataСourier dataСourier;

@Test
@DisplayName("Создание курьера cо всеми обязательными полями")
@Description("Проверка успешного создания курьера должен возвращаться код 201 и ok: true")
public void checkCreateCourier() {
    dataСourier = new DataСourier("milana", "mi05la76", "Milana");
    Response response = given()
            .header("Content-type", "application/json") // заполни heade
            .body(dataСourier) // заполни body
            .when()
            .post(CREATE_ENDPOINT_COURIER); // отправь запрос на ручку

    // Проверяем статус
    response.then().statusCode(CREATE_STATUS_COD);
    // Проверяем, что в ответе есть ok: true
    response.then().assertThat().body("ok", is(true));
  }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("При повторной попытке создания курьера с теми же данными, должен возвращаться код 409")
    public void checkDuplicateLoginCreated() {
        dataСourier = new DataСourier("milana", "mi05la76", "Milana");
        given()
                .header("Content-type", "application/json")
                .body(dataСourier)
                .when()
                .post(CREATE_ENDPOINT_COURIER);

        Response response = given()
                .header("Content-type", "application/json")
                .body(dataСourier)
                .when()
                .post(CREATE_ENDPOINT_COURIER);

        response.then().statusCode(CONFLICT_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    @Description("При попытке создания курьера без логина, должен возвращаться код 400")
    void checkCreateCourierWithoutLogin() {
        dataСourier = new DataСourier("", "mi05la76", "Milana");

        Response response = given()
                .header("Content-type", "application/json")
                .body(dataСourier)
                .when()
                .post(CREATE_ENDPOINT_COURIER);
        response.then().statusCode(BAD_REQUEST_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    @Description("При попытке создания курьера без пароля, должен возвращаться код 400")
    void checkCreateCourierWithoutPassword() {
        dataСourier = new DataСourier("milana", "", "Milana");

        Response response = given()
                .header("Content-type", "application/json")
                .body(dataСourier)
                .when()
                .post(CREATE_ENDPOINT_COURIER);
        response.then().statusCode(BAD_REQUEST_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("При попытке создания курьера без имени, должен возвращаться код 201")
    void checkCreateCourierWithoutFirstname() {
        dataСourier = new DataСourier("milana", "mi05la76", "");

        Response response = given()
                .header("Content-type", "application/json")
                .body(dataСourier)
                .when()
                .post(CREATE_ENDPOINT_COURIER);
        response.then().statusCode(CREATE_STATUS_COD);
        response.then().assertThat().body("ok", is(true));
    }

    // удаление созданной записи по id, после каждог теста, если id не равен нулю
    @AfterEach
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

