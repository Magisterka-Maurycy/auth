package org.maurycy.framework.auth.model

import org.keycloak.representations.AccessTokenResponse

data class LoginReturnDto(
    val accessToken: String,
    val expiresIn: Long,
    val refreshExpiresIn: Long,
    val refreshToken: String
) {
    constructor(tokenResponse: AccessTokenResponse) : this(
        accessToken = tokenResponse.token,
        expiresIn = tokenResponse.expiresIn,
        refreshExpiresIn = tokenResponse.refreshExpiresIn,
        refreshToken = tokenResponse.refreshToken
    )
}