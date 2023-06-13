package org.maurycy.framework.auth.resource

import io.quarkus.logging.Log
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.core.MediaType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.model.AccessDto
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.RegisterDto
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager
import org.maurycy.framework.auth.service.KeycloakService

@QuarkusTest
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
@TestHTTPEndpoint(AccessCheckResource::class)
class AccessCheckResourceTest {

    @Inject
    @field: Default
    lateinit var keycloakService: KeycloakService


    @Test
    fun falseNoRole() {
        val res = keycloakService.login(LoginDto(userName, password))
        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(AccessDto(res.accessToken, listOf()))
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .body(
                CoreMatchers.`is`("false")
            )
    }

    @Test
    fun trueUserRole() {
        val res = keycloakService.login(LoginDto(userName, password))

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(AccessDto(res.accessToken, listOf("user")))
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .body(
                CoreMatchers.`is`("true")
            )
    }
    @Test
    fun falseAdminRole() {
        val res = keycloakService.login(LoginDto(userName, password))

        RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(AccessDto(res.accessToken, listOf("admin")))
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .body(
                CoreMatchers.`is`("false")
            )
    }


        val userName = "us"
        val password = "pass"
        val email = "us@us.com"
        @BeforeEach
        fun before() {
            Log.info("before start")
            if(!set) {
                keycloakService.initialize()
                keycloakService.register( RegisterDto(
                    userName,
                    password,
                    email
                ))
                set = true
                Log.info("registered user")
            }
            Log.info("before end")
        }
    companion object{
        var set = false
    }

}