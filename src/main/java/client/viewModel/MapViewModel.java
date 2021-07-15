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
        PushPanelsAnimation();
        activateLasers();

    }

  /**
     * Sets the robot and its image for each player and loads their image.
     *
     * @param playerID the player id
     * @param x        the x
     * @param y        the y
     */

    public void setRobot(int playerID, int x, int y) {
        int figure = clientModel.getPlayersFigureMap().get(playerID);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Robots/robot" + figure + ".png")));
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(46);
        imageView.setFitHeight(46);

        fieldMap.get(new Point2D(x, y)).getChildren().add(imageView);

        Image image1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TransparentElements/RobotDirectionArrowHUGE.png")));
        imageView = new ImageView();
        imageView.setImage(image1);
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
        Image image;

        if (element.equals("BlueBelt")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/BlueBelt_transparent_animated.gif")));
        } else if (element.equals("OneLaserBeam")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/OneLaserBeamAnimated.gif")));
        } else if (element.equals("DoppleLaserBeam")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/TwoLaserBeam_transparent_animated.gif")));
        } else if (element.equals("TribleLaserBeam")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/ThreeLaserBeam_transparent_animated.gif")));
        } else {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/" + element + ".png")));
        }

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
            case "bottom", "top,bottom,left", "right,top","bottom,top"  -> {
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

                                    /*case 1 -> {
                                        ImageView imageView2 = loadImage("BlueBelt", String.join(",", conveyorBelt.getOrientations()));
                                        imageGroup.getChildren().add(imageView2);
                                    }*/
                                    /*conveyorBelt.getOrientations ().equals ( "[right, left]")|| conveyorBelt.getOrientations ().equals ( "[left, right]" )||
                                            conveyorBelt.getOrientations ().equals ( "[top, bottom]" )||conveyorBelt.getOrientations ().equals ( "[bottom, top]" ))*/

                                    case 2 -> {
                                        ImageView imageView2;
                                        if (conveyorBelt.getOrientations ().get ( 0 ).equals ( "right" )&&conveyorBelt.getOrientations ().get ( 1 ).equals ( "left" )||
                                                conveyorBelt.getOrientations ().get ( 0 ).equals ( "top" )&&conveyorBelt.getOrientations ().get ( 1 ).equals ( "bottom" )||
                                                conveyorBelt.getOrientations ().get ( 0 ).equals ( "bottom" )&&conveyorBelt.getOrientations ().get ( 1 ).equals ( "top" )||
                                                conveyorBelt.getOrientations ().get ( 0 ).equals ( "left" )&&conveyorBelt.getOrientations ().get ( 1 ).equals ( "right" )){
                                            imageView2 = loadImage ( "BlueBelt", String.join ( ",", conveyorBelt.getOrientations ( ) ) );

                                        }else{
                                            //System.out.println ( conveyorBelt.getOrientations () );
                                            imageView2 = loadImage ( "RotatingBeltBlue3", String.join ( ",", conveyorBelt.getOrientations ( ) ) );
                                        }
                                        imageGroup.getChildren().add(imageView2);
                                    }
                                    case 3 -> {
                                        ImageView imageView2 = loadImage("RotatingBeltBlue2", String.join(",", conveyorBelt.getOrientations()));
                                        imageGroup.getChildren().add(imageView2);
                                    }
                                }
                            }

                            if (conveyorBelt.getSpeed ( ) == 1) {
                                ImageView imageView2;
                                if (conveyorBelt.getIsOnBoard ( ).equals ( "Start A" )) {
                                    imageView2 = loadImage ( "GreenBelt", String.join ( ",", conveyorBelt.getOrientations ( ) ) );
                                    imageGroup.getChildren ( ).add ( imageView2 );


                                }
                                if (!conveyorBelt.getIsOnBoard ( ).equals ( "Start A" )) {
                                    if (conveyorBelt.getOrientations ( ).get ( 0 ).equals ( "right" ) && conveyorBelt.getOrientations ( ).get ( 1 ).equals ( "left" ) ||
                                            conveyorBelt.getOrientations ( ).get ( 0 ).equals ( "top" ) && conveyorBelt.getOrientations ( ).get ( 1 ).equals ( "bottom" ) ||
                                            conveyorBelt.getOrientations ( ).get ( 0 ).equals ( "bottom" ) && conveyorBelt.getOrientations ( ).get ( 1 ).equals ( "top" ) ||
                                            conveyorBelt.getOrientations ( ).get ( 0 ).equals ( "left" ) && conveyorBelt.getOrientations ( ).get ( 1 ).equals ( "right" )) {
                                        imageView2 = loadImage ( "GreenBelt", String.join ( ",", conveyorBelt.getOrientations ( ) ) );
                                    } else {
                                        imageView2 = loadImage ( "RotatingBeltGreen1", String.join ( ",", conveyorBelt.getOrientations ( ) ) );
                                    }
                                    imageGroup.getChildren ( ).add ( imageView2 );
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
     * Handles the animations of gears.
     * Reads the orientations of the elements and calls the related animation.
     *
     * @param type the type
     */

    private void animateGears() {
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
/**
*Returns the points of the robots on the field 
*/
    public ArrayList<Robot> getRobotsOnFields(Point2D position) {
        ArrayList<Robot> robotsOnFields = new ArrayList<>();

        for (Map.Entry<Robot, Point2D> entry : clientGameModel.getRobotMap().entrySet()) {
            if (entry.getKey().getxPosition() == (int) position.getX() &&
                    entry.getKey().getyPosition() == (int) position.getY()) {
                robotsOnFields.add(entry.getKey());
            }
        }
        return robotsOnFields;
    }
/**
*Finds the path of the laserPosition*/
    public ArrayList<Point2D> getLaserPath(Laser laser, Point2D laserPosition) {
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
/**
*Allows to walk throug the coordinates
*/
    private Point2D getMoveInDirection(Point2D position, String orientation) {
        double x = position.getX();
        double y = position.getY();
        switch (orientation) {
            case "left" -> x -= 1;
            case "right" -> x += 1;
            case "top" -> y -= 1;
            case "bottom" -> y += 1;
        }

        return new Point2D(x, y);
    }
/**
* Moves the checkpoints on some wicked mapElements
*/

    public void moveCheckPoint(int checkpointID, Point2D newPosition) {
        Point2D oldPosition = clientGameModel.getCheckpointPositionByID(checkpointID);
        int layer;
        if (clientGameModel.getRobotsOnFields(oldPosition).size() == 0) {
            layer = 1;
        } else {
            layer = 3;
        }

        ImageView checkPoint = (ImageView) fieldMap.get(oldPosition).getChildren().get(fieldMap.get(oldPosition).getChildren().size() - layer);
        fieldMap.get(oldPosition).getChildren().remove(fieldMap.get(oldPosition).getChildren().size() - layer);

        boolean isRobotHere = false;
        if (clientGameModel.getRobotsOnFields(newPosition).size() != 0) {
            isRobotHere = true;
        }

        if (!isRobotHere) {
            fieldMap.get(newPosition).getChildren().add(checkPoint);
        } else {
            ImageView orientation = (ImageView) fieldMap.get(newPosition).getChildren().get(fieldMap.get(newPosition).getChildren().size() - 1);
            fieldMap.get(newPosition).getChildren().remove(fieldMap.get(newPosition).getChildren().size() - 1);
            ImageView robot = (ImageView) fieldMap.get(newPosition).getChildren().get(fieldMap.get(newPosition).getChildren().size() - 1);
            fieldMap.get(newPosition).getChildren().remove(fieldMap.get(newPosition).getChildren().size() - 1);
            fieldMap.get(newPosition).getChildren().add(checkPoint);
            fieldMap.get(newPosition).getChildren().add(robot);
            fieldMap.get(newPosition).getChildren().add(orientation);
        }
        clientGameModel.setCheckpointPositionByID(checkpointID, newPosition);

    }

/**
*Activates the lasers along their path
*Some poor robots gets setCheckpointPositionByID
*/
    public void activateLasers() {
//        ImageView laserBeam = new ImageView();
        for (Map.Entry<Point2D, Laser> entry : clientGameModel.getLaserMap().entrySet()) {
            for (Point2D beamPosition : getLaserPath(clientGameModel.getLaserMap().get(entry.getKey()), entry.getKey())) {
                int x = (int) beamPosition.getX();
                int y = (int) beamPosition.getY();
                //TODO: dopple tripple laser implementierne
                //if (entry.getValue ().getType ().equals ( "" ))
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/OneLaserBeamAnimated.gif")));
//                    laserBeam = loadImage("OneLaserBeam", String.valueOf(entry.getValue().getOrientations()));
                ImageView laserBeam1 = new ImageView();
                laserBeam1.setImage(image);
                laserBeam1.setFitWidth(50);
                laserBeam1.setFitHeight(50);
                if (entry.getValue().getOrientations().get(0).equals("bottom")) {
                    laserBeam1.setRotate(90);

                } else if (entry.getValue().getOrientations().get(0).equals("top")) {
                    laserBeam1.setRotate(-90);
                } else {
                    laserBeam1.setRotate(0);
                }

                Point2D newPosition = new Point2D(x, y);

                Platform.runLater(() -> {
                    fieldMap.get(newPosition).getChildren().add(laserBeam1);
                });

               /* FadeTransition fT = new FadeTransition (  );
                fT.setCycleCount ( Animation.INDEFINITE );
                fT.setNode ( laserBeam1 );
                fT.setDuration ( Duration.INDEFINITE );
                fT.setFromValue ( 0.0);
                fT.setToValue ( 1.0 );
                fT.setAutoReverse (true);
                fT.play ();*/

            }
        }
    }
/**
Animation of the energy spaces 
*/

    public void animateEnergySpaces() {
        for (Map.Entry<Point2D, EnergySpace> entry : clientGameModel.getEnergySpaceMap().entrySet()) {
            int x = (int) entry.getKey().getX();
            int y = (int) entry.getKey().getY();

            if (!getRobotsOnFields(new Point2D(x, y)).isEmpty()) {
                ImageView energySpace = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size() - 2);
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mapElements/Elements/GreenEnergySpace.png")));
                try {
                    energySpace = loadImage("GreenEnergySpace", String.valueOf(entry.getValue().getOrientations()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                energySpace.setImage(image);
                energySpace.setFitWidth(50);
                energySpace.setFitHeight(50);
                Point2D energySpacePos = new Point2D(entry.getKey().getX(), entry.getKey().getY());
                fieldMap.get(energySpacePos).getChildren().add(energySpace);
                FadeTransition fT = new FadeTransition();
                fT.setCycleCount(Animation.INDEFINITE);
                fT.setNode(energySpace);
                fT.setDuration(Duration.INDEFINITE);
                fT.setFromValue(0.0);
                fT.setToValue(1.0);
                fT.setAutoReverse(true);
                fT.play();
                break;
            }
        }

    }
/**
*Animation of the push panels according to their register numbers and orientation
*/
    public void PushPanelsAnimation() {
        double ToX = 0;
        double ToY = 0;
        for (Map.Entry<Point2D, PushPanel> entry : clientGameModel.getPushPanelMap().entrySet()) {
            ImageView pushPanel = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size() - 2);
            PushPanel panel = entry.getValue();
            if (panel.getOrientations().equals("left") || panel.getOrientations().get(0).equals("left")) {
                ToX = -1;
                ToY = 0;
            } else if (panel.getOrientations().equals("right") || panel.getOrientations().get(0).equals("right")) {
                ToX = 1;
                ToY = 0;
            } else if (panel.getOrientations().equals("top") || panel.getOrientations().get(0).equals("top")) {
                ToY = -1;
                ToX = 0;
            } else if (panel.getOrientations().get(0).equals("bottom") || panel.getOrientations().equals("bottom")) {

                ToY = 1;
                ToX = 0;
            } else {
                ToX = 1;
                ToY = 0;
            }
            TranslateTransition tt = new TranslateTransition();
            tt.setNode(pushPanel);
            pushPanel.setScaleX(0.90);
            pushPanel.setScaleY(0.90);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setDuration(Duration.INDEFINITE);
            tt.setToX(ToX);
            tt.setToY(ToY);
            tt.play();
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
        if (evt.getPropertyName().equals("EnergySpaces")) {
            clientModel.getClientGameModel().setAnimateEnergySpaces(false);
            Platform.runLater(() -> {
                animateEnergySpaces();
            });
        }
        if (evt.getPropertyName().equals("oldQueueCPMove")) {
            clientModel.getClientGameModel().setQueueCPMove(false);
            Platform.runLater(() -> {
                for (int i = 0; i < clientGameModel.getMoveCPQueue().size(); i++) {
                    ClientGameModel.MoveCPTask newMoveCPTask = clientGameModel.getMoveCPQueue().get(i);
                    int checkpointID = newMoveCPTask.getnumCP();
                    Point2D point2D = newMoveCPTask.getNewPosition();
                    moveCheckPoint(checkpointID, point2D);
                    clientGameModel.getMoveCPQueue().remove(i);
                }
            });
        }

    }
}





