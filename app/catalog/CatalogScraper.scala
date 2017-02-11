package catalog

import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}

class CatalogScraper {
  private lazy val browser = JsoupBrowser()
  private lazy val searchLink = ConfigFactory.load().getString("braczsearch.link")


  private def parseLink(link: String): Document = {
    browser.get(link)
  }

  private def formatLink(command: String) = searchLink.format(command)

  def getAllPlaces(link: String, linkParser: (String) => Document = parseLink): List[BookLocation] = {
    val tables = linkParser(link)
        .extract(elementList(".tableBackground"))
        .filter(_.hasAttr("cellpadding"))
        .filter(a => "3".equals(a.attr("cellpadding")))

    tables match {
      case Nil => List()
      case t =>
        val locationsTr = t.head >> elementList("tr")
        locationsTr match {
          case Nil => List()
          case _ :: tail => tail.map(toPlace).filter(p => p != null).sorted(AvailabilityOrdering)
        }
    }
  }

  def getPlacesGrouped(isbn: String): Map[Boolean, List[BookLocation]] = getAllPlaces(formatLink(isbn)).groupBy(_.available)

  def toPlace(el: Element): BookLocation = {
    val elementsTd = el >> elementList("td")

    if (elementsTd.length < 7)
      return null

    val available: Boolean = elementsTd(4).text.toLowerCase.contains("wypożyczane") &&
      ("Dostępny".equals(elementsTd(5).text) || "Na półce".equals(elementsTd(5).text))

    val date = elementsTd(6).text

    BookLocation(
      elementsTd.head.text,
      available,
      if (date.matches("\\d{2}/\\d{2}/\\d{4}")) date else null)
  }

}

object AvailabilityOrdering extends Ordering[BookLocation] {
  override def compare(x: BookLocation, y: BookLocation): Int = {
    (x.returnDate, y.returnDate) match {
      case (None,_) => 1
      case (_,None) => -1
      case (Some(a),Some(b)) => a.compareTo(b)
    }
  }
}
