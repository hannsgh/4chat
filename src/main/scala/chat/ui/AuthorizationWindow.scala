package chat.ui

import chat.actors.ChatActor
import javafx.event.ActionEvent
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.{FlowPane, VBox}
import javafx.scene.paint.Color
import javafx.stage.{Modality, Stage, WindowEvent}

class AuthorizationWindow(primaryStage: Stage, javaFxChatActor: ChatActor) extends Stage{

  this.setTitle("Авторизация")
  val nameLabel = new Label("Ваше имя: ")
  val nameText = new TextField(javaFxChatActor.currentName)
  nameText.setMaxWidth(100)
  val nameField = new FlowPane(nameLabel, nameText)
  val errorRapport = new Label("")
  errorRapport.setMinWidth(80)
  errorRapport.setPadding(new Insets(10))
  errorRapport.setTextFill(Color.CORAL)
  val login = new Button("Вход")
  val authRoot = new VBox(nameField, errorRapport, login)
  authRoot.setAlignment(Pos.CENTER)

  login.setOnAction((e: ActionEvent) => {
    sendName(nameText.getText())
  })
  val scene = new Scene(authRoot, 250, 200)
  scene.setOnKeyPressed((e: KeyEvent) => {
    if (e.getCode == KeyCode.ENTER){
      sendName(nameText.getText())
    }
  })
  this.setScene(scene)
  this.initModality(Modality.NONE)
  this.initOwner(primaryStage)
  this.show()
  this.setOnCloseRequest((e: WindowEvent) => {
    javaFxChatActor.stopActorSystem()
    primaryStage.close()
  })

  def sendName(name: String): Unit = {
    javaFxChatActor.currentName = name
    if (name.matches("""[a-zA-Z0-9]+""") & name.size < 20){
      javaFxChatActor.loginCluster(name)
    } else {
      errorRapport.setText("Только буквы и цифры, максимальная длина 20")
    }
  }
}