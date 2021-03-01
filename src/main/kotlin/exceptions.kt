class IncompatibleDatabaseError(s: String) : Error(s)

class DocumentNotFoundException(path: String) : Exception("The document $path was not found.")
class DocumentAlreadyExistsException(path: String) : Exception("The document $path already exists.")
class CollectionDoesNotExistException(collection: String) : Exception("The collection $collection does not exist.")
class UpdateActionException(s: String) : Exception(s)
