//Список заказов
//Проверь, что в тело ответа возвращается список заказов.
package org.example;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest extends APIBaseTest {

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверь, что в тело ответа возвращается список заказов, должен вернуться код 200")
    public void checkCreateOrder() {
        given()
                .get(CREATE_ENDPOINT_ORDERS)
                .then()
                .statusCode(OK_STATUS_COD)
                .and()
                .assertThat().body("orders", notNullValue());
    }
}
