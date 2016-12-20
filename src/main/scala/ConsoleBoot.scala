import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object ConsoleBoot extends App {
  val conf = ConfigFactory.load()
  programLoop()
//  private val ISBN = "9788380620438"
//	9788374805537

  def programLoop(): Unit = {
    var link : Option[String] = Option.empty
    while (true) {
      val command: String = StdIn.readLine("*****\nPlease enter ISBN of book: (h + enter => shows help)\n")
      command match {
        case "q" | "Q" => return
        case "h" | "H" => println("Available commands:\nq - quits\nh - shows this help\nl - displays previously used link")
        case "l" | "L" =>
          link match {
            case Some(l) => println(s"Previously used link: $l")
            case None => println("Previous link is not available.")
          }
        case _ =>
          if (isISBN(command)) {
            link = Option.apply(conf.getString("braczsearch.link").format(command))
            val places = new CatalogScraper().getPlaces(link.get)
            val availablePlaces: List[Place] = places filter(_.available)
            availablePlaces match {
              case Nil => println(s"Sorry, could not find available locations for: $command")
              case l =>
                println("This book is available at:")
                l foreach println
            }
          } else {
            println("Please enter 10 or 13 digits only.")
          }
      }
      println()
    }
  }

  private def isISBN(command: String): Boolean = {
    (command.length() == 10 || command.length == 13) && command.matches("\\d.*")
  }
}
