package operations.document

import Collections
import DocumentAlreadyExistsException
import DocumentData
import DocumentPath
import Katabase
import fs.FileSystem
import kotlinx.serialization.json.Json.Default.encodeToString
import kotlinx.serialization.json.JsonElement
import operations.DocumentOperation
import toJsonObject

/**
 * Writes provided [DocumentData] to file at provided [DocumentPath]. Throws [DocumentAlreadyExistsException] if a file at provided [DocumentPath] already exists.
 */
suspend fun Katabase.createDocument(file: DocumentPath, data: DocumentData) =
  pushOperation(DocumentOperation(file)) { fileSystem: FileSystem, _: Collections ->
    if (fileSystem.doesDocumentExist(file))
      throw DocumentAlreadyExistsException("${file.first}/${file.second}")
    fileSystem.writeDocument(file, encodeToString(JsonElement.serializer(), data.toJsonObject()))
  }
