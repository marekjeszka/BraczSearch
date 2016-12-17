import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object ConsoleBoot extends App {
  val conf = ConfigFactory.load()
  programLoop()
//  private val ISBN = "9788380620438"


  def programLoop(): Unit = {
    while (true) {
      val command: String = StdIn.readLine("*****\nPlease enter ISBN of book: (q + enter => quits)\n")
      command match {
        case "q" | "Q" => return
        case _ =>
          if (isISBN(command)) {
            val places = new CatalogScraper().getPlaces(conf.getString("braczsearch.link").format(command))
            val availablePlaces: List[Place] = places filter(_.available)
            availablePlaces match {
              case Nil => println(s"Sorry, could not find available locations for: $command")
              case l => l foreach println
            }
          } else {
            println("Please enter 10 or 13 digits only.")
          }
      }
    }
  }

  private def isISBN(command: String): Boolean = {
    (command.length() == 10 || command.length == 13) && command.matches("\\d.*")
  }
}
