/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.ktor.utils.io.jvm.javaio.toOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ascheja.xmlrpc.protocol.MethodCall
import org.ascheja.xmlrpc.protocol.MethodResponse
import org.ascheja.xmlrpc.protocol.MethodResponseFault
import org.ascheja.xmlrpc.protocol.writeTo

public class XmlRpcFault(public val methodResponse: MethodResponseFault) : RuntimeException()

public suspend fun HttpClient.xmlRpc(
    urlString: String,
    methodCall: MethodCall,
    throwOnFault: Boolean = true
): MethodResponse {
    val response = post(urlString) {
        header(HttpHeaders.Accept, ContentType.Application.Xml)
        setBody(
            object : OutgoingContent.WriteChannelContent() {

                override val contentType: ContentType = ContentType.Application.Xml

                override suspend fun writeTo(channel: ByteWriteChannel) {
                    withContext(Dispatchers.IO) {
                        methodCall.toDocument().writeTo(channel.toOutputStream())
                    }
                }
            }
        )
    }
    val methodResponse = withContext(Dispatchers.IO) {
        response.bodyAsChannel().toInputStream().use { body ->
            MethodResponse.parse { it.parse(body) }
        }
    }
    if (methodResponse is MethodResponseFault && throwOnFault) throw XmlRpcFault(methodResponse)
    return methodResponse
}
