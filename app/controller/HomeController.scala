package controller

import javax.inject.{Inject, Singleton}

import catalog.HistoryCookie
import play.api.mvc._
import views.html.{History, Index}

@Singleton
class HomeController @Inject() extends Controller {
  def home() = Action {
    Ok(Index.render())
  }

  def history() = Action { request =>
    Ok(History.render(HistoryCookie(request).history))
  }
}
