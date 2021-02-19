package com.pinochle.alex.pinochle.models;

import android.view.View;
import android.widget.TextView;

import com.pinochle.alex.pinochle.R;

import java.io.Serializable;
import java.util.Vector;
import java.util.Scanner;

public class Round implements Serializable {
    private int humanScore, compScore; //Scores

    //Constructor
    public Round(){
        this.humanScore = 0;
        this.compScore = 0;
    }

    /* *********************************************************************
    Name: addToScore
    Purpose: To add to a player's score.
    Parameters: player, points
    Return Value: None
    Local Variables: None
    Algorithm: Use player to determine which score variable to add points to.
    Assistance Received: none
    ********************************************************************* */
    public void addToScore(char player, int points){
        if (player == 'h') {humanScore += points; }
        else { compScore += points; }
    }

    /* *********************************************************************
    Name: analyzeCards
    Purpose: To analyze lead and chase cards and declare a winner.
    Parameters: lead, chase, deckOfCards, currentPlayer
    Return Value: None
    Local Variables: None
    Algorithm: Use the game's rules to narrow down the winner.
    Assistance Received: none
    ********************************************************************* */
	public void analyzeCards(Card lead, Card chase, Deck deckOfCards,  BoolRef currentPlayer){
        //Trace down scenario to determine who gets points
        if (lead.getSuit() == deckOfCards.getTrumpSuit()) { //Lead card is of the trump suit

            //If the chase player has a bigger trump card, award points to chase player
            if ((chase.getSuit() == deckOfCards.getTrumpSuit()) && (chase.getRank() > lead.getRank())) {
                givePointsToChase(currentPlayer, lead, chase);
            }
            else {
                //Lead player gets points instead
                givePointsToLead(currentPlayer, lead, chase);
            }
        }
        else { //Lead card isn't of the trump suit

            //If the chase player has the same suit or a card of the trump suit, award points to chase player
            if ((chase.getSuit() == lead.getSuit() && (chase.getRank() > lead.getRank())) || (chase.getSuit() == deckOfCards.getTrumpSuit())) {
                givePointsToChase(currentPlayer, lead, chase);
            }
            else {

                //Lead player gets points instead
                givePointsToLead(currentPlayer, lead, chase);
                
            }
        }
    }

    /* *********************************************************************
    Name: givePointsToLead
    Purpose: To award points to the lead player
    Parameters: currentPlayer, lead, chase
    Return Value: None
    Local Variables: None
    Algorithm: Use currentPlayer to determine which player gets awarded points, and flip currentPlayer.
    Assistance Received: none
    ********************************************************************* */
	public void givePointsToLead(BoolRef currentPlayer, Card lead, Card chase){
        if (!currentPlayer.getVal() == false) { humanScore += (lead.getPoints() + chase.getPoints()); }
        else { compScore += (lead.getPoints() + chase.getPoints()); }
        currentPlayer.flipVal();
    }

    /* *********************************************************************
    Name: givePointsToChase
    Purpose: To award points to the chase player
    Parameters: currentPlayer, lead, chase
    Return Value: None
    Local Variables: None
    Algorithm: Use currentPlayer to determine which player gets awarded points.
    Assistance Received: none
    ********************************************************************* */
	public void givePointsToChase(BoolRef currentPlayer, Card lead, Card chase){
        if (currentPlayer.getVal() == false) { humanScore += (lead.getPoints() + chase.getPoints()); }
	    else { compScore += (lead.getPoints() + chase.getPoints()); }
    }

    /* *********************************************************************
    Name: getCompScore
    Purpose: To return compScore.
    Parameters: None
    Return Value: compScore
    Local Variables: None
    Algorithm: Return compScore.
    Assistance Received: none
    ********************************************************************* */
	public int getCompScore() { return compScore; }

    /* *********************************************************************
    Name: getHumanScore
    Purpose: To return humanScore.
    Parameters: None
    Return Value: humanScore
    Local Variables: None
    Algorithm: Return humanScore.
    Assistance Received: none
    ********************************************************************* */
    public int getHumanScore() { return humanScore; }

    /* *********************************************************************
    Name: setCompScore
    Purpose: To set compScore.
    Parameters: score
    Return Value: None
    Local Variables: None
    Algorithm: Set compScore to score.
    Assistance Received: none
    ********************************************************************* */
	public void setCompScore(int score) { compScore = score; }

    /* *********************************************************************
    Name: setHumanScore
    Purpose: To set humanScore.
    Parameters: score
    Return Value: None
    Local Variables: None
    Algorithm: Set humanScore to score.
    Assistance Received: none
    ********************************************************************* */
    public void setHumanScore(int score) { humanScore = score; }
}