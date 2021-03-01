package fs

import DocumentNotFoundException
import IncompatibleDatabaseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class JVMFileSystem(private val base: String) : FileSystem {
  private val version = Properties().apply { load(File("gradle.properties").inputStream()) }["version"] as String

  override fun init() {
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

  override suspend fun read(file: Pair<String, String>) = withContext(Dispatchers.IO) {
    try {
      File(Path.of(base, "content", file.first, "${file.second}.txt").toUri()).readBytes()
    } catch (e: FileNotFoundException) {
      throw DocumentNotFoundException("${file.first}/${file.second}")
    }
  }

  override suspend fun doesExists(file: Pair<String, String>) = withContext(Dispatchers.IO) {
    File(Path.of(base, "content", file.first, "${file.second}.txt").toUri()).exists()
  }

  override suspend fun write(file: Pair<String, String>, data: ByteArray) = withContext(Dispatchers.IO) {
    File(Path.of(base, "content", file.first, "${file.second}.txt").toUri()).writeBytes(data)
  }

  override suspend fun delete(file: Pair<String, String>) = withContext(Dispatchers.IO) {
    File(Path.of(base, "content", file.first, "${file.second}.txt").toUri()).delete()
  }
}
