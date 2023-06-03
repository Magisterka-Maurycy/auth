package org.maurycy.framework.auth.model

data class AccessDto (val token: String, val oneOfRoles: List<String>)
