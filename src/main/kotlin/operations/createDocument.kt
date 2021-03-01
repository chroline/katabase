@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused", "unused")

package operations

import DocumentAlreadyExistsException
import Interop
import Katabase
import fs.FileSystem

data class CreateDocumentOperation(override val file: Pair<String, String>, val data: Any) : DocumentOperation {
  override suspend fun execute(collections: Map<String, Interop<Any>>, fileSystem: FileSystem) {
    val interop = collections[file.first] ?: error("")
    if (fileSystem.doesExists(file))
      throw DocumentAlreadyExistsException("${file.first}/${file.second}")
    interop.serializer.encode(data).let { fileSystem.write(file, it) }
  }
}

suspend fun Katabase.createDocument(file: Pair<String, String>, data: Any): Unit =
  pushOperation(CreateDocumentOperation(file, data))
