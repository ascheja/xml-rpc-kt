val ktorVersion = "1.6.7"

dependencies {
    api(project(":protocol"))
    api("io.ktor:ktor-client-core:$ktorVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}
