import catalog.{BookLocation, CatalogScraper}

import scala.annotation.tailrec
import scala.io.StdIn

object ConsoleBoot extends App {
  programLoop()
//  private val ISBN = "9788380620438"
//	9788374805537

  @tailrec
  def programLoop(): Unit = {
    val command: String = StdIn.readLine("*****\nPlease enter ISBN of book: (h + enter => shows help)\n")
    val text = command match {
      case "q" | "Q" => return
      case "h" | "H" =>
        "Available commands:\nq - quits\nh - shows this help\nl - displays previously used link"
      case _ =>
        if (isISBN(command)) {
          val places: Map[Boolean, List[BookLocation]] = new CatalogScraper().getPlacesGrouped(command)
          if (places.nonEmpty)
            places.map( place =>
              if (place._1) {
                "This book is available at:\n" + place._2.mkString("\n")
              } else {
                "This book will be available at:\n" + place._2.mkString("\n")
              }
            ).fold(("",""))((a,b) => (a + "\n" + b, a))
          else
            s"Sorry, could not find available locations for: $command"
        } else {
          "Please enter 10 or 13 digits only."
        }
    }
    println(text + "\n")
    programLoop()
  }

  private def isISBN(command: String): Boolean = {
    (command.length() == 10 || command.length == 13) && command.matches("\\d.*")
  }

  private def isEmpty(x: String) = x == null || x.isEmpty
}
