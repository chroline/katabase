package katabase.operations

import katabase.Collections
import katabase.fs.FileSystem

typealias OperationFn<T> = suspend (fileSystem: FileSystem, collections: Collections) -> T

interface OperationChannel<T> {
  val fn: OperationFn<*>
  fun onComplete(value: T?, e: Exception?);
}

open class Operation

data class DocumentOperation(
  val file: Pair<String, String>
) : Operation()

data class CollectionOperation(
  val collection: String
) : Operation()
