import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val commonsCSVVersion = "1.6"
val jacksonVersion = "2.9.6"

plugins {
    kotlin("jvm") version "1.3.50"
}



repositories {
    mavenCentral()
    jcenter()
    maven (url= "https://kotlin.bintray.com/kotlinx")
    maven (url= "http://packages.confluent.io/maven/")
    maven (url = "https://oss.sonatype.org/content/groups/staging/")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("org.apache.commons:commons-csv:$commonsCSVVersion")
}