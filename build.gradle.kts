import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val commonsCSVVersion = "1.7"
val jacksonVersion = "2.9.7"
val poiVersion = "4.1.1"

plugins {
    kotlin("jvm") version "1.3.61"
}


repositories {
    mavenCentral()
    jcenter()
    maven (url= "https://kotlin.bintray.com/kotlinx")
    maven (url= "http://packages.confluent.io/maven/")
}



dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("org.apache.commons:commons-csv:$commonsCSVVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
}

tasks {

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}