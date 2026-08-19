package org.example;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static Utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class APIBaseTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }
}
