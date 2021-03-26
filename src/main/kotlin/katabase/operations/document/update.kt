package katabase.operations.document

import katabase.*
import katabase.fs.FileSystem
import katabase.operations.DocumentOperation

typealias UpdateDocumentData = Map<String, UpdateValue>

/**
 * Special actions used to update document data map.
 */
private enum class UpdateAction {
  /**
   * Increase integer value by 1.
   */
  INCREMENT,

  /**
   * Decrease integer value by 1.
   */
  DECREMENT
}

data class UpdateValue(val value: Any)

private fun update(map: DocumentData, data: UpdateDocumentData): Map<String, Any?> {
  val updatedMap = map.toMutableMap()

  // reassign normal values (not UpdateAction)
  updatedMap.putAll(data.filterValues { it.value !is UpdateAction }.mapValues { it.value.value })

  // reassign UpdateAction values
  data.filterValues { it.value is UpdateAction }.entries.forEach {
    when (it as UpdateAction) {
      UpdateAction.INCREMENT -> {
        if (map[it.key] !is Int) throw UpdateActionException("Property ${it.key} is not an integer.")
        updatedMap[it.key] = updatedMap[it.key] as Int + 1
      }
      UpdateAction.DECREMENT -> {
        if (map[it.key] !is Int) throw UpdateActionException("Property ${it.key} is not an integer.")
        updatedMap[it.key] = updatedMap[it.key] as Int - 1
      }
    }
  }

  return updatedMap
}

/**
 * Updates document at provided [DocumentPath] with provided [UpdateDocumentData].
 */
suspend fun Katabase.updateDocument(file: DocumentPath, data: UpdateDocumentData): Unit =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, collections: Collections ->
    val doc = readDocumentOperation(file)(fileSystem, collections)
    update(doc.toMutableMap(), data).let {
      collections[file.first]!!.encode(it)
    }.let {
      fileSystem.writeDocument(file, it)
    }
  }
