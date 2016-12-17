import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}

class CatalogScraper(val browser: JsoupBrowser = JsoupBrowser()) {
  def parseLink(link: String): Document = {
    browser.get(link)
  }

  def getPlaces(link: String): List[Place] = {
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
          case l @ (_ :: _) => l.tail.map(toPlace).filter(p => p != null)
        }
    }
  }

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
      Option(if (date.matches("\\d{2}/\\d{2}/\\d{4}")) date else null))
  }

}

case class Place(address: String, available: Boolean, returnDate: Option[String] = None)
