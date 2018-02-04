package hw.wrangling
/**
 * Defines the WranglingLike trait that needs to be implemented for
 * the data wrangling assignment
 */

import hw.json.Json
/**
 * Object containing functions to be implemented for Data Wrangling homework.
 */
trait WranglingLike {

  def key(json: Json, key: String): Option[Json]
  def fromState(data: List[Json], state: String): List[Json]
  def ratingLT(data: List[Json], rating: Int): List[Json]
  def ratingGT(data: List[Json], rating: Int): List[Json]
  def category(data: List[Json], category: String): List[Json]
  def groupByState(data: List[Json]): Map[String, List[Json]]
  def groupByCategory(data: List[Json]): Map[String, List[Json]]
  def bestPlace(data: List[Json]): Json
  def hasAmbience(data: List[Json], ambience: String): List[Json]

}

