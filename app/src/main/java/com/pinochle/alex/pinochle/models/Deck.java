package com.pinochle.alex.pinochle.models;

import java.io.Serializable;
import java.util.Vector;
import java.util.Random;

public class Deck implements Serializable {
    //Card deck
    private Vector<Card> cardDeck = new Vector<Card>(0);

    //Trump card
    private Card trumpCard = new Card();

    //Trump suit
    private char trumpSuit;

    //Boolean for if the trump card was popped from the stock
    private boolean trumpPopped = false;

    //Constructor
    public Deck(){
        fillCards();
    }

    /* *********************************************************************
    Name: clearDeck
    Purpose: To clear the deck.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Clear cardDeck.
    Assistance Received: none
    ********************************************************************* */
    public void clearDeck(){
        cardDeck.clear();
    }

    /* *********************************************************************
    Name: dealCards
    Purpose: To deal cards to the players.
    Parameters: humanPlayer and comPlayer.
    Return Value: None
    Local Variables: None
    Algorithm: Clear each player's hands, melds, and capture piles. Then,
                deal cards in an orderly fashion, followed by dealing the trump card.
    Assistance Received: none
    ********************************************************************* */
    public void dealCards(Player humanPlayer, Player comPlayer){
        humanPlayer.clearHand();
        humanPlayer.clearCapturePile();
        humanPlayer.clearMelds();
        comPlayer.clearHand();
        comPlayer.clearCapturePile();
        comPlayer.clearMelds();

        //Deal cards accordingly
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                humanPlayer.addCard(cardDeck.get(0));
                cardDeck.removeElementAt(0);
            }
            for (int j = 0; j < 4; j++) {
                comPlayer.addCard(cardDeck.get(0));
                cardDeck.removeElementAt(0);
            }
        }

        //Deal trump card
        trumpCard = cardDeck.get(0);
        trumpSuit = trumpCard.getSuit();
        cardDeck.removeElementAt(0);
    }

    /* *********************************************************************
    Name: fillCards
    Purpose: To fill the deck.
    Parameters: None
    Return Value: None
    Local Variables: suits[], points[], rank, face
    Algorithm: Use two for loops to gather card faces and suits, assign points and rank based on that,
                and create 48 card objects to be stored in the deck.
    Assistance Received: none
    ********************************************************************* */
    public void fillCards(){
        char suits[] = { 'D', 'C', 'H', 'S' };
        int points[] = { 0, 10, 2, 3, 4, 11 };
        int rank;
        String face;


        //Generate deck with correct properties for the cards
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {

                /*
                I originally had an if-statement where it determines the points based on the face, but I
                changed it to determine the face based on points so I can use a switch statement instead
                just for optimization.
                */

                switch (points[j]) {
                case 11:
                    face = "A";
                    rank = 5;
                    break;
                case 4:
                    face = "K";
                    rank = 3;
                    break;
                case 3:
                    face = "Q";
                    rank = 2;
                    break;
                case 2:
                    face = "J";
                    rank = 1;
                    break;
                case 10:
                    face = "X";
                    rank = 4;
                    break;
                default:
                    face = "9";
                    rank = 0;
                    break;
                }

                //Add the cards
                cardDeck.add(new Card(face, suits[i], points[j], rank));
                cardDeck.add(new Card(face, suits[i], points[j], rank));
            }
        }
    }

    /* *********************************************************************
    Name: getDeck
    Purpose: To get cardDeck.
    Parameters: None
    Return Value: cardDeck
    Local Variables: None
    Algorithm: Return cardDeck.
    Assistance Received: none
    ********************************************************************* */
    public Vector<Card> getDeck(){
        return cardDeck;
    }

    /* *********************************************************************
    Name: getTrumpCard
    Purpose: To get trumpCard.
    Parameters: None
    Return Value: trumpCard
    Local Variables: None
    Algorithm: Return trumpCard.
    Assistance Received: none
    ********************************************************************* */
    public Card getTrumpCard(){
        return trumpCard;
    }

    /* *********************************************************************
    Name: getTrumpPopped
    Purpose: To get trumpPopped.
    Parameters: None
    Return Value: trumpPopped
    Local Variables: None
    Algorithm: Return trumpPopped.
    Assistance Received: none
    ********************************************************************* */
    public boolean getTrumpPopped(){
        return trumpPopped;
    }

    /* *********************************************************************
    Name: getTrumpSuit
    Purpose: To get trumpSuit.
    Parameters: None
    Return Value: trumpSuit
    Local Variables: None
    Algorithm: Return trumpSuit.
    Assistance Received: none
    ********************************************************************* */
    public char getTrumpSuit(){
        return trumpSuit;
    }

    /* *********************************************************************
    Name: getTrumpSuitResource
    Purpose: To get the resource file name for the trump suit after the trump card was popped.
    Parameters: None
    Return Value: file
    Local Variables: file
    Algorithm: Use trumpSuit to set file and return it.
    Assistance Received: none
    ********************************************************************* */
    public String getTrumpSuitResource(){
        String file = "";
        switch (trumpSuit){
            case 'C':
                file = "clubs";
                break;
            case 'D':
                file = "diamonds";
                break;
            case 'H':
                file = "hearts";
                break;
            case 'S':
                file = "spades";
                break;
        }

        return file;
    }

    /* *********************************************************************
    Name: popCard
    Purpose: To pop a card from the deck.
    Parameters: None
    Return Value: poppedCard
    Local Variables: poppedCard
    Algorithm: If the deck is empty, pop and return the trump card and set trumpPopped to true.
                Otherwise, pop a card from the deck.
    Assistance Received: none
    ********************************************************************* */
    public Card popCard(){
        Card poppedCard;

        //If the deck is empty, pop the trump card instead
        if (cardDeck.size() == 0 && trumpPopped == false) {
            poppedCard = popTrumpCard();
            trumpPopped = true;
            return poppedCard;
        }

        //Pop the top card in the deck and return it
        poppedCard = cardDeck.get(0);
        cardDeck.removeElementAt(0);
        return poppedCard;
    }

    /* *********************************************************************
    Name: popTrumpCard
    Purpose: To pop the trump card
    Parameters: None
    Return Value: new Card(trumpCard)
    Local Variables: None
    Algorithm: Set trumpPopped to true and return a new Card object based on trumpCard.
    Assistance Received: none
    ********************************************************************* */
    public Card popTrumpCard(){
        trumpPopped = true;
        return new Card(trumpCard);
    }

    /* *********************************************************************
    Name: setDeck
    Purpose: To set cardDeck.
    Parameters: deck
    Return Value: None
    Local Variables: None
    Algorithm: Set cardDeck to deck.
    Assistance Received: none
    ********************************************************************* */
	public void setDeck(Vector<Card> deck) { cardDeck = deck; }

    /* *********************************************************************
    Name: setTrumpCard
    Purpose: To set setTrumpCard.
    Parameters: trump
    Return Value: None
    Local Variables: None
    Algorithm: Set trumpCard to trump.
    Assistance Received: none
    ********************************************************************* */
	public void setTrumpCard(Card trump) { trumpCard = trump; }

    /* *********************************************************************
    Name: setTrumpPopped
    Purpose: To set trumpPopped.
    Parameters: pop
    Return Value: None
    Local Variables: None
    Algorithm: Set trumpPopped to pop.
    Assistance Received: none
    ********************************************************************* */
	public void setTrumpPopped(boolean pop) { trumpPopped = pop; }

    /* *********************************************************************
    Name: setTrumpSuit
    Purpose: To set trumpSuit.
    Parameters: suit
    Return Value: None
    Local Variables: None
    Algorithm: Set trumpSuit to suit.
    Assistance Received: none
    ********************************************************************* */
	public void setTrumpSuit(char suit) { trumpSuit = suit; }

    /* *********************************************************************
    Name: shuffleCards
    Purpose: To shuffle the cards in cardDeck.
    Parameters: None
    Return Value: None
    Local Variables:
                    copy, shuffled
                    index, copySize
                    rand
    Algorithm: Use a custom shuffle method to randomly pick cards in cardDeck and add
                them to shuffled. After this, set cardDeck to shuffled.
    Assistance Received: none
    ********************************************************************* */
	public void shuffleCards(){
        Vector<Card> copy = cardDeck, shuffled = new Vector<Card>(0);
        int index, copySize = copy.size();
        Random rand = new Random();

        //Use custom shuffle method
        for (int i = 0; i < copySize; i++) {
            index = rand.nextInt(copy.size());
            shuffled.add(copy.get(index));
            copy.removeElementAt(index);
        }

        //Assign the shuffled vector to cardDeck
        cardDeck = shuffled;
    }

    /* *********************************************************************
    Name: trumpToString
    Purpose: To return a string version of trumpCard.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Return trumpCard.toString().
    Assistance Received: none
    ********************************************************************* */
    public String trumpToString(){ return trumpCard.toString(); }
}