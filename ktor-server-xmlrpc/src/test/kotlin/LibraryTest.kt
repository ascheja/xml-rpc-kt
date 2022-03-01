/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.ktor.server

import io.ktor.application.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.ascheja.xmlrpc.protocol.BooleanValue
import org.ascheja.xmlrpc.protocol.MethodCall
import org.ascheja.xmlrpc.protocol.MethodResponse
import org.ascheja.xmlrpc.protocol.MethodResponseSuccess
import org.ascheja.xmlrpc.protocol.writeToByteArray
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class LibraryTest {

    private lateinit var handler: suspend (MethodCall) -> MethodResponse

    private val moduleFn: Application.() -> Unit = {
        routing {
            xmlRpc("/xmlrpc", handler)
        }
    }

    @Test
    fun `handler is called with methodCall and methodResponse is returned`() {
        val methodCall = MethodCall("test")
        val methodResponse = MethodResponseSuccess(BooleanValue(true))
        handler = {
            assertEquals(methodCall, it)
            methodResponse
        }
        withTestApplication(moduleFn) {
            val call = handleRequest(HttpMethod.Post, "/xmlrpc") {
                addHeader(HttpHeaders.ContentType, "application/xml")
                setBody(methodCall.toDocument().writeToByteArray())
            }
            assertEquals(
                methodResponse,
                MethodResponse.parse { it.parse(ByteArrayInputStream(call.response.byteContent)) }
            )
        }
    }
}
