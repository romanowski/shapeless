val hoarderVersion = sys.env.getOrElse("HOADER_VERSION", "1.0.1-SNAPSHOT")

addSbtPlugin("com.github.romanowski" % "hoarder" % hoarderVersion)
addSbtPlugin("com.github.romanowski" % "hoarder-tests" % hoarderVersion)
addSbtPlugin("com.github.romanowski" % "hoarder-amazon" % hoarderVersion)
