package org.ascheja.xmlrpc.protocol

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SerializerTest {

    @Test
    fun `serialize method call empty`() {
        assertEquals(
            javaClass.getResource("/protocol/call_empty.xml")!!.readText(),
            MethodCall("examples.getStateName", emptyList()).toDocument().writeToString().replace("\r", "")
        )
    }

    @Test
    fun `serialize method call with single parameter`() {
        assertEquals(
            javaClass.getResource("/protocol/call_single.xml")!!.readText(),
            MethodCall(
                "examples.getStateName",
                ArrayValue()
            ).toDocument().writeToString().replace("\r", "")
        )
    }

    @Test
    fun `serialize method call with multiple parameters`() {
        assertEquals(
            javaClass.getResource("/protocol/call_multiple.xml")!!.readText().replace("i4>", "int>"),
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
            ).toDocument().writeToString().replace("\r", "")
        )
    }

    @Test
    fun `serialize method response with fault`() {
        assertEquals(
            javaClass.getResource("/protocol/fault.xml")!!.readText(),
            MethodResponseFault(
                4,
                "Too many parameters."
            ).toDocument().writeToString().replace("\r", "")
        )
    }

    @Test
    fun `serialize method response with success empty parameters`() {
        assertEquals(
            javaClass.getResource("/protocol/success_empty.xml")!!.readText(),
            MethodResponseSuccess().toDocument().writeToString().replace("\r", "")
        )
    }

    @Test
    fun `serialize method response with success single parameter`() {
        assertEquals(
            javaClass.getResource("/protocol/success_single.xml")!!.readText(),
            MethodResponseSuccess(StringValue("South Dakota")).toDocument().writeToString().replace("\r", "")
        )
    }

    @Test
    fun `serialize method response with success multiple parameters`() {
        assertEquals(
            javaClass.getResource("/protocol/success_multiple.xml")!!.readText(),
            MethodResponseSuccess(StringValue("South Dakota"), StringValue("North Dakota")).toDocument().writeToString().replace("\r", ""),
        )
    }
}
