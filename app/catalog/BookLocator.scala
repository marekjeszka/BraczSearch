package catalog

import javax.inject.Singleton

import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element

@Singleton
class BookLocator(browser: JsoupBrowser) extends Browser[BookLocation] {
  override protected val catalogLink: String = ConfigFactory.load().getString("braczsearch.cataloglink")
  override protected def getBrowser: JsoupBrowser = browser

  def this() = this(JsoupBrowser())

  def getAllPlaces(link: String): List[BookLocation] =
    getElements(link)(toPlace).filter(p => p != null).sorted(AvailabilityOrdering)

  def getPlacesGrouped(isbn: String): CatalogResult = getPlacesGroupedViaLink(formatLink(isbn))

  def getPlacesGroupedViaLink(link: String): CatalogResult = {
    val allPlaces = getAllPlaces(link)
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
