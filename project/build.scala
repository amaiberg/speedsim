import sbt._
import Keys._
import AndroidKeys._
object General {

val settings = Defaults.defaultSettings ++ Seq (
    name := "Speedlab",
    version := "0.1",
    
    scalaVersion := "2.10.0-M2",
   
 //scalaVersion := "2.9.1",
	resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",
    resolvers += "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
 libraryDependencies += "org.scalala" % "scalala_2.9.1" % "1.0.0.RC2" intransitive(),
   platformName in Android := "android-15")
   
  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    AndroidMarketPublish.settings ++ Seq (
useProguard in Android := true,   // sbt 0.1x
  keyalias in Android := "amaiberg"
 )
}


object AndroidBuild extends Build {
 
 useProguard in Android := true  //  sbt 0.1x
 lazy val main = Project (
    "Speedlab",
    file("."),
settings = General.fullAndroidSettings)


}
