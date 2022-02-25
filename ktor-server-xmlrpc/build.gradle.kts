val ktorVersion = "1.6.7"

dependencies {
    api(project(":protocol"))
    api("io.ktor:ktor-server-core:$ktorVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}
