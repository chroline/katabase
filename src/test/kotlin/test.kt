import fs.JVMFileSystem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import operations.collection.allInCollection
import operations.collection.readAllInCollection
import kotlin.system.measureTimeMillis

data class Person(val name: String, var posts: List<Int> = listOf()) {
  companion object {
    val serializer = DefaultSerializers.json
  }
}

fun main() {

  runBlocking {
    val fileSystem = JVMFileSystem(System.getProperty("user.home") + "/db-test")
    val externalScope = this

    val builder = Katabase.Builder().apply {
      collections = mapOf("people" to Person.serializer)
      this.fileSystem = fileSystem
      this.externalScope = externalScope
    }

    val katabase = builder.build()

    katabase.start()

    println(measureTimeMillis {
      println(katabase.allInCollection("people"))
    })
    println(measureTimeMillis {
      val l = katabase.readAllInCollection("people").filter { it["name"] as Int > 5 }
      println(l)
    })

    cancel()
  }
}

