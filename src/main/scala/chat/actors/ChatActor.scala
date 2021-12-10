package chat.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, UnreachableMember}
import chat.ui.{AuthorizationWindow, ChatWindow}
import javafx.stage.Stage


class ChatActor(val primaryStage: Stage) extends Actor with ActorLogging
  with UserManagement
  with MsgManagement
  with ChatStorage {

  val cluster = Cluster(context.system)
  primaryStage.hide()
  var currentName = "Nobody"
  val authWindow = new AuthorizationWindow(primaryStage, this)
  val chatWindow = new ChatWindow(primaryStage, this)

  override def preStart() = {
    cluster.registerOnMemberUp {
      cluster.subscribe(self, classOf[MemberEvent], classOf[UnreachableMember])
    }
  }

  override def receive = userManagement orElse messageManagement

  override def postStop() = {
    logoutCluster()
    cluster.unsubscribe(self)
  }

  def stopActorSystem() = {
    context.system.terminate()
  }

  def addUser(userName: String, ref: ActorRef) = {
    log.info("add user: {} {}", userName, ref)
    users += (userName -> ref)
    chathistory += (userName -> " ")
    context.watch(ref)
    chatWindow.addUserNameButton(userName)
  }

  def removeUser(userName: String) = {
    log.info("remove user: {} {}", userName)
    users -= (userName)
    chathistory -= (userName)
    chatWindow.removeUserNameButton(userName)
  }
}

object ChatActor {
  def props(primaryStage: Stage) = Props(new ChatActor(primaryStage: Stage))
}