package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest._

class BookLocatorSpec extends FlatSpec with Matchers with BrowserParser {

  private val bookLocator = new BookLocator()
  private val page: String = "http://test1.html"

  "BookLocator" should "map <table> to Place" in {
    val tableStandard = """<table><tr><td><a title="Egzemplarze">Muszkowska</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Blokada</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place1 = bookLocator.toPlace(parseTable(tableStandard, "tr").head)

    place1 should be (BookLocation("Muszkowska", false))

    val tableReadingRoom = """<table><tr><td><a title="Egzemplarze">Rolna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Dostępny</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place2 = bookLocator.toPlace(parseTable(tableReadingRoom, "tr").head)

    place2 should be (BookLocation("Rolna", false))

    val tableAvailable = """<table><tr><td><a title="Egzemplarze">Rolna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Na półce</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place3 = bookLocator.toPlace(parseTable(tableAvailable, "tr").head)

    place3 should be (BookLocation("Rolna", true))

    val tableTaken = """<table><tr><td><a title="Egzemplarze">Błękitna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Wypo&#380;yczony</a></td><td>04/01/2017</td><td><a>Dodaj</a></td></tr></table>"""
    val place4 = bookLocator.toPlace(parseTable(tableTaken, "tr").head)

    place4 should be (BookLocation("Błękitna", false, "04/01/2017"))
  }

  private def getStubbedBookLocator(stubBrowser: JsoupBrowser) = {
    new BookLocator(stubBrowser)
  }

  "BookLocator" should "handle incorrect pages" in {
    val emptyHtml = prepareStubBrowser("<html></html>")
    getStubbedBookLocator(emptyHtml).getAllPlaces(page) should be (List())

    val emptyPage = prepareStubBrowser("")
    getStubbedBookLocator(emptyPage).getAllPlaces(page) should be (List())

    val emptyTable = prepareStubBrowser("""<table class="tableBackground" cellpadding="3"></table>""")
    getStubbedBookLocator(emptyTable).getAllPlaces(page) should be (List())

    val partialTable = prepareStubBrowser("""<table class="tableBackground" cellpadding="3"><tr><td></td></tr><tr></tr></table>""")
    getStubbedBookLocator(partialTable).getAllPlaces(page) should be (List())
  }

  "BookLocator" should "parse correct page" in {
    val correctHtml =
      """<table class="tableBackground" cellpadding="3"><tr></tr>
        |<tr><td>F10 Robocza</td><td></td><td></td><td></td><td>Wypożyczane na 30 dni</td><td>Na półce</td><td></td></tr>
        |<tr><td>F11 Marcinkowskiego</td><td></td><td></td><td></td><td>Czytelnia</td><td>%s</td><td></td></tr>
        |<tr><td>F12 Rolna</td><td></td><td></td><td></td><td>Czytelnia</td><td>Dostępny</td><td></td></tr>
        |</table>""".stripMargin

    val availableBookHtml = prepareStubBrowser(correctHtml)
    val places = getStubbedBookLocator(availableBookHtml).getPlacesGrouped(page)
    places.available.length should be (1)
    places.taken.length should be (2)
    places.available.head should be (BookLocation("F10 Robocza", true))
    places.taken.head should be (BookLocation("F11 Marcinkowskiego", false))
    places.taken(1) should be (BookLocation("F12 Rolna", false))
  }

  "BookLocator" should "find book name" in {
    val html =
      """<table class="tableBackground" cellpadding="3"><tr>
        |<td><a class="largeAnchor" title="Book name" href="test123.com">test123</a></td>
        |</tr></table>"""

    val book = getStubbedBookLocator(prepareStubBrowser(html)).getBookName(page)

    book.get should be ("Book name")
  }
}
