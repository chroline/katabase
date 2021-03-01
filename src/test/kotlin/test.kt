import fs.JVMFileSystem
import kotlinx.serialization.Serializable

@Serializable
data class Person(val name: String, var posts: List<Int> = listOf()) {
  companion object : Interop<Person> {
    override val serializer = DefaultSerializers.protoBuf(Person::class)
    override val converter: Converter<Person> = DefaultConverters.dataClass(Person::class)
    /*override val converter: Converter<Person> = object : Converter<Person> {
        override fun encode(data: Person): Map<Any, Any?> = mapOf("name" to data.name, "age" to data.age)
        override fun decode(data: Map<Any, Any?>): Person = Person(data["name"] as String, data["age"] as Int)
      }*/
  }
}

fun main() {
  val builder = Katabase.Builder().apply {
    collections = mapOf(
      "people" to Person.Companion
    )
    fileSystem = JVMFileSystem("~/db-test")
  }

  val katabase = builder.build()

  katabase.start()
}

