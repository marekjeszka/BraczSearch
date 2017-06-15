package controller

import catalog._
import org.joda.time.DateTime
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

class ControllerSpec extends PlaySpec with Results with MockitoSugar {

  private val ValidISBN = "9788374800808"

  private val stubLocator = mock[BookLocator]
  private val stubSearcher = mock[BookSearcher]

  "'search' endpoint" should {
    val searchController = new SearchController(stubLocator, stubSearcher)
    val aBook1 = CurrentBookLocation("street")
    val aBook2 = CurrentBookLocation("street2")

    "should display link and books" in {
      val link = "http://bracz.org"
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult(link,List(aBook1, aBook2),Nil,Nil))
      when(stubLocator.getBookName(anyString())).thenReturn(Some(""))
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val bodyText: String = contentAsString(searchController.search(ValidISBN)(FakeRequest()))
      bodyText must (
        include(link) and include ("street") and include ("street2") and
        not include "will be available at")
    }

    "should work with empty results" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(CatalogResult("",Nil,Nil,Nil))
      when(stubLocator.getBookName(anyString())).thenReturn(None)
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val bodyText: String = contentAsString(searchController.search(ValidISBN)(FakeRequest()))
      bodyText must (include ("Nothing found") and not include "for")
    }

    "should work with only taken books" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(
        CatalogResult("",Nil,List(FutureBookLocation("street",DateTime.now())),Nil))
      when(stubLocator.getBookName(anyString())).thenReturn(Some("My book"))
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val bodyText: String = contentAsString(searchController.search(ValidISBN)(FakeRequest()))
      bodyText must (include ("will be available at") and include ("My book"))
    }

    "should display book names" in {
      when(stubSearcher.searchByName("")).thenReturn(List(Book("title","author",IncorrectISBN,"1997","")))

      val bodyText: String = contentAsString(searchController.search("")(FakeRequest()))
      bodyText must (include ("title") and include ("author") and include("1997"))
    }

    "should display incomplete locations" in {
      when(stubLocator.getPlacesGrouped(anyString())).thenReturn(
        CatalogResult("",Nil, Nil, List(IncompleteBookLocation("5th Avenue"),IncompleteBookLocation("6th Avenue"))))
      when(stubLocator.getBookName(anyString())).thenReturn(Some("My book"))
      when(stubLocator.isMultipleEntries(anyString())).thenReturn((false, Nil))

      val bodyText: String = contentAsString(searchController.search(ValidISBN)(FakeRequest()))
      bodyText must (include ("5th Avenue") and include ("6th Avenue") and include ("Other locations"))
    }

    "should work when accessing via link" in {
      val resultLink = "http://bracz.org"
      val bookLink = "http://book.org"
      when(stubLocator.getPlacesGroupedViaLink(anyString())).thenReturn(CatalogResult(resultLink,List(aBook1, aBook2),Nil,Nil))
      when(stubLocator.getBookNameViaLink(bookLink)).thenReturn(None)

      val bodyText: String = contentAsString(searchController.searchByLink()(FakeRequest().withBody(bookLink)))
      bodyText must (
        include(resultLink) and include ("street") and include ("street2") and
          include ("<b>This book</b> is available at") and
          not include "will be available at")
    }

    "should handle empty body" in {
      when(stubLocator.getPlacesGroupedViaLink(anyString())).thenReturn(CatalogResult("",Nil,Nil,Nil))
      when(stubLocator.getBookNameViaLink(anyString())).thenReturn(None)

      val bodyText: String = contentAsString(searchController.searchByLink()(FakeRequest().withBody("")))
      bodyText must include ("Nothing found")
    }

    "should work for multiple entries" in {
      when(stubLocator.isMultipleEntries(anyString()))
        .thenReturn((true, List(Book("t1", "a1", IncorrectISBN,"1990", ""))))

      val bodyText: String = contentAsString(searchController.search(ValidISBN)(FakeRequest()))
      bodyText must (
        include ("t1") and include ("a1") and include("1990") and
        include ("Select one of books:"))
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
      val bodyText: String = contentAsString(homeController.history()(withCookies))
      bodyText must (include("123") and include ("456"))
    }
  }

  "version endpoint" should {
    val versionController = new VersionController()

    "displays version" in {
      contentAsString(versionController.version()(FakeRequest())) must fullyMatch regex """\d[.]\d"""
    }
  }
}
