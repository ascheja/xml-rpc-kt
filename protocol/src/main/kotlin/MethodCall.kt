/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.protocol

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilder

public data class MethodCall(
    val methodName: String,
    val params: List<Value>
) {
    public constructor(methodName: String, vararg params: Value) : this(methodName, params.toList())

    public fun toDocument(): Document {
        val document = createDocumentBuilder().newDocument()
        with(Serializer(document)) {
            appendToElement(document)
        }
        return document
    }

    public companion object {
        public fun parse(block: (DocumentBuilder) -> Document): MethodCall {
            return Deserializer().parseMethodCall(block(createDocumentBuilder()).documentElement)
        }
    }
}
