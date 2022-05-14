package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.data.IngredientsForCreateNewBurger;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    private static final String ORDER_PATH = "/api/orders";

    @Step("Creating order with authorization")
    public ValidatableResponse createOrderWithAuthorization(IngredientsForCreateNewBurger ingredientsForCreateNewBurger, String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .spec(getBaseSpec())
                .body(ingredientsForCreateNewBurger)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Creating order without authorization")
    public ValidatableResponse orderCreateWithoutAuthorization(IngredientsForCreateNewBurger ingredientsForCreateNewBurger) {
        return given()
                .spec(getBaseSpec())
                .body(ingredientsForCreateNewBurger)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Getting orders only 1 user with authorization")
    public ValidatableResponse userOrderInfo(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }

    @Step("Getting orders only 1 user without authorization")
    public ValidatableResponse userOrderInfoWithoutToken() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }

    @Step("Getting all orders")
    public ValidatableResponse getListOrders() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH + "/all")
                .then().log().all();
    }
    @Step("Getting all ingredients")
    public ValidatableResponse gettingAllIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get("/api/ingredients")
                .then().log().all();
    }
}