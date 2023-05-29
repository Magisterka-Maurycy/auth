package org.maurycy.framework.auth.resource

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager
import org.maurycy.framework.auth.service.KeycloakService

@QuarkusTest
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class KeycloakServiceTest {
    @Inject
    @field: Default
    lateinit var keycloakService: KeycloakService

    @Test
    fun serviceInitTest() {
        val result = keycloakService.initialize()
        Assertions.assertFalse(result)
    }
}