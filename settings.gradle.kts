
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("stdlib-jdk8"))
    }
}

rootProject.name = "xml-rpc-kt"
include(":protocol", ":ktor-client-xmlrpc", ":ktor-server-xmlrpc")
