package org.maurycy.framework.auth.resource

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.RestAssured
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.core.MediaType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.model.CreateRoleDto
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.RegisterDto
import org.maurycy.framework.auth.model.UserDto
import org.maurycy.framework.auth.model.UserToRoleDto
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager
import org.maurycy.framework.auth.service.KeycloakService

@QuarkusTest
@TestHTTPEndpoint(RolesResource::class)
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class RolesResourceTest {

    @Inject
    @field: Default
    lateinit var keycloakService: KeycloakService

    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    fun getAllRolesTest() {
        RestAssured.`when`()
            .get()
            .then()
            .statusCode(200)
            .body(
                CoreMatchers.containsString("user"),
                CoreMatchers.containsString("admin"),
            )

    }

    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    fun createRoleTest() {
        val roleName = "RoleName"
        RestAssured.given()
            .body(CreateRoleDto(roleName, "RoleDescription"))
            .contentType(MediaType.APPLICATION_JSON)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .body(
                CoreMatchers.containsString("true"),
            )
        //cleanup
        keycloakService.deleteRole(roleName)

    }


    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    fun deleteRoleTest1() {
        val roleName = "RoleName"
        keycloakService.createRole(CreateRoleDto(roleName, "RoleDescription"))
        RestAssured.given()
            .body(CreateRoleDto(roleName, "RoleDescription"))
            .contentType(MediaType.APPLICATION_JSON)
            .`when`()
            .delete(roleName)
            .then()
            .statusCode(204)
    }

    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    @Disabled
    fun deleteRoleTest2() {
        val roleName = "RoleName"
        RestAssured.given()
            .body(CreateRoleDto(roleName, "RoleDescription"))
            .contentType(MediaType.APPLICATION_JSON)
            .`when`()
            .delete(roleName)
            .then()
            .statusCode(204)

    }

    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    fun userToRoleTest() {
        val roleName = "RoleName"
        val userName = "userToRoleTest"
        val email = "$userName@a.com"
        keycloakService.createRole(CreateRoleDto(roleName, "RoleDescription"))
        keycloakService.register(RegisterDto(userName, "b", email))

        RestAssured.given()
            .body(UserToRoleDto(user = userName, role = roleName))
            .contentType(MediaType.APPLICATION_JSON)
            .`when`()
            .post("users")
            .then()
            .statusCode(200)
        keycloakService.deleteRole(roleName)
    }

    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    fun removeRoleFromUserTest() {
        val roleName = "RoleName"
        val userName = "removeRoleFromUserTest"
        val email = "$userName@a.com"
        keycloakService.createRole(CreateRoleDto(roleName, "RoleDescription"))
        keycloakService.register(RegisterDto(userName, "b", email))

        RestAssured.given()
            .body(UserToRoleDto(user = userName, role = roleName))
            .contentType(MediaType.APPLICATION_JSON)
            .`when`()
            .delete("users")
            .then()
            .statusCode(200)
        keycloakService.deleteRole(roleName)
    }

    @Test
    @TestSecurity(user = "testUser", roles = ["admin"])
    fun getRolesTest() {
        val userName = "getRolesTest"
        val email = "$userName@a.com"
        val password = "abc"
        keycloakService.register(RegisterDto(userName, password, email))
        val token = keycloakService.login(LoginDto(userName, password)).accessToken
        RestAssured.given()
            .body(UserDto(token))
            .contentType(MediaType.APPLICATION_JSON)
            .`when`()
            .post("token")
            .then()
            .statusCode(200)
            .body(
                CoreMatchers.containsString("user"),
                CoreMatchers.containsString("offline_access"),
                CoreMatchers.containsString("default-roles-quarkus"),
                CoreMatchers.containsString("uma_authorization")
            )
    }


}