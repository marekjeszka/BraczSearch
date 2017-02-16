package catalog

import javax.inject.Singleton

import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}

@Singleton
class CatalogScraper(browser: JsoupBrowser) {
  private lazy val searchLink = ConfigFactory.load().getString("braczsearch.link")

  def this() = this(JsoupBrowser())

  private def parseLink(link: String): Document = {
    browser.get(link)
  }

  private def formatLink(command: String) = searchLink.format(command)

  def getAllPlaces(link: String): List[BookLocation] = {
    val tables = parseLink(link)
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

  def getPlacesGrouped(isbn: String): CatalogResult = {
    val link = formatLink(isbn)

    val allPlaces = getAllPlaces(link)//.groupBy(_.available)
    CatalogResult(link, allPlaces.filter(_.available), allPlaces.filter(!_.available))
  }

  def toPlace(el: Element): BookLocation = {
    val elementsTd = el >> elementList("td")

    if (elementsTd.length < 7)
      return null

    val available: Boolean = elementsTd(4).text.toLowerCase.contains("wypożyczane") &&
      "Na półce".equals(elementsTd(5).text)

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

case class CatalogResult(link: String, available: List[BookLocation], taken: List[BookLocation])