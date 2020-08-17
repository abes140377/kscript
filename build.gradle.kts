import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import io.minio.MinioClient;
import io.minio.errors.MinioException;

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("io.minio:minio:7.1.0")
    }
}

plugins {
    kotlin("jvm") version "1.2.70"
    id("com.github.johnrengelman.shadow") version "2.0.4"
    `maven-publish`
}

group = "com.github.holgerbrandl.kscript.launcher"
version = "2.9.3"

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib")

    compile("com.offbytwo:docopt:0.6.0.20150202")

    compile("com.jcabi:jcabi-aether:1.0-SNAPSHOT") { // bad to use snapshots but this was the only way to make it work behind proxy
        exclude("org.hibernate", "hibernate-validator")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "jcl-over-slf4j")
        exclude("org.apache.commons", "commons-lang3")
        exclude("cglib", "cglib")
        exclude("org.kuali.maven.wagons", "maven-s3-wagon")
    }
    // compile("com.jcabi:jcabi-aether:0.10.1:sources") //can be used for debugging, but somehow adds logging to dependency resolvement?
    compile("org.apache.maven:maven-core:3.0.3")
    compile("org.slf4j:slf4j-nop:1.7.25")

    testCompile("junit:junit:4.12")
    testCompile( "io.kotlintest:kotlintest:2.0.7")
    testCompile(kotlin("script-runtime"))
}

repositories {
    jcenter()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

val shadowJar by tasks.getting(ShadowJar::class) {
    // set empty string to classifier and version to get predictable jar file name: build/libs/kscript.jar
    archiveName = "kscript.jar"
    doLast {
        copy {
            from(File(projectDir, "src/kscript"))
            into(archivePath.parentFile)
        }
    }
}

// Disable standard jar task to avoid building non-shadow jars
val jar by tasks.getting {
    enabled = false
}
// Build shadowJar when
val assemble by tasks.getting {
    dependsOn(shadowJar)
}

val test by tasks.getting {
    inputs.dir("${project.projectDir}/test/resources")
}

tasks.register<Zip>("packageDist") {
    description = "Package kscript.zip distribution archive from files in build/dist"

    archiveFileName.set("kscript-${version}-bin.zip")
    destinationDirectory.set(file("$buildDir/dist"))

    from("$buildDir/libs")

    dependsOn(":build")
}

publishing {
    publications {
        create<MavenPublication>("zipDistribution") {
            artifact(tasks["packageDist"])
        }
    }
}

tasks.register<Greeting>("hello") {
    group = "Welcome"
    description = "Produces a world greeting"

    message = "Hello"
    recipient = "World"
}

open class Greeting: DefaultTask() {
    lateinit var message: String
    lateinit var recipient: String

    @TaskAction
    fun sayGreeting() {
        // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
        val minioClient = MinioClient("http://minio.dzbw.de:9900", "admin", "admin123")

        // Check if the bucket already exists.
        val isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket("software/kscript").build());
        if(isExist) {
            println("Bucket already exists.");
        } else {
            // Make a new bucket called asiatrip to hold a zip file of photos.
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("software/kscript").build());
        }

        // Upload the zip file to the bucket with putObject
        minioClient.putObject("software/kscript", "build/dist/kscript-2.9.3-bin.zip", null);
        System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");

        println("$message, $recipient!")
    }
}