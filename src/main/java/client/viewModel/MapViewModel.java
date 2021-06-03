package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import game.Element;
import game.boardelements.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MapViewModel implements Initializable {

    @FXML
    public GridPane mapGrid;
    private ClientModel clientModel = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();


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

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        int mapX = clientGameModel.getMap().size();
        int mapY = clientGameModel.getMap().get(0).size();
        try {
            createMapObjects(clientGameModel.getMap(), mapX, mapY);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        clientGameModel.canSetStartingPointProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (clientGameModel.canSetStartingPointProperty().getValue() == true) {
                    Platform.runLater(() -> {
                                setRobot(clientGameModel.getActualPlayerID(), clientGameModel.getX(), clientGameModel.getY());

                            }
                    );
                    clientGameModel.canSetStartingPointProperty().setValue(false);
                }
            }
        });

    }


    public void setRobot (int playerID, int x, int y) {
        int figure = clientModel.getPlayersFigureMap().get(playerID);
        //TODO: image hängt von figur ab
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
        mapGrid.add(imageView, x, y);

    }

    public File findPath (String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    public void clickGrid (MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != mapGrid) {
            Integer colIndex = GridPane.getColumnIndex(clickedNode);
            Integer rowIndex = GridPane.getRowIndex(clickedNode);
            System.out.println(colIndex + "  " + rowIndex);
            //hier sollte er alle Map Elemente durch gehenn und diese 2 Point2D wo sine sind und dann  testen
            //Plan B wir nehemen diese Position und schauen was für ein Element drauf ist, wenn es ein Szartpotn ist
            //dann ja darf man selecten

            clientModel.getClientGameModel().sendStartingPoint(colIndex, rowIndex);
//
//            Point2D positionID = new Point2D(colIndex, rowIndex);
//            System.out.println(positionID);
//           /* for (Point2D startPointPosition :startPointMap.keySet()) {
//                System.out.println(startPointMap.keySet());*/
//            if (startPointMap.containsKey(positionID)) {
//                System.out.println("hier ist einen startpoint " + positionID);
//                clientGameModel.setCanSetStartingPoint(true);
//            } else {
//                System.out.println("hier NOT startpoint ");
//                clientGameModel.setCanSetStartingPoint(false);
//
//            }
                /* if (startPointPosition.equals(positionID)){
                    //clientGameModel.send()
                    System.out.println("hier ist einen startpoint "+positionID);
                }*/
                    /*Alert a = new Alert(Alert.AlertType.NONE);
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("this is not a starting point");
                    a.show();
                }*/

            // }
        }

    }


    public ImageView loadImage (String element) throws FileNotFoundException {
        FileInputStream path = null;
        File input = null;
        ClassLoader classLoader = getClass().getClassLoader();
        path = new FileInputStream((Objects.requireNonNull(classLoader.getResource("images/mapElements/Elements/" + element + ".png")).getFile()));
        // input = new File(Objects.requireNonNull(classLoader.getResource("images/mapElements/Elements/" + element + ".png")).getFile());
        // path = new FileInputStream(input);
        Image image = new Image(path);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        return imageView;
    }


    //sep21.dbs.ifi.lmu.de
    private void createMapObjects (ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) throws IOException {

        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                for (int i = 0; i < map.get(x).get(y).size(); i++) {
                    switch (map.get(x).get(y).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(x).get(y).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            if (antenna.getOrientations().equals("right")) {
                                replaceElementInMap(map, x, y, element, antenna);
                                antennaMap.put(new Point2D(x, y), antenna);
                                mapGrid.add(loadImage("priority-antenna"), y, x);
                            }
                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(x).get(y).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());
                            //if (conveyorBelt.getOrientations().equals("RECHTS")) {
                            replaceElementInMap(map, x, y, element, conveyorBelt);
                            conveyorBeltMap.put(new Point2D(x, y), conveyorBelt);
                            mapGrid.add(loadImage("BlueBelt"), y, x);
                        }

                       /* case "CheckPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, checkPoint);
                            checkPointMap.put(new Point2D(x, y), checkPoint);
                            mapGrid.add(loadImage(""), x, y);
                        }*/
                        case "Empty" -> {
                            Element element = map.get(x).get(y).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, empty);
                            emptyMap.put(new Point2D(x, y), empty);
                            mapGrid.add(loadImage("normal1"), y, x);
                        }
                        //TODO: ORIENTATION
                        case "EnergySpace" -> {
                            Element element = map.get(x).get(y).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, energySpace);
                            energySpaceMap.put(new Point2D(x, y), energySpace);

                            mapGrid.add(loadImage("RedEnergySpace"), y, x);
                        }
                      /*  case "Gear" -> {
                            Element element = map.get(x).get(y).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, gear);
                            gearMap.put(new Point2D(x, y), gear);

                            mapGrid.add(loadImage("RedGear"), x, y);
                        }*/
                        case "Laser" -> {
                            Element element = map.get(x).get(y).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            replaceElementInMap(map, x, y, element, laser);
                            laserMap.put(new Point2D(x, y), laser);

                            mapGrid.add(loadImage("OneLaser"), y, x);
                        }
                        case "Pit" -> {
                            Element element = map.get(x).get(y).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, pit);
                            pitMap.put(new Point2D(x, y), pit);

                            mapGrid.add(loadImage("Pit"), y, x);
                        }
                       /* case "PushPanel" -> {
                            Element element = map.get(x).get(y).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
                            replaceElementInMap(map, x, y, element, pushPanel);
                            pushPanelMap.put(new Point2D(x, y), pushPanel);
                            mapGrid.add(loadImage("PushPanel24"), x, y);
                        }
                        case "RestartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, restartPoint);
                            restartPointMap.put(new Point2D(x, y), restartPoint);
                            mapGrid.add(loadImage("reboot"), x, y);
                        }*/
                        case "StartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, startPoint);
                            startPointMap.put(new Point2D(x, y), startPoint);
                            mapGrid.add(loadImage("StartingPoint"), y, x);
                        }
                        case "Wall" -> {
                            Element element = map.get(x).get(y).get(i);
                            Wall wall = new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            if (wall.getOrientations().equals("top")) {
                                replaceElementInMap(map, x, y, element, wall);
                                wallMap.put(new Point2D(x, y), wall);
                                mapGrid.add(loadImage("Wall"), y, x);
                            }
                            if (wall.getOrientations().equals("bottom")) {
                                replaceElementInMap(map, x, y, element, wall);
                                wallMap.put(new Point2D(x, y), wall);
                                mapGrid.add(loadImage("Wall"), y, x);
                            }
                        }
                        default -> { //place for exception handling
                        }
                    }
                }
            }
        }
    }


    public void replaceElementInMap (ArrayList<ArrayList<ArrayList<Element>>> map, int x, int y, Element element, Object object) {
        if (object instanceof Element) {
            int indexelement = map.get(x).get(y).indexOf(element);
            map.get(x).get(y).remove(element);
            map.get(x).get(y).add(indexelement, (Element) object);
        } else {
            throw new ClassCastException(object + " is not an Element!" +
                    "Can't cast this method on Objects other than Elements!");

        }
    }
    //TODO:if wall and leser gleichzeitg auf the same feld und dann kommt roboter
    /*public void isValidReplacement(){
        if ()
    }*/


}

