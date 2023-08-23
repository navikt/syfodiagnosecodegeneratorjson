import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val jacksonVersion = "2.14.2"
val poiVersion = "5.2.3"
val kotlinVersion = "1.9.10"
val logbackVersion= "1.4.11"
val logstashEncoderVersion = "7.4"
val log4jCoreVersion = "2.20.0"
val jdkVersion = "17"

plugins {
    kotlin("jvm") version "1.9.10"
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

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = jdkVersion
    }
}
