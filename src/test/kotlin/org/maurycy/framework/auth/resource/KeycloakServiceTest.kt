package org.maurycy.framework.auth.resource

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager
import org.maurycy.framework.auth.service.KeycloakService
import org.wildfly.common.Assert

@QuarkusTest
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class KeycloakServiceTest {
    @Inject
    @field: Default
    lateinit var keycloakService: KeycloakService

    @Test
    fun serviceRolesTest() {
        val roles = keycloakService.getRoles()
        Assert.assertTrue(roles.contains("admin"))
        Assert.assertTrue(roles.contains("user"))
    }
}