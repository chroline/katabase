import katabase.DefaultSerializers
import katabase.Katabase
import katabase.fs.JVMFileSystem
import katabase.operations.collection.allInCollection
import katabase.operations.collection.readAllInCollection
import katabase.operations.document.createDocument
import katabase.operations.document.deleteDocument
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
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
      katabase.allInCollection("people").forEach {
        katabase.deleteDocument("people" to it)
      }
    })

    println(measureTimeMillis {
      for (i in 1..100) {
        katabase.createDocument("people" to i.toString(), mapOf("name" to i))
      }
    })

    println(measureTimeMillis {
      val l = katabase.readAllInCollection("people").filter { it["name"] as Int % 7 == 0 }
      println(l)
    })

    cancel()
  }
}

