@file:Suppress("UNCHECKED_CAST")

import fs.FileSystem
import kotlinx.coroutines.runBlocking
import operations.DocumentOperation
import operations.Operation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Katabase private constructor(
  val collections: Map<String, Interop<*>>,
  val fileSystem: FileSystem
) {
  private val stream = OperationStream()

  init {
    fileSystem.init()
    fileSystem.createCollections(collections.keys)
  }

  fun start() {
    stream.subscribe {
      it.second(runBlocking { it.first.execute(collections as Map<String, Interop<Any>>, fileSystem) })
    }
  }

  suspend fun <T> pushOperation(operation: Operation): T = suspendCoroutine { cont ->
    when (operation) {
      is DocumentOperation ->
        if (operation.file.first in collections.keys) {
          stream.push(operation to {
            cont.resume(it as T)
          })
        } else throw CollectionDoesNotExistException(operation.file.first)
    }
  }

  class Builder {
    lateinit var collections: Map<String, Interop<*>>
    lateinit var fileSystem: FileSystem

    fun build() = Katabase(collections, fileSystem)
  }
}
