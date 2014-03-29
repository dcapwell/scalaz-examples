name := "scalaz-examples"

scalaVersion := "2.10.3"

val scalazVersion = "7.0.6"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.3"

libraryDependencies += "org.scalaz" % "scalaz-core_2.10" % scalazVersion

libraryDependencies += "org.scalaz" % "scalaz-concurrent_2.10" % scalazVersion

libraryDependencies += "org.scalaz" % "scalaz-scalacheck-binding_2.10" % scalazVersion

libraryDependencies += "org.scalaz" % "scalaz-effect_2.10" % scalazVersion

libraryDependencies += "org.scalaz" % "scalaz-typelevel_2.10" % scalazVersion

libraryDependencies += "org.scalaz" % "scalaz-iteratee_2.10" % scalazVersion

libraryDependencies += "com.google.guava" % "guava" % "16.0.1"

libraryDependencies += "com.google.code.findbugs" % "jsr305" % "1.3.9"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % "0.3.1"
