import com.github.nscala_time.time.Imports._
import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}

import scala.util.{Failure, Success, Try}

object CatalogScraper {
  private lazy val browser = JsoupBrowser()

  private def parseLink(link: String): Document = {
    browser.get(link)
  }

  def getAllPlaces(link: String, linkParser: (String) => Document = parseLink): List[Place] = {
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

  def getPlacesGrouped(link: String): Map[Boolean, List[Place]] = getAllPlaces(link).groupBy(_.available)

  def toPlace(el: Element): Place = {
    val elementsTd = el >> elementList("td")

    if (elementsTd.length < 7)
      return null

    val available: Boolean = elementsTd(4).text.toLowerCase.contains("wypożyczane") &&
      ("Dostępny".equals(elementsTd(5).text) || "Na półce".equals(elementsTd(5).text))

    val date = elementsTd(6).text

    Place(
      elementsTd.head.text,
      available,
      if (date.matches("\\d{2}/\\d{2}/\\d{4}")) date else null)
  }

}

object AvailabilityOrdering extends Ordering[Place] {
  override def compare(x: Place, y: Place): Int = {
    (x.returnDate, y.returnDate) match {
      case (None,_) => 1
      case (_,None) => -1
      case (Some(a),Some(b)) => a.compareTo(b)
    }
  }
}

case class Place(address: String, available: Boolean, returnDate: Option[DateTime]) {
  import Place.formatter

  private def dateAsString = returnDate match {
    case None => ""
    case Some(d) => formatter.print(d)
  }

  override def toString: String = address + " " + dateAsString
}

object Place {
  private lazy val formatter = DateTimeFormat.forPattern(ConfigFactory.load().getString("braczsearch.dateFormat"))

  def apply(address: String, available: Boolean): Place = new Place(address, available, None)

  def apply(address: String, available: Boolean, returnDate: String): Place = {
    val date: Option[DateTime] = Try(formatter.parseDateTime(returnDate)) match {
      case Failure(_) => None
      case Success(t) => Option(t)
    }
    new Place(address, available, date)
  }
}