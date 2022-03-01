/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.ktor.server

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.contentType
import io.ktor.request.receiveStream
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.Route
import io.ktor.routing.post
import org.ascheja.xmlrpc.protocol.MethodCall
import org.ascheja.xmlrpc.protocol.MethodResponse
import org.ascheja.xmlrpc.protocol.writeToByteArray

public fun Route.xmlRpc(path: String, handler: suspend (MethodCall) -> MethodResponse) {
    post(path) {
        if (call.request.contentType() != ContentType.Application.Xml) {
            return@post call.respond(HttpStatusCode.NotAcceptable, "")
        }
        val methodCall = call.receiveStream().use { input ->
            MethodCall.parse { it.parse(input) }
        }
        val methodResponse = handler(methodCall)
        call.respondBytes(ContentType.Application.Xml, HttpStatusCode.OK) {
            methodResponse.toDocument().writeToByteArray()
        }
    }
}
