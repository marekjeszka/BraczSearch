package controller

import javax.inject._

import catalog._
import views.html._
import play.api.mvc._

@Singleton
class SearchController @Inject() (bookLocator: BookLocator, bookSearcher: BookSearcher) extends Controller {

  def locate(inputText: String) = Action { request =>
    val history = HistoryCookie(request).addItem(inputText)
    if (bookLocator.isIsbn(inputText)) {
      val places = bookLocator.getPlacesGrouped(inputText)
      Ok(Locations.render(places, bookLocator.getBookName(inputText))).withCookies(history.asCookie())
    } else {
      val books = bookSearcher.searchByName(inputText)
      Ok(Books.render(books))
    }
  }
}
