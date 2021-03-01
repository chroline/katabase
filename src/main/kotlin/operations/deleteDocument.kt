@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused")

package operations

import Interop
import Katabase
import fs.FileSystem

data class DeleteDocumentOperation(override val file: Pair<String, String>) : DocumentOperation {
  override suspend fun execute(collections: Map<String, Interop<Any>>, fileSystem: FileSystem) = fileSystem.delete(file)
}

suspend fun Katabase.deleteDocument(file: Pair<String, String>): Boolean = pushOperation(DeleteDocumentOperation(file))
