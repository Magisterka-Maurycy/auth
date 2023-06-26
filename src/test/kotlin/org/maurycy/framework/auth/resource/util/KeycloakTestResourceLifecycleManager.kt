package org.maurycy.framework.auth.resource.util

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait


class KeycloakTestResourceLifecycleManager : QuarkusTestResourceLifecycleManager {
    private val keycloak: GenericContainer<*> = GenericContainer(KEYCLOAK_DOCKER_IMAGE)
        .withExposedPorts(8080)
        .withEnv("DB_VENDOR", "H2")
        .withEnv("KEYCLOAK_ADMIN", "admin")
        .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
        .waitingFor(Wait.forHttp("/realms/quarkus").forStatusCode(200).forPort(8080))

    override fun start(): Map<String, String> {
        keycloak.start()
        val keycloakServerUrl = "http://host.docker.internal:" + keycloak.getMappedPort(8080)

        val conf: MutableMap<String, String> = HashMap()
        conf["quarkus.keycloak.admin-client.server-url"] = keycloakServerUrl
        conf["quarkus.rest-client.refresh-api.url"] = keycloakServerUrl
        conf["quarkus.oidc.auth-server-url"] = "$keycloakServerUrl/realms/quarkus"
        conf["quarkus.oidc.token.issuer"] = "$keycloakServerUrl/realms/quarkus"
        return conf
    }

    override fun stop() {
        keycloak.stop()
    }

    companion object {
        private const val KEYCLOAK_DOCKER_IMAGE: String = "quay.io/maurycy_krzeminski/keycloak"
    }
}