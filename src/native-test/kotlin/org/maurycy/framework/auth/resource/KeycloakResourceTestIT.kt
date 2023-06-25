package org.maurycy.framework.auth.resource

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusIntegrationTest
import org.maurycy.framework.auth.resource.util.KeycloakTestResourceLifecycleManager

@QuarkusIntegrationTest
@QuarkusTestResource(KeycloakTestResourceLifecycleManager::class)
class KeycloakResourceTestIT : KeycloakResourceTest()
