/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package net.ascheja.xmlrpc.protocol

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder

public sealed interface MethodResponse {

    public fun toDocument(): Document {
        val document = createDocumentBuilder().newDocument()
        with(Serializer(document)) {
            appendToElement(document)
        }
        return document
    }

    public companion object {
        public fun parse(block: (DocumentBuilder) -> Document): MethodResponse {
            return Deserializer().parseMethodResponse(block(createDocumentBuilder()).documentElement)
        }
    }
}

public data class MethodResponseSuccess(
    val params: List<Value>
) : MethodResponse {

    public constructor(vararg params: Value) : this(params.toList())
}

public data class MethodResponseFault(
    val faultCode: Int,
    val faultString: String
) : MethodResponse
