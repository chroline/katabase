package katabase.operations.document

import katabase.*
import katabase.fs.FileSystem
import katabase.operations.DocumentOperation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Writes provided [DocumentData] to file at provided [DocumentPath]. Throws [DocumentAlreadyExistsException] if a file at provided [DocumentPath] already exists.
 */
suspend fun Katabase.createDocument(file: DocumentPath, data: DocumentData) =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, _: Collections ->
    if (fileSystem.doesDocumentExist(file))
      throw DocumentAlreadyExistsException("${file.first}/${file.second}")
    fileSystem.writeDocument(file, Json.encodeToString(JsonElement.serializer(), data.toJsonObject()))
  }
