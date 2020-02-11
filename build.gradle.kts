
group = "edu.sc.seis"
version = "4.0.0-SNAPSHOT"

plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`
}

dependencies {
  implementation("edu.sc.seis:sod-model:4.0.0-SNAPSHOT")
  implementation("edu.sc.seis:sod-util:4.0.0-SNAPSHOT")
  implementation(project(":TauP"))
  implementation("com.oregondsp.signalprocessing:oregondsp:2011")
  testImplementation(project(":sod-mock"))
//  testCompile group: "junit", name: "junit", version: "4.12+"
}


configurations.all {
  resolutionStrategy.dependencySubstitution {
    substitute(module("edu.sc.seis:sod-mock")).with(project(":sod-mock"))
    substitute(module("edu.sc.seis:sod-model")).with(project(":sod-model"))
    substitute(module("edu.sc.seis:sod-util")).with(project(":sod-util"))
    substitute(module("edu.sc.seis:seisFile")).with(project(":seisFile"))
  }
}

repositories {
  mavenCentral()
  maven(url = "http://www.seis.sc.edu/software/maven2")
}
