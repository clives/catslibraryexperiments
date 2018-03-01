import Dependencies._

lazy val root = (project in file(".")).
  enablePlugins(ParadoxPlugin).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    name := "catsLibraryExeperiments",
    libraryDependencies += scalaTest % Test,
   // libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1",


    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1",
    libraryDependencies += "org.atnos" %% "eff" % "5.0.0",

      // to write types like Reader[String, ?]
      addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),

      // to get types like Reader[String, ?] (with more than one type parameter) correctly inferred for scala 2.12.x
      scalacOptions += "-Ypartial-unification"
  )
