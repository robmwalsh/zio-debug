name        := "zio debug"
description := "println is for plebs"
version     := "0.0.1"


val scalaVer     = "3.1.1"
val zioVersion       = "2.0.0-RC4"
val zioHttpVersion   = "2.0.0-RC6"
// val animusVersion    = "0.1.12"
val boopickleVerison = "1.4.0"
val laminarVersion   = "0.14.2"
val laminextVersion  = "0.14.3"
val sttpVersion      = "3.5.1"

inThisBuild(
  List(
    organization := "net.robmwalsh",
    homepage := Some(url("https://robmwalsh.github.io/zio.debug/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "robmwalsh",
        "Rob Walsh",
        "rob@robmwalsh.net",
        url("http://robmwalsh.net")
      )
    ),
    
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")
    )
  )
)

val sharedSettings = Seq(
  scalacOptions ++= Seq( "-Xfatal-warnings", "-deprecation"),
  scalaVersion := scalaVer,
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Snapshots s01" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
  ),
  libraryDependencies ++= Seq(
    "io.suzaku"                     %%% "boopickle"   % boopickleVerison,
    "dev.zio"                       %%% "zio"         % zioVersion,
    "dev.zio"                       %%% "zio-streams" % zioVersion,
    "dev.zio"                       %%% "zio-macros"  % zioVersion,
    "dev.zio"                       %%% "zio-test"    % zioVersion % Test,
    "com.softwaremill.sttp.client3" %%% "core"        % sttpVersion
  ), 
  excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13",
  
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
)

lazy val backend = project
  .in(file("modules/backend"))
  .settings(
    sharedSettings,
    Compile / run / mainClass := Some("$package$.Backend"),
    libraryDependencies ++= Seq(
      "io.d11"                        %% "zhttp"                  % zioHttpVersion,
    ) 
  )
  .dependsOn(shared)

lazy val frontend = project
  .in(file("modules/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      // "io.github.kitlangton" %%% "animus"          % animusVersion,
      "com.raquo"            %%% "laminar"         % laminarVersion,
      "io.laminext"          %%% "websocket"       % laminextVersion
    )
  )
  .settings(sharedSettings)
  .dependsOn(shared)

lazy val shared = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("modules/shared"))
  .settings(
    sharedSettings,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
    //scalaJSLinkerConfig ~= { _.withSourceMap(false) }
  )




    