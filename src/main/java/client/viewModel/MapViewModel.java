package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import game.Element;
import game.Game;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * The type Map view model.
 */
public class MapViewModel implements Initializable, PropertyChangeListener {

    private ClientModel clientModel = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();

    /**
     * The Map grid.
     */
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


    }


    /**
     * Sets the robot and its image for each player and loads their image.
     *
     * @param playerID the player id
     * @param x        the x
     * @param y        the y
     */
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


    /**
     * Finds the paths for files,nice tool for loading images.
     *
     * @param fileName the file name
     * @return the file
     */
    public File findPath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }


    /**
     * Loads image views for the realted imaged.
     *
     * @param element      the element
     * @param orientations the orientations
     * @return the image view
     * @throws FileNotFoundException the file not found exception
     */
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


    /**
     * Hnadles the click event on the grid.
     *
     * @param event the event
     */
    public void clickGrid(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != mapGrid) {
            Integer colIndex = GridPane.getColumnIndex(clickedNode.getParent());
            Integer rowIndex = GridPane.getRowIndex(clickedNode.getParent());
            clientModel.getClientGameModel().sendStartingPoint(colIndex, rowIndex);
        }
    }


    /**
     * Important method for turning the robot.
     * Gets the location of the robot and turns it right or left.
     *
     * @param playerID the player id
     * @param rotation the rotation
     */
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


    /**
     * Important method to move the robots.Gets the robots location and replaces it with the new one.
     *
     * @param playerID the player id
     * @param x        the x
     * @param y        the y
     */
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

    /**
     * Creates the map objects on the map.
     * Reads the map obejects and according to their type loads their images on the according coordinate.
     * @param map
     * @param mapX
     * @param mapY
     * @throws IOException
     */

    private void createMapObjects(ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) throws IOException {

        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                Group imageGroup = new Group();
                ImageView imageView = loadImage("normal1", "null");
                imageGroup.getChildren().add(imageView);

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

                mapGrid.getChildren().add(imageGroup);

            }
        }
    }

    /**
     * Handles the property changes for Starting point, move queue and turning queue.
     * @param evt
     */
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

                }
            });
        }

        if (evt.getPropertyName().equals("queueMove")) {
            clientModel.getClientGameModel().setQueueMove(false);
                for (Map.Entry<Robot, Point2D> entry : clientGameModel.getMoveQueue().entrySet()) {

                    int playerID = clientModel.getIDfromRobotName(entry.getKey().getName());
                    moveRobot(playerID, (int) entry.getValue().getX(), (int) entry.getValue().getY());
                    clientModel.getClientGameModel().getRobotMap().replace(entry.getKey(), entry.getValue());
                    clientModel.getClientGameModel().getMoveQueue().remove(entry.getKey());
                }
        }
        if (evt.getPropertyName().equals("queueTurning")) {
            clientModel.getClientGameModel().setQueueTurning(false);
            Platform.runLater(() -> {
                for (Map.Entry<Robot, String> entry : clientGameModel.getTurningQueue().entrySet()) {
                    //TODO check NullPointerException here
                    int playerID = clientModel.getIDfromRobotName(entry.getKey().getName());
                    turnRobot(playerID, entry.getValue());
                    //TODO: wo muss ich die orientation ändern in ClientGameModel?
                    clientModel.getClientGameModel().getTurningQueue().remove(entry.getKey());
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

    /**
     * Handles the animations of the board elements.
     * Reads the orientations of the elements and calls the related animation.
     *
     * @param type the type
     */
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

            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.INDEFINITE);
            transition.setToX(ToX);
            transition.setToY(ToY);
            Group imageGroup = fieldMap.get(entry.getKey());
            ImageView robotOrientation = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);
            ImageView robotV = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 2);
            transition.setNode(robotV);

            transition.play();

        }
    }


    /**
     * Laser animations handler
     * Puts a onelaserbeam on the laser path.
     */
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

    /**
     * Gear animations
     * Gears turn clockwise or counter clockwise thanks to this method.
     */

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
}





