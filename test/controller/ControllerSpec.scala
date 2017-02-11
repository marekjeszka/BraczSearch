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
    "should return a book" in {
      val controller = new SearchController(stubScraper)
      val aBook = BookLocation("street", true)
      when(stubScraper.getPlacesGrouped(anyString())).thenReturn(Map(true -> List(aBook)))

      val result: Future[Result] = controller.search().apply(FakeRequest())

      val bodyText: String = contentAsString(result)
      bodyText mustBe """{"address":"street","available":true,"returnDate":null}"""
    }
  }
}
