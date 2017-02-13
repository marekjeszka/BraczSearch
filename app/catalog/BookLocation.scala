package catalog

import com.github.nscala_time.time.Imports.{DateTime, DateTimeFormat}
import com.typesafe.config.ConfigFactory
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

case class BookLocation(address: String, available: Boolean, returnDate: Option[DateTime]) {
  override def toString: String = address + BookLocation.dateAsString(returnDate)
}

object BookLocation {
  private lazy val formatter = DateTimeFormat.forPattern(ConfigFactory.load().getString("braczsearch.dateFormat"))

  def dateAsString(date: Option[DateTime]) = date match {
    case None => ""
    case Some(d) => " " + formatter.print(d)
  }

  def apply(address: String, available: Boolean): BookLocation = new BookLocation(address, available, None)

  def apply(address: String, available: Boolean, returnDate: String): BookLocation = {
    val date: Option[DateTime] = Try(formatter.parseDateTime(returnDate)) match {
      case Failure(_) => None
      case Success(t) => Option(t)
    }
    new BookLocation(address, available, date)
  }
}
