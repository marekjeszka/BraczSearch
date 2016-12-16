import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element

object CatalogScraper {
  val browser = JsoupBrowser()

  def getPlaces(link: String): List[Place] = {
    val tables = browser.get(link) >> elementList(".tableBackground") filter(_.hasAttr("cellpadding")) filter(a => "3".equals(a.attr("cellpadding")))
    val locationsTr = tables.head >> elementList("tr")
    locationsTr.tail.map(toPlace)
  }

  def toPlace(el: Element): Place = {
    val elementsTd = el >> elementList("td")

    // TODO http://stackoverflow.com/questions/25510899/how-do-i-use-scala-regular-expressions-to-parse-a-line-of-text

    Place(elementsTd.head.text,
      elementsTd(4).text.toLowerCase.contains("wypożyczane") &&
        ("Dostępny".equals(elementsTd(5).text) || "Na półce".equals(elementsTd(5).text)))
  }

  case class Place(address: String, available: Boolean, returnDate: Option[LocalDate] = None)
}
