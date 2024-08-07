
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "no.nav.syfo"
version = "1.0.0"

val jacksonVersion = "2.16.0"
val poiVersion = "5.3.0"
val kotlinVersion = "2.0.0"
val logbackVersion= "1.5.6"
val logstashEncoderVersion = "8.0"
val log4jCoreVersion = "2.23.1"
val javaVersion = JvmTarget.JVM_21

plugins {
    kotlin("jvm") version "2.0.0"
    id("application")
}

application {
    mainClass.set("no.nav.syfo.BootstrapKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-core:$log4jCoreVersion")
}


kotlin {
    compilerOptions {
        jvmTarget = javaVersion
    }
}