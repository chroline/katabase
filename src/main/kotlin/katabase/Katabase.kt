package katabase

import katabase.fs.FileSystem
import katabase.operations.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.coroutines.suspendCoroutine

typealias Collections = Map<String, Serializer>

open class Katabase private constructor(
  private val collections: Collections,
  private val fileSystem: FileSystem,
  private val externalScope: CoroutineScope
) {
  private val operationFlow = MutableSharedFlow<OperationChannel<Any?>>()

  init {
    fileSystem.initDatabase()
    fileSystem.createCollections(collections.keys)
  }

  /**
   * Start processing katabase.operations.
   */
  fun start() = externalScope.launch {
    operationFlow.collect {
      var value: Any? = null
      var e: Exception? = null
      run {
        try {
          value = (it.fn(fileSystem, collections));
        } catch (exception: Exception) {
          e = exception
        }
      }
      it.onComplete(value, e)
    }
  }

  /**
   * Push operation to operationFlow.
   */
  suspend fun <T> pushOperation(
    operation: Operation,
    operationFn: OperationFn<T>
  ): T = suspendCoroutine { c ->
    if ((operation is DocumentOperation && operation.file.first !in collections.keys) ||
      (operation is CollectionOperation && operation.collection !in collections.keys)
    ) throw CollectionDoesNotExistException(
      when (operation) {
        is DocumentOperation -> operation.file.first
        is CollectionOperation -> operation.collection
        else -> error("")
      }
    )

    operationFn to {
      println("hey")
    }

    externalScope.launch {
      operationFlow.emit(object : OperationChannel<Any?> {
        override val fn = operationFn

        override fun onComplete(value: Any?, e: Exception?) {
          if (value != null) {
            @Suppress("UNCHECKED_CAST")
            c.resumeWith(success(value as T))
          } else {
            c.resumeWith(failure(e as Exception))
          }
        }
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
