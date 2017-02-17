package catalog

import play.api.mvc.{AnyContent, Cookie, Request}

case class HistoryCookie(history: List[String]) {

  import HistoryCookie._

  def asString(): String = history.take(10).mkString("/")

  def asCookie(): Cookie = Cookie(COOKIE_NAME, asString())

  def addItem(item: String): HistoryCookie = HistoryCookie(item +: this.history.take(9))
}

object HistoryCookie {
  val COOKIE_NAME = "history"

  def apply(request: Request[AnyContent]): HistoryCookie = {
    val history = request.cookies.get(COOKIE_NAME)
    val items = history match {
      case None => Nil
      case Some(cookie) => cookie.value.split("/").toList.filter(!_.isEmpty)
    }

    new HistoryCookie(items)
  }
}
