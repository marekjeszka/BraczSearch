package controller

import javax.inject._

import catalog.{BookLocator, HistoryCookie}
import play.api.mvc._
import views.html.SearchResult

@Singleton
class SearchController @Inject() (catalogScraper: BookLocator) extends Controller {

  def search(isbn: String) = Action { request =>
    val history = HistoryCookie(request).addItem(isbn)
    val places = catalogScraper.getPlacesGrouped(isbn)
    Ok(SearchResult.render(places, catalogScraper.getBookName(isbn))).withCookies(history.asCookie())
  }
}
