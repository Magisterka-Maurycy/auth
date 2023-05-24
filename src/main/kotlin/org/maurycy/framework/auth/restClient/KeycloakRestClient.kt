package org.maurycy.framework.auth.restClient

import java.util.Base64
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestForm


@Path("/realms/quarkus/protocol/openid-connect/token")
@RegisterRestClient(configKey="refresh-api")
@ClientHeaderParam(name = "Authorization", value = ["{lookupAuth}"])
interface KeycloakRestClient {
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    fun refreshToken(@RestForm("grant_type") grantType:String, @RestForm("refresh_token") refreshToken:String ): Response?

    @Suppress("unused")
    fun lookupAuth(): String {
        return "Basic " +
                Base64.getEncoder().encodeToString("backend-service:".toByteArray())
    }
}