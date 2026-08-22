//Создание заказа
//        Проверь, что когда создаёшь заказ:
//        можно указать один из цветов — BLACK или GREY;
//        можно указать оба цвета;
//        можно совсем не указывать цвет;
//        тело ответа содержит track.
//        Чтобы протестировать создание заказа, нужно использовать параметризацию.
package org.example;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты с параметризацией CreateOrdersTest")
public class CreateOrdersTest extends APIBaseTest {

    public static Object[][] getOrderInformation() {
        return new Object[][]{
                {"Валерия","Павлова","Новорижская, д.16","Комсомольская","+7831535392",FOUR_DAYS,TOMORROW_STRING,"Позвонить за час",new String[]{"BLACK"}},
                {"Дмитрий", "Зайцев", "Орловская, д.3", "Рижская", "+78472656598", ONE_DAY, TOMORROW_STRING, "Доставьте самокат не позднее 10:00", new String[]{"BLACK", "GREY"}},
                {"Виктория", "Зиганшина", "Новорижская, д.16", "Охотный ряд", "+7831533592", FIVE_DAYS,TOMORROW_STRING, "Доставьте самокат не позднее 13:00", new String[]{}},
                {"Олег", "Белов", "Новорижская, д.15", "Тульская", "+78472676598", THREE_DAYS,TOMORROW_STRING, "Не забудьте учесть, что мне нужен самокат серого цвета", new String[]{"GREY"}}
        };
    }

    @ParameterizedTest
    @DisplayName("Создать заказ, проверить код ответа и номер заказа(track)")
    @MethodSource("getOrderInformation")
    public void checkOrderContent(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(new DataOrders(firstName,lastName,address, metroStation,  phone,  rentTime,  deliveryDate,  comment, color))
                        .when()
                        .post(CREATE_ENDPOINT_ORDERS);
        response.then().assertThat().body("track", notNullValue());
        response.then().statusCode(CREATE_STATUS_COD);
    }
}

