@file:Suppress("UNCHECKED_CAST")

import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

interface Interop<T> {
  val serializer: Serializer
  val converter: Converter<T>
}

interface Serializer {
  fun encode(data: Any): ByteArray
  fun decode(data: ByteArray): Any
}

object DefaultSerializers {
  fun <T : Any> protoBuf(theClass: KClass<T>): Serializer {
    return object : Serializer {
      override fun encode(data: Any): ByteArray =
        ProtoBuf.encodeToByteArray(theClass.serializer(), data as T)

      override fun decode(data: ByteArray): Any =
        ProtoBuf.decodeFromByteArray(theClass.serializer(), data)
    }
  }
}

interface Converter<T> {
  fun encode(data: T): Map<Any, Any?>
  fun decode(data: Map<Any, Any?>): T
}

object DefaultConverters {
  fun <T : Any> dataClass(theClass: KClass<T>) = object : Converter<T> {
    override fun encode(data: T): Map<Any, Any> {
      val totalProps = data::class.memberProperties
      return totalProps.fold(mutableMapOf()) { map, prop ->
        map += prop.name to (prop as KProperty1<Any, Any>).get(data)
        map
      }
    }

    override fun decode(data: Map<Any, Any?>): T {
      val totalProps = theClass.memberProperties.map { it.name }
      val constructor = theClass.constructors.first { it.parameters.size == totalProps.size }
      val args = constructor.parameters.map { it.name }
      return constructor.call(*(args.map { data[it] }.toTypedArray()))
    }
  }
}
