package tw.idv.brandy

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test

@QuarkusTest
class GreetingResourceTest {
    @Test
    fun testHelloEndpoint() {
        given()
            .body("{}")
            .header("Content-Type", "application/json")
            .`when`()
            .post("/query/get-all")
            .then()
            .statusCode(200)
    }
}
