package repository.interops

import java.util
import javax.inject.Singleton

import model.ui.AdvertPrices
import model.{Advert, District}

@Singleton
class AdvertRepositoryJv {

  def createAdvert(advert: Advert): Advert = ???

  def findById(id: Long) : Advert = ???
   // sql"""SELECT * FROM advert WHERE id=2""".query[Advert].unique.transact(transactor.xa)

  def getNextAdvertsBeforeTime(timestamp: Long, limit: Int): util.List[Advert] = ???

  def getAdverts(districts: util.Collection[District], priceFrom: Int, priceTo: Int, rooms: util.List[Integer], offset: Int, limit: Int): util.List[Advert] = ???

  def getAdvertsCount(districts: util.Collection[District], priceFrom: Int, priceTo: Int, rooms: util.List[Integer]): Int = ???

  def getAdvertPrices: AdvertPrices = ???

  def deleteAdvert(id: Long): Unit = ???

  def bindToUser(advertId: Long, userId: Long): Unit = ???

  def findBySqPriceCoords(sq: Int, price: Int, lat: Double, lon: Double): util.List[Advert] = ???

}
