#!/usr/bin/env kscript

// Declare dependencies
@file:DependsOn("de.mpicbg.scicomp:kutils:0.4")
@file:DependsOn("com.beust:klaxon:0.24", "com.github.kittinunf.fuel:fuel:1.3.1")

// To use a custom maven repository you can declare it with
@file:MavenRepository("imagej-releases","http://maven.imagej.net/content/repositories/releases" )

// For compatibility with https://github.com/ligee/kotlin-jupyter kscript supports also
@file:DependsOnMaven("net.clearvolume:cleargl:2.0.1")
// Note that for compatibility reasons, only one locator argument is allowed for @DependsOnMaven

// also protected artifact repositories are supported, see <https://github.com/holgerbrandl/kscript/blob/master/test/TestsReadme.md#manual-testing>
// @file:MavenRepository("my-art", "http://localhost:8081/artifactory/authenticated_repo", user="auth_user", password="password")
// You can use environment variables for user and password when string surrounded by double {} brackets 
// @file:MavenRepository("my-art", "http://localhost:8081/artifactory/authenticated_repo", user="{{ARTIFACTORY_USER}}", password="{{ARTIFACTORY_PASSWORD}}")
// will be use 'ARTIFACTORY_USER' and 'ARTIFACTORY_PASSWORD' environment variables
// if the value doesn't found in the script environment  will fail

// Include helper scripts without deployment or prior compilation
//@file:Include("util.kt")

// Define kotlin options
@file:KotlinOpts("-J-Xmx2g")
@file:KotlinOpts("-J-server")
@file:CompilerOpts("-jvm-target 1.8")

// declare application entry point (applies on for kt-files)
// @file:EntryPoint("Foo.bar")

print("1+1")
