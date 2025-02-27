import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestApiTest {

    private static final Logger logger = LoggerFactory.getLogger(RestApiTest.class);

    @BeforeClass
    public void setup() {
        logger.info("Setting up base URI for the API.");
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void testGetUsersAPI() {
        logger.info("Starting test: testGetUsersAPI.");

        Response response = given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();

        logger.info("GET /users request completed with status code: {}", response.getStatusCode());

        List<Map<String, String>> users = response.jsonPath().getList("");
        logger.info("Parsed response into user list. Total users: {}", users.size());

        logger.info("----- User Names and Emails -----");
        for (Map<String, String> user : users) {
            String name = user.get("name");
            String email = user.get("email");
            logger.info("{} | {}", name, email);
        }

        String firstEmail = users.getFirst().get("email");
        Assert.assertTrue(firstEmail.contains("@"), "First email address does not contain '@'");
        logger.info("Test completed successfully: testGetUsersAPI.");
    }
}