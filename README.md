![startScreen](https://github.com/user-attachments/assets/87fb15a4-82ec-43a1-8009-cb04c96e1b55)

# Welcome!
This is our university project for the board game RoboRally.

Here you will find everything needed for you to downaload and run the game on your local machine.

## Run Using JAR
1. Download the JAR files from [here](https://github.com/Hajuj/RoboRally/tree/master/target)
2. To run the server, write:
~~~
java -jar Server-jar-with-dependencies.jar -p PORTNUMBER
~~~
3. To run a client, write:
~~~
java -jar --module-path "PATH-TO-JAVAFX" --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.media,javafx.base ClientApplication-jar-with-dependencies.jar
~~~
4. To run AI, write:
~~~
java -jar SimpleAIModel-jar-with-dependencies.jar
~~~
* **Repeat 3 until you get the desired number of players.**

## Lobby

1. Type in your name and click "Choose username".
2. Select your robot by clicking on it.
3. Click on "Submit selections".
4. Click on "Ready to play!".
5. If you are the first one ready, you can select the racing course in the dropdown-menu and then confirm your selection by clicking on the green check mark.
6. If enough players are ready, the game starts. There are four buttons on the left side.

## Game

1. Green robot button: amount of energy cubes and reached checkpoints.
2. Purple register button: played cards.
3. Pink speech bubbles button: chat.
4. Blue arrow button (only visible in the activation phase): plays the card.

## Project Requirements
- Java 16
- JavaFX 16
- Gson 2.8.6
- Maven
- Log4j 1.2.17
