object Boot extends App {
  private val places = CatalogScraper.getPlaces("http://br-hip.pfsl.poznan.pl/ipac20/ipac.jsp?session=148K750N7N630.1955&menu=search&aspect=basic_search&npp=10&ipp=30&spp=20&profile=br-mar&ri=1&source=~%21bracz&index=ISBN&term=9788380620438&x=15&y=10&aspect=basic_search")
  places filter(_.available) foreach println
}
