resolvers ++= Seq(
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.11")
addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.0.3")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
