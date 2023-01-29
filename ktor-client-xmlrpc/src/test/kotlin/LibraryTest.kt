/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteReadPacket
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.runBlocking
import org.ascheja.xmlrpc.protocol.IntegerValue
import org.ascheja.xmlrpc.protocol.MethodCall
import org.ascheja.xmlrpc.protocol.MethodResponse
import org.ascheja.xmlrpc.protocol.MethodResponseFault
import org.ascheja.xmlrpc.protocol.MethodResponseSuccess
import org.ascheja.xmlrpc.protocol.writeToByteArray
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class LibraryTest {

    companion object {
        private const val serviceUrl = "https://some-service/some-path"
    }

    @Test
    fun `successful response is returned`() {
        val methodCall = MethodCall("whatIsTheQuestion")
        val methodResponse = MethodResponseSuccess(IntegerValue(42))
        val client = HttpClient(
            createMockEngine(methodCall, methodResponse)
        )
        assertEquals(
            methodResponse,
            runBlocking { client.xmlRpc(serviceUrl, methodCall) }
        )
    }

    @Test
    fun `fault response is returned if throwOnFault is deactivated`() {
        val methodCall = MethodCall("whatIsTheQuestion")
        val methodResponse = MethodResponseFault(42, "no more numbers")
        val client = HttpClient(
            createMockEngine(methodCall, methodResponse)
        )
        assertEquals(
            methodResponse,
            runBlocking { client.xmlRpc(serviceUrl, methodCall, false) }
        )
    }

    @Test
    fun `fault response is thrown if throwOnFault is activated`() {
        val methodCall = MethodCall("whatIsTheQuestion")
        val methodResponse = MethodResponseFault(42, "no more numbers")
        val client = HttpClient(
            createMockEngine(methodCall, methodResponse)
        )
        try {
            runBlocking { client.xmlRpc(serviceUrl, methodCall, true) }
            fail("shouldn't go here")
        } catch (e: XmlRpcFault) {
            assertEquals(
                methodResponse,
                e.methodResponse
            )
        }
    }

    private fun createMockEngine(expectedCall: MethodCall, response: MethodResponse): MockEngine {
        return MockEngine { request ->
            assertEquals(serviceUrl, request.url.toString())
            assertEquals(ContentType.Application.Xml, request.body.contentType)
            val methodCall = MethodCall.parse {
                it.parse(
                    ByteArrayInputStream(
                        runBlocking {
                            when (val body = request.body) {
                                is OutgoingContent.ByteArrayContent -> body.bytes()
                                is OutgoingContent.NoContent, is OutgoingContent.ProtocolUpgrade -> byteArrayOf()
                                is OutgoingContent.ReadChannelContent -> body.toByteReadPacket().readBytes()
                                is OutgoingContent.WriteChannelContent -> body.toByteReadPacket().readBytes()
                            }
                        }
                    )
                )
            }
            assertEquals(
                expectedCall,
                methodCall
            )
            respond(
                response.toDocument().writeToByteArray(),
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, ContentType.Application.Xml.toString())
            )
        }
    }
}
