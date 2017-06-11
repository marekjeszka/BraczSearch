package catalog

import com.github.nscala_time.time.Imports.{DateTime, DateTimeFormat}
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success, Try}

sealed trait BookLocation {
  val address: String
}

case class CurrentBookLocation(address: String) extends BookLocation

case class FutureBookLocation(address: String, returnDate: Option[DateTime])
extends BookLocation {
  override def toString: String = address + FutureBookLocation.dateAsString(returnDate)
}

object FutureBookLocation {
  private lazy val formatter = DateTimeFormat.forPattern(ConfigFactory.load().getString("braczsearch.dateFormat"))

  def dateAsString(date: Option[DateTime]): String = date match {
    case None => ""
    case Some(d) => " " + formatter.print(d)
  }

  def apply(address: String): FutureBookLocation = FutureBookLocation(address, None)

  def apply(address: String, returnDate: String): FutureBookLocation = {
    val date: Option[DateTime] = Try(formatter.parseDateTime(returnDate)) match {
      case Failure(_) => None
      case Success(t) => Option(t)
    }
    FutureBookLocation(address, date)
  }
}
