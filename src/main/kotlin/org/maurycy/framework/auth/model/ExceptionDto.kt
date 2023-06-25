package org.maurycy.framework.auth.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class ExceptionDto(val exceptionMessage: String)