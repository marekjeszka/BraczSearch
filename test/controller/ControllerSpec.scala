package controller

import catalog.{BookLocation, CatalogResult, CatalogScraper}
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ControllerSpec extends PlaySpec with Results with MockitoSugar {

  private val stubScraper = mock[CatalogScraper]

  "'search' endpoint" should {
    val searchController = new SearchController(stubScraper)

    "should display link and books" in {
      val aBook1 = BookLocation("street", true)
      val aBook2 = BookLocation("street2", true)
      val link = "http://bracz.org"
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(CatalogResult(link,List(aBook1, aBook2),Nil))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())

      val bodyText: String = contentAsString(result)
      bodyText must (include(link) and include ("street") and include ("street2"))
    }

    "should work with empty results" in {
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,Nil))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must include ("Nothing found")
    }
  }

  "home endpoint" should {
    "exist" in {
      val controller = new HomeController()
      val result = controller.home().apply(FakeRequest())
      result mustNot be (null)
    }
  }
}
