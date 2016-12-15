import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element

object CatalogScraper {
  val browser = JsoupBrowser()

  def getPlaces(link: String): List[Element] = {
    val tables = browser.get(link) >> elementList(".tableBackground") filter (_.hasAttr("cellpadding")) filter (a => "3".equals(a.attr("cellpadding")))
    println(tables head)
//    doc >> elementList(".tableBackground") filter (_.hasAttr("title")) foreach println
//    val items: List[Element] = doc >> elementList("normalBlackFont1")

    List()
  }
}
