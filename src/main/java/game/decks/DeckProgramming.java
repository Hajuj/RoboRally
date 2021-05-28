//package game.decks;
//
//import game.Card;
//import game.Deck;
//import game.programmingcards.*;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
///**
// * @author Ilja Knis
// */
//public class DeckProgramming extends Deck {
//    private ArrayList<Card> deckProgramming;
//
//    public void initializeDeck() {
//
//        for (int i = 0; i < 5; i++) {
//            deckProgramming.add(new MoveI());
//        }
//
//        for (int i = 0; i < 3; i++) {
//            deckProgramming.add(new MoveII());
//            deckProgramming.add(new TurnLeft());
//            deckProgramming.add(new TurnRight());
//        }
//
//        for (int i = 0; i < 2; i++) {
//            deckProgramming.add(new Again());
//        }
//
//        deckProgramming.add(new BackUp());
//        deckProgramming.add(new UTurn());
//        deckProgramming.add(new MoveIII());
//        deckProgramming.add(new PowerUp());
//    }
//
//    public void shuffleDeck() {
//        Collections.shuffle(this.getDeck());
//    }
//
//    protected ArrayList<Card> getDeck() {
//        return deckProgramming;
//    }
//}
