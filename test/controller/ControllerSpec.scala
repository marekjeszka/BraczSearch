package controller

import catalog._
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ControllerSpec extends PlaySpec with Results with MockitoSugar {

  private val stubLocator = mock[BookLocator]
  private val stubSearcher = mock[BookSearcher]

  "'search' endpoint" should {
    val searchController = new SearchController(stubLocator, stubSearcher)
    val aBook1 = BookLocation("street", true)
    val aBook2 = BookLocation("street2", true)

    "should display link and books" in {
      val link = "http://bracz.org"
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult(link,List(aBook1, aBook2),Nil))
      when(stubLocator.getBookName(anyString())).thenReturn(Some(""))
      when(stubLocator.isIsbn(anyString())).thenReturn(true)

      val result: Future[Result] = searchController.search("").apply(FakeRequest())

      val bodyText: String = contentAsString(result)
      bodyText must (
        include(link) and include ("street") and include ("street2") and
        not include "This book will be available at")
    }

    "should work with empty results" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,Nil))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must include ("Nothing found")
    }

    "should work with only taken books" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,List(BookLocation("street",false))))
      when(stubLocator.getBookName(anyString())).thenReturn(Some("My book"))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("will be available at") and include ("My book"))
    }

    "should display book names" in {
      when(stubLocator.isIsbn(anyString())).thenReturn(false)
      when(stubSearcher.searchByName("")).thenReturn(List(Book("title","author","","")))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("title") and include ("author"))
    }
  }

  "home endpoint" should {
    val homeController = new HomeController()

    "exist" in {
      val result = homeController.home().apply(FakeRequest())
      result mustNot be (null)
    }

    "uses history from cookies" in {
      val withCookies = FakeRequest().withCookies(Cookie("history", "123/456"))
      val result = homeController.history().apply(withCookies)
      val bodyText: String = contentAsString(result)
      bodyText must (include("123") and include ("456"))
    }
  }
}
