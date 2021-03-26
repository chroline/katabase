package katabase.operations.collection

import katabase.Collections
import katabase.Katabase
import katabase.fs.FileSystem
import katabase.operations.CollectionOperation

/**
 * Returns list of names of all documents in given collection.
 */
suspend fun Katabase.allInCollection(collection: String) =
  pushOperation(CollectionOperation(collection)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.eachOfCollection(collection)
  }
