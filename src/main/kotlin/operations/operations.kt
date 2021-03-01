package operations

import Interop
import fs.FileSystem

typealias OperationChannel = Pair<Operation, (Any?) -> Any?>

interface Operation {
  suspend fun execute(collections: Map<String, Interop<Any>>, fileSystem: FileSystem): Any
}

interface DocumentOperation : Operation {
  val file: Pair<String, String>
}

interface CollectionOperation : Operation {
  val collection: String
}
