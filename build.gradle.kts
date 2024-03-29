
group = "edu.sc.seis"
version = "4.0.0-SNAPSHOT"

plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`
    `eclipse`
    `maven-publish`
  id("com.github.ben-manes.versions") version "0.47.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

dependencies {
  implementation("edu.sc.seis:seedCodec:1.1.1")
  implementation("edu.sc.seis:seisFile:2.0.6")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("edu.sc.seis:TauP:2.7.0-SNAPSHOT5")
  implementation("com.isti:isti.util:20120201")
  implementation("com.oregondsp.signalprocessing:oregondsp:1.0.1-alpha")
  // Use JUnit Jupiter API for testing.
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")

  // Use JUnit Jupiter Engine for testing.
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}


configurations.all {
  resolutionStrategy.dependencySubstitution {
    substitute(module("edu.sc.seis:seisFile")).using(project(":seisFile"))
    substitute(module("edu.sc.seis:seedCodec")).using(project(":seedCodec"))
  }
}

repositories {
  mavenCentral()
  maven(url = "https://www.seis.sc.edu/software/maven2")
  mavenLocal()
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
