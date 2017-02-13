package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import org.scalatest._
import org.scalatest.mock.MockitoSugar

class CatalogScraperSpec extends FlatSpec with Matchers with MockitoSugar {

  private val catalogScraper = new CatalogScraper()
  private val page: String = "http://test1.html"

  "CatalogScraper" should "map <table> to Place" in {
    val table1 = """<table><tr><td><a title="Egzemplarze">Muszkowska</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Blokada</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place1 = catalogScraper.toPlace(parseTable(table1, "tr").head)

    place1 should be (BookLocation("Muszkowska", false))

    val table2 = """<table><tr><td><a title="Egzemplarze">Rolna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Dostępny</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place2 = catalogScraper.toPlace(parseTable(table2, "tr").head)

    place2 should be (BookLocation("Rolna", true))

    val table3 = """<table><tr><td><a title="Egzemplarze">Rolna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Na półce</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place3 = catalogScraper.toPlace(parseTable(table3, "tr").head)

    place3 should be (BookLocation("Rolna", true))

    val table4 = """<table><tr><td><a title="Egzemplarze">Chwiałkowskiego</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">czytelnia</a></td><td><a title="Egzemplarze">Na półce</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place4 = catalogScraper.toPlace(parseTable(table4, "tr").head)

    place4 should be (BookLocation("Chwiałkowskiego", false))

    val table5 = """<table><tr><td><a title="Egzemplarze">Błękitna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Wypo&#380;yczony</a></td><td>04/01/2017</td><td><a>Dodaj</a></td></tr></table>"""
    val place5 = catalogScraper.toPlace(parseTable(table5, "tr").head)

    place5 should be (BookLocation("Błękitna", false, "04/01/2017"))
  }

  private def parseTable(html: String, htmlElement: String): List[Element] = {
    JsoupBrowser().parseString(html) >> elementList(htmlElement)
  }

  private def getStubbedCatalogScraper(stubBrowser: JsoupBrowser) = {
    new CatalogScraper(stubBrowser)
  }

  "CatalogScraper" should "handle incorrect pages" in {
    val emptyHtml = prepareStubBrowser("<html></html>")
    getStubbedCatalogScraper(emptyHtml).getAllPlaces(page) should be (List())

    val emptyPage = prepareStubBrowser("")
    getStubbedCatalogScraper(emptyPage).getAllPlaces(page) should be (List())

    val emptyTable = prepareStubBrowser("""<table class="tableBackground" cellpadding="3"></table>""")
    getStubbedCatalogScraper(emptyTable).getAllPlaces(page) should be (List())

    val partialTable = prepareStubBrowser("""<table class="tableBackground" cellpadding="3"><tr><td></td></tr><tr></tr></table>""")
    getStubbedCatalogScraper(partialTable).getAllPlaces(page) should be (List())
  }

  "CatalogScraper" should "parse correct page" in {
    val correctHtml =
      """<table class="tableBackground" cellpadding="3"><tr></tr>
        |<tr><td>F10 Robocza</td><td></td><td></td><td></td><td>Wypożyczane na 30 dni</td><td>Na półce</td><td></td></tr>
        |<tr><td>F11 Marcinkowskiego</td><td></td><td></td><td></td><td>Czytelnia</td><td>%s</td><td></td></tr>
        |<tr><td>F12 Rolna</td><td></td><td></td><td></td><td>Wypożyczane na 30 dni</td><td>Dostępny</td><td></td></tr>
        |</table>""".stripMargin

    val availableBookHtml = prepareStubBrowser(correctHtml)
    val places1 = getStubbedCatalogScraper(availableBookHtml).getAllPlaces(page)
    places1.length should be (3)
    places1.head should be (BookLocation("F10 Robocza", true))
    places1(1) should be (BookLocation("F11 Marcinkowskiego", false))
    places1(2) should be (BookLocation("F12 Rolna", true))
  }

  private def prepareStubBrowser(html: String): JsoupBrowser = {
    import org.mockito.Matchers.anyString
    import org.mockito.Mockito.when

    val stubBrowser = mock[JsoupBrowser]

    when(stubBrowser.get(anyString())).thenReturn(JsoupBrowser().parseString(html))
    stubBrowser
  }
}
