import hw.json._

object Wrangling {

  def fromState(data: List[Json], state: String): List[Json] =
    data.filter(json => json match {
      case JsonDict(map) => map.get("state") match {
        case Some(JsonString(st)) => st == state
        case _ => ???
      }
        case _ => ???
    })


  def ratingLT(data: List[Json], rating: Int): List[Json] =
    data.filter(json => json match {
      case JsonDict(map) => map.get("stars") match {
        case Some(JsonNumber(n)) => n <= rating
        case _ => ???
      }
      case _ => ???
    })

  def ratingGT(data: List[Json], rating: Int): List[Json] =
    data.filter(json => json match {
      case JsonDict(map) => map.get("stars") match {
        case Some(JsonNumber(n)) => n >= rating
        case _ => ???
      }
      case _ => ???
    })

  def category(data: List[Json], category: String): List[Json] =
    data.filter(json => json match {
      case JsonDict(map) => map.get("categories") match {
        case Some(JsonArray(arr)) => arr.contains(JsonString(category))
        case _ => ???
      }
      case _ => ???
    })

  def groupByState(data: List[Json]): Map[String, List[Json]] =
    data.groupBy(json => json match {
      case JsonDict(map) => map.get("state") match {
        case Some(JsonString(st)) => st
        case _ => ???
      }
      case _ => ???
    })

  def groupByCategory(data: List[Json]): Map[String, List[Json]] = {
    data.flatMap(json => json match {
      case dict@JsonDict(map) => map.get("categories") match {
        case Some(JsonArray(cats)) => cats.map(cat => cat match {
          case JsonString(str) => (str, dict)
          case _ => ???
        })
        case _ => ???
      }
      case _ => ???
    }).groupBy({case (cat, _) => cat})
      .map({case (k, v) =>
        k -> v.map({case (_, json) => json})
      })
  }

  def bestPlace(data: List[Json]): Option[Json] = {
    val starsGroup = data.groupBy(json => json match {
      case JsonDict(map) => map.get("stars") match {
        case Some(JsonNumber(n)) => n
        case _ => ???
      }
      case _ => ???
    })

    val maxRating = starsGroup.keys.max

    val reviewsGroup = starsGroup(maxRating).groupBy(json => json match {
      case JsonDict(map) => map.get("review_count") match {
        case Some(JsonNumber(n)) => n
        case _ => ???
      }
      case _ => ???
    })

    reviewsGroup(reviewsGroup.keys.max).headOption
  }

  def hasAmbience(data: List[Json], ambience: String): List[Json] =
    data.filter(json => json match {
      case JsonDict(map) => map.get("attributes") match {
        case Some(JsonDict(map)) => map.get("Ambience") match {
          case Some(JsonDict(dict)) =>
            dict.toList.contains(JsonString(ambience) -> JsonBool(true))
          case Some(_) => ???
          case None => false
        }
        case _ => ???
      }
      case _ => ???
    })

  def addUid(data: List[Json]): List[Json] = data.zipWithIndex.map({
    case (json, ix) => json match {
      case JsonDict(map) => JsonDict(map + ("uid" -> JsonNumber(ix)))
      case _ => ???
    }
  })

}
