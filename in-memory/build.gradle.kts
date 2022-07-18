plugins {
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val slf4jVersion = "1.7.36" // SLF4J releases: http://www.slf4j.org/news.html
val luceneVersion = "9.2.0" // Lucene releases: https://lucene.apache.org/core/downloads.html
val classGraphVersion = "4.8.149" // ClassGraph releases: https://github.com/classgraph/classgraph/releases

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    runtimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")

    implementation("io.github.classgraph:classgraph:$classGraphVersion")
    implementation("org.apache.lucene:lucene-queryparser:$luceneVersion")
    implementation("org.apache.lucene:lucene-analysis-common:$luceneVersion")
}

application {
    mainClass.set("dgroomes.Runner")
}
