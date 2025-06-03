plugins {
    `java-library`
    `eclipse`
}

repositories {
    mavenCentral()
}

dependencies {
    ////////////////////////////////////////// TESTING for JUNIT ////////////////////////////////////////////////////////////////////////
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")

    // Mockito core for mocking
    testImplementation("org.mockito:mockito-core:5.11.0")

    // JUnit 5 integration for Mockito
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")

    // Mockito Inline for inline mocking
    //testImplementation("org.mockito:mockito-inline:5.11.0")

    // AssertJ for fluent assertions
    testImplementation("org.assertj:assertj-core:3.25.1")

    ///////////////////////////////////////// AWS LAMBDA //////////////////////////////////////////////////////////////////////////////////////////
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.1")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")

    // Apache Commons Math: A math library offering functions for statistics, geometry, linear algebra, etc.
    api(libs.commons.math3)

    // Google Guava: A utility library offering advanced collections, caching, functional programming, etc.
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    // show standard out and standard error of the test JVM(s) on the console
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }

    // You can remove this entirely if you don't want to use -javaagent manually
    // jvmArgs = listOf(
    //     "-javaagent:${configurations.testRuntimeClasspath.get().find { it.name.contains("mockito-core") }}=inline"
    // )

    systemProperty("cucumber.junit-platform.naming-strategy", "long")
}

// All libraries  included - simple, easy way
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Builds a self-contained JAR for AWS Lambda"

    archiveBaseName.set("app")
    archiveVersion.set("")
    archiveClassifier.set("")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "weather.server.WeatherReqeust"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

//seperate lib files -  If I need to use lambda layer
tasks.register<Zip>("buildLambdaZip") {
    group = "build"
    description = "Package AWS Lambda zip with JAR and dependencies"

    dependsOn("fatJar") // Ensure fatJar is created before this task runs

    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    archiveFileName.set("lambda.zip")

    // Put the fat JAR inside the zip
    into("lib") {
        from(tasks.named<Jar>("fatJar").flatMap { it.archiveFile })
    }
}

// Runs first and ensures that fatJar runs as part of the build process.
tasks.named("build") {
    dependsOn("fatJar")
}

// Creates the fat JAR that includes all your dependencies.
tasks.named("startScripts") {
    dependsOn("fatJar")
}


