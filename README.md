xml-rpc for Kotlin/Jvm
======================
[![Maven Central](https://img.shields.io/maven-central/v/org.ascheja.xmlrpc/protocol)](https://mvnrepository.com/artifact/org.ascheja.xmlrpc)

Kotlin binding for [xml-rpc](http://xmlrpc.com)


Add the dependency to your project
----------------------------------

### Client dependency using Ktor
```
implementation("org.ascheja.xmlrpc:ktor-client-xmlrpc:VERSION")
```

### Server dependency using Ktor
```
implementation("org.ascheja.xmlrpc:ktor-server-xmlrpc:VERSION")
```

### If you want to provide the transport yourself and just need the protocol
```
implementation("org.ascheja.xmlrpc:protocol:VERSION")
```

Getting started
---------------

### Client
```kotlin
val client: HttpClient = createHttpClient()
val call = MethodCall("some-method", IntegerValue(42), StringValue("some-string"))
val response = client.xmlRpc("https://some-server/rpc", call) as MethodResponseSuccess // or check for MethodResponseFault if you set the parameter `throwOnFault = false`
val firstParam = response.params.firstOrNull() as? StringValue ?: error("expected first parameter of response to be a StringValue")
```

### Server
```kotlin
embeddedServer(Netty) {
    routing {
        xmlRpc("/rpc") { request ->
            when (request.methodName) {
                "ping" -> MethodResponseSuccess(StringValue("pong"))
                else -> MethodResponseFault(42, "unknown method ${request.methodName}")
            }
        }
    }
}.start(wait = true)
```
