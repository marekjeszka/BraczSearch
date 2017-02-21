package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.{MustMatchers, WordSpec}

class BookSearcherSpec extends WordSpec with MustMatchers with BrowserParser {
  private val bookSearcher = new BookSearcher()

  "BookSearcher" should {
    "map <table> to Book" in {
      val tableStandard = """<table><tr><td>1</td><td><a href="http://br-hip.pfsl.poznan.pl/">W godzinie apelu</a></td><td>&nbsp;</td><td>Jabski, Zachariasz S. (1940-2015).</td><td>Czochowa</td><td>1999.</td><td>&nbsp;</td></tr></table>"""
      val place1 = bookSearcher.toBook(parseTable(tableStandard, "tr").head)

      place1.get must be (Book("W godzinie apelu", "Jabski, Zachariasz S. (1940-2015).", "", "http://br-hip.pfsl.poznan.pl/"))
    }

    "parse correct pages" in {
      val correctHtml =
        """<table class="tableBackground" cellpadding="3"><tr></tr>
          |<tr><td></td><td>one</td><td></td><td>author1</td><td></td><td>1991</td><td></td></tr>
          |<tr><td></td><td><a href="books.com">two</a></td><td></td><td>author2</td><td></td><td>1992</td><td></td></tr>
          |<tr><td></td><td>three</td><td></td><td>author3</td><td></td><td>1993</td><td></td></tr>
          |</table>""".stripMargin

      val availableBookHtml = prepareStubBrowser(correctHtml)
      val places = getStubbedBookSearcher(availableBookHtml).searchByName("")

      places.head must be (Book("one", "author1", "", ""))
      places(1) must be (Book("two", "author2", "", "books.com"))
      places(2) must be (Book("three", "author3", "", ""))
    }

    "parses pages for ISBN" in {
      val aBook = Book("title", "author", "", "link.com")
      val htmlISBN =
        """<table><tr><td><a class="normalBlackFont1">ISBN:&nbsp;</a></td>
          |<td><table><tr><td valign="top"><a class="normalBlackFont1">9788380620438</a></td>
          |</tr></table></td></tr></table>""".stripMargin

      val book = getStubbedBookSearcher(prepareStubBrowser(htmlISBN)).findIsbn(Some(aBook))

      book.get must be (aBook.copy(isbn = "9788380620438"))
    }
  }

  private def getStubbedBookSearcher(stubBrowser: JsoupBrowser) = {
    new BookSearcher(stubBrowser)
  }
}
