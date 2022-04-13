val ktorVersion = "2.0.0"

dependencies {
    api(project(":protocol"))
    api("io.ktor:ktor-server-core:$ktorVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}
