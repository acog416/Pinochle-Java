package com.pinochle.alex.pinochle.models;

import android.content.Context;

import java.io.Serializable;
import java.util.Vector;
import java.util.Random;
import java.lang.*;

public class Player implements Serializable {
    //Hand and capture pile vectors
    protected Vector<Card> hand = new Vector<Card>(0), capturePile = new Vector<Card>(0);

    //Meld vector
    protected Vector<Vector<String>> melds = new Vector<Vector<String>>(0);

    //Help strategy
    private String strategy = "";

    //Constructor
    public Player(){}

    /* *********************************************************************
    Name: addCard
    Purpose: To add a card to a player's hand.
    Parameters:
                playingCard
    Return Value: None
    Local Variables: None
    Algorithm: Add playingCard to hand.
    Assistance Received: none
    ********************************************************************* */
    public void addCard(Card playingCard){
        hand.add(playingCard);
    }

    /* *********************************************************************
    Name: addCardsToCapturePile
    Purpose: To add two cards to the player's capture pile.
    Parameters:
                card1 and card2
    Return Value: None
    Local Variables: None
    Algorithm: Add card1 and card2 to capturePile.
    Assistance Received: none
    ********************************************************************* */
    public void addCardsToCapturePile(Card card1, Card card2){
        capturePile.add(card1);
        capturePile.add(card2);
    }

    /* *********************************************************************
    Name: autoPick
    Purpose: To use AI to automatically pick the best card for the scenario.
    Parameters:
                playerPosition
                trumpSuit
                currentPlayer
    Return Value: pick
    Local Variables:
                pick and reducedHand
    Algorithm: If melds are possible, take out the cards for the best one and use
                autoPickLead() or autoPickChase() to return a card and set the helpStrategy variable.
    Assistance Received: none
    ********************************************************************* */
    public Card autoPick(String playerPosition, char trumpSuit, Card lead, boolean currentPlayer){
        Card pick = new Card();
        Vector<Card> reducedHand = createReducedHand(trumpSuit);

        if (playerPosition.equals("lead")){
            pick = autoPickLead(reducedHand, trumpSuit, currentPlayer);
        }
        else{
            pick = autoPickChase(reducedHand, trumpSuit, lead, currentPlayer);
        }

        return pick;
    }

    /* *********************************************************************
    Name: autoPickChase
    Purpose: To use AI to pick the best card for a chase player.
    Parameters:
                reducedHand
                trumpSuit
                lead
                currentPlayer
    Return Value: pick
    Local Variables:
                pick
                minRank
                lowestRank
    Algorithm: Take different paths depending on if the lead card is a trump card and
                which cards are available in your hand, with melds being taken into account.
    Assistance Received: none
    ********************************************************************* */
    public Card autoPickChase(Vector<Card> reducedHand, char trumpSuit, Card lead, boolean currentPlayer){
        Card pick = new Card();
        int minRank = 99;

        //For if the lead card is of trump suit
        if (lead.getSuit() == trumpSuit){
            //Loop through hand and try to find the first bigger card of trump suit
            for (Card current : reducedHand) {
                if (current.getSuit() != trumpSuit) { continue; }

                //Find least expensive card
                if (current.getRank() > lead.getRank() && current.getRank() < minRank) {
                    pick = current;
                    minRank = current.getRank();
                }
            }

            //If a suitable card has been found, print the strategy and return the card
            if (pick.getFace() != "") {
                if (currentPlayer == false){
                    strategy = "You should play " + pick.toString() + " to beat the lead player with a larger card of the trump suit.";
                }
                else{
                    strategy = "Computer chose " + pick.toString() + " to beat the lead player with a larger card of the trump suit.";
                }

                return pick;
            }

            //If no trump suit card, just throw lowest-ranking card
            boolean lowestCardFound = false;
            int lowestRank = 0;

            int i = 0;
            while (!lowestCardFound) {
                if (reducedHand.get(i).getSuit() != trumpSuit && reducedHand.get(i).getRank() == lowestRank) {
                    pick = reducedHand.get(i);
                    lowestCardFound = true;
                }

                //Prevent segmentation fault
                if ((i + 1) == reducedHand.size()) {
                    i = 0;
                    lowestRank++;
                }
                else { i++; }
            }

            if (currentPlayer == false){
                strategy = "You should play " + pick.toString() + " because there is no good counterpick.";
            }
            else{
                strategy = "Computer chose " + pick.toString() + " because there was no good counterpick.";
            }
        }

        //For if the lead card isn't of trump suit
        else{

            //First, try finding a larger card of the same suit
            for (Card current : reducedHand){
                if (current.getSuit() == lead.getSuit() && current.getRank() > lead.getRank() && current.getRank() < minRank){
                    pick = current;
                    minRank = current.getRank();
                }
            }

            //If a suitable card has been found, print the strategy and return the card
            if (pick.getFace() != "") {

                if (currentPlayer == false){
                    strategy = "You should play " + pick.toString() + " to beat the lead player with a larger card of the same suit.";
                }
                else{
                    strategy = "Computer chose " + pick.toString() + " to beat the lead player with a larger card of the same suit.";
                }

                return pick;
            }

            //Reset minRank for finding the least expensive choice
            minRank = 99;

            //If the first strategy fails, try finding a card of trump suit
            for (Card current : reducedHand){
                if (current.getSuit() == trumpSuit && current.getRank() < minRank){
                    pick = current;
                    minRank = current.getRank();
                }
            }

            //If a suitable card has been found, print the strategy and return the card
            if (pick.getFace() != "") {
                if (currentPlayer == false){
                    strategy = "You should play " + pick.toString() + " to beat the lead player with a trump card.";
                }
                else{
                    strategy = "Computer chose " + pick.toString() + " to beat the lead player with a trump card.";
                }

                return pick;
            }

            //If no trump suit card, just throw lowest-ranking card
            boolean lowestCardFound = false;
            int lowestRank = 0;

            int i = 0;
            while (!lowestCardFound) {
                if (reducedHand.get(i).getSuit() != trumpSuit && reducedHand.get(i).getRank() == lowestRank) {
                    pick = reducedHand.get(i);
                    lowestCardFound = true;
                }

                //Prevent segmentation fault
                if ((i + 1) == reducedHand.size()) {
                    i = 0;
                    lowestRank++;
                }
                else { i++; }
            }

            if (currentPlayer == false){
                strategy = "You should play " + pick.toString() + " because there is no good counterpick.";
            }
            else{
                strategy = "Computer chose " + pick.toString() + " because there was no good counterpick.";
            }
        }

        return pick;
    }

    /* *********************************************************************
    Name: autoPickLead
    Purpose: To use AI to pick the best card for a chase player.
    Parameters:
                reducedHand
                trumpSuit
                currentPlayer
    Return Value: pick
    Local Variables:
                pick
                rand
                decision
                lowestRank
                highestRank
                picked
    Algorithm: Try picking a trump card first, then try the highest-ranking non-trump card
                if one isn't available. Rand is used to determine if the AI should save a trump card
                or not.
    Assistance Received: none
    ********************************************************************* */
    public Card autoPickLead(Vector<Card> reducedHand, char trumpSuit, boolean currentPlayer){
        Card pick = new Card();
        Random rand = new Random();
        int decision = rand.nextInt(2);
        int lowestRank = 0, highestRank = 5;
        boolean picked = false;

        //For finding a trump card
        //Effort to regulate trump picks
        if (decision == 0){
            while (!picked){
                //Loop through reducedHand and find best choice for trump card
                for (Card current : reducedHand){
                    if (current.getSuit() == trumpSuit && current.getRank() == lowestRank){
                        pick = current;
                        picked = true;
                        break;
                    }
                }

                //Increase card rank if no cards of that rank are found
                lowestRank++;

                //Break out of loop if there aren't any cards of trump suit
                if (lowestRank == 6) { break; }
            }

            if (picked){
                if (currentPlayer == false){
                    strategy = "You should play " + pick.toString() + " to use a trump card to beat the opponent.";
                }
                else{
                    strategy = "Computer chose " + pick.toString() + " to use a trump card to beat the opponent.";
                }
            }
        }

        //For finding a high-level non-trump card
        while (!picked){
            //Loop through reducedHand and find best choice for trump card
            for (Card current : reducedHand){
                if (current.getSuit() != trumpSuit && current.getRank() == highestRank){
                    pick = current;
                    picked = true;
                    break;
                }
            }

            if (picked){
                if (picked){
                    if (currentPlayer == false){
                        strategy = "You should play " + pick.toString() + " to save a trump card and use a high-level card against the opponent.";
                    }
                    else{
                        strategy = "Computer chose " + pick.toString() + " to save a trump card and use a high-level card against the opponent.";
                    }
                }
            }

            //Decrease card rank if no cards of that rank are found
            highestRank--;
        }
        return pick;
    }

    /* *********************************************************************
    Name: alreadyImportedFromMelds
    Purpose: To determine if a copy of a card should be created when loading melds.
    Parameters: cardToCheck
    Return Value: None
    Local Variables: None
    Algorithm: Check every card in hand for cardToCheck and check its shared variable.
               Return true if it's shared and false otherwise.
    Assistance Received: none
    ********************************************************************* */
    public boolean alreadyImportedFromMelds(String cardToCheck){
        //Iterate through hand, return true if card already was imported from melds
        for (Card card : hand){
            if (card.toString().equals(cardToCheck) && card.getShared()){
                return true;
            }
        }

        //Else, return false
        return false;
    }

    /* *********************************************************************
    Name: canMeld
    Purpose: To determine if a meld is possible.
    Parameters:
                choice, trumpSuit
    Return Value: true/false
    Local Variables:
                handSize
                face
                faces, suits
                queenSpades, jackDiamonds, ace, ten, king, queen, jack
                aceCount, kingCount, queenCount, jackCount
    Algorithm: Check choice and go to the correct check process corresponding to
                choice's value. The hand will be checked to see if all conditions are
                possible for that meld and return true if it is, and false otherwise.
                If a card is good for the meld, its meldCandidate flag is set, and if a meld
                isn't possible, then all flags are reset.
    Assistance Received: none
    ********************************************************************* */
    public boolean canMeld(int choice, char trumpSuit){
        int handSize = hand.size();

        //Variables for holding information while checking certain melds
        String face;
        Vector<String> faces = new Vector<String>(0), suits = new Vector<String>(0);
        boolean queenSpades = false, jackDiamonds = false, ace = false, ten = false, king = false, queen = false, jack = false;
        int aceCount = 0, kingCount = 0, queenCount = 0, jackCount = 0;

        //Go to the correct meld-checking process
        switch (choice) {
            case 1: //Flush
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("Flush")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getSuit() == trumpSuit) {
                        face = hand.get(i).getFace();
                        if (face.equals("A") && !faces.contains(face)) {
                            ace = true;
                            faces.add(face);
                            hand.get(i).setMeldCandidate(true);
                        }
                        else if (face.equals("X") && !faces.contains(face)) {
                            ten = true;
                            faces.add(face);
                            hand.get(i).setMeldCandidate(true);
                        }
                        else if (face.equals("K") && !faces.contains(face)) {
                            king = true;
                            faces.add(face);
                            hand.get(i).setMeldCandidate(true);
                        }
                        else if (face.equals("Q") && !faces.contains(face)) {
                            queen = true;
                            faces.add(face);
                            hand.get(i).setMeldCandidate(true);
                        }
                        else if (face.equals("J") && !faces.contains(face)) {
                            jack = true;
                            faces.add(face);
                            hand.get(i).setMeldCandidate(true);
                        }
                    }

                    if (ace && ten && king && queen && jack) {
                        return true;
                    }
                }

                break;

            case 2: //Royal Marriage
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("RoyalMarriage")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getSuit() == trumpSuit && hand.get(i).getFace().equals("K") && king == false) {
                        king = true;
                        hand.get(i).setMeldCandidate(true);
                    }
                    else if (hand.get(i).getSuit() == trumpSuit && hand.get(i).getFace().equals("Q") && queen == false) {
                        queen = true;
                        hand.get(i).setMeldCandidate(true);
                    }
                    if (king && queen) {
                        return true;
                    }
                }

                break;

            case 3: //Marriage
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("Marriage")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getSuit() != trumpSuit && hand.get(i).getFace().equals("K") && king == false) {
                        System.out.println("Winner is " + hand.get(i).toString());
                        king = true;
                        System.out.println("Changing from " + hand.get(i).getMeldCandidate() + " to true");
                        hand.get(i).setMeldCandidate(true);
                        System.out.println("Now it's " + hand.get(i).getMeldCandidate());
                    }
                    else if (hand.get(i).getSuit() != trumpSuit && hand.get(i).getFace().equals("Q") && queen == false) {
                        System.out.println("Winner is " + hand.get(i));
                        queen = true;
                        System.out.println("Changing from " + hand.get(i).getMeldCandidate() + " to true");
                        hand.get(i).setMeldCandidate(true);
                        System.out.println("Now it's " + hand.get(i).getMeldCandidate());
                    }
                    if (king && queen) {
                        return true;
                    }
                }

                break;

            case 4: //Dix
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("Dix")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getSuit() == trumpSuit && hand.get(i).getFace().equals("9")) {
                        hand.get(i).setMeldCandidate(true);
                        return true;
                    }
                }

                break;

            case 5: //Four Aces
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("FourAces")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getFace().equals("A")) {
                        if (!suits.contains(String.valueOf(hand.get(i).getSuit()))) {
                            suits.add(String.valueOf(hand.get(i).getSuit()));
                            hand.get(i).setMeldCandidate(true);
                            aceCount++;
                        }
                    }

                    //Check if we've reached enough aces
                    if (aceCount == 4) {
                        return true;
                    }
                }

                break;

            case 6: //Four Kings
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("FourKings")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getFace().equals("K")) {
                        if (!suits.contains(String.valueOf(hand.get(i).getSuit()))) {
                            suits.add(String.valueOf(hand.get(i).getSuit()));
                            hand.get(i).setMeldCandidate(true);
                            kingCount++;
                        }
                    }

                    //Check if we've reached enough kings
                    if (kingCount == 4) {
                        return true;
                    }
                }

                break;

            case 7: //Four Queens
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("FourQueens")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getFace().equals("Q")) {
                        if (!suits.contains(String.valueOf(hand.get(i).getSuit()))) {
                            suits.add(String.valueOf(hand.get(i).getSuit()));
                            hand.get(i).setMeldCandidate(true);
                            queenCount++;
                        }
                    }

                    //Check if we've reached enough aces
                    if (queenCount == 4) {
                        return true;
                    }
                }

                break;

            case 8: //Four Jacks
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("FourJacks")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getFace().equals("J")) {
                        if (!suits.contains(String.valueOf(hand.get(i).getSuit()))) {
                            suits.add(String.valueOf(hand.get(i).getSuit()));
                            hand.get(i).setMeldCandidate(true);
                            jackCount++;
                        }
                    }

                    //Check if we've reached enough aces
                    if (jackCount == 4) {
                        return true;
                    }
                }

                break;

            default: //Pinochle
                for (int i = 0; i < handSize; i++){
                    //If card exists in the same meld, skip over it
                    if (hand.get(i).existsInMeld("Pinochle")) { continue; }

                    //Else, proceed with the meld check
                    if (hand.get(i).getFace().equals("Q") && hand.get(i).getSuit() == 'S' && queenSpades == false) {
                        queenSpades = true;
                        hand.get(i).setMeldCandidate(true);
                    }
                    if (hand.get(i).getFace().equals("J") && hand.get(i).getSuit() == 'D' && jackDiamonds == false) {
                        jackDiamonds = true;
                        hand.get(i).setMeldCandidate(true);
                    }
                    if (queenSpades && jackDiamonds) {
                        return true;
                    }
                }

                break;
        }

        //If meld isn't possible reset meldCandidate values and return false
        for (int i = 0; i < handSize; i++) {
            if (hand.get(i).getMeldCandidate()) {
                hand.get(i).setMeldCandidate(false);
            }
        }
        return false;
    }

    /* *********************************************************************
    Name: clearCapturePile
    Purpose: To clear a player's capture pile.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Clear capturePile.
    Assistance Received: none
    ********************************************************************* */
    public void clearCapturePile(){
        capturePile.clear();
    }

    /* *********************************************************************
    Name: clearHand
    Purpose: To clear a player's hand.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Clear hand.
    Assistance Received: none
    ********************************************************************* */
    public void clearHand(){
        hand.clear();
    }

    /* *********************************************************************
    Name: clearMelds
    Purpose: To clear a player's melds.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Clear melds.
    Assistance Received: none
    ********************************************************************* */
    public void clearMelds(){
        melds.clear();
    }

    /* *********************************************************************
    Name: convertMeldsToCards
    Purpose: To convert a string of melds into cards when loading from a file.
    Parameters: cardCount
    Return Value: None
    Local Variables:
                meldName
                face, suit
                cardIsShared
                points, rank
    Algorithm: For each meld in the string, create any necessary cards to fill the rest of the hand.
    Assistance Received: none
    ********************************************************************* */
    public void convertMeldsToCards(IntRef cardCount){
        String meldName;
        char face, suit;
        boolean cardIsShared = false;
        int points, rank;

        for (int i = 0; i < melds.size(); i++){
            //Set meldName to add to new card's existing melds
            meldName = melds.get(i).get(0);

            //Iterate through cards and add them to the deck
            for (int j = 1; j < melds.get(i).size(); j++){
                face = melds.get(i).get(j).charAt(0);
                suit = melds.get(i).get(j).charAt(1);

                //Check if card was already imported
                if (alreadyImportedFromMelds(String.valueOf(face) + String.valueOf(suit))) { continue; }

                //Check if card is marked as shared
                if (melds.get(i).get(j).length() == 3 && melds.get(i).get(j).charAt(2) == '*'){
                    cardIsShared = true;
                }

                //Gather and assign data
                if (face == 'A'){
                    points = 11;
                    rank = 5;
                }
                else if (face == 'X'){
                    points = 10;
                    rank = 4;
                }
                else if (face == 'K'){
                    points = 4;
                    rank = 3;
                }
                else if (face == 'Q'){
                    points = 3;
                    rank = 2;
                }
                else if (face == 'J'){
                    points = 2;
                    rank = 1;
                }
                else{
                    points = 0;
                    rank = 0;
                }

                //Add meld to hand
                hand.add(new Card(String.valueOf(face), suit, points, rank));

                //Tie meld to card
                hand.get(hand.size() - 1).addMeld(meldName);

                //If card is shared, mark as shared
                if (cardIsShared){
                    hand.get(hand.size() - 1).setShared(true);
                }

                //Increment cardCount
                cardCount.add(1);

                //Reset cardIsShared
                cardIsShared = false;
            }
        }
    }

    /* *********************************************************************
    Name: createReducedHand
    Purpose: To take out cards that could be used for a high-rewarding meld while
                automatically picking a card.
    Parameters:
                trumpSuit
    Return Value: reducedHand
    Local Variables:
                reducedHand
                meldsByRank[]
                meldFound
    Algorithm: Create a copy of the hand, and use canMeld() in a loop to find the best
                choice for a meld. Then, go through the hand copy (reducedHand) and remove those
                cards and return.
    Assistance Received: none
    ********************************************************************* */
    public Vector<Card> createReducedHand(char trumpSuit){
        Vector<Card> reducedHand = new Vector<Card>(0);
        int[] meldsByRank = { 1, 5, 6, 7, 2, 8, 9, 3, 4 };
        boolean meldFound = false;

        //Create a copy of the hand
        for (int i = 0; i < hand.size(); i++){
            reducedHand.add(hand.get(i));
        }

        //Decide which cards to avoid based on available melds
        for (int i = 0; i < 9; i++) {
            if (canMeld(meldsByRank[i], trumpSuit)) {
                meldFound = true;
                break;
            }
        }

        int i = -1;
        while (meldFound){
            i++;
            if (reducedHand.get(i).getMeldCandidate()) {
                reducedHand.removeElementAt(i);
                i = -1;
            }

            //Prevent segmentation fault
            if ((i + 1) == reducedHand.size()) {
                break;
            }
        }

        //Reset meldCandidate status after checks
        for (int j = 0; j < hand.size(); j++){
            if (hand.get(j).getMeldCandidate()){
                hand.get(j).setMeldCandidate(false);
            }
        }

        if (reducedHand.size() > 0){
            return reducedHand;
        }
        else{
            return hand;
        }
    }

    /* *********************************************************************
    Name: getCapturePile
    Purpose: To return a player's capture pile.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Return capturePile.
    Assistance Received: none
    ********************************************************************* */
    public Vector<Card> getCapturePile(){
        return capturePile;
    }

    /* *********************************************************************
    Name: getHand
    Purpose: To return a player's hand.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Return hand.
    Assistance Received: none
    ********************************************************************* */
    public Vector<Card> getHand(){
        return hand;
    }

    /* *********************************************************************
    Name: getStrategy
    Purpose: To return a player's strategy for displaying a recommendation.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Return strategy.
    Assistance Received: none
    ********************************************************************* */
    public String getStrategy(){ return strategy; }

    /* *********************************************************************
    Name: getMelds
    Purpose: To return a player's melds.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Return melds.
    Assistance Received: none
    ********************************************************************* */
    public Vector<Vector<String>> getMelds(){
        return melds;
    }

    /* *********************************************************************
    Name: loadMelds
    Purpose: To load melds from a file line and recreate their meld vector entries.
    Parameters:
                line and trumpSuit
    Return Value: meldCollection
    Local Variables:
                meldCollection
                currentMeld
                meldData, card
                delimiter, lineIndex, meldListIndex, count, index
    Algorithm: Store the meld currently at the front of the line string in a substring,
                split the cards, and store them in currentMeld. After this, determine what kind
                of meld it is, and insert the name at the front. Then, insert it in meldCollection
                and set melds to meldCollection.
    Assistance Received: none
    ********************************************************************* */
    public void loadMelds(String line, char trumpSuit){
        Vector<Vector<String>> meldCollection = new Vector<Vector<String>>(0);
        Vector<String> currentMeld = new Vector<String>(0);
        String meldData, card;
        int delimiter, lineIndex, meldListIndex = 0, count = 1, index;
        boolean end = false;

        //Go through each meld and create the player's meld vector
        while (!end) {
            lineIndex = 0;
            delimiter = line.indexOf(",");
            if (delimiter == -1) {
                end = true;
                meldData = line;
            }
            else{
                meldData = line.substring(0, delimiter);
            }

            //Count the amount of cards in the current meld
            while (lineIndex + 1 != meldData.length()) {
                if (meldData.charAt(lineIndex) == ' ') {
                    count++;
                }
                lineIndex++;
            }

            lineIndex = 0;

            //Get card string and add to vector
            for (int i = 0; i < count; i++) {
                card = meldData.substring(lineIndex, lineIndex+2);
                currentMeld.add(card);
                lineIndex += 3;
            }

            meldCollection.add(new Vector<String>(currentMeld));

            //Clear temporary vector for next meld
            currentMeld.clear();

            //Erase current meld from data string
            if (!end) {
                line = line.substring(delimiter + 2);
            }

            meldListIndex++;
            count = 1;
        }

        //Loop through melds and label them accordingly
        boolean queenSpades = false, jackDiamonds = false, queen = false, king = false;
        for (int i = 0; i < meldCollection.size(); i++) {
            if (meldCollection.get(i).size() == 1) {
                meldCollection.get(i).insertElementAt("Dix", 0);
            }
            else if (meldCollection.get(i).size() == 5) {
                meldCollection.get(i).insertElementAt("Flush", 0);
            }
            else if (meldCollection.get(i).size() == 2) {
                //Royal Marriage
                if (meldCollection.get(i).get(0).charAt(0) == trumpSuit && meldCollection.get(i).get(0).charAt(1) == trumpSuit) { //Check suits for trump suit
                    meldCollection.get(i).insertElementAt("Royal Marriage", 0);
                    continue;
                }

                //Marriage
                for (int j = 0; j < 2; j++) {
                    if (meldCollection.get(i).get(j).charAt(0) == 'Q' || meldCollection.get(i).get(j).charAt(1) == 'Q') {
                        queen = true;
                    }
                    if (meldCollection.get(i).get(j).charAt(0) == 'K' || meldCollection.get(i).get(j).charAt(1) == 'K') {
                        king = true;
                    }
                }
                if (queen && king) {
                    meldCollection.get(i).insertElementAt("Marriage", 0);
                    queen = king = false;
                    continue;
                }

                //Pinochle
                for (int j = 0; j < 2; j++) {
                    if (meldCollection.get(i).get(j).equals("QS")) {
                        queenSpades = true;
                    }
                    if (meldCollection.get(i).get(j).equals("JD")) {
                        jackDiamonds = true;
                    }
                }
                if (queenSpades && jackDiamonds) {
                    meldCollection.get(i).insertElementAt("Pinochle", 0);
                    queenSpades = jackDiamonds = false;
                }
            }
            else { //Four Aces/Kings/Queens/Jacks
                if (meldCollection.get(i).get(0).charAt(0) == 'A') {
                    meldCollection.get(i).insertElementAt("Four Aces", 0);
                }
                else if (meldCollection.get(i).get(0).charAt(0) == 'K') {
                    meldCollection.get(i).insertElementAt("Four Kings", 0);
                }
                else if (meldCollection.get(i).get(0).charAt(0) == 'Q') {
                    meldCollection.get(i).insertElementAt("Four Queens", 0);
                }
                else {
                    meldCollection.get(i).insertElementAt("Four Jacks", 0);
                }
            }
        }

        //Set melds vector
        melds = meldCollection;
    }

    /* *********************************************************************
    Name: makeMove
    Purpose: Placeholder function for players making a move.
    Parameters:
                cardChoice
                turnState
                trumpSuit
                lead
                currentPlayer
    Return Value: None
    Local Variables: None
    Algorithm: Return new card.
    Assistance Received: none
    ********************************************************************* */
    public Card makeMove(String cardChoice, int turnState, char trumpSuit, Card lead, BoolRef currentPlayer){
        return new Card();
    }

    /* *********************************************************************
    Name: meld
    Purpose: Placeholder function for players declaring a meld.
    Parameters:
                round
                trumpSuit
                ctx
    Return Value: None
    Local Variables: None
    Algorithm: None
    Assistance Received: none
    ********************************************************************* */
    public void meld(Round round, char trumpSuit, Context ctx){}

    /* *********************************************************************
    Name: pickOutMeldCandidates
    Purpose: To add a meld name to each card-candidate's existingMelds vector,
                and mark them as shared if necessary.
    Parameters: hand, meldName
    Return Value: meldList
    Local Variables: meldList, sharedFlag
    Algorithm: Check each card in hand to see if its meldCandidate flag is set,
                and if it is, add meldName to its existingMelds vector, and add that
                card's toString() value to meldList, which will be returned.
    Assistance Received: none
    ********************************************************************* */
    public Vector<String> pickOutMeldCandidates(Vector<Card> hand, String meldName){
        Vector<String> meldList = new Vector<String>(0);
        boolean sharedFlag = false;

        meldList.add(meldName);
        for (int i = 0; i < hand.size(); i++){

            if (hand.get(i).getMeldCandidate()){
                hand.get(i).addMeld(meldName);
                if (hand.get(i).howManyMelds() > 1){
                    hand.get(i).setShared(true);
                    sharedFlag = true;
                }
                meldList.add(hand.get(i).toString());
                hand.get(i).setMeldCandidate(false);

                if (sharedFlag){
                    hand.get(i).markAsShared(melds, hand.get(i).toString());
                    sharedFlag = false;
                }
            }
        }

        return meldList;
    }

    /* *********************************************************************
    Name: setCapturePile
    Purpose: To set a player's capture pile.
    Parameters: newPile
    Return Value: None
    Local Variables: None
    Algorithm: Set capturePile to newPile.
    Assistance Received: none
    ********************************************************************* */
    public void setCapturePile(Vector<Card> newPile){
        capturePile = newPile;
    }

    /* *********************************************************************
    Name: setHand
    Purpose: To set a player's hand.
    Parameters: newHand
    Return Value: None
    Local Variables: None
    Algorithm: Set hand to newHand.
    Assistance Received: none
    ********************************************************************* */
    public void setHand(Vector<Card> newHand){
        hand = newHand;
    }
}