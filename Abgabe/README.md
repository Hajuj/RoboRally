![](https://gitlab2.cip.ifi.lmu.de/dbs_sep/dbs_sep2021/blinde-bonbons/-/raw/master/src/main/resources/images/Gui/startScreen.gif)

# Welcome!
This is our university project for the board game RoboRally.

Here you will find everything needed for you to downaload and run the game on your local machine.

## Run Using JAR
1. Download the JAR files from [here](https://gitlab2.cip.ifi.lmu.de/dbs_sep/dbs_sep2021/blinde-bonbons/-/tree/Abgabe/target)
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
java -jar SimpleAIModel-jar-with-dependencies.jar -h HOST_IP -p PORTNUMBER
~~~
* **Repeat 3 until you get the desired number of players.**

## Game
Look at the [Wiki](https://gitlab2.cip.ifi.lmu.de/dbs_sep/dbs_sep2021/blinde-bonbons/-/wikis/home) to find everything relevant for the Game.
##
* JavaDocs are [here](https://gitlab2.cip.ifi.lmu.de/dbs_sep/dbs_sep2021/blinde-bonbons/-/tree/Abgabe/JavaDocs)
## Project Requirements
- Java 16
- JavaFX 16
- Gson 2.8.6
- Maven
- Log4j 1.2.17
