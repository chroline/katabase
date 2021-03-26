package katabase.operations

import katabase.Collections
import katabase.fs.FileSystem

typealias OperationChannel = Pair<OperationFn<*>, (Any?) -> Any?>

typealias OperationFn<T> = suspend (fileSystem: FileSystem, collections: Collections) -> T

open class Operation

data class DocumentOperation(
  val file: Pair<String, String>
) : Operation()

data class CollectionOperation(
  val collection: String
) : Operation()
