package client.viewModel;

import client.model.ClientModel;
import game.Element;
import game.*;
import game.Robot;
import game.boardelements.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import json.JSONDeserializer;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.GameStartedBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MapViewModel implements Initializable {

    @FXML
    public GridPane mapGrid;
    private ClientModel clientModel = ClientModel.getInstance();

    public int mapX;
    public int mapY;

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


    public void selectMap (String mapName) throws IOException {
        //TODO maybe try block instead of throws IOException
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Maps/DizzyHighway.json")).getFile());
        String content = new String(Files.readAllBytes(file.toPath()));
        JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(content);
        GameStartedBody gameStartedBody = (GameStartedBody) jsonMessage.getMessageBody();
        this.map = gameStartedBody.getGameMap();
        int mapX = map.size();
        int mapY = map.get(0).size();
        createMapObjects(map, mapX, mapY);
    }

    public File findPath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }


    //sep21.dbs.ifi.lmu.de
    private void createMapObjects (ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) throws IOException {
        FileInputStream input;
        Image image;
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                for (int i = 0; i < map.get(x).get(y).size(); i++) {
                    switch (map.get(x).get(y).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(x).get(y).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, antenna);
                            antennaMap.put(new Point2D(x, y), antenna);
                            input = new FileInputStream(findPath("images/mapElements/priority-antenna.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(x).get(y).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, conveyorBelt);
                            conveyorBeltMap.put(new Point2D(x, y), conveyorBelt);
                            input = new FileInputStream(findPath("images/mapElements/bluecvb-straight-down.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "CheckPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, checkPoint);
                            checkPointMap.put(new Point2D(x, y), checkPoint);
                            input = new FileInputStream(findPath("images/mapElements/robot1.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "Empty" -> {
                            Element element = map.get(x).get(y).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, empty);
                            emptyMap.put(new Point2D(x, y), empty);
                            input = new FileInputStream(findPath("images/mapElements/robot1.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "EnergySpace" -> {
                            Element element = map.get(x).get(y).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
                            replaceElementInMap(map, x, y, element, energySpace);
                            energySpaceMap.put(new Point2D(x, y), energySpace);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "Gear" -> {
                            Element element = map.get(x).get(y).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, gear);
                            gearMap.put(new Point2D(x, y), gear);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "Laser" -> {
                            Element element = map.get(x).get(y).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
                            replaceElementInMap(map, x, y, element, laser);
                            laserMap.put(new Point2D(x, y), laser);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "Pit" -> {
                            Element element = map.get(x).get(y).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, pit);
                            pitMap.put(new Point2D(x, y), pit);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "PushPanel" -> {
                            Element element = map.get(x).get(y).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
                            replaceElementInMap(map, x, y, element, pushPanel);
                            pushPanelMap.put(new Point2D(x, y), pushPanel);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "RestartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, restartPoint);
                            restartPointMap.put(new Point2D(x, y), restartPoint);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "StartPoint" -> {
                            Element element = map.get(x).get(y).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
                            replaceElementInMap(map, x, y, element, startPoint);
                            startPointMap.put(new Point2D(x, y), startPoint);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
                        }
                        case "Wall" -> {
                            Element element = map.get(x).get(y).get(i);
                            Wall wall = new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations());
                            replaceElementInMap(map, x, y, element, wall);
                            wallMap.put(new Point2D(x, y), wall);
                            input = new FileInputStream(findPath("images/mapElements/energyspace-left-right.png"));
                            image = new Image(input);
                            ImageView imageView = new ImageView();
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            mapGrid.add(imageView, x, y);
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

    private ArrayList<ArrayList<ArrayList<Element>>> map;

    //TODO: eine Methode für die Files erstellen mit statt resources/images/robot1.png -> resources/images/"+element.getType+".png"
    public void fillGridPaneWithMap (GameStartedBody gameStartedBody) throws FileNotFoundException {
        map = gameStartedBody.getGameMap();
        mapX = map.size();
        mapY = map.get(0).size();

        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                for (Element element : map.get(x).get(y)) {

                    switch (element.getType()) {
                        case "Antenna" -> {
                            FileInputStream input = new FileInputStream("resources/images/robot1.png");
                            Image image = new Image(input);
                            mapGrid.add(new ImageView(image), y, x);


                           /* Point2D antenna = new Point2D(x,y);
                            FileInputStream input = new FileInputStream("resources/images/robot1.png");
                            Image image = new Image(input);
                            ImageView imageView = new ImageView(image);*/

                     /*   }
                        case "ConveyorBelt" -> {
                            String position = x + "/" + y;
                            conveyorBeltMap.put(position, new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations()));
                        }
                        case "CheckPoint" -> {
                            String position = x + "/" + y;
                            checkPointMap.put(position, new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount()));
                        }
                        case "Empty" -> {
                            String position = x + "/" + y;
                            emptyMap.put(position, new Empty(element.getType(), element.getIsOnBoard()));
                        }
                        case "EnergySpace" -> {
                            String position = x + "/" + y;
                            energySpaceMap.put(position, new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount()));
                        }
                        case "Gear" -> {
                            String position = x + "/" + y;
                            gearMap.put(position, new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        case "Laser" -> {
                            String position = x + "/" + y;
                            laserMap.put(position, new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount()));
                        }
                        case "Pit" -> {
                            String position = x + "/" + y;
                            pitMap.put(position, new Pit(element.getType(), element.getIsOnBoard()));
                        }
                        case "PushPanel" -> {
                            String position = x + "/" + y;
                            pushPanelMap.put(position, new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters()));
                        }
                        case "RestartPoint" -> {
                            String position = x + "/" + y;
                            restartPointMap.put(position, new RestartPoint(element.getType(), element.getIsOnBoard()));
                        }
                        case "StartPoint" -> {
                            String position = x + "/" + y;
                            startPointMap.put(position, new StartPoint(element.getType(), element.getIsOnBoard()));
                        }
                        case "Wall" -> {
                            String position = x + "/" + y;
                            wallMap.put(position, new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations()));
                        }
                        default -> {
                        }
                    }*/

                        }
                    }
                }
            }


        }
    }

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        try {
            selectMap(clientModel.getSelectedMap());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
