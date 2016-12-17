import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import org.scalamock.scalatest.MockFactory
import org.scalatest._

class CatalogScraperSpec extends FlatSpec with Matchers with MockFactory {


  "CatalogScraper" should "map <table> to Place" in {
    val catalogScraper = new CatalogScraper()

    val table1 = "<table><tr><td><a title=\"Egzemplarze\">Muszkowska</a></td><td><a title=\"Egzemplarze\">Literatura</a></td><td><a title=\"Egzemplarze\">16F/29912</a></td><td><a title=\"Egzemplarze\">821.111-3</a></td><td><a title=\"Egzemplarze\">wypo&#380;yczane</a></td><td><a title=\"Egzemplarze\">Blokada</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"
    val place1 = catalogScraper.toPlace(parseTable(table1, "tr").head)

    place1 should be (Place("Muszkowska", false))

    val table2 = "<table><tr><td><a title=\"Egzemplarze\">Rolna</a></td><td><a title=\"Egzemplarze\">Literatura</a></td><td><a title=\"Egzemplarze\">16F/29912</a></td><td><a title=\"Egzemplarze\">821.111-3</a></td><td><a title=\"Egzemplarze\">wypo&#380;yczane</a></td><td><a title=\"Egzemplarze\">Dostępny</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"
    val place2 = catalogScraper.toPlace(parseTable(table2, "tr").head)

    place2 should be (Place("Rolna", true))

    val table3 = "<table><tr><td><a title=\"Egzemplarze\">Rolna</a></td><td><a title=\"Egzemplarze\">Literatura</a></td><td><a title=\"Egzemplarze\">16F/29912</a></td><td><a title=\"Egzemplarze\">821.111-3</a></td><td><a title=\"Egzemplarze\">wypo&#380;yczane</a></td><td><a title=\"Egzemplarze\">Na półce</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"
    val place3 = catalogScraper.toPlace(parseTable(table3, "tr").head)

    place3 should be (Place("Rolna", true))

    val table4 = "<table><tr><td><a title=\"Egzemplarze\">Chwiałkowskiego</a></td><td><a title=\"Egzemplarze\">Literatura</a></td><td><a title=\"Egzemplarze\">16F/29912</a></td><td><a title=\"Egzemplarze\">821.111-3</a></td><td><a title=\"Egzemplarze\">czytelnia</a></td><td><a title=\"Egzemplarze\">Na półce</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"
    val place4 = catalogScraper.toPlace(parseTable(table4, "tr").head)

    place4 should be (Place("Chwiałkowskiego", false))

    val table5 = "<table><tr><td><a title=\"Egzemplarze\">Błękitna</a></td><td><a title=\"Egzemplarze\">Literatura</a></td><td><a title=\"Egzemplarze\">16F/29912</a></td><td><a title=\"Egzemplarze\">821.111-3</a></td><td><a title=\"Egzemplarze\">wypo&#380;yczane</a></td><td><a title=\"Egzemplarze\">Wypo&#380;yczony</a></td><td>04/01/2017</td><td><a>Dodaj</a></td></tr></table>"
    val place5 = catalogScraper.toPlace(parseTable(table5, "tr").head)

    place5 should be (Place("Błękitna", false, Option("04/01/2017")))
  }

  private def parseTable(html: String, htmlElement: String): List[Element] = {
    JsoupBrowser().parseString(html) >> elementList(htmlElement)
  }

  "CatalogScraper" should "handle incorrect pages" in {
    val emptyHtml = new CatalogScraper(prepareStubBrowser("<html></html>"))
    val page: String = "http://test1.html"
    emptyHtml.getPlaces(page) should be (List())

    val emptyPage = new CatalogScraper(prepareStubBrowser(""))
    emptyPage.getPlaces(page) should be (List())

    val emptyTable = new CatalogScraper(prepareStubBrowser("<table class=\"tableBackground\" cellpadding=\"3\"></table>"))
    emptyTable.getPlaces(page) should be (List())

    val partialTable = new CatalogScraper(prepareStubBrowser("<table class=\"tableBackground\" cellpadding=\"3\"><tr><td></td></tr><tr></tr></table>"))
    partialTable.getPlaces(page) should be (List())
  }

  private def prepareStubBrowser(html: String): JsoupBrowser = {
    val stubBrowser = stub[JsoupBrowser]
    (stubBrowser.get _).when(*).returning(JsoupBrowser().parseString(html))
    stubBrowser
  }

}
