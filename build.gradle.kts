group = "no.nav.syfo"
version = "1.0.0"

val jacksonVersion = "2.16.0"
val poiVersion = "5.2.5"
val kotlinVersion = "1.9.21"
val logbackVersion= "1.4.14"
val logstashEncoderVersion = "7.4"
val log4jCoreVersion = "2.22.0"
val javaVersion = JavaVersion.VERSION_21

plugins {
    kotlin("jvm") version "1.9.21"
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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
}
