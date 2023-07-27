package org.maurycy.framework.auth.resource

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.maurycy.framework.auth.model.LoginDto
import org.maurycy.framework.auth.model.LoginReturnDto
import org.maurycy.framework.auth.model.RefreshDto
import org.maurycy.framework.auth.model.RegisterDto
import org.maurycy.framework.auth.service.KeycloakService

@Path("keycloak")
class KeycloakResource(
    private val keycloakService: KeycloakService
) {

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun login(loginDto: LoginDto): LoginReturnDto = keycloakService.login(loginDto)

    @POST
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun refresh(refreshDto: RefreshDto): Response? = keycloakService.refresh(refreshDto)

    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun register(registerDto: RegisterDto): Response = keycloakService.register(registerDto)
}