package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import game.Element;
import game.Game;
import game.Player;
import game.Robot;
import game.boardelements.*;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MapViewModel implements Initializable {

    private ClientModel clientModel = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();

    @FXML
    public GridPane mapGrid;

    private Map<Point2D, Group> fieldMap = new HashMap<Point2D, Group>();

    private Map<Point2D, Antenna> antennaMap = new HashMap<>();
    private Map<Point2D, CheckPoint> checkPointMap = new HashMap<>();
    private Map<Point2D, ConveyorBelt> conveyorBeltMap = new HashMap<>();
    private Map<Point2D, Empty> emptyMap = new HashMap<>();
    private Map<Point2D, EnergySpace> energySpaceMap = new HashMap<>();
    private Map<Point2D, Gear> gearMap = new HashMap<>();
    private Map<Point2D, Laser> laserMap = new HashMap<>();
    private Map<Point2D, Pit> pitMap = new HashMap<>();
    private Map<Point2D, PushPanel> pushPanelMap = new HashMap<>();
    private Map<Point2D, RestartPoint> restartPointMap = new HashMap<>();
    private Map<Point2D, StartPoint> startPointMap = new HashMap<>();
    private Map<Point2D, Wall> wallMap = new HashMap<>();
    private ArrayList<ArrayList<ArrayList<Element>>> map;
    int oldX;
    int oldY;


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

        int mapX = clientGameModel.getMap().size();
        int mapY = clientGameModel.getMap().get(0).size();
        try {
            createMapObjects(clientGameModel.getMap(), mapX, mapY);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        clientGameModel.getMoveQueueObservable().addListener(new MapChangeListener<Robot, Point2D>() {

            @Override
            public void onChanged (Change<? extends Robot, ? extends Point2D> change) {
                Platform.runLater(() -> {
                    for (Map.Entry<Robot, Point2D> entry : clientGameModel.getMoveQueue().entrySet()) {
                        int playerID = clientModel.getIDfromRobotName(entry.getKey().getName());
                        moveRobot(playerID, (int) entry.getValue().getX(), (int) entry.getValue().getY());
                        clientModel.getClientGameModel().getRobotMap().replace(entry.getKey(), entry.getValue());
                        clientModel.getClientGameModel().getMoveQueue().remove(entry.getKey());
                    }
                });
            }
        });


        clientGameModel.getStartingPointQueueObservable().addListener(new MapChangeListener<Robot, Point2D>() {
            @Override
            public void onChanged (Change<? extends Robot, ? extends Point2D> change) {
                Platform.runLater(() -> {
                            for (Map.Entry<Robot, Point2D> entry : clientGameModel.getStartingPointQueue().entrySet()) {
                                int playerID = clientModel.getIDfromRobotName(entry.getKey().getName());
                                setRobot(playerID, (int) entry.getValue().getX(), (int) entry.getValue().getY());
                                clientModel.getClientGameModel().getRobotMap().put(entry.getKey(), entry.getValue());
                                clientModel.getClientGameModel().getStartingPointQueue().remove(entry.getKey());
                            }
                        }
                );
                //setRobot(clientGameModel.getActualPlayerID(), clientGameModel.getX(), clientGameModel.getY());

            }
        });

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
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        fieldMap.get(new Point2D(x, y)).getChildren().add(imageView);

        try {
            input = new FileInputStream(findPath("images/TransparentElements/RobotDirection.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image image2 = new Image(input);
        imageView = new ImageView();
        imageView.setImage(image2);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setRotate(-90);
        fieldMap.get(new Point2D(x, y)).getChildren().add(imageView);
    }


    public void refreshOrientation () {
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream(findPath("images/TransparentElements/RobotDirection.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        mapGrid.add(imageView, clientGameModel.getPlayer().getRobot().getxPosition(), clientGameModel.getPlayer().getRobot().getxPosition());
    }

    public File findPath (String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }


    public ImageView loadImage (String element, String orientations) throws FileNotFoundException {
        FileInputStream path = null;
        Image image;
        path = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("images/mapElements/Elements/" + element + ".png")).getFile()));
        image = new Image(path);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        switch (orientations) {
            case "top", "bottom,top,left" -> {
                imageView.setRotate(0);
            }
            case "right", "right,left", "left,right,bottom" -> {
                imageView.setRotate(90);
            }
            case "left", "right,left,top" -> {
                imageView.setRotate(-90);
            }
            case "bottom", "top,bottom,left" -> {
                imageView.setRotate(180);
            }
            case "left,top,right" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(90);
            }
            case "bottom,left,top" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(0);
            }
            case "top,right,bottom" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(180);
            }
            case "right,bottom,left" -> {
                imageView.setScaleX(-1);
                imageView.setRotate(-90);
            }
            case "null" -> {
                imageView.setImage(image);
            }
        }
        return imageView;


    }

    private String handleLaser () {
        String laserT = "";
        for (Point2D loc : laserMap.keySet()) {
            if (wallMap.containsKey(loc)) {
                laserT = "OneLaser";
            } else {
                laserT = "OneLaserBeam";
            }
        }
        return laserT;
    }
  /*  private String handleBelts() {

    }*/


    /*public File findPath(String element) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource("images/mapElements/" + element + ".jpg")).getFile());
    }*/


    public void clickGrid (MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != mapGrid) {
            Integer colIndex = GridPane.getColumnIndex(clickedNode.getParent());
            Integer rowIndex = GridPane.getRowIndex(clickedNode.getParent());
            System.out.println(colIndex + "  " + rowIndex);

            clientModel.getClientGameModel().sendStartingPoint(colIndex, rowIndex);
        }
    }


//
//            Point2D positionID = new Point2D(colIndex, rowIndex);

    public void moveRobot (int playerID, int x, int y) {
        Robot robot = null;
        for (HashMap.Entry<Robot, Point2D> entry : clientGameModel.getRobotMap().entrySet()) {
            if (entry.getKey().getName().equals(Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(playerID)))) {
                robot = entry.getKey();
                break;
            }
        }
        Point2D oldPosition = clientGameModel.getRobotMap().get(robot);
        Point2D newPosition = new Point2D(x, y);
        Group imageGroup = fieldMap.get(oldPosition);
        ImageView robotOrientation = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);
        ImageView robotV = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 2);
        fieldMap.get(oldPosition).getChildren().remove(fieldMap.get(oldPosition).getChildren().size() - 1);
        fieldMap.get(oldPosition).getChildren().remove(fieldMap.get(oldPosition).getChildren().size() - 1);
        fieldMap.get(newPosition).getChildren().add(robotV);
        fieldMap.get(newPosition).getChildren().add(robotOrientation);

        clientGameModel.getRobotMap().replace(robot, newPosition);

    }


    private void createMapObjects (ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) throws IOException {

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
                            ImageView imageView2 = loadImage("priority-antenna", String.join(",", antenna.getOrientations()));
                            imageGroup.getChildren().add(imageView2);
                        }

                        case "CheckPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
                            ImageView imageView2 = loadImage("victory-counter", "null");
                            imageGroup.getChildren().add(imageView2);

                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(x).get(y).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());


                            if (conveyorBelt.getOrientations().size() == 1 || conveyorBelt.getOrientations().size() == 2) {
                                if (conveyorBelt.getSpeed() == 2) {
                                    ImageView imageView2 = loadImage("BlueBelt", String.join(",", conveyorBelt.getOrientations()));
                                    imageGroup.getChildren().add(imageView2);
                                }
                                if (conveyorBelt.getSpeed() == 1) {
                                    ImageView imageView2 = loadImage("GreenBelt", String.join(",", conveyorBelt.getOrientations()));
                                    imageGroup.getChildren().add(imageView2);
                                }
                            }

                            if (conveyorBelt.getOrientations().size() == 3) {
                                if (conveyorBelt.getSpeed() == 2) {
                                    ImageView imageView2 = loadImage("RotatingBeltBlue2", String.join(",", conveyorBelt.getOrientations()));
                                    imageGroup.getChildren().add(imageView2);
                                } else {
                                    ImageView imageView2 = loadImage("GreenBelt", String.join(",", conveyorBelt.getOrientations()));
                                    imageGroup.getChildren().add(imageView2);

                                }

                            }

                        }

                        case "EnergySpace" -> {
                            Element element = map.get(x).get(y).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            ImageView imageView2 = loadImage("RedEnergySpace", "null");
                            imageGroup.getChildren().add(imageView2);
                        }

                        case "Gear" -> {
                            Element element = map.get(x).get(y).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            ImageView imageView2 = loadImage("RedGear", String.join(",", gear.getOrientations()));
                            imageGroup.getChildren().add(imageView2);
                        }
                        //TODO:laser 1 or two handeln und dann orientation
                        case "Laser" -> {
                            Element element = map.get(x).get(y).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            ImageView imageView2 = loadImage("OneLaser", String.join(",", laser.getOrientations()));
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
                            ImageView imageView2 = loadImage("PushPanel24", String.join(",", pushPanel.getOrientations()));
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
}

