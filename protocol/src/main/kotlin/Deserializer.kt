/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.protocol

import org.w3c.dom.Element

internal class Deserializer internal constructor() {

    companion object {

        private fun Element.associateChildren(): Map<String, Element> = buildMap {
            for (i in 0 until childNodes.length) {
                val child = childNodes.item(i) as? Element ?: continue
                put(child.tagName, child)
            }
        }

        private fun <T> Element.mapChildren(block: (Element) -> T): List<T> = buildList {
            for (i in 0 until childNodes.length) {
                val child = childNodes.item(i) as? Element ?: continue
                add(block(child))
            }
        }
    }

    internal fun parseMethodCall(methodCallElement: Element): MethodCall {
        if (methodCallElement.tagName != "methodCall") error("not a method call")
        val children = methodCallElement.associateChildren()
        return MethodCall(
            children["methodName"]?.textContent ?: error("missing methodName"),
            children["params"]?.mapChildren { paramElement ->
                if (paramElement.tagName != "param") error("expecting <param>, got <${paramElement.tagName}>")
                parseValue(paramElement.associateChildren()["value"] ?: error("<param> missing a <value>"))
            } ?: emptyList()
        )
    }

    internal fun parseMethodResponse(methodResponseElement: Element): MethodResponse {
        if (methodResponseElement.tagName != "methodResponse") error("not a method response")
        val children = methodResponseElement.associateChildren()
        if (children.size != 1) error("<methodResponse> with invalid size: ${children.size}")
        val (tagName, element) = children.entries.first()
        return when (tagName) {
            "fault" -> {
                val faultStruct = parseValue(
                    element.associateChildren()["value"] ?: error("missing <value> in <fault>")
                ) as? StructValue ?: error("expected <fault>'s <value> to be a <struct>")
                MethodResponseFault(
                    (faultStruct.members["faultCode"] as? IntegerValue)?.value ?: error("missing faultCode in <fault>"),
                    (faultStruct.members["faultString"] as? StringValue)?.value ?: error("missing faultString in <fault>")
                )
            }
            "params" -> {
                MethodResponseSuccess(
                    element.mapChildren { paramElement ->
                        if (paramElement.tagName != "param") error("expecting <param>, got <${paramElement.tagName}>")
                        parseValue(paramElement.associateChildren()["value"] ?: error("<param> missing a <value>"))
                    }
                )
            }
            else -> error("unexpected <$tagName> in <methodResponse>")
        }
    }

    internal fun parseValue(valueElement: Element): Value {
        val children = valueElement.associateChildren()
        if (children.size != 1) error("<value> with invalid size: ${children.size}")
        val (tagName, element) = children.entries.first()
        return when (tagName) {
            "i4", "int" -> IntegerValue(element.textContent.toInt())
            "string" -> StringValue(element.textContent)
            "boolean" -> BooleanValue(element.textContent.toInt() == 1)
            "double" -> DoubleValue(element.textContent.toDouble())
            "datetime.iso8601" -> DateTimeIso8601Value(element.textContent)
            "base64" -> Base64Value(element.textContent)
            "struct" -> StructValue(
                element.mapChildren { memberElement ->
                    val memberChildren = memberElement.associateChildren()
                    Pair(
                        memberChildren["name"]?.textContent ?: error("<member> missing <name>"),
                        parseValue(memberChildren["value"] ?: error("<member> missing <value>"))
                    )
                }.toMap()
            )
            "array" -> {
                val dataElement = element.associateChildren()["data"] ?: error("missing <data> element in <array>")
                ArrayValue(dataElement.mapChildren(::parseValue))
            }
            else -> error("unknown value type <$tagName>")
        }
    }
}
