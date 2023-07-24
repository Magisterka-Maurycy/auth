package org.maurycy.framework.auth.resource.admin

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.RestAssured
import jakarta.ws.rs.core.MediaType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.model.RegisterDto
import org.maurycy.framework.auth.model.RegisterStartDto
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager

@QuarkusTest
@TestHTTPEndpoint(AdminUtilsResource::class)
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class AdminUtilsResourceTest {

    @Test
    @TestSecurity(user = "testUser", roles = ["admin", "user"])
    fun registerWithoutPassword() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterStartDto("user1",  "test@email"))
            .`when`()
            .post("register-without-password")
            .then()
            .statusCode(201)
            .body(
                CoreMatchers.`is`("")
            )
    }
    @Test
    @TestSecurity(user = "testUser", roles = ["admin", "user"])
    fun registerWithoutPasswordWrongEmail() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterStartDto("user2",  "test"))
            .`when`()
            .post("register-without-password")
            .then()
            .statusCode(400)
             .body(
                CoreMatchers.`is`("{\"errorMessage\":\"Invalid email address.\"}")
                )
    }
}