package katabase

import katabase.fs.FileSystem
import katabase.operations.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias Collections = Map<String, Serializer>

open class Katabase private constructor(
  private val collections: Collections,
  private val fileSystem: FileSystem,
  private val externalScope: CoroutineScope
) {
  private val operationFlow = MutableSharedFlow<OperationChannel>()

  init {
    fileSystem.initDatabase()
    fileSystem.createCollections(collections.keys)
  }

  /**
   * Start processing katabase.operations.
   */
  fun start() = externalScope.launch {
    operationFlow.collect { it.second(run { it.first(fileSystem, collections) }) }
  }

  /**
   * Push operation to operationFlow.
   */
  suspend fun <T> pushOperation(
    operation: Operation,
    operationFn: OperationFn<T>
  ): T = suspendCoroutine { c ->
    if ((operation is DocumentOperation && operation.file.first in collections.keys) ||
      (operation is CollectionOperation && operation.collection in collections.keys)
    ) throw CollectionDoesNotExistException(
      when (operation) {
        is DocumentOperation -> operation.file.first
        is CollectionOperation -> operation.collection
        else -> error("")
      }
    )

    externalScope.launch {
      operationFlow.emit(operationFn to {
        @Suppress("UNCHECKED_CAST")
        c.resume(it as T)
      })
    }
  }

  class Builder {
    lateinit var collections: Collections
    lateinit var fileSystem: FileSystem
    lateinit var externalScope: CoroutineScope

    fun build() = Katabase(collections, fileSystem, externalScope)
  }
}