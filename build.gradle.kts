plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    signing
}

group = "org.ascheja.xmlrpc"
version = System.getenv("GITHUB_REF_NAME")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = rootProject.group as String
                artifactId = subProject.name
                version = rootProject.version as String
                pom {
                    name.set("${project.group}:${subProject.name}")
                    description.set("xml-rpc implementation written in Kotlin")
                    url.set("https://github.com/ascheja/xml-rpc-kt")
                    inceptionYear.set("2022")
                    scm {
                        url.set("https://github.com/ascheja/xml-rpc-kt")
                        connection.set("scm:git:https://github.com/ascheja/xml-rpc-kt.git")
                        developerConnection.set("scm:git:ssh://github.com:ascheja/xml-rpc-kt.git")
                    }
                    developers {
                        developer {
                            id.set("ascheja")
                            name.set("Andreas Scheja")
                            email.set("a.scheja@gmail.com")
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
        useInMemoryPgpKeys(
            System.getenv("GPG_PRIVATE_KEY"),
            System.getenv("GPG_PASSPHRASE"),
        )
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
