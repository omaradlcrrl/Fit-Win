package org.example.fitwinkmp.core.error

class ApiException(val statusCode: Int, override val message: String) : Exception(message)

class NetworkException(override val message: String = "Sin conexión a internet") : Exception(message)
