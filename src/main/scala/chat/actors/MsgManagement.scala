package chat.actors

sealed trait Message
case class PubMsg(message: String) extends Message
case class PrivateMsg(message: String) extends Message
case class PubSend(message: String) extends Message
case class PrivateSend(remoteName: String, message: String) extends Message

trait MsgManagement {this: ChatActor =>

  protected def messageManagement: Receive = {
    case PubMsg(message) => {
      getUserName(sender()) match {
        case Some(userName) => {
          appendPublishStory(userName, message)
          chatWindow.publishPost(getPublishStory())
          log.info("PublishMessage: {} from {}", message, sender())
        }
        case None =>
          log.error("Not authorized user {} send publish message {}", sender(), message)
      }
    }
    case PrivateMsg(message) => {
      getUserName(sender()) match {
        case Some(userName) =>
          appendStory(userName, message)
          chatWindow.privatePost(userName, getStory(userName))
          log.info("PrivateMessage: {} from {}", message, sender())
        case None =>
          log.error("Not authorized user {} say private message{}", sender(), message)
      }
    }
  }

  def sendPublishMessage(message: String) =  {
    getAllUserRefs.foreach(userRef => {
      userRef ! PubMsg(message)
    })
  }
  def sendPrivateMessage(userName: String, message: String) =  {
    getRef(userName) match {
      case Some(ref) =>
        ref ! PrivateMsg(message)
      case None =>
        log.error("Private message {} to not authorized user", message)
    }
  }
}