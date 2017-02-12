package controller

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import views.html.Index

@Singleton
class HomeController @Inject() extends Controller {
  def home() = Action {
    Ok(Index.render())
  }
}
