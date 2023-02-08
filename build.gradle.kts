import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val jacksonVersion = "2.14.2"
val poiVersion = "5.2.3"
val kotlinVersion = "1.8.10"
val jdkVersion = "17"

plugins {
    kotlin("jvm") version "1.8.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
}

tasks {

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = jdkVersion
    }
}