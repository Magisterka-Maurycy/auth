package org.maurycy.framework.auth.service

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.maurycy.framework.auth.model.CreateRoleDto
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.LoginReturnDto
import org.maurycy.framework.auth.model.RefreshDto
import org.maurycy.framework.auth.model.RegisterDto
import org.maurycy.framework.auth.model.UserDto
import org.maurycy.framework.auth.restClient.KeycloakRestClient


@ApplicationScoped
class KeycloakService(
    private val keycloak: Keycloak,
    @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
    private val serverUrl: String,
    @RestClient
    private val keycloakRestClient: KeycloakRestClient,
    @ConfigProperty(name = "clientId", defaultValue = "backend-service")
    private val clientId: String,
    @ConfigProperty(name = "realmName", defaultValue = "quarkus")
    private val realmName: String
) {

    fun login(loginDto: LoginDto): LoginReturnDto {
        val response = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .clientId(clientId)
            .realm(realmName)
            .grantType(CredentialRepresentation.PASSWORD)
            .username(loginDto.userName)
            .password(loginDto.password)
            .build().tokenManager().grantToken()
        Log.info(response)
        return LoginReturnDto(response)
    }

    fun refresh(refreshDto: RefreshDto): Response? {
        return keycloakRestClient.refreshToken("refresh_token", refreshDto.refreshToken)
    }

    fun register(registerDto: RegisterDto): Response {
        val realmResource = keycloak.realm(realmName)
        val userRepresentation = UserRepresentation()
        userRepresentation.username = registerDto.userName
        userRepresentation.email = registerDto.email
        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = registerDto.password
        userRepresentation.credentials = listOf(credentialRepresentation)
        userRepresentation.isEnabled = true
        val response = realmResource.users()
            .create(userRepresentation)
        val userId = CreatedResponseUtil.getCreatedId(response)
        val userResource = realmResource.users()[userId]

        val userRealmRole = realmResource.roles()["user"].toRepresentation()
        userResource.roles().realmLevel() //
            .add(listOf(userRealmRole))
        Log.info(response)
        return response
    }


    fun getRoles(): List<String> {
        return keycloak.realm(realmName).roles().list().toList().map {
            it.name
        }
    }

    fun createRole(createRoleDto: CreateRoleDto): Boolean {
        val role = RoleRepresentation()
        role.name = createRoleDto.name
        role.description = createRoleDto.description

        keycloak.realm(realmName).roles().list().forEach {
            if (it.name == role.name) {
                return false
            }
        }
        keycloak.realm(realmName).roles().create(role)
        return true
    }

    fun deleteRole(name: String) {
        return keycloak.realm(realmName).roles().deleteRole(name)
    }

    fun getUserRoles(userDto: UserDto): List<Any?> {
        return io.vertx.ext.auth.impl.jose.JWT.parse(userDto.token)
            .getJsonObject("payload")
            .getJsonObject("realm_access")
            .getJsonArray("roles").list
    }

    fun addRoleToUser(userName: String, roleName: String) {
        val id = keycloak.realm(realmName).users().search(userName, true)[0].id
        val userResource = keycloak.realm(realmName).users()[id]

        val roleRepresentationList: List<RoleRepresentation> = userResource.roles().realmLevel().listAvailable()

        for (roleRepresentation in roleRepresentationList) {
            if (roleRepresentation.name == roleName) {
                userResource.roles().realmLevel().add(listOf(roleRepresentation))
                break
            }
        }
    }

    fun removeRoleFromUser(userName: String, roleName: String) {
        val id = keycloak.realm(realmName).users().search(userName, true)[0].id
        val userResource = keycloak.realm(realmName).users()[id]
        val roleRepresentationList: List<RoleRepresentation> = userResource.roles().realmLevel().listAll()
        for (roleRepresentation in roleRepresentationList) {
            Log.info(roleRepresentation.name)
            if (roleRepresentation.name == roleName) {
                userResource.roles().realmLevel().remove(listOf(roleRepresentation))
                Log.info("role removed")
            }
        }
    }
}