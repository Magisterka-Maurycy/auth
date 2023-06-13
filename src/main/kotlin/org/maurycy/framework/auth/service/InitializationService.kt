package org.maurycy.framework.auth.service

import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes

@Suppress("unused")
@ApplicationScoped
class InitializationService(
    private val keycloakService: KeycloakService
) {

    fun initializeKeycloak(@Observes startupEvent: StartupEvent) {
        Log.info("startup event start")
        keycloakService.initialize()
        Log.info("startup event finished")
    }
}