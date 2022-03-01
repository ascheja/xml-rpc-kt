plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    signing
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    repositories {
        mavenLocal()
        mavenCentral()
    }
    kotlin {
        explicitApi()
    }
}

subprojects {
    val subProject = this
    apply(plugin = "maven-publish")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "signing")
    tasks.test {
        useJUnitPlatform()
    }
    java {
        withJavadocJar()
        withSourcesJar()
    }
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                group = "org.ascheja.xmlrpc"
                artifactId = subProject.name
                version = System.getenv("GITHUB_REF_NAME")
                pom {
                    name.set(subProject.name)
                    description.set("")
                    url.set("https://github.com/ascheja/xml-rpc-kt")
                    inceptionYear.set("2022")
                    scm {
                        url.set("https://github.com/ascheja/xml-rpc-kt")
                        connection.set("scm:git:https://github.com/ascheja/xml-rpc-kt.git")
                        developerConnection.set("scm:git@github.com:ascheja/xml-rpc-kt.git")
                    }
                    developers {
                        developer {
                            id.set("ascheja")
                            name.set("Andreas Scheja")
                        }
                    }
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
                from(components["java"])
            }
        }
    }
    signing {
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
        sign(publishing.publications["mavenJava"])
    }
}

nexusPublishing {
    repositories {
        create("Ossrh") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("OSSRH_USERNAME"))
            password.set(System.getenv("OSSRH_TOKEN"))
        }
    }
}
