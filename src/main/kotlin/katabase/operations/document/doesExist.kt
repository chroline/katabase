package katabase.operations.document

import katabase.Collections
import katabase.DocumentPath
import katabase.Katabase
import katabase.fs.FileSystem
import katabase.operations.DocumentOperation

/**
 * Checks whether there a file exists at provided [DocumentPath].
 */
suspend fun Katabase.doesDocumentExist(file: DocumentPath) =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.doesDocumentExist(file)
  }
