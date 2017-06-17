import org.romanowski.hoarder.actions.CachedCI
import org.romanowski.hoarder.amazon.S3TravisPRValidation

object HoarderCISetup extends S3TravisPRValidation(
  bucketName = "hoarder-s3-scripted-test-shapless",
  prefix = sys.env.getOrElse("TRAVIS_BRANCH", "default")
)

object HoarderCI extends CachedCI.PluginBase(HoarderCISetup)
