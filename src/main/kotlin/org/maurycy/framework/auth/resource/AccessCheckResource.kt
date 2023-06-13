package org.maurycy.framework.auth.resource

import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.maurycy.framework.auth.model.AccessDto
import org.maurycy.framework.auth.model.UserDto
import org.maurycy.framework.auth.service.KeycloakService

@Path("access")
class AccessCheckResource(
    private val keycloakService: KeycloakService
) {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun access(accessDto: AccessDto): Boolean {
        val roles = keycloakService.getUserRoles(UserDto(accessDto.token))
        roles.forEach {
            if(accessDto.oneOfRoles.contains(it)){
                return true
            }
        }
        return false
    }
}