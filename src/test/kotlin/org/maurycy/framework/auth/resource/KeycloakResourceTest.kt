package org.maurycy.framework.auth.resource

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.ws.rs.core.MediaType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.RegisterDto


@QuarkusTest
@TestHTTPEndpoint(KeycloakResource::class)
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class KeycloakResourceTest {
    @Test
    fun register() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("user","password","test@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
    }

    @Test
    fun registerConflict() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("registerConflict","password","registerConflict@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("registerConflict","password","registerConflict@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(409)
    }

    @Test
    fun login(){
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("login","password","login@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginDto("login","password"))
            .`when`()
            .post("login")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(CoreMatchers.containsString("accessToken"),
                CoreMatchers.containsString("expiresIn"),
                CoreMatchers.containsString("refreshExpiresIn"),
                CoreMatchers.containsString("refreshToken"))
    }

    @Test
    fun refresh(){

    }


}