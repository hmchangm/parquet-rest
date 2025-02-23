package tw.idv.brandy

import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

@QuarkusTest
class QueryResourceTest {
    @Test
    fun testQueryEndpoint() {
        Given {
            body("{}")
            header("Content-Type", "application/json")
        } When {
            post("/query/get-all")
        } Then {
            statusCode(200)
        }
    }
}
