/*
 * Copyright (c) 2024 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.ktor.server

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.contentType
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.ascheja.xmlrpc.protocol.MethodCall
import org.ascheja.xmlrpc.protocol.MethodResponse
import org.ascheja.xmlrpc.protocol.writeToByteArray
import java.io.ByteArrayInputStream

public fun Route.xmlRpc(path: String, handler: suspend ApplicationCall.(MethodCall) -> MethodResponse) {
    post(path) {
        if (call.request.contentType() != ContentType.Application.Xml) {
            return@post call.respond(HttpStatusCode.NotAcceptable, "")
        }
        val methodCall = ByteArrayInputStream(call.receive<ByteArray>()).use { input ->
            MethodCall.parse { it.parse(input) }
        }
        val methodResponse = call.handler(methodCall)
        call.respondBytes(ContentType.Application.Xml, HttpStatusCode.OK) {
            methodResponse.toDocument().writeToByteArray()
        }
    }
}
