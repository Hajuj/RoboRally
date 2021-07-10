package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Antenna extends Element {

    public Antenna(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);
    }

    /**
     * This comparator sorts two points by destination distance.
     */
    /*public class NearestComparator implements Comparator<Point2D> {

     *//** The point to be reached. *//*
        private Point2D destination;

        *//**
     * Instantiates a new comparator that sorts points by destination distance, descendant.
     *
     * @param destination the point to reach
     *//*
        public NearestComparator(Point2D destination) {
            this.destination = destination;
        }

        *//**
     * Sort two points by destination distance, descendant.
     *
     *
     *//*
        @Override
        public int compare(Point2D p1, Point2D p2) {    //hier kann man das statt 2 punkte ein for schleife für jedes Punkte
            double p1_distance = p1.distance(destination);
            double p2_distance = p2.distance(destination);
            return (p1_distance < p2_distance) ? -1 : ((p1_distance > p2_distance) ? 1 : 0);
        }
    }*/
    //The sorting code right now is like this

   /* private List<Point2D> getSendOrder(Point2D destination) {
        LinkedList<Point2D> sendOrderList = new LinkedList<Point2D>();
        sendOrderList.add(myPosition);

        Iterator<Point2D> keyIter = neighborLinks.keySet().iterator();
        while (keyIter.hasNext()) {
            sendOrderList.add(keyIter.next());
        }

        // sort list by distance from destination
        Collections.sort(sendOrderList, new NearestComparator(destination));
        return sendOrderList;

    }*/
}
