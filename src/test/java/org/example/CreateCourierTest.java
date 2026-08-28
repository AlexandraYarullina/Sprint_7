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

@Test
@DisplayName("Создание курьера cо всеми обязательными полями")
@Description("Проверка успешного создания курьера должен возвращаться код 201 и ok: true")
public void checkCreateCourier() {
    createTestCourier();
  }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("При повторной попытке создания курьера с теми же данными, должен возвращаться код 409")
    public void checkDuplicateLoginCreated() {
        given()
                .header("Content-type", "application/json")
                .body(new DataCourier(LOGIN, PASSWORD, FIRSTNAME))
                .when()
                .post(CREATE_ENDPOINT_COURIER);

        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataCourier(LOGIN, PASSWORD, FIRSTNAME))
                .when()
                .post(CREATE_ENDPOINT_COURIER);

        response.then().statusCode(CONFLICT_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    @Description("При попытке создания курьера без логина, должен возвращаться код 400")
    void checkCreateCourierWithoutLogin() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataCourier("", PASSWORD, FIRSTNAME))
                .when()
                .post(CREATE_ENDPOINT_COURIER);
        response.then().statusCode(BAD_REQUEST_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    @Description("При попытке создания курьера без пароля, должен возвращаться код 400")
    void checkCreateCourierWithoutPassword() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataCourier(LOGIN, "", FIRSTNAME))
                .when()
                .post(CREATE_ENDPOINT_COURIER);
        response.then().statusCode(BAD_REQUEST_STATUS_COD);
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("При попытке создания курьера без имени, должен возвращаться код 201")
    void checkCreateCourierWithoutFirstname() {

        Response response = given()
                .header("Content-type", "application/json")
                .body(new DataCourier(LOGIN, PASSWORD, ""))
                .when()
                .post(CREATE_ENDPOINT_COURIER);
        response.then().statusCode(CREATE_STATUS_COD);
        response.then().assertThat().body("ok", is(true));
    }

    // удаление курьера по id
    @AfterEach
    public void deleteCourier() {
        deleteTestCourier();
    }
}

