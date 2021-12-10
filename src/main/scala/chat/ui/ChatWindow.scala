package chat.ui

import chat.actors.ChatActor
import javafx.event.{ActionEvent, Event}
import javafx.scene.control.{Button, Tab}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.stage.{Stage, WindowEvent}

class ChatWindow(val primaryStage: Stage, val javaFxChatActor: ChatActor) extends Stage with ChatView {

  defaultTab.setId(javaFxChatActor.defaultRoom)

  defaultTab.setOnSelectionChanged((e: Event) => {
    storyArea.setText(javaFxChatActor.getPublishStory())
  })
  sendButton.setOnAction((e: ActionEvent) => {
    send()
  })
  messageField.setOnKeyPressed((e: KeyEvent) => {
    if (e.getCode == KeyCode.ENTER) {
      send()
    }
  })
  storyArea.setText(javaFxChatActor.getPublishStory())

  def send() = {
    val selectedTabText = userTabs.getSelectionModel.getSelectedItem.getText
    if (selectedTabText == javaFxChatActor.defaultRoom) {
      javaFxChatActor.sendPublishMessage(messageField.getText)
    } else {
      if (selectedTabText != javaFxChatActor.currentName & javaFxChatActor.getAllUserNames().contains(selectedTabText)) {
        javaFxChatActor.sendPrivateMessage(selectedTabText, messageField.getText)
      }
      javaFxChatActor.appendMyStory(selectedTabText, messageField.getText)
      storyArea.setText(javaFxChatActor.getStory(selectedTabText))
    }
    messageField.setText("")
  }
  def addUserNameButton(userName: String): Unit = {
    val userButton = new Button(userName)
    userButton.setMaxWidth(Double.MaxValue)
    if (userName == javaFxChatActor.currentName){
      userButton.setStyle("-fx-background-color: rgb(0,0,255);")
    }
    userButton.setOnAction((e: ActionEvent) => {
      val tab = searchTab(userName)
      if (tab == null) {
        val freshTab = addTab(userName)
        userTabs.getSelectionModel().select(freshTab)
        storyArea.setText(javaFxChatActor.getStory(userName))
      } else {
        userTabs.getSelectionModel().select(tab)
        storyArea.setText(javaFxChatActor.getStory(userName))
      }
    })
    userList.getChildren().add(userButton)
  }
  def removeUserNameButton(userName: String) = {
    userList.getChildren().forEach(node => {
      node match {
        case button: Button => {
          if (button.getText() == userName) {
            userList.getChildren().remove(button)
          }
        }
      }
    })
  }
  def searchTab(userName: String): Tab = {
    var foundTab: Tab = null
    userTabs.getTabs().forEach(tab => {
      if (tab.getText == userName & tab.getId != javaFxChatActor.defaultRoom) {
        foundTab = tab
      }
    })
    foundTab
  }
  def addTab(userName: String): Tab = {
    val tab = new Tab(userName)
    if (userName == javaFxChatActor.currentName){
      tab.setStyle("-fx-background-color: rgb(100,107,191);")
    }
    tab.setOnSelectionChanged((e: Event) => {
      storyArea.setText(javaFxChatActor.getStory(userName))
      println("Выбран " + userName)
    })
    userTabs.getTabs().add(tab)
    tab
  }


  def publishPost(publishStory: String) = {
    println("Сообщение в общий чат")
    if (userTabs.getSelectionModel.getSelectedItem.getId == javaFxChatActor.defaultRoom) {
      storyArea.setText(publishStory)
    }
  }
  def privatePost(userName: String, story: String) = {
    val selectedTabText = userTabs.getSelectionModel.getSelectedItem.getText
    if (searchTab(userName) == null) {
      addTab(userName)
    }
    if (selectedTabText == userName) {
      storyArea.setText(story)
    }
  }
  this.setOnCloseRequest((e: WindowEvent) => {
    primaryStage.close()
    javaFxChatActor.stopActorSystem()
  })
}