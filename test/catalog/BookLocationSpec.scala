package catalog

import org.scalatest._

class BookLocationSpec extends FlatSpec with Matchers {
  "Place" should "be ordered by date" in {
    val place1 = FutureBookLocation("street", "01/05/2017")
    val place2 = FutureBookLocation("street", "01/02/2017")
    val place3 = IncompleteBookLocation("street")
    val sorted = List(place1, place2, place3).sorted(AvailabilityOrdering)

    sorted(0) should be (place2)
    sorted(1) should be (place1)
    sorted(2) should be (place3)
  }

  "Place" should "be printable" in {
    FutureBookLocation("street", "01/01/2017").toString should be ("street 01/01/2017")
  }
}
