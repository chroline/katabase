package operations

import Interop
import Katabase
import fs.FileSystem

data class DoesDocumentExistOperation(override val file: Pair<String, String>) : DocumentOperation {
  override suspend fun execute(collections: Map<String, Interop<Any>>, fileSystem: FileSystem): Boolean =
    fileSystem.doesExists(file)
}

suspend fun Katabase.doesDocumentExist(file: Pair<String, String>): Boolean =
  pushOperation(DoesDocumentExistOperation(file))
