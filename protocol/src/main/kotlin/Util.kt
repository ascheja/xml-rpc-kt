/*
 * Copyright (c) 2022 Andreas Scheja. Use of this source code is governed by the Apache 2.0 license.
 */

package net.ascheja.xmlrpc.protocol

import org.w3c.dom.Document
import java.io.ByteArrayOutputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

private val declarationBytes = "<?xml version='1.0' encoding='UTF-8'?>".toByteArray()

internal fun createDocumentBuilder(): DocumentBuilder {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder()
}

internal fun createDefaultTransformer(prettyPrint: Boolean): Transformer {
    return TransformerFactory.newInstance().newTransformer().apply {
        if (prettyPrint) {
            setOutputProperty(OutputKeys.INDENT, "yes")
        }
        setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    }
}

public fun Document.writeToByteArray(prettyPrint: Boolean = true): ByteArray {
    return ByteArrayOutputStream().use { out ->
        out.writeBytes(declarationBytes)
        if (prettyPrint) out.writeBytes(System.lineSeparator().toByteArray())
        createDefaultTransformer(prettyPrint).transform(DOMSource(this), StreamResult(out))
        out.toByteArray()
    }
}

public fun Document.writeToString(prettyPrint: Boolean = true): String {
    return writeToByteArray(prettyPrint).toString(Charsets.UTF_8)
}
