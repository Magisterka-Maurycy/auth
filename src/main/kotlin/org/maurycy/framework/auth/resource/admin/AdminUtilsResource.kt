package org.maurycy.framework.auth.resource.admin

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.maurycy.framework.auth.model.RegisterStartDto
import org.maurycy.framework.auth.model.ResetUserDto
import org.maurycy.framework.auth.service.KeycloakService


@Path("admin/util")
@RolesAllowed("admin")
class AdminUtilsResource(
    private val keycloakService: KeycloakService
)  {
    @POST
    @Path("register-without-password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun registerStart(registerDto: RegisterStartDto): Response = keycloakService.registerStart(registerDto)
}