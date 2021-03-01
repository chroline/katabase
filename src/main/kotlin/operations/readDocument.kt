package operations

import Interop
import Katabase
import fs.FileSystem

data class ReadDocumentOperation(override val file: Pair<String, String>) : DocumentOperation {
  override suspend fun execute(collections: Map<String, Interop<Any>>, fileSystem: FileSystem): Any =
    fileSystem.read(file).let {
      collections[file.first]!!.serializer.decode(it)
    }
}

suspend fun Katabase.readDocument(file: Pair<String, String>): Any = pushOperation(ReadDocumentOperation(file))
