package controller

import javax.inject._

import catalog.CatalogScraper
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class SearchController @Inject() (catalogScraper: CatalogScraper) extends Controller {

  def search() = Action {
    import catalog.BookLocation._
    val head = catalogScraper.getPlacesGrouped("9788374805537")(true).head
    val json = Json.toJson(head)
    Ok(json)
  }
}
