package controller

import javax.inject.{Inject, Singleton}

import catalog.HistoryCookie
import play.api.mvc._
import views.html.Index

@Singleton
class HomeController @Inject() extends Controller {
  def home() = Action { request =>
    Ok(Index.render(HistoryCookie(request).history))
  }
}
