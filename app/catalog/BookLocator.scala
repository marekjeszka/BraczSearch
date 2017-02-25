package catalog

import java.net.URLDecoder
import javax.inject.Singleton

import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element

/**
  * Locator that works with ISBN and locates books.
  * @param browser Browser implementation.
  */
@Singleton
class BookLocator(browser: JsoupBrowser) extends Browser[BookLocation] {
  override protected val catalogLink: String = ConfigFactory.load().getString("braczsearch.cataloglink")
  override protected def getBrowser: JsoupBrowser = browser

  def this() = this(JsoupBrowser())

  /**
    * Checks if there is more than one entry for given ISBN.
    * @param isbn correct ISBN
    * @return true with list of entries or false with Nil
    */
  def isMultipleEntries(isbn: String): (Boolean, List[Book]) = {
    val entries =
      getBrowser
        .get(formatLink(isbn))
        .extract(elementList(".mediumBoldAnchor"))
        .flatMap(
          e => {
            val link = e.attr("href").split("'").find(_.startsWith("http"))
            link match {
              case None => None
              case Some(l) => Some(Book(e.innerHtml, "", ISBN(isbn), "", URLDecoder.decode(l, "UTF-8")))
            }
          })
    if (entries.nonEmpty) {

      (true, entries)
    } else {
      (false, Nil)
    }
  }

  def getAllPlaces(link: String): List[BookLocation] =
    getElements(link)(toPlace).flatten.sorted(AvailabilityOrdering)

  def getPlacesGrouped(isbn: String): CatalogResult = getPlacesGroupedViaLink(formatLink(isbn))

  def getPlacesGroupedViaLink(link: String): CatalogResult = {
    val allPlaces = getAllPlaces(link)
    CatalogResult(link, allPlaces.filter(_.available), allPlaces.filter(!_.available))
  }

  def toPlace(el: Element): Option[BookLocation] = {
    val elementsTd = el >> elementList("td")

    if (elementsTd.length < 7)
      return None

    val available: Boolean = elementsTd(4).text.toLowerCase.contains("wypożyczane") &&
      "Na półce".equals(elementsTd(5).text)

    val date = elementsTd(6).text

    Some(BookLocation(
      elementsTd.head.text,
      available,
      if (date.matches("\\d{2}/\\d{2}/\\d{4}")) date else null))
  }

  def getBookName(isbn: String): Option[String] =
    getBookNameViaLink(formatLink(isbn))

  def getBookNameViaLink(link: String): Option[String] =
    parseLink(link).extract(elementList(".largeAnchor")).headOption.flatMap(h => h.attrs.get("title"))
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
