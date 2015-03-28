logLevel := Level.Warn

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repository" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Spring repository"   at "https://repo.spring.io/libs-release"
  //  "Local Ivy Repository" at "~/.ivy2/local/"

)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.8")

addSbtPlugin("net.litola" % "play-sass" % "0.4.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")