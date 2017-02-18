package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import org.scalatest.mock.MockitoSugar

trait BrowserParser extends MockitoSugar {
  protected def parseTable(html: String, htmlElement: String): List[Element] = {
    JsoupBrowser().parseString(html) >> elementList(htmlElement)
  }

  protected def prepareStubBrowser(html: String): JsoupBrowser = {
    import org.mockito.Matchers.anyString
    import org.mockito.Mockito.when

    val stubBrowser = mock[JsoupBrowser]

    when(stubBrowser.get(anyString())).thenReturn(JsoupBrowser().parseString(html))
    stubBrowser
  }
}
