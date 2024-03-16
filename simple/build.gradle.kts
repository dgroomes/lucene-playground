plugins {
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(libs.slf4j.api)
    runtimeOnly(libs.slf4j.simple)

    implementation(libs.lucene.queryparser)
    implementation(libs.lucene.analysis)
}

application {
    mainClass.set("dgroomes.Runner")
}
