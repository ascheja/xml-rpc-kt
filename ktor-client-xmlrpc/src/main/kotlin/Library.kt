/*
 * Copyright (c) 2024 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.ByteArrayContent
import org.ascheja.xmlrpc.protocol.MethodCall
import org.ascheja.xmlrpc.protocol.MethodResponse
import org.ascheja.xmlrpc.protocol.MethodResponseFault
import org.ascheja.xmlrpc.protocol.writeTo
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

public class XmlRpcFault(public val methodResponse: MethodResponseFault) : RuntimeException()

public suspend fun HttpClient.xmlRpc(
    urlString: String,
    methodCall: MethodCall,
    throwOnFault: Boolean = true
): MethodResponse {
    val response = post(urlString) {
        header(HttpHeaders.Accept, ContentType.Application.Xml)
        setBody(
            ByteArrayContent(
                ByteArrayOutputStream().use {
                    methodCall.toDocument().writeTo(it)
                    it.toByteArray()
                },
                ContentType.Application.Xml,
            )
        )
    }
    val methodResponse = ByteArrayInputStream(response.body<ByteArray>()).use { body ->
        MethodResponse.parse { it.parse(body) }
    }
    if (methodResponse is MethodResponseFault && throwOnFault) throw XmlRpcFault(methodResponse)
    return methodResponse
}
