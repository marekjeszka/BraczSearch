package controller

import catalog.{BookLocation, CatalogScraper}
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

    "should return books" in {
      val aBook1 = BookLocation("street", true)
      val aBook2 = BookLocation("street2", true)
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(Map(true -> List(aBook1, aBook2)))

      val result: Future[Result] = searchController.search("").apply(FakeRequest())

      val bodyText: String = contentAsString(result)
      bodyText mustBe
        """[
          |{"address":"street","available":true,"returnDate":null},
          |{"address":"street2","available":true,"returnDate":null}
          |]""".stripMargin.replaceAll("\\s", "")
    }

    "should work for incorrect isbn" in {
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(Map[Boolean,List[BookLocation]]())

      val result: Future[Result] = searchController.search("").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe """"Nothing found""""
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
