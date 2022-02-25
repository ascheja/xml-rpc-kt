/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package net.ascheja.xmlrpc.protocol

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

internal class Serializer internal constructor(private val document: Document) {

    internal fun MethodCall.appendToElement(root: Node) {
        val methodCallElement = root.appendChild(createElement("methodCall"))
        methodCallElement.appendNewElementWithTextContent("methodName", methodName)
        val paramsElement = methodCallElement.appendNewElement("params")
        for (value in params) {
            val paramElement = paramsElement.appendNewElement("param")
            value.appendToElement(paramElement)
        }
    }

    internal fun MethodResponse.appendToElement(root: Node) {
        val methodResponse = root.appendChild(createElement("methodResponse"))
        when (this) {
            is MethodResponseFault -> {
                val faultElement = methodResponse.appendNewElement("fault")
                StructValue(
                    "faultCode" to IntegerValue(faultCode),
                    "faultString" to StringValue(faultString)
                ).appendToElement(faultElement)
            }
            is MethodResponseSuccess -> {
                val paramsElement = methodResponse.appendNewElement("params")
                for (value in params) {
                    val paramElement = paramsElement.appendNewElement("param")
                    value.appendToElement(paramElement)
                }
            }
        }
    }

    internal fun Value.appendToElement(parent: Node) {
        val valueElement = parent.appendNewElement("value")
        when (this) {
            is ArrayValue -> {
                val arrayElement = valueElement.appendNewElement("array")
                val dataElement = arrayElement.appendNewElement("data")
                for (value in data) {
                    value.appendToElement(dataElement)
                }
            }
            is Base64Value -> valueElement.appendNewElementWithTextContent("base64", value)
            is BooleanValue -> valueElement.appendNewElementWithTextContent("boolean", if (value) "1" else "0")
            is DateTimeIso8601Value -> valueElement.appendNewElementWithTextContent("datetime.iso8601", value)
            is DoubleValue -> valueElement.appendNewElementWithTextContent("double", value.toString())
            is IntegerValue -> valueElement.appendNewElementWithTextContent("int", value.toString())
            is StringValue -> valueElement.appendNewElementWithTextContent("string", value)
            is StructValue -> {
                val structElement = valueElement.appendNewElement("struct")
                for ((name, value) in members) {
                    val memberElement = structElement.appendNewElement("member")
                    memberElement.appendNewElementWithTextContent("name", name)
                    value.appendToElement(memberElement)
                }
            }
        }
    }

    private fun createElement(tagName: String) = document.createElement(tagName)

    private fun createElementWithTextContent(tagName: String, text: String): Element {
        return createElement(tagName).apply {
            textContent = text
        }
    }

    private fun Node.appendNewElement(tagName: String) = appendChild(createElement(tagName))

    private fun Node.appendNewElementWithTextContent(tagName: String, text: String) = appendChild(createElementWithTextContent(tagName, text))
}
