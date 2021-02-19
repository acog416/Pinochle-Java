package com.pinochle.alex.pinochle.models;

import java.io.Serializable;
import java.util.Vector;

public class Card implements Serializable {
    //Private
    private String face; //Face
    private Vector<String> existingMelds = new Vector<String>(0); //Card's current melds
    private char suit; //Suit
    private int points, rank; //Points and rank
    private boolean meldCandidate, isShared; //Bools for if the card is a candidate for a meld
                                        //and whether or not it's present in two or more melds
    
    //Constructors
    public Card(String face, char suit, int points, int rank){
        this.face = face;
        this.suit = suit;
        this.points = points;
        this.rank = rank;
        this.meldCandidate = false;
        this.isShared = false;
    }

    public Card(Card card){
        this.face = card.getFace();
        this.suit = card.getSuit();
        this.points = card.getPoints();
        this.rank = card.getPoints();
        this.meldCandidate = card.getMeldCandidate();
        this.isShared = card.getShared();
    }

    public Card(){
        this.face = "";
        this.suit = 'n';
        this.points = 0;
        this.rank = 0;
        this.meldCandidate = false;
        this.isShared = false;
    }

    /* *********************************************************************
    Name: addMeld
    Purpose: To add a meld to existingMelds.
    Parameters: meld
    Return Value: None
    Local Variables: None
    Algorithm: Add meld to existingMelds.
    Assistance Received: none
    ********************************************************************* */
    public void addMeld(String meld){
        existingMelds.add(meld);
    }

    /* *********************************************************************
    Name: clearMelds
    Purpose: To clear the card and other cards in a meld from any association with that meld.
                (Continued in clearHandFromMeld() for cleanliness)
    Parameters: melds, hand
    Return Value: None
    Local Variables: None
    Algorithm: For each meld in existingMelds, go through player's melds and see if that card is
                in the current player meld. If it is, continue the process in clearHandFromMeld()
                and remove that meld from the player's melds.
    Assistance Received: none
    ********************************************************************* */
    public void clearMelds(Vector<Vector<String>> melds, Vector<Card> hand){
        for (int i = 0; i < existingMelds.size(); i++){
            
            for (int j = 0; j < melds.size(); j++){
                //The size check is here because rarely during C++ testing, the loop executed despite the condition
                //being false for unknown reasons, so this is just extra security.
                if (existingMelds.size() > 0 && melds.get(j).get(0).equals(existingMelds.get(i))) {
                    if (melds.get(j).contains(toString()) || melds.get(j).contains(toString().substring(0,2))) {
                        clearHandFromMeld(hand, melds.get(j), existingMelds.get(i));
                    }

                    melds.removeElementAt(j);
                }
            }

        }
    }

    /* *********************************************************************
    Name: clearHandFromMeld
    Purpose: To clear the card and other cards in a meld from any association with that meld.
                (Continued from clearHandFromMeld() for cleanliness)
    Parameters: hand, meldList, meld
    Return Value: None
    Local Variables: card
    Algorithm: Go through meldList and search for each card in the hand. If the current card is found,
                get its melds and remove the meld to be deleted from its existingMelds vector.
    Assistance Received: none
    ********************************************************************* */
    public void clearHandFromMeld(Vector<Card> hand, Vector<String> meldList, String meld){

        for (int i = 1; i < meldList.size(); i++){

            for (int j = 0; j < hand.size(); j++){

                String card = hand.get(j).toString();

                //Protection from asterisks messing things up
                String currentCardInMeld = meldList.get(i).length() == 3 ? meldList.get(i).substring(0,2)
                                            : meldList.get(i);

                //If card equals the current card in the meld we're deleting, get its melds and remove
                //the targeted meld from its existingMeldsList
                if (card.equals(currentCardInMeld)){
                    Vector<String> currentCardMelds = hand.get(j).getExistingMelds();

                    for (int k = 0; k < currentCardMelds.size(); k++){

                        if (currentCardMelds.get(k).equals(meld)){
                            currentCardMelds.removeElementAt(k);

                            if (currentCardMelds.size() == 1){
                                hand.get(j).setShared(false);
                            }
                            break;
                        }
                    }
                }
            }

        }
    }

    /* *********************************************************************
    Name: existsInMeld
    Purpose: To determine if a card exists in a certain meld.
    Parameters: meld
    Return Value: true/false
    Local Variables: None
    Algorithm: Return true if existingMelds contains meld, otherwise, return false.
    Assistance Received: none
    ********************************************************************* */
    public boolean existsInMeld(String meld){
        if (existingMelds.contains(meld)) return true;
        return false;
    }

    /* *********************************************************************
    Name: getFace
    Purpose: To return face.
    Parameters: None
    Return Value: face
    Local Variables: None
    Algorithm: Return face.
    Assistance Received: none
    ********************************************************************* */
    public String getFace(){
        return face;
    }

    /* *********************************************************************
    Name: getExistingMelds
    Purpose: To return existingMelds.
    Parameters: None
    Return Value: existingMelds
    Local Variables: None
    Algorithm: Return existingMelds.
    Assistance Received: none
    ********************************************************************* */
    public Vector<String> getExistingMelds(){
        return existingMelds;
    }

    /* *********************************************************************
    Name: getMeldCandidate
    Purpose: To return meldCandidate.
    Parameters: None
    Return Value: meldCandidate
    Local Variables: None
    Algorithm: Return meldCandidate.
    Assistance Received: none
    ********************************************************************* */
    public boolean getMeldCandidate(){
        return meldCandidate;
    }

    /* *********************************************************************
    Name: getPoints
    Purpose: To return points.
    Parameters: None
    Return Value: points
    Local Variables: None
    Algorithm: Return points.
    Assistance Received: none
    ********************************************************************* */
    public int getPoints(){
        return points;
    }

    /* *********************************************************************
    Name: getRank
    Purpose: To return rank.
    Parameters: None
    Return Value: rank
    Local Variables: None
    Algorithm: Return rank.
    Assistance Received: none
    ********************************************************************* */
    public int getRank(){
        return rank;
    }

    /* *********************************************************************
    Name: getResourceName
    Purpose: To return the resource name for the card.
    Parameters: None
    Return Value: file.toString()
    Local Variables: lowerSuit, lowerFace, file
    Algorithm: Build file and return its toString() value.
    Assistance Received: none
    ********************************************************************* */
    public String getResourceName() {
        char lowerSuit = Character.toLowerCase(suit);
        String lowerFace = face.toLowerCase();
        StringBuilder file = new StringBuilder();

        file.append(lowerSuit);
        file.append("_");
        file.append(lowerFace);

        return file.toString();
    }

    /* *********************************************************************
    Name: getShared
    Purpose: To return isShared.
    Parameters: None
    Return Value: isShared
    Local Variables: None
    Algorithm: Return isShared.
    Assistance Received: none
    ********************************************************************* */
    public boolean getShared(){
        return isShared;
    }

    /* *********************************************************************
    Name: getSuit
    Purpose: To return suit.
    Parameters: None
    Return Value: suit
    Local Variables: None
    Algorithm: Return suit.
    Assistance Received: none
    ********************************************************************* */
    public char getSuit(){
        return suit;
    }

    /* *********************************************************************
    Name: howManyMelds
    Purpose: To return how many melds a card exists in.
    Parameters: None
    Return Value: existingMelds.size()
    Local Variables: None
    Algorithm: Return existingMelds.size().
    Assistance Received: none
    ********************************************************************* */
    public int howManyMelds(){
        return existingMelds.size();
    }

    /* *********************************************************************
    Name: markAsShared
    Purpose: To mark a card as shared.
    Parameters: melds, card
    Return Value: None
    Local Variables: meldType
    Algorithm: Go through existingMelds and for each meld, go through player's melds
                and find that meld. If the card to be found is found, mark it with an asterisk.
    Assistance Received: none
    ********************************************************************* */
    public void markAsShared(Vector<Vector<String>> melds, String card){
        for (String meldType : existingMelds){
            
            for (int i = 0; i < melds.size(); i++){
                //If the meld name doesn't match the current meld, skip
                if (melds.get(i).get(0) != meldType) continue;

                //Search for the card and mark it
                for (int j = 1; j < melds.get(i).size(); j++){

                    /*If the card in the current vector matches the
                    card string, mark it*/
                    if (melds.get(i).get(j).equals(card)){
                        melds.get(i).set(j, melds.get(i).get(j).concat("*"));
                    }
                }
            }
        }
    }

    /* *********************************************************************
    Name: setMeldCandidate
    Purpose: To set meldCandidate.
    Parameters: flag
    Return Value: None
    Local Variables: None
    Algorithm: Set meldCandidate to flag.
    Assistance Received: none
    ********************************************************************* */
    public void setMeldCandidate(boolean flag){
        meldCandidate = flag;
    }

    /* *********************************************************************
    Name: setShared
    Purpose: To set isShared.
    Parameters: status
    Return Value: None
    Local Variables: None
    Algorithm: Set isShared to status.
    Assistance Received: none
    ********************************************************************* */
    void setShared(boolean status){
        isShared = status;
    }

    /* *********************************************************************
    Name: toString
    Purpose: To return a string representation of the card.
    Parameters: None
    Return Value: face + suit
    Local Variables: None
    Algorithm: Return face + suit.
    Assistance Received: none
    ********************************************************************* */
    public String toString(){
        return face + suit;
    }
}