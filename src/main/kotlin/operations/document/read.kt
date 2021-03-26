package operations.document

import Collections
import DocumentData
import DocumentPath
import Katabase
import fs.FileSystem
import operations.DocumentOperation
import operations.OperationFn

/**
 * Reads document at provided DocumentPath, then decodes it using provided serializers. Returns [ByteArray].
 */
fun rawReadDocumentOperation(file: DocumentPath): OperationFn<ByteArray> =
  { fileSystem: FileSystem, _: Collections ->
    fileSystem.readDocument(file)
  }

/**
 * Reads document at provided DocumentPath. Returns [ByteArray].
 */
fun readDocumentOperation(file: DocumentPath): OperationFn<DocumentData> =
  { fileSystem: FileSystem, collections: Collections ->
    fileSystem.readDocument(file).let {
      val serializer = collections[file.first]!!
      serializer.decode(it.decodeToString())
    }
  }

/**
 * @see [rawReadDocumentOperation]
 */
suspend fun Katabase.readDocumentRaw(file: Pair<String, String>) =
  pushOperation(DocumentOperation(file), rawReadDocumentOperation(file))

/**
 * @see [readDocumentOperation]
 */
suspend fun Katabase.readDocument(file: Pair<String, String>) =
  pushOperation(DocumentOperation(file), readDocumentOperation(file))
