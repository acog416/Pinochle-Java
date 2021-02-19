package com.pinochle.alex.pinochle.models;

import android.util.Pair;

import java.io.Serializable;
import java.util.Vector;
import java.util.Random;

public class Game implements Serializable {
    //Scores, number of turns, number of rounds
    private IntRef totalHumanScore, totalCompScore;
    private int numTurns, turnCount, numRounds;

    //Coin value
    private boolean coin;

	//Current player
	private BoolRef currentPlayer;

	//Card deck
	private Deck deckOfCards = new Deck();

    //Loaded value
    private boolean loaded;

	//Vector of player pointers for polymorphism
    private Vector<Player> players = new Vector<Player>(0);
    
    //Constructor
    public Game(){
        this.totalHumanScore = new IntRef(0);
        this.totalCompScore = new IntRef(0);
        this.numTurns = 24;
        this.turnCount = 1;
        this.numRounds = 0;

        this.currentPlayer = new BoolRef(false);
        this.players.add(new Human());
        this.players.add(new Computer());
    }

    //Change players
    public void changePlayer(){
        currentPlayer.flipVal();
    }

    /* *********************************************************************
    Name: convertListToVector
    Purpose: To convert a list of cards into a vector of Card objects.
    Parameters: cardList
    Return Value: list
    Local Variables:
                    list
                    index, count, points, rank
                    suit
                    face, cardType
    Algorithm: Count how many cards need to be created, then create a new
                Card object for each card in the string.
    Assistance Received: none
    ********************************************************************* */
	public Vector<Card> convertListToVector(String cardList){
        //Fill in later
        Vector<Card> list = new Vector<Card>(0);
        int index = 0, count = 1;
        int points, rank;
        char suit;
        String face, cardType;

        //Get count of cards
        while (index + 1 != cardList.length()) {
            if (cardList.charAt(index) == ' ') {
                count++;
            }
            index++;
        }

        //Reset index
        index = 0;
        //Gather data and create cards based on data
        for (int i = 0; i < count; i++) {
            cardType = cardList.substring(index, index+2);
            face = Character.toString(cardType.charAt(0));
            suit = cardType.charAt(1);

            if (face.equals("A")) {
                points = 11;
                rank = 5;
            }
            else if (face.equals("K")) {
                points = 4;
                rank = 4;
            }
            else if (face.equals("Q")) {
                points = 3;
                rank = 2;
            }
            else if (face.equals("J")) {
                points = 2;
                rank = 1;
            }
            else if (face.equals("X")) {
                points = 10;
                rank = 4;
            }
            else {
                points = 0;
                rank = 0;
            }
            list.add(new Card(face, suit, points, rank));

            //Move to next card
            index += 3;
        }

        return list;
    }

    /* *********************************************************************
    Name: convertMeldsToCards
    Purpose: To call the player's convertMeldsToCards() function.
    Parameters: player, cardCount
    Return Value: None
    Local Variables: None
    Algorithm: Call player.get(player).convertMeldsToCards() with cardCount passed in.
    Assistance Received: none
    ********************************************************************* */
    public void convertMeldsToCards(int player, IntRef cardCount){
        players.get(player).convertMeldsToCards(cardCount);
    }

    /* *********************************************************************
    Name: extractTrumpCard
    Purpose: To create a Card object for the trump card in a save file and
                return it as the trumpCard object.
    Parameters: cardType
    Return Value: trumpCard
    Local Variables:
                    face, suit
                    points, rank
    Algorithm: Gather data based on cardType, create a Card object using that data,
                and set that object as trumpCard, which will be returned.
    Assistance Received: none
    ********************************************************************* */
	public Card extractTrumpCard(String cardType){
        char face,  suit;
        int points, rank;

        //Gather face and suit
        face = cardType.charAt(0);
        suit = cardType.charAt(1);

        //Assign the rest of the data depending on the face and suit
        if (face == 'A') {
            points = 11;
            rank = 5;
        }
        else if (face == 'K') {
            points = 3;
            rank = 4;
        }
        else if (face == 'Q') {
            points = 3;
            rank = 2;
        }
        else if (face == 'J') {
            points = 2;
            rank = 1;
        }
        else if (face == 'X') {
            points = 10;
            rank = 4;
        }
        else {
            points = 0;
            rank = 0;
        }

        Card trumpCard = new Card(String.valueOf(face), suit, points, rank);
        return trumpCard;
    }

    /* *********************************************************************
    Name: getCoinResults
    Purpose: To return a pair containing the results of the coin toss.
    Parameters: None
    Return Value: Pair containing the results.
    Local Variables: None
    Algorithm: Use StringBuilder to build the results and return them in a pair.
    Assistance Received: none
    ********************************************************************* */
    public Pair<String, String> getCoinResults(){
        StringBuilder results1 = new StringBuilder("The coin says "),
            results2 = new StringBuilder("The first player going is ");

        if (coin == false){
            results1.append("heads.");
        }
        else{
            results1.append("tails.");
        }

        if (!currentPlayer.getVal()){
            results2.append("Human!");
        }
        else{
            results2.append("Computer!");
        }

        return new Pair<>(results1.toString(), results2.toString());
    }

    /* *********************************************************************
    Name: getCurrentPlayer
    Purpose: To return currentPlayer.
    Parameters: None
    Return Value: None
    Local Variables: currentPlayer
    Algorithm: Return currentPlayer.
    Assistance Received: none
    ********************************************************************* */
    public BoolRef getCurrentPlayer(){ return currentPlayer; }

    /* *********************************************************************
    Name: getDeck
    Purpose: To return deckOfCards.
    Parameters: None
    Return Value: deckOfCards
    Local Variables: None
    Algorithm: Return deckOfCards.
    Assistance Received: none
    ********************************************************************* */
    public Deck getDeck(){ return deckOfCards; }

    /* *********************************************************************
    Name: getTurnCount
    Purpose: To return turnCount.
    Parameters: None
    Return Value: turnCount
    Local Variables: None
    Algorithm: Return turnCount.
    Assistance Received: none
    ********************************************************************* */
    public int getTurnCount() { return turnCount; }

    /* *********************************************************************
    Name: getNumRounds
    Purpose: To return numRounds.
    Parameters: None
    Return Value: numRounds
    Local Variables: None
    Algorithm: Return numRounds.
    Assistance Received: none
    ********************************************************************* */
    public int getNumRounds(){ return numRounds; }

    /* *********************************************************************
    Name: getPlayers
    Purpose: To return players.
    Parameters: None
    Return Value: players
    Local Variables: None
    Algorithm: Return players.
    Assistance Received: none
    ********************************************************************* */
    public Vector<Player> getPlayers(){ return players; }

    /* *********************************************************************
    Name: getTotalHumanScore
    Purpose: To return totalHumanScore.
    Parameters: None
    Return Value: totalHumanScore
    Local Variables: None
    Algorithm: Return totalHumanScore.
    Assistance Received: none
    ********************************************************************* */
	public IntRef getTotalHumanScore() { return totalHumanScore; }

    /* *********************************************************************
    Name: getTotalCompScore
    Purpose: To return totalCompScore.
    Parameters: None
    Return Value: totalCompScore
    Local Variables: None
    Algorithm: Return totalCompScore.
    Assistance Received: none
    ********************************************************************* */
	public IntRef getTotalCompScore() { return totalCompScore; }

    /* *********************************************************************
    Name: incrementRounds
    Purpose: To increment numRounds.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Increment numRounds.
    Assistance Received: none
    ********************************************************************* */
    public void incrementRounds() { numRounds++; }

    /* *********************************************************************
    Name: incrementTurnCount
    Purpose: To increment turnCount
    Parameters: params
    Return Value: None
    Local Variables: None
    Algorithm: Increment turnCount.
    Assistance Received: none
    ********************************************************************* */
    public void incrementTurnCount() { turnCount++; }

    /* *********************************************************************
    Name: isLoaded
    Purpose: To determine if a game was loaded.
    Parameters: None
    Return Value: loaded
    Local Variables: None
    Algorithm: Return loaded.
    Assistance Received: none
    ********************************************************************* */
    public boolean isLoaded() { return loaded; }

    /* *********************************************************************
    Name: loadMelds
    Purpose: To call a player's loadMelds() function with necessary parameters.
    Parameters: player, line, trumpSuit
    Return Value: None
    Local Variables: None
    Algorithm: Call players.get(player).loadMelds() with necessary parameters.
    Assistance Received: none
    ********************************************************************* */
    public void loadMelds(int player, String line, char trumpSuit){
        players.get(player).loadMelds(line, trumpSuit);
    }

    /* *********************************************************************
    Name: setLoaded
    Purpose: To set loaded to a value.
    Parameters: value
    Return Value: None
    Local Variables: None
    Algorithm: Set loaded to value.
    Assistance Received: none
    ********************************************************************* */
    public void setLoaded(boolean value) { loaded = value; }

    /* *********************************************************************
    Name: setRounds
    Purpose: To set numRounds to a value.
    Parameters: value
    Return Value: None
    Local Variables: None
    Algorithm: Set numRounds to value.
    Assistance Received: none
    ********************************************************************* */
    public void setRounds(int value) { numRounds = value; }

    /* *********************************************************************
    Name: setCapturePile
    Purpose: To call a player's setCapturePile() function with necessary parameters.
    Parameters: player, capturePile
    Return Value: None
    Local Variables: None
    Algorithm: Call player.get(player).setCapturePile() with necessary parameters.
    Assistance Received: none
    ********************************************************************* */
    public void setCapturePile(int player, Vector<Card> capturePile){
        players.get(player).setCapturePile(capturePile);
    }

    /* *********************************************************************
    Name: setCurrentPlayer
    Purpose: To set currentPlayer to a certain player.
    Parameters: player
    Return Value: None
    Local Variables: None
    Algorithm: Set currentPlayer's value to player.
    Assistance Received: none
    ********************************************************************* */
    public void setCurrentPlayer(boolean player) { currentPlayer.setVal(player); }

    /* *********************************************************************
    Name: setDeck
    Purpose: To set deckOfCards' cardDeck.
    Parameters: deck
    Return Value: None
    Local Variables: None
    Algorithm: Call deckOfCards' setDeck function with deck as a parameter.
    Assistance Received: none
    ********************************************************************* */
    public void setDeck(Vector<Card> deck){
        deckOfCards.setDeck(deck);
    }

    /* *********************************************************************
    Name: setHand
    Purpose: To call a player's setHand() function with necessary parameters.
    Parameters: player, hand
    Return Value: None
    Local Variables: None
    Algorithm: Call player.get(player).setHand() with necessary parameters.
    Assistance Received: none
    ********************************************************************* */
    public void setHand(int player, Vector<Card> hand){
        players.get(player).setHand(hand);
    }

    /* *********************************************************************
    Name: setTotalCompScore
    Purpose: To set totalCompScore.
    Parameters: score
    Return Value: None
    Local Variables: None
    Algorithm: Set totalCompScore's value to score.
    Assistance Received: none
    ********************************************************************* */
    public void setTotalCompScore(int score) { totalCompScore.setVal(score); }

    /* *********************************************************************
    Name: setTotalHumanScore
    Purpose: To set totalHumanScore.
    Parameters: score
    Return Value: None
    Local Variables: None
    Algorithm: Set totalHumanScore's value to score.
    Assistance Received: none
    ********************************************************************* */
    public void setTotalHumanScore(int score) { totalHumanScore.setVal(score); }

    /* *********************************************************************
    Name: setTrumpCard
    Purpose: To set deckOfCards's trump card.
    Parameters: card
    Return Value: None
    Local Variables: None
    Algorithm: Call deckOfCards' setTrumpCard function with card as a parameter.
    Assistance Received: none
    ********************************************************************* */
    public void setTrumpCard(Card card) { deckOfCards.setTrumpCard(card); }

    /* *********************************************************************
    Name: setTrumpCard
    Purpose: To set deckOfCards's trumpPopped variable.
    Parameters: popped
    Return Value: None
    Local Variables: None
    Algorithm: Call deckOfCards' setTrumpPopped function with popped as a parameter.
    Assistance Received: none
    ********************************************************************* */
    public void setTrumpPopped(boolean popped) { deckOfCards.setTrumpPopped(popped); }

    /* *********************************************************************
    Name: setTrumpSuit
    Purpose: To set deckOfCards's trumpSuit variable.
    Parameters: suit
    Return Value: None
    Local Variables: None
    Algorithm: Call deckOfCards' setTrumpSuit function with suit as a parameter.
    Assistance Received: none
    ********************************************************************* */
    public void setTrumpSuit(char suit) { deckOfCards.setTrumpSuit(suit); }

    /* *********************************************************************
    Name: setTurnCount
    Purpose: To set turnCount.
    Parameters: count
    Return Value: None
    Local Variables: None
    Algorithm: Set turnCount to count.
    Assistance Received: none
    ********************************************************************* */
    public void setTurnCount(int count) { turnCount = count; }

    /* *********************************************************************
    Name: tossCoin
    Purpose: To analyze a coin toss and see who goes first in a new game.
    Parameters: toss
    Return Value: None
    Local Variables: rand
    Algorithm: Use rand to set coin, and use your toss and coin's value to
                set currentPlayer.
    Assistance Received: none
    ********************************************************************* */
    public void tossCoin(char toss){
        //Initialize random generator for coin toss
        Random rand = new Random();

        //Reset numRounds
        numRounds = 0;

        //Assign a value for coin
        switch(rand.nextInt(2)){
            case 0:
                coin = false;
                break;
            default:
                coin = true;
                break;
        }

        //Decide who goes first
        if (toss == 'h' && coin == false) { currentPlayer.setVal(false); }
        else if (toss == 't' && coin == false) { currentPlayer.setVal(true); }
        else if (toss == 'h' && coin == true) { currentPlayer.setVal(true); }
        else { currentPlayer.setVal(false); }
    }
}