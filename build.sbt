addCommandAlias("root", ";project root")
addCommandAlias("core", ";project coreJVM")
addCommandAlias("scratch", ";project scratchJVM")
addCommandAlias("examples", ";project examplesJVM")

addCommandAlias("validate", ";root;validateJVM;validateJS")
addCommandAlias("validateJVM", ";coreJVM/compile;coreJVM/mimaReportBinaryIssues;coreJVM/test;examplesJVM/compile;coreJVM/doc")
addCommandAlias("validateJS", ";coreJS/compile;coreJS/mimaReportBinaryIssues;coreJS/test;examplesJS/compile;coreJS/doc")

addCommandAlias("runAll", ";examplesJVM/runAll")
addCommandAlias("releaseAll", ";root;release skip-tests")

lazy val commonSettings = Seq(
  scalacOptions := Seq(
    "-feature",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Xfatal-warnings",
    "-deprecation",
    "-unchecked"
  ),

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),

  scalacOptions in console in Compile -= "-Xfatal-warnings",
  scalacOptions in console in Test    -= "-Xfatal-warnings",
  scalaVersion := "2.11.8",
  initialCommands in console := """import shapeless._"""
) ++ scalaMacroDependencies



lazy val commonJvmSettings = Seq(
  parallelExecution in Test := false
)

lazy val coreSettings = commonSettings

lazy val root = project.in(file("."))
  .aggregate(core, scratch)
  .dependsOn(core, scratch)
  .settings(coreSettings:_*)

lazy val core =project
  .settings((unmanagedSourceDirectories in Compile) += baseDirectory.value /  "src" / "main" / "src_managed")
  .settings((unmanagedSourceDirectories in Compile) += baseDirectory.value /  "src" / "main" / "scala_2.11+")
  .settings(moduleName := "shapeless")
  .settings(coreSettings:_*)

lazy val scratch =
  project
  .dependsOn(core)
  .settings(moduleName := "scratch")
  .settings(coreSettings:_*)
  //.jsSettings(commonJsSettings:_*)


lazy val runAll = TaskKey[Unit]("runAll")

def runAllIn(config: Configuration): Setting[Task[Unit]] = {
  runAll in config := {
    val classes = (discoveredMainClasses in config).value
    val runner0 = (runner in run).value
    val cp = (fullClasspath in config).value
    val s = streams.value
    classes.foreach(c => runner0.run(c, Attributed.data(cp), Seq(), s.log))
  }
}

lazy val examples = project
  .dependsOn(core)
  .settings(moduleName := "examples")
  .settings(
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4")
        case _ => Seq()
      }
    }
  )
  .settings(runAllIn(Compile))
  .settings(coreSettings:_*)

lazy val scalaMacroDependencies: Seq[Setting[_]] = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "macro-compat" % "1.1.1",
    scalaOrganization.value % "scala-reflect" % scalaVersion.value % "provided",
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided",
    compilerPlugin("org.scalamacros" % "paradise_2.11.8" % "2.1.0" /*cross CrossVersion.patch*/)
  )
)

