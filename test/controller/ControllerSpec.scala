package controller

import catalog.{BookLocation, CatalogResult, BookLocator}
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ControllerSpec extends PlaySpec with Results with MockitoSugar {

  private val stubScraper = mock[BookLocator]

  "'search' endpoint" should {
    val searchController = new SearchController(stubScraper)
    val aBook1 = BookLocation("street", true)
    val aBook2 = BookLocation("street2", true)

    "should display link and books" in {
      val link = "http://bracz.org"
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(CatalogResult(link,List(aBook1, aBook2),Nil))
      when(stubScraper.getBookName(anyString())).thenReturn(Some(""))

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
      when(stubScraper.getBookName(anyString())).thenReturn(Some("My book"))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("will be available at") and include ("My book"))
    }
  }

  "home endpoint" should {
    val homeController = new HomeController()

    "exist" in {
      val result = homeController.home().apply(FakeRequest())
      result mustNot be (null)
    }

    "use history from cookies" in {
      val withCookies = FakeRequest().withCookies(Cookie("history", "123/456"))
      val result = homeController.history().apply(withCookies)
      val bodyText: String = contentAsString(result)
      bodyText must (include("123") and include ("456"))
    }
  }
}
