package org.maurycy.framework.auth.service

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.RolesRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.LoginReturnDto
import org.maurycy.framework.auth.model.RefreshDto
import org.maurycy.framework.auth.model.RegisterDto
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

    fun initialize(): Boolean {
        if (keycloak.realms().findAll().map(RealmRepresentation::getRealm).contains(realmName)) {
            return false
        }
        val realm = buildRealmRepresentation()
        keycloak.realms().create(realm)
        Log.info("realm: ${realm.realm} created")
        return true
    }

    private fun buildRealmRepresentation(): RealmRepresentation {
        val realm = RealmRepresentation()
        realm.id = realmName
        realm.realm = realmName
        realm.isEnabled = true
        realm.users = listOf()
        realm.clients = buildClients()
        realm.groups = listOf()
        realm.roles = buildRolesRepresentation()
        return realm
    }

    private fun buildRolesRepresentation(): RolesRepresentation {
        val roles = RolesRepresentation()
        val userRole = RoleRepresentation("user", null, false)
        val adminRole = RoleRepresentation("admin", null, false)
        val realmRoles = listOf(userRole, adminRole)
        roles.realm = realmRoles
        return roles
    }

    private fun buildClients(): List<ClientRepresentation> {
        val client = ClientRepresentation()
        client.id = clientId
        client.clientId = clientId
        client.name = clientId
        client.isEnabled = true
        client.redirectUris = listOf("*")
        client.webOrigins = listOf()
        client.isDirectAccessGrantsEnabled = true
        client.protocol = "openid-connect"
        client.isPublicClient = true
        return listOf(client)
    }
}