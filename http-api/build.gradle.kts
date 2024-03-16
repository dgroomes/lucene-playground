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

    implementation(libs.http.components)
    implementation(libs.lucene.queryparser)
    implementation(libs.lucene.analysis)
    implementation(libs.lucene.facet)
}

application {
    mainClass.set("dgroomes.Runner")
}
