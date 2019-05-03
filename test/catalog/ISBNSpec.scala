package catalog

import org.scalatest._

class ISBNSpec extends WordSpec with Matchers {
  "ISBN" should {
    "check its correctness" in {
      val correct = List("8389896591", "9788374800808", "837132538X")
      correct.foreach(isbn => withClue(s"Correct ISBN $isbn:") {
        ISBN(isbn) shouldBe a[CorrectISBN]
      })

      val incorrect = List("1234567", "1234567890", "1234567890123")
      incorrect.foreach(isbn => withClue(s"Incorrect ISBN $isbn:") {
        ISBN(isbn) should be theSameInstanceAs IncorrectISBN
      })
    }
  }
}
