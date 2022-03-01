/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package org.ascheja.xmlrpc.protocol

import java.util.Base64

public sealed interface Value

public data class IntegerValue(val value: Int) : Value

public data class BooleanValue(val value: Boolean) : Value

public data class StringValue(val value: String) : Value

public data class DoubleValue(val value: Double) : Value

public data class DateTimeIso8601Value(val value: String) : Value

public data class Base64Value(val value: String) : Value {
    public fun toByteArray(): ByteArray = Base64.getDecoder().decode(value)
}

public data class StructValue(val members: Map<String, Value>) : Value {
    public constructor(vararg members: Pair<String, Value>) : this(members.toMap())
}

public data class ArrayValue(val data: List<Value>) : Value {
    public constructor(vararg data: Value) : this(data.toList())
}
