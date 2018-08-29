trait DistanceCalculator {
  def calculateDistanceInKilometer(thisLocation: Location, otherLocation: Location): Int

  def calculateDistanceInKilometerFast(thisLocation: Location, otherLocation: Location): Int

}

case class Location(latitude: Double, longitude: Double)

class DistanceCalculatorImpl extends DistanceCalculator {

  private val AVERAGE_RADIUS_OF_EARTH_KM = 6371

  // http://jonisalonen.com/2014/computing-distance-between-coordinates-can-be-simple-and-fast/
  override def calculateDistanceInKilometerFast(thisLocation: Location, otherLocation: Location): Int = {
    val deglen: Double = 110.25
    val x: Double = thisLocation.latitude - otherLocation.latitude
    val y: Double = thisLocation.longitude - otherLocation.latitude * Math.cos(otherLocation.latitude)
    (deglen * Math.sqrt(x * x + y * y)).toInt
  }

  // https://en.wikipedia.org/wiki/Haversine_formula
  override def calculateDistanceInKilometer(thisLocation: Location, otherLocation: Location): Int = {
    val latDistance = Math.toRadians(thisLocation.latitude - otherLocation.latitude)
    val lngDistance = Math.toRadians(thisLocation.longitude - otherLocation.longitude)
    val sinLat = Math.sin(latDistance / 2)
    val sinLng = Math.sin(lngDistance / 2)
    val a = sinLat * sinLat +
      (Math.cos(Math.toRadians(thisLocation.latitude)) *
        Math.cos(Math.toRadians(otherLocation.latitude)) *
        sinLng * sinLng)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    (AVERAGE_RADIUS_OF_EARTH_KM * c).toInt
  }
}