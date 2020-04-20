
group = "edu.sc.seis"
version = "4.0.0-SNAPSHOT"

plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`
    eclipse
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

dependencies {
  implementation("edu.sc.seis:seedCodec:1.0.11")
  implementation("edu.sc.seis:seisFile:1.7.4")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("edu.sc.seis:sod-model:4.0.0-SNAPSHOT")
  implementation("edu.sc.seis:sod-util:4.0.0-SNAPSHOT")
  implementation(project(":TauP"))
  implementation("com.oregondsp.signalprocessing:oregondsp:2011")
  testImplementation(project(":sod-mock"))
  // Use JUnit Jupiter API for testing.
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")

  // Use JUnit Jupiter Engine for testing.
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}


configurations.all {
  resolutionStrategy.dependencySubstitution {
    substitute(module("edu.sc.seis:sod-mock")).with(project(":sod-mock"))
    substitute(module("edu.sc.seis:sod-model")).with(project(":sod-model"))
    substitute(module("edu.sc.seis:sod-util")).with(project(":sod-util"))
    substitute(module("edu.sc.seis:seisFile")).with(project(":seisFile"))
    substitute(module("edu.sc.seis:seedCodec")).with(project(":seedCodec"))
  }
}

repositories {
  mavenCentral()
  maven(url = "http://www.seis.sc.edu/software/maven2")
}
