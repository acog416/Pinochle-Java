/************************************************************
 Name:  Alex Cogland
 Project 3:  Pinochle Java/Android
 Class:  CMPS 366 01 (OPL)
 Date:  12/09/2020
************************************************************/

package com.pinochle.alex.pinochle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.pinochle.alex.pinochle.models.Card;
import com.pinochle.alex.pinochle.models.Game;
import com.pinochle.alex.pinochle.models.IntRef;
import com.pinochle.alex.pinochle.models.Round;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Objects;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    //Indicator for opening a text file
    private static final int PICK_TXT_FILE = 1;

    /* *********************************************************************
    Name: onCreate
    Purpose: To initialize MainActivity.
    Parameters:
                savedInstanceState
    Return Value: None
    Local Variables:
                None
    Algorithm: Algorithm
    Assistance Received: none
    ********************************************************************* */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* *********************************************************************
    Name: launchCoinFlipActivity
    Purpose: To launch CoinFlipActivity.
    Parameters:
                view
    Return Value: None
    Local Variables:
                vars
    Algorithm: Algorithm
    Assistance Received: none
    ********************************************************************* */
    public void launchCoinFlipActivity(View view){
        Intent intent = new Intent(this, CoinFlipActivity.class);
        startActivity(intent);
    }

    /* *********************************************************************
    Name: getNextLine
    Purpose: To get the next non-empty line in a serialization file.
    Parameters:
                reader
    Return Value: line
    Local Variables:
                line
    Algorithm: Keep assigning reader.readLine() to line until it hits
                a value that isn't an empty string.
    Assistance Received: none
    ********************************************************************* */
    public String getNextLine(BufferedReader reader){
        String line = "";
        try{
            while ((line = reader.readLine()).equals("")){}
        } catch(IOException e){
            e.printStackTrace();
        }
        return line;
    }

    /* *********************************************************************
    Name: loadButton
    Purpose: Event handler for tapping the load button.
    Parameters:
                view
    Return Value: None
    Local Variables:
                openIntent
    Algorithm: Create an intent for opening a file from the device's file system
                and start it.
    Assistance Received: none
    ********************************************************************* */
    public void loadButton(View view){
        Intent openIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        openIntent.addCategory(Intent.CATEGORY_OPENABLE);
        openIntent.setType("text/plain");
        startActivityForResult(openIntent, PICK_TXT_FILE);
    }

    /*----------------------------------------------------------------------------------------------
    SERIALIZATION
    ----------------------------------------------------------------------------------------------*/

    /* *********************************************************************
    Name: loadDataIntoObjects
    Purpose: To load data from a serialization file into the game and round objects.
    Parameters:
                game
                currentRound
                reader
    Return Value: None
    Local Variables:
                line, stringData, trumpCard
                totalScore, currentScore, computerMeldData, humanMeldData
                list, capturePile
                delimiter, turnCount
                cardCount, trumpSuit, trumpPicked, invalid
    Algorithm: Read data line-by-line through the file and assign data correctly into
                the game and currentRound objects.
    Assistance Received: none
    ********************************************************************* */
    public void loadDataIntoObjects(Game game, Round currentRound, BufferedReader reader){
        String line, stringData, trumpCard;
        String totalScore, currentScore, computerMeldData = "", humanMeldData = "";
        Vector<Card> list, capturePile;
        int delimiter, turnCount;
        IntRef cardCount = new IntRef(0);
        char trumpSuit = '0';
        boolean trumpPicked = false;
        boolean invalid = false;

        //Get round
        line = getNextLine(reader);
        delimiter = line.indexOf(':');
        stringData = line.substring(delimiter+2);
        game.setRounds(Integer.valueOf(stringData));

        //Get player data
        for (int i = 0; i < 2; i++){
            line = getNextLine(reader);
            if (line.equals("Computer:")){
                //Computer Score
                line = getNextLine(reader);
                delimiter = line.indexOf(":");
                stringData = line.substring(delimiter + 2);
                delimiter = stringData.indexOf("/");
                totalScore = stringData.substring(0, delimiter - 1);
                game.setTotalCompScore(Integer.valueOf(totalScore));
                currentScore = stringData.substring(delimiter + 2);
                currentRound.setCompScore(Integer.valueOf(currentScore));

                //Computer Hand
                line = getNextLine(reader);
                delimiter = line.indexOf(":");
                stringData = line.substring(delimiter + 2);
                list = game.convertListToVector(stringData);
                cardCount.add(list.size());
                game.setHand(1, list);

                //Computer Capture Pile
                line = getNextLine(reader);
                if (!line.equals("   Capture Pile:")) {
                    delimiter = line.indexOf(":");
                    stringData = line.substring(delimiter + 2);
                    capturePile = game.convertListToVector(stringData);
                    cardCount.add(capturePile.size());
                    game.setCapturePile(1, capturePile);
                }

                //Computer Melds
                line = getNextLine(reader);
                if (!line.equals("   Melds:")) {
                    computerMeldData = line;
                }
            }
            else{
                //Human Score
                line = getNextLine(reader);
                delimiter = line.indexOf(":");
                stringData = line.substring(delimiter + 2);
                delimiter = stringData.indexOf("/");
                totalScore = stringData.substring(0, delimiter - 1);
                game.setTotalHumanScore(Integer.valueOf(totalScore));
                currentScore = stringData.substring(delimiter + 2);
                currentRound.setHumanScore(Integer.valueOf(currentScore));

                //Human Hand
                line = getNextLine(reader);
                delimiter = line.indexOf(":");
                stringData = line.substring(delimiter + 2);
                list = game.convertListToVector(stringData);
                cardCount.add(list.size());
                game.setHand(0, list);

                //Human Capture Pile
                line = getNextLine(reader);
                if (!line.equals("   Capture Pile:")) {
                    delimiter = line.indexOf(":");
                    stringData = line.substring(delimiter + 2);
                    capturePile = game.convertListToVector(stringData);
                    cardCount.add(capturePile.size());
                    game.setCapturePile(0, capturePile);
                }

                //Human Melds
                line = getNextLine(reader);
                if (!line.equals("   Melds:")) {
                    humanMeldData = line;
                }
            }
        }

        //Trump Card data
        line = getNextLine(reader);
        delimiter = line.indexOf(':');
        trumpCard = line.substring(delimiter+2);

        //Trump card hasn't been picked
        if (trumpCard.length() == 2){
            Card extractedCard = game.extractTrumpCard(trumpCard);
            game.setTrumpCard(extractedCard);
            trumpSuit = extractedCard.getSuit();
            game.setTrumpSuit(extractedCard.getSuit());
            cardCount.add(1);
        }

        //Trump card has been picked
        else if (trumpCard.length() == 1){

            if (trumpCard.equals("H") || trumpCard.equals("S") || trumpCard.equals("C") || trumpCard.equals("D")){
                trumpSuit = trumpCard.charAt(0);
                game.setTrumpCard(new Card("9", trumpSuit, 0, 0));
                game.setTrumpSuit(trumpSuit);
                game.setTrumpPopped(true);
            }
            else{
                invalid = true;
            }

        }

        //Trump card is invalid
        else{
            invalid = true;
        }

        //If invalid, reset activity
        if (invalid){
            System.out.println("Save file is invalid/corrupt. The trump card data has been corrupted.");
            finish();
            startActivity(getIntent());
        }

        //Load computer melds
        if (computerMeldData != "") {
            //Remove space at the end if necessary
            if (computerMeldData.charAt(computerMeldData.length()-1) == ' '){
                computerMeldData = computerMeldData.substring(0, computerMeldData.length()-1);
            }
            delimiter = computerMeldData.indexOf(":");
            stringData = computerMeldData.substring(delimiter + 2);
            game.loadMelds(1, stringData, trumpSuit);
            game.convertMeldsToCards(1, cardCount);
        }

        //Load human melds
        if (humanMeldData != "") {
            //Remove space at the end if necessary
            if (humanMeldData.charAt(humanMeldData.length()-1) == ' '){
                humanMeldData = humanMeldData.substring(0, humanMeldData.length()-1);
            }
            delimiter = humanMeldData.indexOf(":");
            stringData = humanMeldData.substring(delimiter + 2);
            game.loadMelds(0, stringData, trumpSuit);
            game.convertMeldsToCards(0, cardCount);
        }

        //Load Stock
        line = getNextLine(reader);
        if (!line.equals("Stock: ")) {
            delimiter = line.indexOf(":");
            stringData = line.substring(delimiter + 2);
            list = game.convertListToVector(stringData);
            cardCount.add(list.size());
            game.setDeck(list);
        }
        else{
            game.setDeck(new Vector<Card>(0));
        }

        //Current Player
        line = getNextLine(reader);
        delimiter = line.indexOf(":");
        stringData = line.substring(delimiter + 2);
        if (stringData.equals("Human")) { game.setCurrentPlayer(false); }

        else { game.setCurrentPlayer(true); }

        //Get turnCount
        int humanCaptureSize = game.getPlayers().get(0).getCapturePile().size(),
                compCaptureSize = game.getPlayers().get(1).getCapturePile().size();

        if (humanCaptureSize == 0 && compCaptureSize == 0){
            turnCount = 1;
        }
        else{
            turnCount = ((humanCaptureSize + compCaptureSize) / 2) + 1;
        }

        game.setTurnCount(turnCount);

        //Continue game if cardCount is at 48
        if (cardCount.getVal() != 48){
            System.out.println("Save file is invalid/corrupt. The amount of cards is incorrect.");
            finish();
            startActivity(getIntent());
        }

        System.out.println("Game loaded");
    }

    /* *********************************************************************
    Name: onActivityResult
    Purpose: To handle reading the chosen file and resuming the game.
    Parameters:
                requestCode, resultCode, resultData
    Return Value: None
    Local Variables:
                loadedGame, currentRound, uri, is, line, saveString,
                reader, resumeIntent
    Algorithm: Algorithm
    Assistance Received: none
    ********************************************************************* */
    @Override //For loading from a text file
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Game loadedGame = new Game();
        loadedGame.setLoaded(true);
        Round currentRound = new Round();

        if (requestCode == PICK_TXT_FILE){
            Uri uri;

            if (resultData != null){
                uri = resultData.getData();
                InputStream is = null;
                String line, saveString;

                try{
                    //Set up variables to read save file
                    is = getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            Objects.requireNonNull(is)));
                    loadDataIntoObjects(loadedGame, currentRound, reader);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }

                Intent resumeIntent = new Intent(this, MainGameActivity.class);
                resumeIntent.putExtra("GameObject", loadedGame);
                resumeIntent.putExtra("RoundObject", currentRound);
                startActivity(resumeIntent);
            }
        }
    }
}
