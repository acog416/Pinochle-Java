package com.pinochle.alex.pinochle.models;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;

import com.pinochle.alex.pinochle.MainGameActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Human extends Player implements Serializable {

    /* *********************************************************************
    Name: applyMeld
    Purpose: To apply a meld after picking from a dialog box.
    Parameters:
                round
                pick
                options
                trumpSuit
    Return Value: None
    Local Variables:
                    points, option
                    pointsArr[]
                    pickStr
                    meldList
    Algorithm: Use parameters to set points and option. Then, use canMeld to reassign cards for the
                chosen meld and add the meld name to each chosen card's existingMelds. Then, add
                the meld to the player's meld vector.
    Assistance Received: none
    ********************************************************************* */
    public void applyMeld(Round round, int pick, List<String> options, char trumpSuit){
        int points, option;
        int[] pointsArr = {150, 40, 20, 10, 100, 80, 60, 40, 40};
        String pickStr;
        Vector<String> meldList = new Vector<String>(0);

        //Get name and points
        pickStr = options.get(pick);
        if (pickStr.equals("Flush")){ points = 150; option = 1; }
        else if (pickStr.equals("RoyalMarriage")){ points = 40; option = 2; }
        else if (pickStr.equals("Marriage")){ points = 20; option = 3; }
        else if (pickStr.equals("Dix")){ points = 10; option = 4; }
        else if (pickStr.equals("FourAces")){ points = 100; option = 5; }
        else if (pickStr.equals("FourKings")){ points = 80; option = 6; }
        else if (pickStr.equals("FourQueens")){ points = 60; option = 7; }
        else if (pickStr.equals("FourJacks")){ points = 40; option = 8; }
        else { points = 40; option = 9; }

        //Assign meld candidates
        canMeld(option, trumpSuit);

        //Pick out candidates
        meldList = pickOutMeldCandidates(hand, pickStr);

        //Add to score and meld vectors depending on the meld
        round.addToScore('h', points);
        MainGameActivity.log.add("You declared " + meldList.get(0) + " for " + points + " points!");

        //Add meld to player's meld vector
        if (meldList.size() != 0){
            melds.add(meldList);
        }
    }

    /* *********************************************************************
    Name: makeMove
    Purpose: To pick a card after choosing from the hand.
    Parameters:
                cardChoice
                turnState
                trumpSuit
                lead
                currentPlayer
    Return Value: choice
    Local Variables:
                    lowerCaseChoice, currentCard
                    choice
    Algorithm: Go through your hand and pick out the card you chose from the hand, while clearing
                any melds the card is associated with if necessary.
    Assistance Received: none
    ********************************************************************* */
    public Card makeMove(String cardChoice, int turnState, char trumpSuit, Card lead, BoolRef currentPlayer){
        //Note: The parameters aren't needed for the human player. They're there to achieve polymorphism.
        String lowerCaseChoice = cardChoice.toLowerCase(), currentCard;
        Card choice = new Card();

        //Go through your hand to find and remove your chosen card
        //See if I can turn this for loop into its own function
        for (int i = 0; i < hand.size(); i++) {

            //Create a lowercase copy for comparison
            currentCard = hand.get(i).toString().toLowerCase();

            //If it's a match, remove card from deck
            if (currentCard.equals(lowerCaseChoice)) {
                //Clear any melds the card is in
                if (hand.get(i).howManyMelds() > 0) {
                    hand.get(i).clearMelds(melds, hand);
                }

                choice = hand.get(i);
                hand.removeElementAt(i);
                break;
            }
        }

        MainGameActivity.log.add("You played " + choice.toString());

        return choice;
    }

    /* *********************************************************************
    Name: meld
    Purpose: To handle picking a meld through a dialog box.
    Parameters:
                round
                trumpSuit
                ctx
    Return Value: None
    Local Variables:
                    choiceDialog
                    pointsArr[]
                    bestMeld
                    names[]
                    options
    Algorithm: Go through each possible meld and add them to the options list. Then have the user pick
                the one they want and go through applyMeld() if they didn't hit cancel.
    Assistance Received: none
    ********************************************************************* */
    public void meld(final Round round, final char trumpSuit, Context ctx){
        AlertDialog.Builder choiceDialog = new AlertDialog.Builder(ctx);
        String[] names = {"Flush", "RoyalMarriage", "Marriage", "Dix", "FourAces", "FourKings", "FourQueens", "FourJacks", "Pinochle"};
        String bestMeld = "";
        int[] pointsArr = {150, 40, 20, 10, 100, 80, 60, 40, 40};
        int highestPoints = 0;
        List<String> options = new ArrayList<String>();

        //Find the best meld
        for (int i = 0; i < 9; i++){
            if (canMeld(i+1, trumpSuit)){
                options.add(names[i]);

                //Update bestMeld and highestPoints if necessary
                if (pointsArr[i] > highestPoints){
                    bestMeld = names[i];
                    highestPoints = pointsArr[i];
                }

                //Reset meldCandidate status after checks
                for (int j = 0; j < hand.size(); j++){
                    hand.get(j).setMeldCandidate(false);
                }
            }
        }

        //If there are options, build the dialog box and show it
        if (options.size() > 0){
            //Add help option
            options.add("Cancel");

            final List<String> optionsFinal = options;
            final int optionsSize = options.size();

            choiceDialog.setTitle("Choose a meld if you'd like. (Best pick is " + bestMeld
                    + " for " + highestPoints + " points)")

            .setItems(options.toArray(new CharSequence[options.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which < optionsSize-1){
                        //Apply the meld
                        applyMeld(round, which, optionsFinal, trumpSuit);

                    }
                    else{
                        //User tapped cancel
                        return;
                    }
                }
            });

            AlertDialog choiceDisplay = choiceDialog.create();
            choiceDialog.show();
        }


    }
}