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
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    private ArrayList<ArrayList<ArrayList<Element>>> map;

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
        System.out.println(clientModel.getPlayersFigureMap());
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

    //die MEthode mit Eleemtn als Parametre schicekn
    public ImageView loadImage(String element, String orientations) throws FileNotFoundException {
        FileInputStream path = null;
        Image image;
        path = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("images/mapElements/Elements/" + element + ".png")).getFile()));
        image = new Image(path);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
            switch (orientations){
                case "top", "bottom,top,left"->{imageView.setRotate(0);}
                case "right", "right,left", "left,right,bottom" ->{imageView.setRotate(90);}
                case "left", "right,left,top"->{imageView.setRotate(-90);}
                case "bottom","top,bottom,left" ->{imageView.setRotate(180);}
                case "left,top,right" -> {imageView.setScaleX(-1);
                                            imageView.setRotate(90);}
                case "bottom,left,top" -> {imageView.setScaleX(-1);
                                            imageView.setRotate(0);}
                case "top,right,bottom" ->{imageView.setScaleX(-1);
                                            imageView.setRotate(180); }
                case "right,bottom,left"->{imageView.setScaleX(-1);
                                            imageView.setRotate(-90); }
                case "null" ->{ imageView.setImage(image);}
            }
            return imageView;


    }


    public void clickGrid(MouseEvent event) {
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

            // }
        }
    }

    //sep21.dbs.ifi.lmu.de
    private void createMapObjects(ArrayList<ArrayList<ArrayList<Element>>> map, int mapX, int mapY) throws IOException {

        for (int y = 0; y < mapX; y++) {
            for (int x = 0; x < mapY; x++) {
                mapGrid.add(loadImage("normal1","null"), x, y);
                for (int i = 0; i < map.get(y).get(x).size(); i++) {
                    switch (map.get(y).get(x).get(i).getType()) {
                        case "Antenna" -> {
                            Element element = map.get(y).get(x).get(i);
                            Antenna antenna = new Antenna(element.getType(), element.getIsOnBoard(), element.getOrientations());
//                            replaceElementInMap(map, x, y, element, antenna);
////                            antennaMap.put(new Point2D(x, y), antenna);
                            mapGrid.add(loadImage("priority-antenna", String.join(",", antenna.getOrientations())), x, y);
                            System.out.println(String.join(",", antenna.getOrientations()));

                        }


                        case "CheckPoint" -> {
                            Element element = map.get(y).get(x).get(i);
                            CheckPoint checkPoint = new CheckPoint(element.getType(), element.getIsOnBoard(), element.getCount());
//                            replaceElementInMap(map, x, y, element, checkPoint);
//                            checkPointMap.put(new Point2D(x, y), checkPoint);
                            mapGrid.add(loadImage("victory-counter", "null"), x, y);
                        }
                        case "ConveyorBelt" -> {
                            Element element = map.get(y).get(x).get(i);
                            ConveyorBelt conveyorBelt = new ConveyorBelt(element.getType(), element.getIsOnBoard(),
                                    element.getSpeed(), element.getOrientations());
//                            replaceElementInMap(map, x, y, element, conveyorBelt);
//                            conveyorBeltMap.put(new Point2D(x, y), conveyorBelt);


                            if (conveyorBelt.getOrientations().size() == 1 || conveyorBelt.getOrientations().size() == 2) {
                                if (conveyorBelt.getSpeed() == 2) {
                                    mapGrid.add(loadImage("BlueBelt", String.join(",", conveyorBelt.getOrientations())), x, y);
                                }
                                if (conveyorBelt.getSpeed() == 1) {
                                    mapGrid.add(loadImage("GreenBelt",String.join(",", conveyorBelt.getOrientations())), x, y);
                                }
                            }

                            if (conveyorBelt.getOrientations().size() == 3) {
                                if (conveyorBelt.getSpeed() == 2) {
                                    //String rotation =conveyorBelt.getOrientations().get(2);
                                    //String mirror_rot = conveyorBelt.getOrientations().get(3);
                                    mapGrid.add(loadImage("RotatingBeltBlue2", String.join(",", conveyorBelt.getOrientations())), x, y);
                                }else{
                                    mapGrid.add(loadImage("GreenBelt", String.join(",", conveyorBelt.getOrientations())), x, y);

                                }

                            }

                        }

                        case "EnergySpace" -> {
                            Element element = map.get(y).get(x).get(i);
                            EnergySpace energySpace = new EnergySpace(element.getType(), element.getIsOnBoard(), element.getCount());
//                            replaceElementInMap(map, x, y, element, energySpace);
//                            energySpaceMap.put(new Point2D(x, y), energySpace);
                            mapGrid.add(loadImage("GreenEnergySpace","null"), x, y);
                        }

                        case "Gear" -> {
                            Element element = map.get(y).get(x).get(i);
                            Gear gear = new Gear(element.getType(), element.getIsOnBoard(), element.getOrientations());
//                            replaceElementInMap(map, x, y, element, gear);
//                            gearMap.put(new Point2D(x, y), gear);

                            mapGrid.add(loadImage("RedGear",String.join(",", gear.getOrientations())), x, y);
                        }
                        case "Laser" -> {
                            Element element = map.get(y).get(x).get(i);
                            Laser laser = new Laser(element.getType(), element.getIsOnBoard(),
                                    element.getOrientations(), element.getCount());
//                            replaceElementInMap(map, x, y, element, laser);
//                            laserMap.put(new Point2D(x, y), laser);
                            mapGrid.add(loadImage("OneLaser",String.join(",", laser.getOrientations())), x, y);


                        }
                        case "Pit" -> {
                            Element element = map.get(y).get(x).get(i);
                            Pit pit = new Pit(element.getType(), element.getIsOnBoard());
//                            replaceElementInMap(map, x, y, element, pit);
//                            pitMap.put(new Point2D(x, y), pit);

                            mapGrid.add(loadImage("Pit", "null"), x, y);
                        }

                        case "PushPanel" -> {
                            Element element = map.get(y).get(x).get(i);
                            PushPanel pushPanel = new PushPanel(element.getType(), element.getIsOnBoard(), element.getOrientations(),
                                    element.getRegisters());
//                            replaceElementInMap(map, x, y, element, pushPanel);
//                            pushPanelMap.put(new Point2D(x, y), pushPanel);
                            mapGrid.add(loadImage("PushPanel24", String.join(",", pushPanel.getOrientations())), x, y);
                        }

                        case "RestartPoint" -> {
                            Element element = map.get(y).get(x).get(i);
                            RestartPoint restartPoint = new RestartPoint(element.getType(), element.getIsOnBoard(), element.getOrientations());
//                            replaceElementInMap(map, x, y, element, restartPoint);
//                            restartPointMap.put(new Point2D(x, y), restartPoint);
                            mapGrid.add(loadImage("reboot", String.join(",", restartPoint.getOrientations())), x, y);
                        }


                        case "StartPoint" -> {
                            Element element = map.get(y).get(x).get(i);
                            StartPoint startPoint = new StartPoint(element.getType(), element.getIsOnBoard());
//                            replaceElementInMap(map, x, y, element, startPoint);
//                            startPointMap.put(new Point2D(x, y), startPoint);
                            mapGrid.add(loadImage("StartingPoint","null"), x, y);
                        }


                        case "Empty" -> {
                            Element element = map.get(y).get(x).get(i);
                            Empty empty = new Empty(element.getType(), element.getIsOnBoard());
//                            replaceElementInMap(map, x, y, element, empty);
//                            emptyMap.put(new Point2D(x, y), empty);
                            mapGrid.add(loadImage("normal1","null"), x, y);
                        }

                        //TODO: dopple oder one wall
                        case "Wall" -> {
                            Element element = map.get(y).get(x).get(i);
                            Wall wall = new Wall(element.getType(), element.getIsOnBoard(), element.getOrientations());
//                            replaceElementInMap(map, x, y, element, wall);
//                            wallMap.put(new Point2D(x, y), wall);
                            mapGrid.add(loadImage("Wall", String.join(",", wall.getOrientations())), x, y);
                            System.out.println(String.join(",", wall.getOrientations()));
                        }
                        default -> { //place for exception handling
                        }
                    }
                }
            }
        }
    }



    private String toString(ArrayList<String> orientations) {
        String liste= "" ;
        for (String s:orientations) {
            //liste += s + " \t";
            String.join(", ", orientations);
           // System.out.println(liste);

        }
        System.out.println(liste);

/*
       for (int i = 0; i< orientations.size();i++ ) {
            liste += orientations.get(i) + " \t";
           // liste = String.join(", ", orientations);
            //liste = orientations.get(i) ;
            //liste += String.join(", ", orientations);
            System.out.println(liste +" ///");
            //System.out.println(String.join(", ", orientations.toString()));
        }*/
        return liste;
    }


//    public void replaceElementInMap (ArrayList<ArrayList<ArrayList<Element>>> map, int x, int y, Element element, Object object) {
//        if (object instanceof Element) {
//            int indexelement = map.get(x).get(y).indexOf(element);
//            map.get(x).get(y).remove(element);
//            map.get(x).get(y).add(indexelement, (Element) object);
//        } else {
//            throw new ClassCastException(object + " is not an Element!" +
//                    "Can't cast this method on Objects other than Elements!");
//
//        }
//    }
    //TODO:if wall and leser gleichzeitg auf the same feld und dann kommt roboter
    /*public void isValidReplacement(){
        if ()
    }*/



}

