name := "sitemaps"

version := "1.0"

scalaVersion := "2.11.6"

lazy val `sitemaps` = (project in file(".")).enablePlugins(
  SbtWeb,
  PlayJava,
  net.litola.SassPlugin
)

libraryDependencies ++= Seq(javaWs,
  "org.mariadb.jdbc"          % "mariadb-java-client"           % "latest.integration",
  "javax.inject"              % "javax.inject"                  % "latest.integration",
  "org.springframework.boot"  % "spring-boot-starter-data-jpa"  % "latest.integration",
  "org.projectlombok"         % "lombok"                        % "latest.integration",
  "javax.persistence"         % "persistence-api"               % "latest.integration",
  "com.thoughtworks.xstream"  % "xstream"                       % "latest.integration",
  "commons-validator"         % "commons-validator"             % "latest.integration",
  "org.jsoup"                 % "jsoup"                         % "latest.integration",
  "com.thoughtworks.xstream"  % "xstream"                       % "latest.integration"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

CoffeeScriptKeys.bare := true