package controller

import javax.inject.Singleton

import catalog.BuildInfo
import play.api.mvc.{Action, Controller}

@Singleton
class VersionController extends Controller {
  def version() = Action {
    Ok(BuildInfo.version)
  }
}
