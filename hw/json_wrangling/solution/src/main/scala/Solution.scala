import hw.json._
import hw.wrangling.WranglingLike

object Wrangling extends WranglingLike {

  def key(json: Json, key: String): Option[Json] = json match {
    case JsonDict(map) => map.get(JsonString(key))
    case _ => None
  }

  def fromState(data: List[Json], state: String): List[Json] =
    data.filter(json => key(json, "state") match {
      case Some(JsonString(st)) => st == state
      case _ => false
    })

  def ratingLT(data: List[Json], rating: Double): List[Json] =
    data.filter(json => key(json, "stars") match {
      case Some(JsonNumber(n)) => n <= rating
      case _ => false
    })

  def ratingGT(data: List[Json], rating: Double): List[Json] =
    data.filter(json => key(json, "stars") match {
      case Some(JsonNumber(n)) => n >= rating
      case _ => false
    })

  def category(data: List[Json], category: String): List[Json] =
    data.filter(json => key(json, "categories") match {
        case Some(JsonArray(arr)) => arr.contains(JsonString(category))
        case _ => false
    })

  def groupByState(data: List[Json]): Map[String, List[Json]] =
    data.groupBy(json => key(json, "state") match {
        case Some(JsonString(st)) => st
        case _ => "<missing-state>"
      })

  def groupByCategory(data: List[Json]): Map[String, List[Json]] = {
    data.flatMap(json => json match {
      case dict@JsonDict(map) => map.get(JsonString("categories")) match {
        case Some(JsonArray(cats)) => cats.map(cat => cat match {
          case JsonString(str) => (str, dict)
          case _ => ???
        })
        case _ => ???
      }
      case _ => ???
    })
    .groupBy({case (cat, _) => cat})
    .map({case (k, v) =>
      k -> v.map({case (_, json) => json})
    })
  }

  def bestPlace(data: List[Json]): Json = {
    val starsGroup = data.groupBy(json => key(json, "stars") match {
      case Some(JsonNumber(n)) => n
      case _ => -100
    })

    val maxRating = starsGroup.keys.max

    val reviewsGroup = starsGroup(maxRating)
      .groupBy(json => key(json, "review_count") match {
        case Some(JsonNumber(n)) => n
        case _ => -100
      })

    reviewsGroup(reviewsGroup.keys.max).head
  }

  def hasAmbience(data: List[Json], ambience: String): List[Json] =
    data.filter(json => key(json, "attributes").flatMap(innerJson =>
        key(innerJson, "Ambience")) match {
          case Some(JsonDict(dict)) =>
            dict.toList.contains(JsonString(ambience) -> JsonBool(true))
          case _ => false
        })

  def addUid(data: List[Json]): List[Json] = data.zipWithIndex.map({
    case (json, ix) => json match {
      case JsonDict(map) => JsonDict(map + (JsonString("uid") -> JsonNumber(ix)))
      case _ => ???
    }
  })

}
