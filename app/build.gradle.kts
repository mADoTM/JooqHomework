plugins {
    application
}

application {
    mainClass.set("ru.mail.Main")
}

dependencies {
    implementation(project(":jooq-generated"))
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}