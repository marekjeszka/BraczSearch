package controller

import javax.inject._

import catalog.CatalogScraper
import play.api.mvc._
import views.html.SearchResult

@Singleton
class SearchController @Inject() (catalogScraper: CatalogScraper) extends Controller {

  def search(isbn: String) = Action {
    val places = catalogScraper.getPlacesGrouped(isbn)
    Ok(SearchResult.render(places.available))
  }
}
