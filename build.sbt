name := "coproducts-conf"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.6.0",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "ru.tinkoff" %% "typed-schema" % "0.10.7.1",
  "ru.tinkoff" %% "derevo-tschema" % "0.5.0",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "de.heikoseeberger" %% "akka-http-circe" % "1.25.2",
  "com.lihaoyi" %% "scalatags" % "0.6.7",
  "io.circe" %% "circe-core" % "0.11.1",
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-literal" % "0.11.1",
  "org.webjars.npm" % "swagger-ui-dist" % "3.20.8"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")