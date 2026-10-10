
plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("org.wit.placemark.main.MainKt")
}

sourceSets {
    main {
        kotlin.srcDirs(
            "../app/src/main/java/org/wit/placemark/models",
            "../app/src/main/java/org/wit/placemark/main"
        )
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
