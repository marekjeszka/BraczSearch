package controller

import javax.inject._

import catalog.{CatalogScraper, HistoryCookie}
import play.api.mvc._
import views.html.SearchResult

@Singleton
class SearchController @Inject() (catalogScraper: CatalogScraper) extends Controller {

  def search(isbn: String) = Action { request =>
    val history = HistoryCookie(request).addItem(isbn)
    val places = catalogScraper.getPlacesGrouped(isbn)
    Ok(SearchResult.render(places)).withCookies(history.asCookie())
  }
}