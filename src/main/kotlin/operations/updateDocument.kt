package operations

import Interop
import Katabase
import UpdateActionException
import fs.FileSystem

typealias UpdateDocumentData = Map<String, UpdateDocumentOperation.UpdateValue>

data class UpdateDocumentOperation(override val file: Pair<String, String>, val data: UpdateDocumentData) :
  DocumentOperation {
  private enum class IntegerAction { INCREMENT, DECREMENT }
  data class UpdateValue(val value: Any)

  private fun update(old: Map<Any, Any?>): Map<Any, Any?> {
    val map = old.toMutableMap()
    map.putAll(data.filterValues { it.value !is IntegerAction }.mapValues { it.value.value })
    data.filterValues { it.value is IntegerAction }.entries.forEach {
      if (map[it.key] !is Int) throw UpdateActionException("Property ${it.key} is not an integer.")
      when (it as IntegerAction) {
        IntegerAction.INCREMENT -> map[it.key] = map[it.key] as Int + 1
        IntegerAction.DECREMENT -> map[it.key] = map[it.key] as Int - 1
      }
    }
    return map
  }

  override suspend fun execute(collections: Map<String, Interop<Any>>, fileSystem: FileSystem) {
    val interop = collections[file.first]!!
    val doc = ReadDocumentOperation(file).execute(collections, fileSystem)
    doc.let {
      interop.converter.encode(it)
    }.let {
      update(it)
    }.let {
      interop.converter.decode(it)
    }.let {
      interop.serializer.encode(it)
    }.let {
      fileSystem.write(file, it)
    }
  }
}

suspend fun Katabase.updateDocument(file: Pair<String, String>, data: UpdateDocumentData): Unit =
  pushOperation(UpdateDocumentOperation(file, data))
