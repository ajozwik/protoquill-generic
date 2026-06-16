object Versions {
  val logbackVersion        = "1.5.34"
  val scalaTestVersion      = "3.2.20"
  val quillVersion: String  = scala.util.Properties.propOrElse("quill.version", "4.8.6")
  val zioInteropCatsVersion = "23.1.0.13"
}
