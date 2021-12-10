package chat.actors

import akka.actor.ActorRef

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}
import scala.collection.mutable

trait ChatStorage {
  var currentName: String

  val defaultRoom = "Общая группа"
  val users = mutable.HashMap[String, ActorRef]()
  val chathistory = mutable.HashMap[String, String]()
  var hello = "Добро пажаловать в чат !!!"

  def removeStory(userName: String): Unit = {
    chathistory -= (userName)
  }

  def appendStory(userName: String, message: String): Unit = {
    println(s"appendStory: ${userName}, ${message}")
    if (!getAllUserNames().contains(userName)) {
      chathistory += (userName -> " ")
    }
    chathistory(userName) = chathistory(userName) + "\n" + getTime()+ " [" + userName + "]: " + message
    println(s"story: ${chathistory(userName)}")
  }

  def appendMyStory(userName: String, message: String): Unit = {
    chathistory(userName) = chathistory(userName) + "\n"  + getTime()+ " [" + currentName + "]: " + message
  }

  def appendPublishStory(userName: String, message: String): Unit = {
    hello = hello + "\n" + getTime()+ " [" + userName + "]: " + message
  }

  def getStory(userName: String): String = {
    chathistory.get(userName).getOrElse("")
  }

  def getPublishStory(): String = {
    hello
  }

  def getUserName(ref: ActorRef): Option[String] = {
    users.find(_._2 == ref).map(_._1)
  }

  def getAllUserNames(): List[String] = {
    users.keys.toList
  }

  def getRef(userName: String): Option[ActorRef] = {
    users.get(userName)
  }

  def getAllUserRefs(): List[ActorRef] = {
    users.values.toList
  }
  def getTime() = {

    val now = Calendar.getInstance().getTime()
    val timeFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm")
    timeFormat.setTimeZone(TimeZone.getTimeZone("Europe/Samara"))
    timeFormat.format(now)
  }
}
