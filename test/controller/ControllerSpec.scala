package controller

import catalog._
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
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
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val result: Future[Result] = searchController.search("9788374800808")(FakeRequest())

      val bodyText: String = contentAsString(result)
      bodyText must (
        include(link) and include ("street") and include ("street2") and
        not include "will be available at")
    }

    "should work with empty results" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,Nil))
      when(stubLocator.getBookName(anyString())).thenReturn(None)
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val m = mock[ISBN]

      val result: Future[Result] = searchController.search("9788374800808")(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("Nothing found") and not include "for")
    }

    "should work with only taken books" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,List(BookLocation("street",false))))
      when(stubLocator.getBookName(anyString())).thenReturn(Some("My book"))
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val result: Future[Result] = searchController.search("9788374800808")(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("will be available at") and include ("My book"))
    }

    "should display book names" in {
      when(stubSearcher.searchByName("")).thenReturn(List(Book("title","author",IncorrectISBN,"1997","")))

      val result: Future[Result] = searchController.search("")(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("title") and include ("author") and include("1997"))
    }

    "should work when accessing via link" in {
      val resultLink = "http://bracz.org"
      val bookLink = "http://book.org"
      when(stubLocator.getPlacesGroupedViaLink(anyString())).thenReturn(CatalogResult(resultLink,List(aBook1, aBook2),Nil))
      when(stubLocator.getBookNameViaLink(bookLink)).thenReturn(None)

      val result = searchController.searchByLink()(FakeRequest().withBody(bookLink))
      val bodyText: String = contentAsString(result)
      bodyText must (
        include(resultLink) and include ("street") and include ("street2") and
          include ("<b>This book</b> is available at") and
          not include "will be available at")
    }

    "should handle empty body" in {
      when(stubLocator.getPlacesGroupedViaLink(anyString())).thenReturn(CatalogResult("",Nil,Nil))
      when(stubLocator.getBookNameViaLink(anyString())).thenReturn(None)

      val result = searchController.searchByLink()(FakeRequest().withBody(""))
      val bodyText: String = contentAsString(result)
      bodyText must include ("Nothing found")
    }

    "should work for multiple entries" in {
      when(stubLocator.isMultipleEntries(anyString()))
        .thenReturn((true, List(Book("t1", "a1", IncorrectISBN,"1990", ""))))

      val result: Future[Result] = searchController.search("9788374800808")(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must (include ("t1") and include ("a1") and include("1990"))
    }
  }

  "home endpoint" should {
    val homeController = new HomeController()

    "exist" in {
      val result = homeController.home()(FakeRequest())
      result mustNot be (null)
    }

    "uses history from cookies" in {
      val withCookies = FakeRequest().withCookies(Cookie("history", "123/456"))
      val result = homeController.history()(withCookies)
      val bodyText: String = contentAsString(result)
      bodyText must (include("123") and include ("456"))
    }
  }
}
