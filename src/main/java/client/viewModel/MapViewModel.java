package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import game.Element;
import game.Game;
import game.Player;
import game.Robot;
import game.boardelements.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MapViewModel implements Initializable, PropertyChangeListener {

    private ClientModel clientModel = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();

    @FXML
    public GridPane mapGrid;

    private Map<Point2D, Group> fieldMap = new HashMap<Point2D, Group>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientModel.addPropertyChangeListener(this);
        clientGameModel.addPropertyChangeListener(this);

        int mapX = clientGameModel.getMap().size();
        int mapY = clientGameModel.getMap().get(0).size();
        try {
            createMapObjects(clientGameModel.getMap(), mapX, mapY);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // clientGameModel.blueBeltAnimePropertyProperty().bind(startAnimation("BlueBelt"));
  /*      clientGameModel.getanimationType().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                handleLaserAnime();
            }
        });*/
//        clientGameModel.canSetStartingPointProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
//                if (clientGameModel.canSetStartingPointProperty().getValue() == true) {
//                    Platform.runLater(() -> {
//                                setRobot(clientGameModel.getActualPlayerID(), clientGameModel.getX(), clientGameModel.getY());
//                            }
//                    );
//                    clientGameModel.canSetStartingPointProperty().setValue(false);
//                }
//            }
//        });
    }

//    private void startAnimation(String type) {
//        Double toX = null;
//        Double toY = null;
//        switch (type) {
//            case "BlueBelt" -> {
//
//            }
//
//        }
//
//        ArrayList<Point2D> laserPath = clientGameModel.getLaserPath((Point2D) clientGameModel.getLaserMap().keySet(), (Laser) clientGameModel.getLaserMap().values());
//        TranslateTransition transition = new TranslateTransition();
//        transition.setDuration(Duration.seconds(3));
//        transition.setToX(laserPath.indexOf(0));
//        transition.setToY(laserPath.size());
//        Group imageGroup = fieldMap.get((Point2D) clientGameModel.getLaserMap().keySet());
//        transition.setNode(imageGroup);
//        transition.play();
//
//    }


    //startings points
    public void setRobot (int playerID, int x, int y) {
        int figure = clientModel.getPlayersFigureMap().get(playerID);
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream(findPath("Robots/robot" + figure + ".png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(46);
        imageView.setFitHeight(46);

        fieldMap.get(new Point2D(x, y)).getChildren().add(imageView);

        try {
            input = new FileInputStream(findPath("images/TransparentElements/RobotDirectionArrowHUGE.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image image2 = new Image(input);
        imageView = new ImageView();
        imageView.setImage(image2);
        imageView.setFitWidth(46);
        imageView.setFitHeight(46);
        imageView.setRotate(clientGameModel.getAntennaOrientation());
        fieldMap.get(new Point2D(x, y)).getChildren().add(imageView);
    }


    public File findPath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }


    public ImageView loadImage(String element, String orientations) throws FileNotFoundException {
        FileInputStream path = null;
        Image image;
        path = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("images/mapElements/Elements/" + element + ".png")).getFile()));
        image = new Image(path);

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        switch (orientations) {
            case "top", "bottom,top,left", "left,bottom" -> {
                imageView.setRotate(0);
            }
            case "right", "right,left", "left,right,bottom", "top,left" -> {
                imageView.setRotate(90);
            }
            case "left", "right,left,top", "bottom,right", "left,right" -> {
                imageView.setRotate(-90);
            }
            case "bottom", "top,bottom,left", "right,top" -> {
                imageView.setRotate(180);
            }
            case "left,top,right", "bottom,left" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(90);
            }
            case "bottom,left,top", "right,bottom" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(0);
            }
            case "top,right,bottom", "left,top" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(180);
            }
            case "right,bottom,left", "top,right" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(-90);
            }
            case "null" -> {
                imageView.setImage(image);
            }
        }
        return imageView;


    }


    public void clickGrid(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != mapGrid) {
            Integer colIndex = GridPane.getColumnIndex(clickedNode.getParent());
            Integer rowIndex = GridPane.getRowIndex(clickedNode.getParent());
            clientModel.getClientGameModel().sendStartingPoint(colIndex, rowIndex);
        }
    }


    public void turnRobot(int playerID, String rotation) {
        Platform.runLater(() -> {
            Robot robot = null;
            for (HashMap.Entry<Robot, Point2D> entry : clientGameModel.getRobotMap().entrySet()) {
                if (entry.getKey().getName().equals(Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(playerID)))) {
                    robot = entry.getKey();
                    break;
                }
            }
            Point2D oldPosition = clientGameModel.getRobotMap().get(robot);
            double angle = 0;
            if (rotation.equals("clockwise")) {
                angle = 90;
            } else if (rotation.equals("counterclockwise")) {
                angle = -90;
            }
            Group imageGroup = fieldMap.get(oldPosition);
            ImageView robotOrientation = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);
            robotOrientation.setRotate(robotOrientation.getRotate() + angle);
            fieldMap.get(oldPosition).getChildren().remove(fieldMap.get(oldPosition).getChildren().size() - 1);
            fieldMap.get(oldPosition).getChildren().add(robotOrientation);

        });
    }


    public void moveRobot(int playerID, int x, int y) {
        Robot robot = null;
        for (HashMap.Entry<Robot, Point2D> entry : clientGameModel.getRobotMap().entrySet()) {
            if (entry.getKey().getName().equals(Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(playerID)))) {
                robot = entry.getKey();
                break;
            }
        }
        Point2D oldPosition = clientGameModel.getRobotMap().get(robot);
        Point2D newPosition = new Point2D(x, y);
        Platform.runLater(() -> {
            Group imageGroup = fieldMap.get(oldPosition);
            ImageView robotOrientation = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);
            ImageView robotV = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 2);
            fieldMap.get(oldPosition).getChildren().remove(fieldMap.get(oldPosition).getChildren().size() - 1);
            fieldMap.get(oldPosition).getChildren().remove(fieldMap.get(oldPosition).getChildren().size() - 1);
            fieldMap.get(newPosition).getChildren().add(robotV);
            fieldMap.get(newPosition).getChildren().add(robotOrientation);
        });
        clientGameModel.getRobotMap().replace(robot, newPosition);
    }


    private void createMapObjects(ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) throws IOException {

        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                Group imageGroup = new Group();
                ImageView imageView = loadImage("normal1", "null");
                imageGroup.getChildren().add(imageView);
                // ImageView imageView2 = new ImageView();
                for (int i = 0; i < map.get(x).get(y).size(); i++) {
                    switch (map.get(x).get(y).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(x).get(y).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            clientGameModel.getAntennaMap().put(new Point2D(x, y), antenna);
                            ImageView imageView2 = loadImage("priority-antenna", String.join(",", antenna.getOrientations()));
                            imageGroup.getChildren().add(imageView2);
                        }

                        case "CheckPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());

                            ImageView imageView2 = loadImage("victory-counter" + checkPoint.getCount(), "null");
                            imageGroup.getChildren().add(imageView2);

                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(x).get(y).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());


                            if (conveyorBelt.getSpeed() == 2) {
                                switch (conveyorBelt.getOrientations().size()) {
                                    case 1 -> {
                                        ImageView imageView2 = loadImage("BlueBelt", String.join(",", conveyorBelt.getOrientations()));
                                        imageGroup.getChildren().add(imageView2);
                                    }
                                    case 2 -> {
                                        ImageView imageView2 = loadImage("RotatingBeltBlue3", String.join(",", conveyorBelt.getOrientations()));
                                        imageGroup.getChildren().add(imageView2);
                                    }
                                    case 3 -> {
                                        ImageView imageView2 = loadImage("RotatingBeltBlue2", String.join(",", conveyorBelt.getOrientations()));
                                        imageGroup.getChildren().add(imageView2);
                                    }
                                }
                            }

                            if (conveyorBelt.getSpeed() == 1) {
                                switch (conveyorBelt.getOrientations().size()) {
                                    case 1 -> {
                                        ImageView imageView2 = loadImage("GreenBelt", String.join(",", conveyorBelt.getOrientations()));
                                        imageGroup.getChildren().add(imageView2);
                                    }
                                    case 2 -> {
                                        ImageView imageView2;
                                        if (conveyorBelt.getIsOnBoard().equals("Start A")) {
                                            imageView2 = loadImage("GreenBelt", String.join(",", conveyorBelt.getOrientations()));
                                        } else {
                                            imageView2 = loadImage("RotatingBeltGreen1", String.join(",", conveyorBelt.getOrientations()));
                                        }
                                        imageGroup.getChildren().add(imageView2);
                                    }

                                }

                            }

                        }

                        case "EnergySpace" -> {
                            //TODO gibt es einen Unterschied wenn die ES richtung links oder recht; Anpassung für isOnBoard 1A/5B
                            Element element = map.get(x).get(y).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            ImageView imageView2;
                            if (energySpace.getIsOnBoard().equals("5B")) {
                                imageView2 = loadImage("RedEnergySpace", "null");
                            } else {
                                imageView2 = loadImage("RedEnergySpace", "right");
                            }
                            imageGroup.getChildren().add(imageView2);
                        }

                        case "Gear" -> {
                            Element element = map.get(x).get(y).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            ImageView imageView2 = null;
                            String gearOrient = String.join(",", gear.getOrientations());
                            if (gearOrient.equals("clockwise")) {
                                imageView2 = loadImage("GreenGear", String.join(",", gear.getOrientations()));
                            } else if (gearOrient.equals("counterclockwise")) {
                                imageView2 = loadImage("RedGear", String.join(",", gear.getOrientations()));
                            }
                            imageGroup.getChildren().add(imageView2);
                        }


                        //TODO:laser 1 or two handeln und dann orientation
                        case "Laser" -> {
                            Element element = map.get(x).get(y).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            ImageView imageView2 = loadImage("Laser" + laser.getCount(), String.join(",", laser.getOrientations()));
                            imageGroup.getChildren().add(imageView2);
                        }
                        case "Pit" -> {
                            Element element = map.get(x).get(y).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
                            ImageView imageView2 = loadImage("Pit", "null");
                            imageGroup.getChildren().add(imageView2);
                        }

                        case "PushPanel" -> {

                            Element element = map.get(x).get(y).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
                            String pushPanelType = String.valueOf(pushPanel.getRegisters());
                            ImageView imageView2;
                            if (pushPanelType.equals("[1, 3, 5]")) {
                                imageView2 = loadImage("PushPanel135", String.join(",", pushPanel.getOrientations()));
                            } else {
                                imageView2 = loadImage("PushPanel24", String.join(",", pushPanel.getOrientations()));
                            }
                            imageGroup.getChildren().add(imageView2);
                        }

                        case "RestartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            ImageView imageView2 = loadImage("reboot", String.join(",", restartPoint.getOrientations()));
                            imageGroup.getChildren().add(imageView2);
                        }


                        case "StartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
                            ImageView imageView2 = loadImage("StartingPoint", "null");
                            imageGroup.getChildren().add(imageView2);
                        }


                        case "Empty" -> {
                            Element element = map.get(x).get(y).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
                            ImageView imageView2 = loadImage("normal1", "null");
                            imageGroup.getChildren().add(imageView2);
                        }

                        //TODO: dopple oder one wall
                        case "Wall" -> {
                            Element element = map.get(x).get(y).get(i);
                            Wall wall = new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            ImageView imageView2 = loadImage("Wall", String.join(",", wall.getOrientations()));
                            imageGroup.getChildren().add(imageView2);
                        }
                        default -> { //place for exception handling
                        }

                    }
                }

                fieldMap.put(new Point2D(x, y), imageGroup);
                mapGrid.setConstraints(imageGroup, x, y);
                // mapGrid.setAlignment(Pos.CENTER);
                mapGrid.getChildren().add(imageGroup);
                /*GridPane.setHalignment(imageGroup, HPos.CENTER);
                GridPane.setValignment(imageGroup, VPos.CENTER);*/
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        clientModel.getClientGameModel().setStartingPoint(false);
        if (evt.getPropertyName().equals("gameFinished")) {
            fieldMap.clear();
        }
        if (evt.getPropertyName().equals("startingPoint")) {
            Platform.runLater(() -> {
                for (Map.Entry<Robot, Point2D> entry : clientGameModel.getStartingPointQueue().entrySet()) {
                    int playerID = clientModel.getIDfromRobotName(entry.getKey().getName());
                    setRobot(playerID, (int) entry.getValue().getX(), (int) entry.getValue().getY());
                    clientModel.getClientGameModel().getRobotMap().put(entry.getKey(), entry.getValue());
                    clientModel.getClientGameModel().getStartingPointQueue().remove(entry.getKey());
                    //handleAnimation("BlueConveyorBelt");
                    // handleLaserAnime();
                }
            });
        }

        if (evt.getPropertyName().equals("queueMove")) {
            clientModel.getClientGameModel().setQueueMove(false);
            for (int i = 0; i < clientGameModel.getMoveQueue().size(); i++) {
                ClientGameModel.MoveTask newMoveTask = clientGameModel.getMoveQueue().get(i);
                int playerID = newMoveTask.getPlayerID();
                Point2D point2D = newMoveTask.getNewPosition();
                moveRobot(playerID, (int) point2D.getX(), (int) point2D.getY());
                clientGameModel.getMoveQueue().remove(i);
            }
        }
        if (evt.getPropertyName().equals("queueTurning")) {
            clientModel.getClientGameModel().setQueueTurning(false);
            Platform.runLater(() -> {
                for (int i = 0; i < clientGameModel.getTurningQueue().size(); i++) {
                    ClientGameModel.TurnTask newTurnTask = clientGameModel.getTurningQueue().get(i);
                    int playerID = newTurnTask.getplayerID();
                    String rotation = newTurnTask.getRotation();
                    turnRobot(playerID, rotation);
                    clientGameModel.getTurningQueue().remove(i);
                }
            });
        }
        if (evt.getPropertyName().equals("Gears")) {
            clientModel.getClientGameModel().setAnimateGears(false);
            Platform.runLater(() -> {
                animateGears();
            });
        }
    }

    public void handleAnimation(String type) {
        double ToX = 0;
        double ToY = 0;
        double move;
        switch (type) {
            case "BlueConveyorBelt" -> move = 2;
            case "GreenConveyorBelt" -> move = 1;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        for (Map.Entry<Point2D, ConveyorBelt> entry : clientGameModel.getConveyorBeltMap().entrySet()) {
            ConveyorBelt belt = entry.getValue();
            if (belt.getOrientations().equals("left") || belt.getOrientations().get(0).equals("left")) {
                ToX = entry.getKey().getX() - move;
                ToY = 0.0;
            }
            if (belt.getOrientations().equals("right") || belt.getOrientations().get(0).equals("right")) {
                ToX = entry.getKey().getX() + move;
                ToY = 0.0;
            }
            if (belt.getOrientations().equals("top") || belt.getOrientations().get(0).equals("top")) {
                ToY = entry.getKey().getY() - move;
                ToX = 0.0;
            }
            if (belt.getOrientations().equals("bottom") || belt.getOrientations().get(0).equals("bottom")) {
                ToY = entry.getKey().getY() + move;
                ToX = 0.0;
            }
            // System.out.println("Bis HERE IST ALLES GUT GELAUFEN");
            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.INDEFINITE);
            transition.setToX(ToX);
            transition.setToY(ToY);
            Group imageGroup = fieldMap.get(entry.getKey());
            // moveRobot(clientGameModel.getActualPlayerID(), (int)ToX, (int)ToY );
            ImageView robotOrientation = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);
            ImageView robotV = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 2);
            transition.setNode(robotV);
            //transition.setNode(robotOrientation);
            //System.out.println("ANIMATION BIS HERE IST GUT");
            transition.play();

        }
    }

   /* public void handleLaserAnime() {
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream(findPath("Robots/Elements/OneLaserBeam.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);

        for (Map.Entry<Point2D, Laser> entry : clientGameModel.getLaserMap().entrySet()) {
            Laser laser = entry.getValue();
            ArrayList<Point2D> laserPath = clientGameModel.getLaserPath(entry.getKey(), laser);

            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.INDEFINITE);
            transition.setToX(laserPath.indexOf(0));
            transition.setToY(laserPath.size());
            ImageView laserBeam = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size()-1);

            //Group imageGroup = fieldMap.get((Point2D) clientGameModel.getLaserMap().keySet());
            //ImageView laserBeam = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);

            laserBeam.setImage(image);
            transition.setNode(laserBeam);
            transition.play();
        }


    }*/

    public void handleLaserAnime() {
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream(findPath("Robots/Elements/OneLaserBeam.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);

        for (Map.Entry<Point2D, Laser> entry : clientGameModel.getLaserMap().entrySet()) {
            Laser laser = entry.getValue();


            ImageView laserBeam = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size()-1);
            laserBeam.setImage(image);
            FadeTransition ft = new FadeTransition(Duration.millis(3000), laserBeam);
            ft.setNode(laserBeam);
            ft.setFromValue(1.0);
            ft.setToValue(0.1);
            ft.setCycleCount(Timeline.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
        }

    }
    //(Point2D laserPosition, Laser laser);
    // Point2D laserPosition = null;

    //hier Kriege ich den Laser Position x,y
       /* for (Point2D pos: clientGameModel.getLaserMap().keySet()  ) {
         laserPosition.add(pos);

        }*/


       /* ArrayList<Point2D> laserPath = clientGameModel.getLaserPath((Point2D) clientGameModel.getLaserMap().keySet(), (Laser) clientGameModel.getLaserMap().values());
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(3));
        transition.setToX(laserPath.indexOf(0));
        transition.setToY(laserPath.size());
        Group imageGroup = fieldMap.get((Point2D) clientGameModel.getLaserMap().keySet());
        transition.setNode(imageGroup);
        transition.play();*/


    private void animateGears () {
        for (Map.Entry<Point2D, Gear> entry : clientGameModel.getGearMap().entrySet()) {
            int layer = 1;
            if (clientGameModel.isRobotOnField(entry.getKey())) {
                layer = 3;
            }
            ImageView gear = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size() - layer);

            RotateTransition rotateTransition = new RotateTransition(Duration.millis(5000), gear);
            rotateTransition.setByAngle(90);
            mapGrid.setAlignment(Pos.CENTER);
            gear.setScaleY(0.70);
            gear.setScaleX(0.70);
            gear.setPreserveRatio(true);
            rotateTransition.setCycleCount(Animation.INDEFINITE);
            rotateTransition.setInterpolator(Interpolator.LINEAR);
            rotateTransition.setDuration(Duration.seconds(2));
            rotateTransition.play();


        }
    }


    public ArrayList<Robot> getRobotsOnFields (Point2D position) {
        ArrayList<Robot> robotsOnFields = new ArrayList<>();

        for (Map.Entry<Robot, Point2D> entry : clientGameModel.getRobotMap().entrySet()) {
            if (entry.getKey().getxPosition() == (int) position.getX() &&
                    entry.getKey().getyPosition() == (int) position.getY()) {
                robotsOnFields.add(entry.getKey());
            }
        }
        return robotsOnFields;
    }


    public ArrayList<Point2D> getLaserPath (Laser laser, Point2D laserPosition) {
        ArrayList<Point2D> laserPath = new ArrayList<>();
        laserPath.add(laserPosition);
        boolean foundBlocker = false;
        double tempPosition;
        switch (laser.getOrientations().get(0)) {
            case "top" -> {
                tempPosition = laserPosition.getY();
                while (!foundBlocker) {
                    tempPosition--;
                    laserPath.add(new Point2D(laserPosition.getX(), tempPosition));
                    for (int i = 0; i < clientGameModel.getMap().get((int) laserPosition.getX()).get((int) tempPosition).size(); i++) {
                        //Is a robot in the line of the laser?
                        if (!getRobotsOnFields(new Point2D(laserPosition.getX(), tempPosition)).isEmpty()) {
                            foundBlocker = true;
                            //Robot robotShot = getRobotsOnFields(new Point2D(laserPosition.getX(), tempPosition)).get(0);
                            break;
                        }
                        if (clientGameModel.getMap().get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            case "bottom" -> {
                tempPosition = laserPosition.getY();
                while (!foundBlocker) {
                    tempPosition++;
                    laserPath.add(new Point2D(laserPosition.getX(), tempPosition));
                    for (int i = 0; i < clientGameModel.getMap().get((int) laserPosition.getX()).get((int) tempPosition).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(laserPosition.getX(), tempPosition)).isEmpty()) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) laserPosition.getX()).get((int) tempPosition).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            case "left" -> {
                tempPosition = laserPosition.getX();
                while (!foundBlocker) {
                    tempPosition--;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for (int i = 0; i < clientGameModel.getMap().get((int) tempPosition).get((int) laserPosition.getY()).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(tempPosition, laserPosition.getY())).isEmpty()) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            case "right" -> {
                tempPosition = laserPosition.getX();
                while (!foundBlocker) {
                    tempPosition++;
                    laserPath.add(new Point2D(tempPosition, laserPosition.getY()));
                    for (int i = 0; i < clientGameModel.getMap().get((int) tempPosition).get((int) laserPosition.getY()).size(); i++) {
                        if (!getRobotsOnFields(new Point2D(tempPosition, laserPosition.getY())).isEmpty()) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("Wall")) {
                            foundBlocker = true;
                            break;
                        }
                        if (clientGameModel.getMap().get((int) tempPosition).get((int) laserPosition.getY()).get(i).getType().equals("CheckPoint")) {
                            foundBlocker = true;
                        }
                    }
                }
            }
            default -> {
                //Place for exception handling
            }
        }

        return laserPath;
    }


    public void activateLasers () {
        for (Point2D position : clientGameModel.getLaserMap().keySet()) {
            for (Point2D beamPosition : getLaserPath(clientGameModel.getLaserMap().get(position), position)) {
                int x = (int) beamPosition.getX();
                int y = (int) beamPosition.getY();
                FileInputStream input = null;
                Image image;
                try {
                    input = new FileInputStream(findPath("Robots/Elements/OneLaserBeam.png"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                image = new Image(input);
                ImageView imageView = new ImageView();
                imageView.setImage(image);
                imageView.setFitWidth(46);
                imageView.setFitHeight(46);
                Point2D newPosition = new Point2D(x, y);
                Platform.runLater(() -> {
                    fieldMap.get(newPosition).getChildren().add(imageView);
                });


            }
        }
    }
}
   /* public void start(Stage primaryStage)
    {
        ImageView iv = new ImageView();
        Image image = new Image("file:res/flowers.jpg");
        iv.setImage(image);

        FadeTransition ft = new FadeTransition();
        ft.setNode(iv);
        ft.setDuration(new Duration(2000));
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(6);
        ft.setAutoReverse(true);

        iv.setOnMouseClicked(me -> ft.play());

        Group root = new Group();
        root.getChildren().add(iv);
        Scene scene = new Scene(root, image.getWidth(), image.getHeight());

        primaryStage.setTitle("FadeTransition Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/





