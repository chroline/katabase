import operations.OperationChannel

/**
 * Stream of operations. Contains a queue of OperationChannels to be performed in order ot addition (first in, first out)
 */
class OperationStream {
  private val queue = mutableListOf<OperationChannel>()
  private lateinit var listener: (OperationChannel) -> Unit

  private fun send() {
    listener(queue.removeFirst())
    if (queue.size > 0) send()
  }

  /**
   * Add an operations.OperationChannel to channel stack
   */
  fun push(channel: OperationChannel) {
    queue += channel
    if (queue.size == 1) send()
  }

  /**
   * Provide listener for performing operations
   */
  fun subscribe(listener: (OperationChannel) -> Unit) {
    this.listener = listener
  }
}
