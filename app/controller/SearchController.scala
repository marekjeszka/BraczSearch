package controller

import javax.inject._

import catalog.CatalogScraper
import play.api.libs.json._
import play.api.mvc._

@Singleton
class SearchController @Inject() (catalogScraper: CatalogScraper) extends Controller {

  def search(isbn: String) = Action {
    import catalog.BookLocation._
    val places = catalogScraper.getPlacesGrouped(isbn).get(true)
    Ok(places match {
      case None => Json.toJson("Nothing found")
      case Some(l) => Json.toJson(l)
    })
  }
}
