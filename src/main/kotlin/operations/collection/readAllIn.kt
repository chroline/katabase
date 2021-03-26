package operations.collection

import Collections
import DocumentData
import Katabase
import fs.FileSystem
import operations.CollectionOperation

/**
 * Reads all documents in a collection, then decodes it using provided serializer. Returns list of [DocumentData]s.
 */
suspend fun Katabase.readAllInCollection(collection: String): List<DocumentData> =
  pushOperation(CollectionOperation(collection)) { fileSystem: FileSystem, collections: Collections ->
    fileSystem.readAllIn(collection).map {
      val serializer = collections[collection]!!
      serializer.decode(it.decodeToString())
    }
  }

/**
 * Reads all documents in a collection. Returns list of [ByteArray]s.
 */
suspend fun Katabase.rawReadAllInCollection(collection: String): List<ByteArray> =
  pushOperation(CollectionOperation(collection)) { fileSystem: FileSystem, _: Collections ->
    fileSystem.readAllIn(collection)
  }
