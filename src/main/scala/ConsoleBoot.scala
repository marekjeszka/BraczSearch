import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object ConsoleBoot extends App {
  val conf = ConfigFactory.load()
  programLoop()
//  private val ISBN = "9788380620438"


  def programLoop() = {
    val ISBN: String = StdIn.readLine("Please enter ISBN of book:\n")
    val places = new CatalogScraper().getPlaces(conf.getString("braczsearch.link").format(ISBN))
    places filter(_.available) foreach println
  }

}
