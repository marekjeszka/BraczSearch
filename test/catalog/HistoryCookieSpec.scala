package catalog

import org.scalatest.{MustMatchers, WordSpec}
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class HistoryCookieSpec extends WordSpec with MustMatchers {
  "HistoryCookie" should {
    "works with empty/incomplete cookies" in {
      val withoutCookies = FakeRequest()
      val cookie = HistoryCookie(withoutCookies)

      cookie.history must be (Nil)

      val incompleteCookie = Cookie("history", "")
      val cookie2 = HistoryCookie(FakeRequest().withCookies(incompleteCookie))

      cookie2.history must be (Nil)
    }

    "parses history cookie" in {
      val cookieValue = "123/456"
      val withCookies = FakeRequest().withCookies(Cookie("history", cookieValue))
      val cookie = HistoryCookie(withCookies)

      cookie.asString() must be (cookieValue)
      cookie.asCookie() must be (Cookie("history", cookieValue))
    }

    "adds items correctly" in {
      val cookie = HistoryCookie(List("12","34")).addItem("00")

      cookie.history.head must be ("00")
      cookie.history(1) must be ("12")
      cookie.history(2) must be ("34")
    }

    "stores at most 10 items" in {
      val cookie = HistoryCookie((1 to 10).toList.map(Integer.toString)).addItem("0")
      val history = cookie.history

      history.size must be (10)
      history must not contain "10"
    }
  }
}
