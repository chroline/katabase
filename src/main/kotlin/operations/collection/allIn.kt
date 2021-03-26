package operations.collection

import Collections
import Katabase
import fs.FileSystem
import operations.CollectionOperation

/**
 * Returns list of names of all documents in given collection.
 */
suspend fun Katabase.allInCollection(collection: String) =
  pushOperation(CollectionOperation(collection)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.eachOfCollection(collection)
  }
