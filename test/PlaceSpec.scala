import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class PlaceSpec extends FlatSpec with Matchers with MockFactory {
  "Place" should "be ordered by date" in {
    val place1 = Place("street", true, "01/05/2017")
    val place2 = Place("street", true, "01/02/2017")
    val place3 = Place("street", true)
    val sorted = List(place1, place2, place3).sorted(AvailabilityOrdering)

    sorted(0) should be (place2)
    sorted(1) should be (place1)
    sorted(2) should be (place3)
  }

  "Place" should "be printable" in {
    Place("street", true, "01/01/2017").toString should be ("street 01/01/2017")
  }
}
