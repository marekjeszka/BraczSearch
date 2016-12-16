import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element

object CatalogScraper {
  val browser = JsoupBrowser()

  def getPlaces(link: String): List[Element] = {
    val tables = browser.get(link) >> elementList(".tableBackground") filter(_.hasAttr("cellpadding")) filter(a => "3".equals(a.attr("cellpadding")))
    val locationsTr = tables.head >> elementList("tr")
    locationsTr.tail.map(toPlace).foreach(println)

    List()
  }

  def toPlace(el: Element): Place = {
    val elements = el.extract(elementList(".normalBlackFont1"))
    println(elements.head.text)

    // TODO http://stackoverflow.com/questions/25510899/how-do-i-use-scala-regular-expressions-to-parse-a-line-of-text

    // bug - Czyt. og. is missing element at idx 3
    Place(elements.head.text, !"Wypożyczony".eq(elements(5).text))
  }

  case class Place(address: String, available: Boolean, returnDate: Option[LocalDate] = None)
}

/*

JsoupElement(<tr height="15">
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">CW Wypożyczalnia Al. Marcinkowskiego 23</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Literatura beletrystyczna</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">CW63210</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Lit ang</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Wypożyczane na 30 dni</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Wypożyczony</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">04/01/2017</a></td>
 <td nowrap="true" bgcolor="white" width="1%"><a name="requestLink1" class="tinyAnchor" href="javascript:AddCopy('bkey414390','ikey1251001',1, true);" id="requestlink1">Dodaj egz. do listy podręcznej</a></td>
</tr>)
JsoupElement(<tr height="15">
 <td bgcolor="#FCFCDC"><a class="normalBlackFont1" bgcolor="#FCFCDC" title="Egzemplarze">Al. Marcinkowskiego 23</a></td>
 <td bgcolor="#FCFCDC"><a class="normalBlackFont1" bgcolor="#FCFCDC" title="Egzemplarze">Księg. podstawowy</a></td>
 <td bgcolor="#FCFCDC"><a class="normalBlackFont1" bgcolor="#FCFCDC" title="Egzemplarze">KP191352</a></td>
 <td bgcolor="#FCFCDC">&nbsp;</td>
 <td bgcolor="#FCFCDC"><a class="normalBlackFont1" bgcolor="#FCFCDC" title="Egzemplarze">Rewers - do Czyt. Ogólnej</a></td>
 <td bgcolor="#FCFCDC"><a class="normalBlackFont1" bgcolor="#FCFCDC" title="Egzemplarze">Dostępny</a></td>
 <td bgcolor="#FCFCDC">&nbsp;</td>
 <td nowrap="true" bgcolor="#FCFCDC" width="1%"><a name="requestLink2" class="tinyAnchor" href="javascript:AddCopy('bkey414390','ikey1251165',2, true);" id="requestlink2">Dodaj egz. do listy podręcznej</a></td>
</tr>)
JsoupElement(<tr height="15">
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">F02 os.Oświecenia 59 tel. 61 8767121</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Literatura beletrystyczna</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">02F/65438</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">sens.821.111-3</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Filia 02 wypożyczane na 30 dni</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">Wypożyczony</a></td>
 <td bgcolor="white"><a class="normalBlackFont1" title="Egzemplarze">04/01/2017</a></td>
 <td nowrap="true" bgcolor="white" width="1%"><a name="requestLink3" class="tinyAnchor" href="javascript:AddCopy('bkey414390','ikey1252445',3, true);" id="requestlink3">Dodaj egz. do listy podręcznej</a></td>
</tr>)

 */
