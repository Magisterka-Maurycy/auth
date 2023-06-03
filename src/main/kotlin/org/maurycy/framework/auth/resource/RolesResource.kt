package org.maurycy.framework.auth.resource

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.keycloak.representations.idm.RoleRepresentation
import org.maurycy.framework.auth.model.CreateRoleDto
import org.maurycy.framework.auth.model.UserDto
import org.maurycy.framework.auth.model.UserToRoleDto
import org.maurycy.framework.auth.service.KeycloakService

@Path("roles")
@RolesAllowed("admin")
class RolesResource(
    private val keycloakService: KeycloakService
) {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllRoles(): List<RoleRepresentation> {
        return keycloakService.getRoles()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun createRole(createRoleDto: CreateRoleDto): Boolean {
        return keycloakService.createRole(createRoleDto)
    }

    @DELETE
    @Path("{name}")
    fun deleteRole(@PathParam("name") name: String) {
        return keycloakService.deleteRole(name)
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("users")
    fun userToRole(userToRoleDto: UserToRoleDto) {
        return keycloakService.addRoleToUser(userName = userToRoleDto.user, roleName = userToRoleDto.role)
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("users")
    fun removeRoleFromUser(userToRoleDto: UserToRoleDto) {
        return keycloakService.removeRoleFromUser(userName = userToRoleDto.user, roleName = userToRoleDto.role)
    }

    @POST
    @Path("token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun getRoles(userDto: UserDto): List<Any?> {
        return keycloakService.getUserRoles(userDto)
    }


}