package com.pinochle.alex.pinochle.models;

import android.content.Context;

import com.pinochle.alex.pinochle.MainGameActivity;

import java.io.Serializable;
import java.util.Vector;

public class Computer extends Player implements Serializable {
    /* *********************************************************************
    Name: makeMove
    Purpose: To use AI to pick a card out of its hand.
    Parameters:
                cardChoice
                turnState
                trumpSuit
                lead
                currentPlayer
    Return Value: choice
    Local Variables: playerPosition, choice
    Algorithm: Use turnState to determine what playerPosition should be set to. Then, use
                autoPick() to get a card choice, pick it out of the hand, and return it.
    Assistance Received: none
    ********************************************************************* */
    public Card makeMove(String cardChoice, int turnState, char trumpSuit, Card lead, BoolRef currentPlayer){
        String playerPosition;

        if (turnState == 0){
            playerPosition = "lead";
        }
        else{
            playerPosition = "chase";
        }
        
        Card choice = autoPick(playerPosition, trumpSuit, lead, currentPlayer.getVal());

        //Remove card from hand
        for (int i = 0; i < hand.size(); i++){
            if (hand.get(i).toString().equals(choice.toString())){
                //Clear any melds the card is in
                if (hand.get(i).howManyMelds() > 0) {
                    hand.get(i).clearMelds(melds, hand);
                }
                hand.removeElementAt(i);
                break;
            }
        }

        MainGameActivity.log.add(getStrategy());
        return choice;
    }

    /* *********************************************************************
    Name: meld
    Purpose: To automatically determine the best possible meld to declare.
    Parameters:
                params
    Return Value: None
    Local Variables:
                    names[]
                    meldStr
                    points, winningChoice
                    meldsByRank[], pointsArr[]
                    meldFound
                    meldList
    Algorithm: Use canMeld to check every meld from best-ranking to worst-ranking
                until it either hits a possible meld or it checks every one with no possible melds.
                If a meld is possible, use canMeld again to set the flags again and then "pick the cards out,"
                followed by adding the meld to the player's meld vector.
    Assistance Received: none
    ********************************************************************* */
    public void meld(Round round, char trumpSuit, Context ctx){
        String[] names = { "Flush", "FourAces", "FourKings", "FourQueens", "RoyalMarriage", "FourJacks", "Pinochle", "Marriage", "Dix" };
        String meldStr = "";
        int points = 0, winningChoice = 0;
        int meldsByRank[] = { 1, 5, 6, 7, 2, 8, 9, 3, 4 }, pointsArr[] = { 150, 100, 80, 60, 40, 40, 40, 20, 10 };
        boolean meldFound = false;
        Vector<String> meldList = new Vector<String>(0);

        //Go through each meld using meldsByRank to see if any meld is possible
        for (int i = 0; i < 9; i++){
            if (canMeld(meldsByRank[i], trumpSuit)){
                meldStr = names[i];
                points = pointsArr[i];
                winningChoice = meldsByRank[i];
                meldFound = true;
                break;
            }
        }

        //Reset meldCandidate status after checks
        for (int i = 0; i < hand.size(); i++){
            if (hand.get(i).getMeldCandidate()){
                hand.get(i).setMeldCandidate(false);
            }
        }

        //If no meld found, return
        if (!meldFound){
            return;
        }

        //Set meld candidate flags for best meld
        canMeld(winningChoice, trumpSuit);

         //Add to score and meld vectors depending on the meld
         meldList = pickOutMeldCandidates(hand, meldStr);
         round.addToScore('c', points);
         MainGameActivity.log.add("COM declared " + meldList.get(0) + " for " + points + " points!");
 
         //Add meld to player's meld vector
         if (meldList.size() != 0){
             melds.add(meldList);
         }

        
    }
}