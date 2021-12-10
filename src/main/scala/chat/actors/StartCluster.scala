package chat.actors

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import javafx.stage.Stage

object StartCluster {
  def apply(config: Config, primaryStage: Stage)= {
    val regularConfig = ConfigFactory.load
    val system = ActorSystem("ChatSystem", config.withFallback(regularConfig))

    val javaFxChatActor = system.actorOf(ChatActor.props(primaryStage: Stage).
      withDispatcher("akka.actor.javafx-dispatcher"), "javaFxChatActor")
  }
}