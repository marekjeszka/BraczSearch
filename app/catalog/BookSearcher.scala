package catalog

import javax.inject.Singleton

import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

@Singleton
class BookSearcher(browser: JsoupBrowser) extends Browser[Book] {
  override protected val catalogLink: String = ConfigFactory.load().getString("braczsearch.searchlink")
  override protected def getBrowser: JsoupBrowser = browser

  def this() = this(JsoupBrowser())

  def searchByName(name: String): List[Book] = {
    getElements(formatLink(name))(toBook)
  }

  def toBook(el: Element): Book = {
    val elementsTd = el >> elementList("td")
    val title = elementsTd(1).text
    val author = elementsTd(3).text
    val year = elementsTd(5).text
    val link = elementsTd(1) >> elementList("a") match {
      case Nil => ""
      case l => l.head.attr("href")
    }
    Book(title, author, year, link)
  }

}
