/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.protocol

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeserializerTest {

    @Test
    fun `deserialize method call empty`() {
        assertEquals(
            MethodCall("examples.getStateName", emptyList()),
            MethodCall.parse { it.parse(javaClass.getResourceAsStream("/protocol/call_empty.xml")!!) }
        )
    }

    @Test
    fun `deserialize method call with single parameter`() {
        assertEquals(
            MethodCall(
                "examples.getStateName",
                ArrayValue()
            ),
            MethodCall.parse { it.parse(javaClass.getResourceAsStream("/protocol/call_single.xml")!!) }
        )
    }

    @Test
    fun `deserialize method call with multiple parameters`() {
        assertEquals(
            MethodCall(
                "examples.getStateName",
                IntegerValue(41),
                ArrayValue(
                    BooleanValue(true),
                    DoubleValue(-12.214),
                    DateTimeIso8601Value("19980717T14:08:55"),
                    Base64Value("eW91IGNhbid0IHJlYWQgdGhpcyE="),
                ),
                StructValue(
                    "something" to StringValue("4")
                ),
                ArrayValue(
                    StringValue("40")
                )
            ),
            MethodCall.parse { it.parse(javaClass.getResourceAsStream("/protocol/call_multiple.xml")!!) }
        )
    }

    @Test
    fun `deserialize method response with fault`() {
        assertEquals(
            MethodResponseFault(
                4,
                "Too many parameters."
            ),
            MethodResponse.parse { it.parse(javaClass.getResourceAsStream("/protocol/fault.xml")!!) }
        )
    }

    @Test
    fun `deserialize method response with success empty parameters`() {
        assertEquals(
            MethodResponseSuccess(),
            MethodResponse.parse { it.parse(javaClass.getResourceAsStream("/protocol/success_empty.xml")!!) }
        )
    }

    @Test
    fun `deserialize method response with success single parameter`() {
        assertEquals(
            MethodResponseSuccess(StringValue("South Dakota")),
            MethodResponse.parse { it.parse(javaClass.getResourceAsStream("/protocol/success_single.xml")!!) }
        )
    }

    @Test
    fun `deserialize method response with success multiple parameters`() {
        assertEquals(
            MethodResponseSuccess(StringValue("South Dakota"), StringValue("North Dakota")),
            MethodResponse.parse { it.parse(javaClass.getResourceAsStream("/protocol/success_multiple.xml")!!) }
        )
    }
}
