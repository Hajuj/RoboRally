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
import java.util.stream.Collectors;

public class MapViewModel implements Initializable, PropertyChangeListener {

    private ClientModel clientModel = ClientModel.getInstance ( );
    private ClientGameModel clientGameModel = ClientGameModel.getInstance ( );

    @FXML
    public GridPane mapGrid;

    private Map<Point2D, Group> fieldMap = new HashMap<Point2D, Group> ( );
    private Element Wall;
    private int wallCount = 0 ;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientModel.addPropertyChangeListener ( this );
        clientGameModel.addPropertyChangeListener ( this );
        //handleAnimation();
        int mapX = clientGameModel.getMap ( ).size ( );
        int mapY = clientGameModel.getMap ( ).get ( 0 ).size ( );
        try {
            createMapObjects ( clientGameModel.getMap ( ), mapX, mapY );
        } catch (IOException ioException) {
            ioException.printStackTrace ( );
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
    public void setRobot(int playerID, int x, int y) {
        int figure = clientModel.getPlayersFigureMap ( ).get ( playerID );
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream ( findPath ( "Robots/robot" + figure + ".png" ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        }
        image = new Image ( input );
        ImageView imageView = new ImageView ( );
        imageView.setImage ( image );
        imageView.setFitWidth ( 46 );
        imageView.setFitHeight ( 46 );

        fieldMap.get ( new Point2D ( x, y ) ).getChildren ( ).add ( imageView );

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
        imageView.setRotate(-90);
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
                    handleAnimation ( );
                    try {
                        implementLaserBeam ( );

                    } catch (FileNotFoundException e) {
                        e.printStackTrace ( );
                    }
                }
            });
        }

        if (evt.getPropertyName().equals("queueMove")) {
            clientModel.getClientGameModel().setQueueMove(false);
                for (Map.Entry<Robot, Point2D> entry : clientGameModel.getMoveQueue().entrySet()) {
                    //nullpointer hier. warum=
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
        if (evt.getPropertyName ( ).equals ( "BlueConveyorBelt" )) {
            clientModel.getClientGameModel ( ).setAnimateBelts ( false );
            Platform.runLater ( () -> {
                handleAnimation ( );
            } );
        }
    }

    public void handleAnimation() {
        double ToX = 0;
        double ToY = 0;

        for (Map.Entry<Point2D, ConveyorBelt> entry : clientGameModel.getConveyorBeltMap ( ).entrySet ( )) {
            ConveyorBelt belt = entry.getValue ( );
            if (belt.getOrientations ( ).equals ( "left" ) || belt.getOrientations ( ).get ( 0 ).equals ( "left" )) {
                ToX = -2;
                ToY = 0;
            }
            else if (belt.getOrientations ( ).equals ( "right" ) || belt.getOrientations ( ).get ( 0 ).equals ( "right" )) {
                ToX = 2;
                ToY = 0;
            }
            else if (belt.getOrientations ( ).equals ( "top" ) || belt.getOrientations ( ).get ( 0 ).equals ( "top" )) {
                ToY = -2;
                ToX = 0;
            }
            else if (belt.getOrientations ( ).equals ( "bottom" ) || belt.getOrientations ( ).get ( 0 ).equals ( "bottom" )) {
                ToY = 2;
                ToX = 0;
            }else{
                ToX=2;
                ToY=0;
            }

            TranslateTransition transition = new TranslateTransition ( );
            transition.setToX ( ToX );
            transition.setToY ( ToY );

            ImageView belts = (ImageView) fieldMap.get ( entry.getKey ( ) ).getChildren ( ).get ( fieldMap.get ( entry.getKey ( ) ).getChildren ( ).size ( ) - 1 );
            belts.setScaleY ( 0.95 );
            belts.setScaleX ( 0.95 );
            transition.setNode ( belts );
            transition.setCycleCount ( Animation.INDEFINITE );
            transition.setDuration ( Duration.INDEFINITE );
            transition.setAutoReverse ( true );
            transition.play ();

        }
    }
 /*   private List<Laser> lasers (){
      //  for (Map.Entry<Point2D, Laser> entry : clientGameModel.getLaserMap().entrySet()) {
        List<Laser> collect = mapGrid.getChildren().stream().map(n -> (Laser) n).collect(Collectors.toList());
        return collect;
       // }
    }
    public void handleLaserAnime() {
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream(findPath("Robots/Elements/OneLaserBeam.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);

        Path laserPath = new Path();

        LineTo lineTo = new LineTo();

        boolean foundWall = false;
        while(foundWall){
            lineTo.setX(GridPane.getColumnIndex(clickedNode.getParent()));
            lineTo.setY(55.0f);
        }
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

    public ArrayList<ImageView>  implementLaserBeam() throws FileNotFoundException {
        FileInputStream input = null;
        Image image;
        try {
            input = new FileInputStream ( findPath ( "Robots/Elements/OneLaserBeam.png" ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        }
        image = new Image ( input );
        ImageView laserBeam = null;
        ArrayList<ImageView> laserBeams = new ArrayList<>();
        TranslateTransition transition = new TranslateTransition ( );

        transition.setCycleCount ( Animation.INDEFINITE );
        transition.setDuration ( Duration.INDEFINITE );
        double X = 0;
        double Y = 0;
        for (Map.Entry<Point2D, Laser> entry : clientGameModel.getLaserMap ( ).entrySet ( )) {
            laserBeam = loadImage ( "OneLaserBeam", String.valueOf ( entry.getValue ( ).getOrientations ( ) ) );
            mapGrid.add ( laserBeam, (int) entry.getKey ( ).getX ( ), (int) entry.getKey ( ).getY ( ) );
            if (entry.getValue ().getOrientations ().equals ( "left" )|| entry.getValue ().getOrientations ().get ( 0 ).equals ( "left" )){
                laserBeam.setRotate ( 90 );
                //transition.setNode ( laserBeam );
                X=-20;
                Y=0;

                //transition.play ();

            }if(entry.getValue ().getOrientations ().equals ( "right" ) ||entry.getValue ().getOrientations ().get ( 0 ).equals ( "right" )){
                laserBeam.setRotate ( 90 );
                //transition.setNode ( laserBeam );
                laserBeam.setNodeOrientation (NodeOrientation.RIGHT_TO_LEFT );
                X=20;
                Y=0;

                //transition.play ();


            }if(entry.getValue ().getOrientations ().equals ( "bottom" )|| entry.getValue ().getOrientations ().get ( 0 ).equals ( "bottom" )){
                laserBeam.setRotate (90);
                //transition.setNode ( laserBeam );
                laserBeam.setNodeOrientation (NodeOrientation.INHERIT);
               Y=20;
               X=0;

                //transition.play ();


            }if(entry.getValue ().getOrientations ().equals ( "top" )|| entry.getValue ().getOrientations ().get ( 0 ).equals ( "top" )) {
                laserBeam.setRotate ( 180 );
                //transition.setNode ( laserBeam );
                laserBeam.setNodeOrientation (NodeOrientation.INHERIT );
               Y=-20;
               X=0;
                //transition.play ();

            }

        }
        laserBeam.setImage ( image );
        laserBeams.add ( laserBeam );
        transition.setNode ( laserBeam );
        transition.setToY ( Y );
        transition.setToX ( X );
        transition.play ();

        Collection<Laser> entrySet =  clientGameModel.getLaserMap ( ).values ();

          /*  if (entrySet..getOrientations ().equals ( "left" )|| entrySet.getValue ().getOrientations ().get ( 0 ).equals ( "left" )) {
                transition.setNode ( node );
                transition.play ( );
            }*/


        return laserBeams;
    }

    public List<Node> getAllBoardElement(){
        return mapGrid.getChildren ().stream ().filter ( Objects::nonNull).collect( Collectors.toList());
    }

    public void handleLaserAnime() throws FileNotFoundException {
        for (ImageView laserBeam : implementLaserBeam ( )) {
            TranslateTransition transition = new TranslateTransition ( );
            transition.setNode ( laserBeam );
            transition.setCycleCount ( Animation.INDEFINITE );
            transition.setDuration ( Duration.INDEFINITE );
            double X = 20;
            double Y = 10;
            double newPosX = laserBeam.getLayoutX ( ) + X;
            for (Node element: getAllBoardElement ()) {
                if(laserBeam.intersects ( element.getBoundsInParent ())){

                }
            }



           // double newPosY = laserBeam.getLayoutY ( ) + Y;

            transition.setToX ( newPosX );
           // transition.setToY ( newPosY );
            transition.play ();
           /* clientGameModel.getWallMap ().forEach ( (point2D, wall) -> point2D.getX () );
            if ( transition.getNode ().getBoundsInParent ().intersects ( fieldMap.get ( Wall ).getBoundsInParent () ) ){
                transition.setToY ( newPosY -Y);
                transition.setToX ( newPosX-X );
           }else{
                transition.setToY ( newPosY );
                transition.setToX ( newPosX );
            }*/
        }
    }
        //transition.setToY(10);

        //transition.setFromX ( laserBeam.getTranslateX ( ) );
        //transition.setFromY ( laserBeam.getTranslateY ( ) );


        boolean isShooting = false;




 /*       for (Map.Entry<Point2D, Wall> entry : clientGameModel.getWallMap ( ).entrySet ( )) {
            //  transition.getNode ().getBoundsInParent ().intersects ( Wall );
            if (entry.getKey ( ).getX ( ) == newPosX) {
                System.out.println ( "ich habe einen Wand getroffen" );
                transition.setToX ( newPosX - X );

            }
            if (entry.getKey ( ).getY ( ) ==  newPosY) {
                transition.setToY ( newPosY - Y );
            } else {
                transition.setToY ( newPosY );

            }
            transition.play ( );
        }
    }*/

  /*  public Point2D getNextWall() {

        Point2D coordEntry = null;
        for (Map.Entry<Point2D, Wall> entry : clientGameModel.getWallMap ( ).entrySet ( )) {
            coordEntry = entry.getKey ( );
            break;
        }
        wallCount++;
        return coordEntry;
    }*/

      /*
        Set<Map.Entry<Point2D, Wall>> wallEntry = clientGameModel.getWallMap ( ).entrySet ( );
        transition.setToX ( 5 );
       // transition.setToY ( 5 );
        //transition.interpolate ( 5 ) ;
        transition.play ();*/




  /*    if (isShooting)
            return;
    isShooting = false;
        transition.setFromX ( laserBeam.getTranslateX ( ) );

        if (wallEntry.contains ( laserBeam.getTranslateX ( ) )) {
        transition.setToX ( -X );
        isShooting = true;
    } else {
        transition.setToX ( X );
    }
        transition.setAutoReverse ( true );
        transition.play ( );
    */

       // transition.setToX(newPosX);
        //transition.setToX(newPosY);


        //transition.play();
       // Set<Map.Entry<Point2D, Wall>> wallEntry= clientGameModel.getWallMap ().entrySet ();
        //HashSet<Point2D> laserEntry = new HashSet<> ( (int)transition.getToX (),(int) transition.getToY () ) ;
      /*  boolean isBlocker = false;
        while (isBlocker = false){
            if (wallEntry.contains ( laserEntry )) {
                System.out.println ( "bis hier geht die Shooting" );
                isBlocker = true;
            }

        }*/



          /* FadeTransition ft = new FadeTransition(Duration.millis(3000), laserBeam);
            ft.setNode(laserBeam);
            ft.setFromValue(1.0);
            ft.setToValue(0.1);
            ft.setCycleCount(Timeline.INDEFINITE);
            ft.setAutoReverse(true);
            SequentialTransition sequentialTransition = new SequentialTransition(transition, ft);
            sequentialTransition.play();*/



            //(Point2D laserPosition, Laser laser);
    // Point2D laserPosition = null;

    //hier Kriege ich den Laser Position x,y
       /* for (Point2D pos: clientGameModel.getLaserMap().keySet()  ) {
         laserPosition.add(pos);

        }

            //ImageView laserBeampos = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size() - 1);
            //laserBeam.setImage(image);
            TranslateTransition transition = new TranslateTransition();
            transition.setToX(5);
            transition.setToY(0);
            transition.setNode(laserBeam);
            transition.setAutoReverse(true);
            transition.setCycleCount(Animation.INDEFINITE);
            transition.setDuration(Duration.INDEFINITE);

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





