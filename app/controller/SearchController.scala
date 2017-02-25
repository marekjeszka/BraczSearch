package controller

import javax.inject._

import catalog._
import views.html._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class SearchController @Inject() (bookLocator: BookLocator, bookSearcher: BookSearcher) extends Controller {

  def search(inputText: String) = Action { request =>
    ISBN(inputText) match {
      case CorrectISBN(isbn) =>
        val history = HistoryCookie(request).addItem(isbn)
        val multipleEntries = bookLocator.isMultipleEntries(isbn)
        if (multipleEntries._1)
          Ok(Books.render(multipleEntries._2))
        else {
          val places = bookLocator.getPlacesGrouped(isbn)
          Ok(Locations.render(places, bookLocator.getBookName(isbn))).withCookies(history.asCookie())
        }
      case IncorrectISBN =>
        val books = bookSearcher.searchByName(inputText)
        Ok(Books.render(books))
    }
  }

  def searchByLink(): Action[String] = Action.async(BodyParsers.parse.text) { request =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    Future {
      val link = request.body
      val places = bookLocator.getPlacesGroupedViaLink(link)
      Ok(Locations.render(places, bookLocator.getBookNameViaLink(link)))
    }
  }
}
