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
    val aBook1 = BookLocation("street", true)
    val aBook2 = BookLocation("street2", true)

    "should display link and books" in {
      val link = "http://bracz.org"
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(CatalogResult(link,List(aBook1, aBook2),Nil))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())

      val bodyText: String = contentAsString(result)
      bodyText must (
        include(link) and include ("street") and include ("street2") and
        not include "This book will be available at")
    }

    "should work with empty results" in {
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,Nil))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must include ("Nothing found")
    }

    "should work with only taken books" in {
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,List(BookLocation("street",false))))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must include ("This book will be available at")
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
