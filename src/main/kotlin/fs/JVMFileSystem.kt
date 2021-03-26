package fs

import DocumentNotFoundException
import IncompatibleDatabaseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * Filesystem for JVM IO.
 */
class JVMFileSystem(private val base: String) : FileSystem {
  private val version = Properties().apply { load(File("gradle.properties").inputStream()) }["version"] as String

  override fun initDatabase() {
    Files.createDirectories(Paths.get(base))
    val baseFile = File(Path.of(base).resolve(".katabase-db").toUri())

    if (!baseFile.exists()) {
      baseFile.createNewFile()
      baseFile.writeText(version)

      Files.createDirectory(Paths.get("$base/content"))
    }

    if (baseFile.readText() != version) throw IncompatibleDatabaseError("The provided database is outdated.")
  }

  override fun createCollections(collections: Set<String>) =
    collections.forEach {
      val path = Path.of(base, "content", it)
      Files.createDirectories(path)
    }

  /**
   * Document operations
   */

  val pathOfFile = { file: Pair<String, String> -> Path.of(base, "content", file.first, "${file.second}.json").toUri() }

  override suspend fun readDocument(file: Pair<String, String>) = withContext(Dispatchers.IO) {
    if (!doesDocumentExist(file)) throw DocumentNotFoundException("${file.first}/${file.second}")
    File(pathOfFile(file)).readBytes()
  }

  override suspend fun doesDocumentExist(file: Pair<String, String>) = withContext(Dispatchers.IO) {
    File(pathOfFile(file)).exists()
  }

  override suspend fun writeDocument(file: Pair<String, String>, data: String) = withContext(Dispatchers.IO) {
    File(pathOfFile(file)).writeText(data)
  }

  override suspend fun deleteDocument(file: Pair<String, String>): Unit = withContext(Dispatchers.IO) {
    if (!doesDocumentExist(file)) throw DocumentNotFoundException("${file.first}/${file.second}")
    File(pathOfFile(file)).delete()
  }

  /**
   * Collection operations
   */

  val pathOfCollection = { collection: String -> Path.of(base, "content", collection).toUri() }

  override suspend fun eachOfCollection(collection: String) = withContext(Dispatchers.IO) {
    val path = pathOfCollection(collection)
    File(path).walk().iterator().asSequence().filter filter@{
      if (Path.of(it.path).toUri() == path) return@filter false
      if (Path.of(it.path).toUri().path.split(".").last() != "json") return@filter false
      true
    }.map { Path.of(it.path).toUri().path.split(path.path).last().split(".")[0] }.toList()
  }

  override suspend fun readAllIn(collection: String) = withContext(Dispatchers.IO) {
    val docs = eachOfCollection(collection)
    docs.map {
      readDocument(collection to it)
    }
  }
}
