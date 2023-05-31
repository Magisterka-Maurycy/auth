package org.maurycy.framework.auth.resource

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.ws.rs.core.MediaType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.LoginReturnDto
import org.maurycy.framework.auth.model.RefreshDto
import org.maurycy.framework.auth.model.RegisterDto
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager


@QuarkusTest
@TestHTTPEndpoint(KeycloakResource::class)
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class KeycloakResourceTest {
    @Test
    fun register() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("user", "password", "test@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
    }

    @Test
    fun registerConflict() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("registerConflict", "password", "registerConflict@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("registerConflict", "password", "registerConflict@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(409)
    }

    @Test
    fun login() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("login", "password", "login@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginDto("login", "password"))
            .`when`()
            .post("login")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                CoreMatchers.containsString("accessToken"),
                CoreMatchers.containsString("expiresIn"),
                CoreMatchers.containsString("refreshExpiresIn"),
                CoreMatchers.containsString("refreshToken")
            )
    }

    @Test
    fun refresh() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RegisterDto("refresh", "password", "refresh@email"))
            .`when`()
            .post("register")
            .then()
            .statusCode(201)
        val loginReturnDto = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginDto("refresh", "password"))
            .`when`()
            .post("login")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                CoreMatchers.containsString("accessToken"),
                CoreMatchers.containsString("expiresIn"),
                CoreMatchers.containsString("refreshExpiresIn"),
                CoreMatchers.containsString("refreshToken")
            )
            .extract().body().`as`(LoginReturnDto::class.java)

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RefreshDto(loginReturnDto.refreshToken))
            .`when`()
            .post("refresh")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
//                TODO: refresh returns are needed to be transformed
                CoreMatchers.containsString("access_token"),
                CoreMatchers.containsString("expires_in"),
                CoreMatchers.containsString("refresh_expires_in"),
                CoreMatchers.containsString("refresh_token")
            )

    }

    @Test
    fun refreshFail() {
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(RefreshDto("failRefreshToken"))
            .`when`()
            .post("refresh")
            .then()
            .statusCode(400)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                CoreMatchers.containsString("exceptionMessage")
            )
    }


}