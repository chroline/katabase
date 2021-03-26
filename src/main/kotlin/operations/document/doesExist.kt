package operations.document

import Collections
import DocumentPath
import Katabase
import fs.FileSystem
import operations.DocumentOperation

/**
 * Checks whether there a file exists at provided [DocumentPath].
 */
suspend fun Katabase.doesDocumentExist(file: DocumentPath) =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.doesDocumentExist(file)
  }
