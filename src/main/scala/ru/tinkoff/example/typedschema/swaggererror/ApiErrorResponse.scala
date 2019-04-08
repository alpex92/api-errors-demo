package ru.tinkoff.example.typedschema.swaggererror

import io.circe.generic.JsonCodec

import org.manatki.derevo.derive
import org.manatki.derevo.tschemaInstances.swagger

// TODO: Support different response types

@JsonCodec
@derive(swagger)
case class ApiErrorResponse(description: String, code: Option[Int] = None)

object ApiErrorResponse