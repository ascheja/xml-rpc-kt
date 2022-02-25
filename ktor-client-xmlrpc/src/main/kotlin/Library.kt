/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package net.ascheja.xmlrpc.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ascheja.xmlrpc.protocol.MethodCall
import net.ascheja.xmlrpc.protocol.MethodResponse
import net.ascheja.xmlrpc.protocol.MethodResponseFault
import net.ascheja.xmlrpc.protocol.writeToByteArray

public class XmlRpcFault(public val methodResponse: MethodResponseFault) : RuntimeException()

public suspend fun HttpClient.xmlRpc(urlString: String, methodCall: MethodCall, throwOnFault: Boolean = true): MethodResponse {
    val response = post<HttpResponse>(urlString) {
        header(HttpHeaders.Accept, ContentType.Application.Xml)
        contentType(ContentType.Application.Xml)
        body = methodCall.toDocument().writeToByteArray()
    }
    val methodResponse = withContext(Dispatchers.IO) {
        MethodResponse.parse { it.parse(response.content.toInputStream()) }
    }
    if (methodResponse is MethodResponseFault && throwOnFault) throw XmlRpcFault(methodResponse)
    return methodResponse
}
