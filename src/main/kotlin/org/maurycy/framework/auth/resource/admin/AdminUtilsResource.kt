package org.maurycy.framework.auth.resource.admin

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.maurycy.framework.auth.model.ResetUserDto
import org.maurycy.framework.auth.service.KeycloakService


@Path("utils")
@RolesAllowed("admin")
class AdminUtilsResource(
    private val keycloakService: KeycloakService
)  {
    @POST
    @Path("reset")
    fun resetUserWithEmail(resetUser: ResetUserDto){
        return keycloakService.resetUserWithEmail(resetUser.email)
    }
}