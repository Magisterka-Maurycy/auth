package org.maurycy.framework.auth.resource

import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.maurycy.framework.auth.model.AccessDto
import org.maurycy.framework.auth.model.ResetUserDto
import org.maurycy.framework.auth.model.UserDto
import org.maurycy.framework.auth.service.KeycloakService

@Path("util")
class UtilResource(
    private val keycloakService: KeycloakService
) {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("access")
    suspend fun accessCheck(accessDto: AccessDto): Boolean {
        val roles = keycloakService.getUserRoles(UserDto(accessDto.token))
        roles.forEach {
            if (accessDto.oneOfRoles.contains(it)) {
                return true
            }
        }
        return false
    }

    @POST
    @Path("forget-password")
    suspend fun resetUserWithEmail(resetUser: ResetUserDto): Boolean {
        return keycloakService.resetUserWithEmail(resetUser.email)
    }

}