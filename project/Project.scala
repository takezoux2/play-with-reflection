import sbt._
import Keys._
import xml.{XML, NodeSeq}
import play.Play.autoImport._
import PlayKeys._


object MyProject extends Build {
  val projectName = "play-with-reflection"
  val projectVersion = "0.1-SNAPSHOT"
  val projectScalaVersion = "2.11.1"
  val _organization = "com.takezoux2"
 

  val forLibrary = Seq(
    "org.scala-lang" % "scala-reflect" % projectScalaVersion
  )



  /** override run command to run child play project. */
  def runChildPlayServer = Command.command("run")( state => {
    val makeJarState = Command.process("project library",state)
    Command.process("package",makeJarState)
    val subState = Command.process("project server",state)
    Command.process("run",subState)
    state
  })
  def startChildPlayServer = Command.command("start")( state => {
    val subState = Command.process("project server",state)
    Command.process("start",subState)
    state
  })



  lazy val root = Project(id = projectName,
    base =  file("."),
    settings = commonSettings ++ Seq(
      description := "parent project for " + projectName,
      commands ++= Seq(runChildPlayServer,startChildPlayServer),
    scalacOptions += """-Dfile.encoding=utf8"""
    )).aggregate(library,server)
  
  
  def ideaPluginSettings = {
    import org.sbtidea.SbtIdeaPlugin
    SbtIdeaPlugin.settings
  }
  
  lazy val commonSettings = Defaults.defaultSettings ++ ideaPluginSettings ++ Seq(
    version := projectVersion,
    organization := this._organization,
    scalaVersion := projectScalaVersion,
    publishTo := {
      if (System.getenv("PLAY_HOME") != null){
        Some( Resolver.file("playRepository", Path(System.getenv("PLAY_HOME")) / "repository" / "cache")(
          Patterns(Nil, "[organisation]/[module](_[scalaVersion])/[type]s/[artifact]-[revision](-[classifier]).[ext]"  :: Nil, false)
        ))
      }else{
        None
      }
    }
  )

  
  lazy val library = Project(id = "library",
    base = file("./library"),
    settings = commonSettings ++ Seq(
      description := "Library for " + projectName,
      libraryDependencies ++= forLibrary,
      packageBin in Compile <<= (packageBin in Compile,baseDirectory).map( (file,baseDir) => {
        println(file)
        // jarの出力先を簡単に変更できる方法あったら誰か教えて>_<
        val copyPath = baseDir / ".." / "server" / "lib" / file.getName
        IO.copyFile(file,copyPath)
        copyPath
      })
    )
  )//.dependsOn(liblib)

  lazy val server = Project(
    id = "server",
    base = file("./server")
  ).settings(
    version := projectVersion,
    organization := this._organization,
    checksums in update := Nil, // for maven compatible in Windows
    scalaVersion := projectScalaVersion,
    initialCommands := """System.setProperty( "file.encoding", "UTF-8" )""",
    scalacOptions += """-Dfile.encoding=utf8"""
    ).enablePlugins(play.PlayScala)


}
