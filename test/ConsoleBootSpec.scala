import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class ConsoleBootSpec extends FlatSpec with Matchers with MockFactory {
  val consoleBoot = ConsoleBoot

  "ConsoleBoot" should "load search ling from config" in {
    consoleBoot.searchLink should not be empty
  }
}
