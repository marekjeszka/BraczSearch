package controller

import javax.inject._

import catalog._
import views.html._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class SearchController @Inject() (bookLocator: BookLocator, bookSearcher: BookSearcher) extends Controller {

  def search(inputText: String) = Action { request =>
    val history = HistoryCookie(request).addItem(inputText)
    if (bookLocator.isIsbn(inputText)) {
      val places = bookLocator.getPlacesGrouped(inputText)
      Ok(Locations.render(places, bookLocator.getBookName(inputText))).withCookies(history.asCookie())
    } else {
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
