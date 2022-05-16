package praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import praktikum.BaseTest;
import praktikum.client.UserClient;
import praktikum.data.User;
import praktikum.data.UserCredentials;
import static org.junit.Assert.*;

public class ChangingUserDataTests extends BaseTest {
    private static User user;
    private static UserClient userClient;
    private static ValidatableResponse response;
    private static String accessToken;

    @BeforeClass
    public static void SetUp() {
            userClient = new UserClient();
            user = User.getRandom();
            response = userClient.userCreate(user);
            userClient.authorization(UserCredentials.from(user));
            accessToken = response.extract().path("accessToken").toString().substring(7);
        }

        @After
        public void tearDown() {
            userClient.deletingUser(accessToken, user);
        }

        @Test
        @DisplayName("Changing user")
        @Description("Getting information about user")
        public void getUserDataTest() {
            userClient.gettingInformationUser(accessToken);
            int statusCode = response.extract().statusCode();
            boolean isGeted= response.extract().path("success");
            String userEmail = response.extract().path("user.email");
            String userName = response.extract().path("user.name");
            assertEquals("Incorrect status code",200, statusCode);
            assertTrue("Information doesn't get",isGeted);
            assertEquals("User email doesn't match", user.getEmail(), userEmail);
            assertEquals("User name doesn't match", user.getName(), userName);
        }

        @Test
        @DisplayName("Changing user")
        @Description("Test for change information about user without authorization")
        public void changeInformationUserWithoutAuthorizationTest() {
            response = userClient.changeInformationUserWithoutToken(user);
            int statusCode = response.extract().statusCode();
            boolean isNotChanged = response.extract().path("success");
            String message = response.extract().path("message");
            assertEquals("Incorrect status code",401, statusCode);
            assertFalse("Information was changed", isNotChanged);
            assertEquals("Error message doesn't match","You should be authorised", message);
        }

        @Test
        @DisplayName("Changing user")
        @Description("Test for change information about user with authorization {email}")
        public void changeEmailWithAuthorizationTest() {
            String newEmail = User.getRandomEmail();
            User newUser = User.builder()
                    .email(newEmail)
                    .password(user.getPassword())
                    .name(user.getName())
                    .build();
            userClient.changeInformationUserWithToken(accessToken, newUser);
            int statusCode = response.extract().statusCode();
            boolean isChanged= response.extract().path("success");
            String userEmail = response.extract().path("user.email");
            assertEquals("Incorrect status code", 200, statusCode);
            assertTrue("Information wasn't changed", isChanged);
            assertEquals("Email not changed", user.getEmail().toLowerCase(), userEmail);
        }

        @Test
        @DisplayName("Changing user")
        @Description("Test for change information about user with authorization {password}")
        public void changePasswordWithAuthorizationTest() {
            String newPassword = User.getRandomData();
            User newUser = User.builder()
                    .email(user.getEmail())
                    .password(newPassword)
                    .name(user.getName())
                    .build();
            userClient.changeInformationUserWithToken(accessToken, newUser);
            int statusCode = response.extract().statusCode();
            boolean isChanged= response.extract().path("success");
            String userName = response.extract().path("user.name");
            assertEquals("Incorrect status code", 200, statusCode);
            assertTrue("Information wasn't changed",isChanged);
            assertEquals("Name not changed", user.getName(), userName);
        }

        @Test
        @DisplayName("Changing user")
        @Description("Test for change information about user with authorization {name}")
        public void changeNameWithAuthorizationTest() {
            String newName = User.getRandomData();
            User newUser = User.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .name(newName)
                    .build();
            userClient.changeInformationUserWithToken(accessToken, newUser);
            int statusCode = response.extract().statusCode();
            boolean isChanged= response.extract().path("success");
            String userName = response.extract().path("user.name");
            assertEquals("Incorrect status code", 200, statusCode);
            assertTrue("Information wasn't changed",isChanged);
            assertEquals(user.getName(), userName);
        }

        @Test
        @DisplayName("Changing user")
        @Description("Test for change information about user with authorization {exactly the same email}")
        public void changeEmailOnExactlyWithAuthorizationTest() {
            String exactlyUserEmail = user.getEmail();
            User newUser = User.builder()
                    .email(exactlyUserEmail)
                    .password(user.getPassword())
                    .name(user.getName())
                    .build();
            userClient.changeInformationUserWithToken(accessToken, newUser);
            int statusCode = response.extract().statusCode();
            boolean isNotChanged = response.extract().path("success");
            String message = response.extract().path("message");
            assertEquals("Incorrect status code", 403, statusCode);
            assertFalse("Information was changed",isNotChanged);
            assertEquals("User with such email already exists", message);
        }
    }