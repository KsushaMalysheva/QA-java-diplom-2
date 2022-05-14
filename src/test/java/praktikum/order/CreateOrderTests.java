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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CreateOrderTests {

    private UserClient userClient;
    private User user;
    String accessToken;
    private IngredientsForCreateNewBurger ingredientsForCreateNewBurger;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        //Сгенерировать случайные данные полей
        user = User.getRandom();
        ValidatableResponse response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);
        //Сгенерировать случайный бургер
        ingredientsForCreateNewBurger = IngredientsForCreateNewBurger.getRandom();

    }

    @After
        public void tearDown() {
            userClient.deletingUser(accessToken, user);
        }

    @Test
    @DisplayName("Create order with ingredients and without login")
    @Description("Successfully create order with ingredients and without login")
    public void successfulCreateOrderWithIngredientsAndWithoutLogin() {

        //Создать заказ
        ValidatableResponse responseOrder = orderClient.orderCreateWithoutAuthorization(ingredientsForCreateNewBurger);
        //Получить статус кода запроса
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        //Получить значение ключа "success"
        boolean isOrderCreated = responseOrder.extract().path("success");
        // Получение номера созданого заказа
        int orderNumber = responseOrder.extract().path("order.number");
        String name = responseOrder.extract().path("name");

        //Проверить статус код
        assertEquals("Incorrect status code", 200, statusCodeResponseOrder);
        //Проверить создание заказа
        assertTrue("Order is not created", isOrderCreated);
        //Проверить наличие номера созданного заказа
        assertNotNull("Пустой номер заказа", orderNumber);
        assertNotNull("Name is empty", name);
    }

    @Test
    @DisplayName("Create order with ingredients and login")
    @Description("Successfully create order with ingredients and login")
    public void successfulCreateOrderWithIngredientsAndWithLogin() {

        //Создать заказ
        ValidatableResponse responseOrder = orderClient.createOrderWithAuthorization(ingredientsForCreateNewBurger, accessToken);

        //Получить статус кода запроса
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        //Получить значение ключа "success"
        boolean isOrderCreated = responseOrder.extract().path("success");
        // Получение номера созданого заказа
        int orderNumber = responseOrder.extract().path("order.number");

        //Проверить статус код
        assertEquals("Incorrect status code", 200, statusCodeResponseOrder);
        //Проверить создание заказа
        assertTrue("Order doesn't created", isOrderCreated);
        //Проверить наличие номера созданного заказа
        assertNotNull("Пустой номе заказа", orderNumber);
    }

    @Test
    @DisplayName("Create order with not really ingredients and with login")
    @Description("Unsuccessfully create order with not really ingredients and with login")
    public void unsuccessfulCreateOrderWithNotReallyIngredientsAndWithLogin() {

        //Создать заказ
        ValidatableResponse responseOrder = orderClient.createOrderWithAuthorization(IngredientsForCreateNewBurger.getNotReallyIngredients(), accessToken);

        //Получить статус кода запроса
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        assertEquals("Incorrect status code", 500, statusCodeResponseOrder);
    }

    @Test
    @DisplayName("Create order without ingredients and with login")
    @Description("Unsuccessfully create order without ingredients and with login")
    public void unsuccessfulCreateOrderWithoutIngredientsAndWithLogin() {

        //Создать заказ
        ValidatableResponse responseOrder = orderClient.createOrderWithAuthorization(IngredientsForCreateNewBurger.getWithoutIngredients(), accessToken);

        //Получить статус кода запроса
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        boolean isOrderCreated = responseOrder.extract().path("success");
        // Получение номера ключа "message"
        String orderMessage = responseOrder.extract().path("message");

        //Проверить статус код
        assertEquals("Incorrect status code", 400,statusCodeResponseOrder);
        //Проверить создание заказа
        assertFalse("Order be created", isOrderCreated);
        //Проверить текст
        assertEquals("Error message doesn't match", "Ingredient ids must be provided", orderMessage);
    }
    @Test
    @DisplayName("Creating orders")
    @Description("Creating order with incorrect hashes ingredients")
    public void creatingOrderWithIncorrectHashesTest() {
        String incorrectHashes = Order.getRandomHashes();
        Order order = new Order();
        order.setIngredients(Collections.singletonList(incorrectHashes));

        //Создать заказ
        ValidatableResponse responseOrder = orderClient.createOrderWithAuthorization(ingredientsForCreateNewBurger,accessToken);

        int statusCode = responseOrder.extract().statusCode();
        assertNotEquals("Incorrect status code", 500, statusCode);
    }
}

