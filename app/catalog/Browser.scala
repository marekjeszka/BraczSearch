package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._

trait Browser[A] {

  protected def catalogLink: String

  protected def getBrowser: JsoupBrowser

  protected def formatLink(command: String): String = catalogLink.format(command)

  private def parseLink(link: String): Document = getBrowser.get(link)

  protected def getElements(link: String)(rowExtractor: Element => A): List[A] = {
    val table = parseLink(link)
      .extract(elementList(".tableBackground"))
      .filter(_.hasAttr("cellpadding"))
      .filter(a => "3".equals(a.attr("cellpadding")))

    table match {
      case Nil => List()
      case t =>
        val locationsTr = t.head >> elementList("tr")
        locationsTr match {
          case Nil => List()
          case _ :: tail => tail.map(rowExtractor)
        }
    }
  }
}
