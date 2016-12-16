import com.typesafe.config.ConfigFactory

object ConsoleBoot extends App {
  val conf = ConfigFactory.load()
  private val ISBN = "9788380620438"
  private val places = CatalogScraper.getPlaces(conf.getString("braczsearch.link").format(ISBN))
  places filter(_.available) foreach println
}
