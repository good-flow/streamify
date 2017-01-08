lazy val streamify = (project in file("streamify")).settings(
  organization := "com.good-flow",
  name         := "streamify",
  version      := "0.0.1-SNAPSHOT",
  resolvers ++= Seq(
    "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases"
    //,"sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  ),
  crossPaths       := false,
  autoScalaLibrary := false,
  libraryDependencies ++= Seq(
    "org.reactivestreams" % "reactive-streams"     % "1.0.0"   % "compile",
    "org.slf4j"           % "slf4j-api"            % "1.7.22"  % "compile",
    "org.projectlombok"   % "lombok"               % "1.16.12" % "provided",
    "junit"               % "junit"                % "4.12"    % "test",
    "com.novocode"        % "junit-interface"      % "0.11"    % "test",
    "org.reactivestreams" % "reactive-streams-tck" % "1.0.0"   % "test",
    "com.h2database"      % "h2"                   % "1.4.193" % "test",
    "ch.qos.logback"      % "logback-classic"      % "1.1.8"   % "test"      exclude("org.slf4j", "slf4j-api")
  ),
  publishTo := {
    val v = version.value
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  sbtPlugin := false,
  scalaVersion := "2.12.1",
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
  scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { x => false },
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  incOptions := incOptions.value.withNameHashing(true),
  logBuffered in Test := false,
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v"),
  updateOptions := updateOptions.value.withCachedResolution(true),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-Xlint:-options"),
  javacOptions in doc := Seq("-source", "1.8"),
  pomExtra :=
    <url>http://good-flow.com/</url>
      <licenses>
        <license>
          <name>CC0</name>
          <url>http://creativecommons.org/publicdomain/zero/1.0/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:seratch/streamify.git</url>
        <connection>scm:git:git@github.com:seratch/streamify.git</connection>
      </scm>
      <developers>
        <developer>
          <id>seratch</id>
          <name>Kazuhiro Sera</name>
          <url>http://git.io/sera</url>
        </developer>
      </developers>
)
