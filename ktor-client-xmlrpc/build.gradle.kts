val ktorVersion = "2.2.2"

dependencies {
    api(project(":protocol"))
    api("io.ktor:ktor-client-core:$ktorVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}
