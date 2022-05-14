package praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.Order;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.data.User;
import praktikum.data.IngredientsForCreateNewBurger;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class GetOrderUserTests {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private ValidatableResponse responseOrder;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        responseOrder = userClient.userCreate(user);
        accessToken = responseOrder.extract().path("accessToken").toString().substring(7);
        ValidatableResponse ingredients = orderClient.gettingAllIngredients();
        firstIngredient = ingredients.extract().path("data[1]._id");
    }

    @After
    public void tearDown() {
        userClient.deletingUser(accessToken, user);
    }

    @Test
    @DisplayName("Get orders from a unique logged in user")
    @Description("Successfully get orders from a unique logged in user")
    public void successfulGetOrdersWithLogin() {

        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));
        //Получить заказ пользователя
        ValidatableResponse responseOrder = orderClient.userOrderInfo(accessToken);
        //Получить статус кода запроса
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        //Получить значение ключа "success"
        boolean isGetOrders = responseOrder.extract().path("success");
        List<String> orders = responseOrder.extract().path("orders");
        assertEquals("Incorrect status code", 200, statusCodeResponseOrder);
        assertTrue("Orders wasn't get", isGetOrders);
        assertNotNull("Orders is empty", orders);
    }

    @Test
    @DisplayName("Get list all orders")
    public void getListOrders () {

        ValidatableResponse responseOrder = orderClient.getListOrders();

        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        List<Object> orders = responseOrder.extract().jsonPath().getList("orders");
        int sizeListOrders = orders.size();

        List<Object> listOfIdOrders = responseOrder.extract().jsonPath().getJsonObject("orders._id");
        int sizeListOfIdOrders = listOfIdOrders.size();

        assertEquals("Incorrect status code", 200, statusCodeResponseOrder);
        assertFalse(orders.isEmpty());
        assertEquals(sizeListOfIdOrders, sizeListOrders);
    }

    @Test
    @DisplayName("Get orders from a unique unregistered user")
    @Description("Unsuccessfully get orders from a unique unregistered user")
    public void unsuccessfulGetOrdersWithoutLogin() {

        //Получить заказ
        ValidatableResponse responseOrder = orderClient.userOrderInfoWithoutToken();

        //Получить статус кода запроса
        int statusCode = responseOrder.extract().statusCode();
        boolean isNotGeted = responseOrder.extract().path("success");
        String message = responseOrder.extract().path("message");
        assertEquals("Incorrect status code", 401, statusCode);
        assertFalse("Orders wasn't get", isNotGeted);
        assertEquals("Error message not matches", "You should be authorised", message);
    }
}