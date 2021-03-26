package katabase

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser

interface Serializer {
  /**
   * Converts data map to JSON string.
   */
  fun encode(data: DocumentData): String

  /**
   * Converts JSON string to data map.
   */
  fun decode(data: String): DocumentData
}

val jsonParser: Parser = Parser.default()

object DefaultSerializers {
  /**
   * Default serializer for converting data to JSON using Klaxon, and vice versa.
   */
  val json = object : Serializer {
    override fun encode(data: DocumentData): String = JsonObject(data).toJsonString()

    override fun decode(data: String): DocumentData =
      (jsonParser.parse(StringBuilder(data)) as JsonObject).map
  }
}
