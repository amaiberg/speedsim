//resolvers += "nexus snapshots" at "http://nexus.scala-tools.org/content/repositories/snapshots"

//addSbtPlugin("org.scala-tools.sbt" % "sbt-android-plugin" % "0.6.1-SNAPSHOT")
resolvers += Resolver.url("scalasbt snapshots", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scala-sbt" % "sbt-android-plugin" % "0.6.1-SNAPSHOT" changing())

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0-SNAPSHOT")
//libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-proguard-plugin" % (v+"-0.1.1"))

