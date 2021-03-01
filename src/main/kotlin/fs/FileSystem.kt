package fs

interface FileSystem {
  fun init()
  fun createCollections(collections: Set<String>)

  suspend fun read(file: Pair<String, String>): ByteArray
  suspend fun doesExists(file: Pair<String, String>): Boolean
  suspend fun write(file: Pair<String, String>, data: ByteArray)
  suspend fun delete(file: Pair<String, String>): Boolean
}
