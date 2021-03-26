package katabase.operations.document

import katabase.Collections
import katabase.DocumentPath
import katabase.Katabase
import katabase.fs.FileSystem
import katabase.operations.DocumentOperation

/**
 * Deletes file at provided [DocumentPath].
 */
suspend fun Katabase.deleteDocument(file: DocumentPath) =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.deleteDocument(file)
  }

