@Suppress("DSL_SCOPE_VIOLATION")

repositories {
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    signing
    libs.plugins.binary.compatibility.validator
}

version = Plugin.Config.version

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.property)
    testImplementation(libs.mockk)
    testImplementation(kotlin("gradle-plugin"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

gradlePlugin {
    plugins.create(Plugin.Config.name) {
        id = Plugin.Config.id
        implementationClass = Plugin.Config.implementationClass
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            pom {
                groupId = "com.chromaticnoise.multiplatform-swiftpackage"
                artifactId = "com.chromaticnoise.multiplatform-swiftpackage.gradle.plugin"

                name.set("Multiplatform Swift Package")
                description.set("Gradle plugin to generate a Swift.package file and XCFramework to distribute a Kotlin Multiplatform iOS library")
                url.set("https://github.com/ge-org/multiplatform-swiftpackage")

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("Georg Dresler")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/ge-org/multiplatform-swiftpackage.git")
                    developerConnection.set("scm:git:ssh://git@github.com/ge-org/multiplatform-swiftpackage.git")
                    url.set("https://github.com/ge-org/multiplatform-swiftpackage")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            name = "mavencentral"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl)
            credentials {
                username = System.getenv("SONATYPE_NEXUS_USERNAME")
                password = System.getenv("SONATYPE_NEXUS_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["pluginMaven"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
