package catalog

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

  def isIsbn(isbn: String): Boolean = {
    if (isbn.matches("\\d{13}|\\d{10}|\\d{9}X")) {
      val isbnX = isbn.endsWith("X")
      val digits = if (isbnX) isbn.substring(0,9) else isbn
      val ints = (Stream.iterate(BigInt(digits))(_ / 10) takeWhile (_ != 0) map (_ % 10)).toList.reverse
      if (isbn.length == 10) {
        val sum = Range.inclusive(10, 1, -1).toList.zip(ints).map(a => a._1 * a._2).sum + (if (isbnX) 10 else 0)
        sum % 11 == 0
      } else {
        (List.fill(7)(1,3).flatMap(a => List(a._1,a._2)) :+ 1)
          .zip(ints)
          .map(a => a._1 * a._2)
          .sum % 10 == 0
      }
    }
    else
      false
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
