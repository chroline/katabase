package katabase.fs

/**
 * Filesystem IO with functions used by katabase.operations.
 */
interface FileSystem {
  fun initDatabase()
  fun createCollections(collections: Set<String>)

  suspend fun readDocument(file: Pair<String, String>): ByteArray
  suspend fun doesDocumentExist(file: Pair<String, String>): Boolean
  suspend fun writeDocument(file: Pair<String, String>, data: String)
  suspend fun deleteDocument(file: Pair<String, String>)

  suspend fun eachOfCollection(collection: String): List<String>
  suspend fun readAllIn(collection: String): List<ByteArray>
}
