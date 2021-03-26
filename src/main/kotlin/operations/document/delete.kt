package operations.document

import Collections
import DocumentPath
import Katabase
import fs.FileSystem
import operations.DocumentOperation

/**
 * Deletes file at provided [DocumentPath].
 */
suspend fun Katabase.deleteDocument(file: DocumentPath) =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.deleteDocument(file)
  }

