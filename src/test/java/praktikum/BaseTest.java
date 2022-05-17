package praktikum;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;

public class BaseTest {
    @BeforeClass
    static public void setUpBase() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://stellarburgers.nomoreparties.site/api/")
                .setContentType(ContentType.JSON)
                .build();
    }
}
