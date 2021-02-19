package com.pinochle.alex.pinochle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pinochle.alex.pinochle.models.BoolRef;
import com.pinochle.alex.pinochle.models.Card;
import com.pinochle.alex.pinochle.models.Deck;
import com.pinochle.alex.pinochle.models.Game;
import com.pinochle.alex.pinochle.models.IntRef;
import com.pinochle.alex.pinochle.models.Player;
import com.pinochle.alex.pinochle.models.Round;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class MainGameActivity extends AppCompatActivity {

    //Game and round objects
    Game newGame;
    Round currentRound;

    //Deck of cards
    Deck deckOfCards;

    //Vectors for players and stock
    Vector<Player> players;
    Vector<Card> stock;

    //Boolean variables
    BoolRef currentPlayer = new BoolRef(false);
    boolean loaded, turnFinished;

    //Stat-holders
    int numRounds, turnCount, humanScore, compScore, playerVal;
    char trumpSuit;
    String trumpCard;
    IntRef totalHumanScore, totalCompScore;
    Card lead = new Card(), chase = new Card(), poppedFromDeck = new Card();

    //Log vector
    public static Vector<String> log = new Vector<String>(0);

    //Holds the state of the turn to indicate which code to execute
    int turnState = 0;

    //Indicator for creating a save file
    private static final int CREATE_FILE = 1;

    /*----------------------------------------------------------------------------------------------
    MAIN FUNCTIONALITY
    ----------------------------------------------------------------------------------------------*/


    /* *********************************************************************
    Name: onCreate
    Purpose: To initialize MainGameActivity.
    Parameters:
                savedInstanceState
    Return Value: None
    Local Variables:
                intent
    Algorithm: If a new game, initialize a new round and start. If loaded, load data into
                variables and resume game.
    Assistance Received: none
    ********************************************************************* */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        //Get the game and round objects
        Intent intent = getIntent();
        newGame = (Game)intent.getSerializableExtra("GameObject");

        //Initialize a new round or load data depending on if loading from a file
        loaded = newGame.isLoaded();
        if (!loaded){
            currentRound = new Round();
            initializeRound(newGame, currentRound);
        }
        else{
            currentRound = (Round)intent.getSerializableExtra("RoundObject");
            deckOfCards = newGame.getDeck();
            players = newGame.getPlayers();
            currentPlayer = newGame.getCurrentPlayer();
            playerVal = boolToInt(currentPlayer.getVal());
            humanScore = currentRound.getHumanScore();
            compScore = currentRound.getCompScore();
            totalHumanScore = newGame.getTotalHumanScore();
            totalCompScore = newGame.getTotalCompScore();
            trumpSuit = deckOfCards.getTrumpSuit();
            trumpCard = deckOfCards.trumpToString();
            turnCount = newGame.getTurnCount();
            numRounds = newGame.getNumRounds();
            stock = deckOfCards.getDeck();
            turnState = 0;

            //Clear log if needed
            if (log.size() > 0) { log.clear(); }
        }

        //Render the board
        renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                turnCount, turnState, turnFinished, deckOfCards, lead, chase);
    }

    /* *********************************************************************
    Name: boolToInt
    Purpose: To convert a boolean value to an int value.
    Parameters: b
    Return Value: 0 or 1
    Local Variables: None
    Algorithm: Return 0 if b is false, or 1 if b is true.
    Assistance Received: none
    ********************************************************************* */
    public int boolToInt(boolean b){
        if (b == false){
            return 0;
        }
        else{
            return 1;
        }
    }

    /* *********************************************************************
    Name: continueGame
    Purpose: Resume the game after tapping the continue button.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Update variables with current stats and render the board.
    Assistance Received: none
    ********************************************************************* */
    public void continueGame(){
        //Update stats
        getStats(newGame, currentRound);

        //Render board
        renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                turnCount, turnState, turnFinished, deckOfCards, lead, chase);
    }

    /* *********************************************************************
    Name: getStats
    Purpose: Update some variables to correspond with the current state from models.
    Parameters:
             game
             currentRound
    Return Value: None
    Local Variables:
             vars
    Algorithm: Get update variables from models.
    Assistance Received: none
    ********************************************************************* */
    public void getStats(Game game, Round currentRound){
        playerVal = boolToInt(currentPlayer.getVal());
        humanScore = currentRound.getHumanScore();
        compScore = currentRound.getCompScore();
        stock = deckOfCards.getDeck();
        trumpSuit = deckOfCards.getTrumpSuit();
        trumpCard = deckOfCards.trumpToString();
        turnCount = game.getTurnCount();
    }

    /* *********************************************************************
    Name: initializeRound
    Purpose: Set member variables in the game and round objects to initial values
                for a new round.
    Parameters:
             game, currentRound
    Return Value: None
    Local Variables:
             vars
    Algorithm: Set variables to start-game values, clear log if needed, shuffle and deal cards.
    Assistance Received: none
    ********************************************************************* */
    public void initializeRound(Game game, Round currentRound){
        //Set data
        deckOfCards = game.getDeck();
        deckOfCards.setTrumpPopped(false);
        players = game.getPlayers();
        currentPlayer = game.getCurrentPlayer();
        playerVal = boolToInt(currentPlayer.getVal());
        humanScore = currentRound.getHumanScore();
        compScore = currentRound.getCompScore();
        totalHumanScore = game.getTotalHumanScore();
        totalCompScore = game.getTotalCompScore();
        trumpSuit = deckOfCards.getTrumpSuit();
        trumpCard = deckOfCards.trumpToString();
        game.setTurnCount(1);
        turnCount = game.getTurnCount();

        //Clear log if needed
        if (log.size() > 0) { log.clear(); }

        //Increment round count and save to variable
        game.incrementRounds();
        numRounds = game.getNumRounds();

        //Refill deck if necessary
        deckOfCards.clearDeck();
        deckOfCards.fillCards();

        //Shuffle and deal cards
        deckOfCards.shuffleCards();
        deckOfCards.dealCards(players.get(0), players.get(1));
        stock = deckOfCards.getDeck();
    }

    /* *********************************************************************
    Name: isRoundOver
    Purpose: To determine if a round is over.
    Parameters: None
    Return Value: true/false
    Local Variables: None
    Algorithm: If both players' hands are empty, return true. Otherwise, return false.
    Assistance Received: none
    ********************************************************************* */
    public boolean isRoundOver(){
        if (players.get(0).getHand().size() == 0 && players.get(1).getHand().size() == 0){
            return true;
        }
        return false;
    }

    /* *********************************************************************
    Name: isTurnOver
    Purpose: To determine if a turn is over.
    Parameters: None
    Return Value: true/false
    Local Variables: None
    Algorithm: If turnState is 2, return true. Otherwise, return false.
    Assistance Received: none
    ********************************************************************* */
    public boolean isTurnOver(){
        if (turnState == 2){
            return true;
        }
        return false;
    }

    /* *********************************************************************
    Name: makeMove
    Purpose: To handle playing a card for both players.
    Parameters:
             cardStr
    Return Value: None
    Local Variables: None
    Algorithm:
               1) Use turnState to determine whether to set the player's pick to
                    lead or chase.
               2) If turn is over, go through end-turn procedures.
               3) If round is over, go through round-end procedures.
    Assistance Received: none
    ********************************************************************* */
    public void makeMove(String cardStr){
        //Choose the right path depending on turnState
        switch(turnState){
            case 0:
                lead = new Card(players.get(playerVal).makeMove(cardStr, turnState, trumpSuit, lead, currentPlayer));
                currentPlayer.flipVal();
                turnState++;
                break;
            case 1:
                chase = new Card(players.get(playerVal).makeMove(cardStr, turnState, trumpSuit, lead, currentPlayer));
                turnState++;
                break;
        }

        //If the turn or round is over, go to the appropriate function
        if (isTurnOver()){
            //Get current board
            turnFinished = true;
            getStats(newGame, currentRound);
            renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                    turnCount, turnState, turnFinished, deckOfCards, lead, chase);
            turnFinished = false;

            //Analyze cards and go through end-of-turn procedures
            currentRound.analyzeCards(lead, chase, deckOfCards, currentPlayer);

            //Update scores
            humanScore = currentRound.getHumanScore();
            compScore = currentRound.getCompScore();

            //Go through turn-end procedures
            turnEnded();

            //Clear listeners for continue button
            clearListeners();

            //Display continue button and re-render board
            final Button continue_button = findViewById(R.id.continue_button);
            continue_button.setVisibility(View.VISIBLE);
            continue_button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //Render the updated board
                    continue_button.setVisibility(View.INVISIBLE);
                    continue_button.setOnClickListener(null);
                    getStats(newGame, currentRound);
                    renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                            turnCount, turnState, turnFinished, deckOfCards, lead, chase);
                }
            });
        }
        else{
            //Update stats and re-render board.
            getStats(newGame, currentRound);
            renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                    turnCount, turnState, turnFinished, deckOfCards, lead, chase);
        }
        if (isRoundOver()){
            //Update scores
            humanScore = currentRound.getHumanScore();
            compScore = currentRound.getCompScore();

            //Render board
            renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                    turnCount, turnState, turnFinished, deckOfCards, lead, chase);

            //Remove continue_button from view and go through round-end procedures
            final Button continue_button = findViewById(R.id.continue_button);
            continue_button.setVisibility(View.INVISIBLE);
            continue_button.setOnClickListener(null);
            roundEnded();
        }



    }

    /*----------------------------------------------------------------------------------------------
    DIALOG BOXES
    ----------------------------------------------------------------------------------------------*/

    /* *********************************************************************
    Name: quitGame
    Purpose: To return to a prior activity from the game.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Use NavUtils to go to an upper-activity.
    Assistance Received: none
    ********************************************************************* */
    //Return to parent activity. This is its own function because I can't reference "this" in a
    //dialog event listener, and because it skips the dialog otherwise.
    public void quitGame(){
        NavUtils.navigateUpFromSameTask(this);
    }


    /* *********************************************************************
    Name: showHelp
    Purpose: To offer advice on which card to pick.
    Parameters: None
    Return Value: None
    Local Variables:
                playerPosition, dialog, helpDialog
    Algorithm: Get the player position (lead/chase), then get a card pick and strategy
                from Players.autoPick() and display in an AlertDialog
    Assistance Received: none
    ********************************************************************* */
    public void showHelp(){
        String playerPosition = turnState == 0 ? "lead" : "chase";
        //Get a strategy
        players.get(0).autoPick(playerPosition, trumpSuit, lead, currentPlayer.getVal());
        //Create an instance of the dialog fragment and show it
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(players.get(0).getStrategy());

        dialog.setPositiveButton(R.string.continue_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Continue
            }
        });

        //Show dialog
        AlertDialog helpDialog = dialog.create();
        helpDialog.show();
    }

    /* *********************************************************************
    Name: showStock
    Purpose: Display the remaining stock
    Parameters: None
    Return Value: None
    Local Variables:
                builder, scrollView, stockView, iv
    Algorithm: For every card in the stock, create and store an ImageView in a
                HorizontalScrollView and place in an AlertDialog.
    Assistance Received: none
    ********************************************************************* */
    public void showStock(){
        //Component variables
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout stockView = new LinearLayout(this);

        //For each card, create an ImageView and add to stockView
        for (Card currentCard : stock){
            ImageView iv = createCardImage(currentCard, false);
            iv.setMaxHeight(20);
            iv.setMaxWidth(10);
            stockView.addView(iv);
        }

        //Add stockView to scrollView and set scrollView as the AlertDialog's view
        scrollView.addView(stockView);
        builder.setTitle("Stock");
        builder.setView(scrollView);

        //Show dialog
        AlertDialog stockWindow = builder.create();
        stockWindow.show();
    }

    /* *********************************************************************
    Name: showTurnWinner
    Purpose: To show who won the turn in an AlertDialog
    Parameters: None
    Return Value: None
    Local Variables: dialog
    Algorithm: Set dialog's message and write to log depending on who won.
    Assistance Received: none
    ********************************************************************* */
    public void showTurnWinner(){
        //Create an instance of the dialog fragment and show it
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //Set message and log depending on who won
        if (!currentPlayer.getVal()){
            dialog.setMessage((R.string.human_won_turn));
            log.add("You won the turn!");
        }
        else{
            dialog.setMessage((R.string.comp_won_turn));
            log.add("COM won the turn!");
        }

        //onClickListener for continue button
        dialog.setPositiveButton(R.string.continue_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Continue
            }
        });

        //Show dialog
        AlertDialog winnerDialog = dialog.create();
        winnerDialog.show();
    }

    /* *********************************************************************
    Name: showRoundWinner
    Purpose: To show who won the turn in an AlertDialog
    Parameters: None
    Return Value: None
    Local Variables: winnerPrompt, dialog
    Algorithm: Depending on who won, set dialog's message and set currentPlayer
                based on results.
    Assistance Received: none
    ********************************************************************* */
    public void showRoundWinner(){
        String winnerPrompt;

        //Create an instance of AlertDialog and show it
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (humanScore > compScore){
            winnerPrompt = "You won the round with " + humanScore + " points! Do you want to start" +
                    " another round?";
            currentPlayer.setVal(false);
        }
        else{
            winnerPrompt = "Computer won the round with " + compScore + " points! Do you want to start" +
                    " another round?";
            currentPlayer.setVal(true);
        }
        dialog.setMessage(winnerPrompt);

        //If yes, start a new round
        dialog.setPositiveButton(R.string.yes_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Continue
                currentRound = new Round();
                initializeRound(newGame, currentRound);
                renderBoard(players, compScore, totalCompScore, humanScore, totalHumanScore, numRounds,
                        turnCount, turnState, turnFinished, deckOfCards, lead, chase);
            }
        });

        //Otherwise, display overall winner in an AlertDialog and quit
        dialog.setNegativeButton(R.string.no_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showOverallWinner();
            }
        });
        AlertDialog winnerDialog = dialog.create();
        winnerDialog.show();
    }

    /* *********************************************************************
    Name: showOverallWinner
    Purpose: To show who won the game in an AlertDialog
    Parameters: None
    Return Value: None
    Local Variables: dialog, winnerPrompt
    Algorithm: Depending on who won, set dialog's message and set currentPlayer
                based on results.
    Assistance Received: none
    ********************************************************************* */
    public void showOverallWinner(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String winnerPrompt;

        if (totalHumanScore.getVal() > totalCompScore.getVal()){
            winnerPrompt = "You won the game with " + totalHumanScore.getVal() + " points!";
            currentPlayer.setVal(false);
        }
        else{
            winnerPrompt = "Computer won the game with " + totalCompScore.getVal() + " points!";
            currentPlayer.setVal(true);
        }
        dialog.setMessage(winnerPrompt);

        dialog.setPositiveButton(R.string.continue_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Go back to main activity
                quitGame();
            }
        });

        AlertDialog winnerDialog = dialog.create();
        winnerDialog.show();
    }

    /*----------------------------------------------------------------------------------------------
    RENDERING
    ----------------------------------------------------------------------------------------------*/

    /* *********************************************************************
    Name: clearListeners
    Purpose: To remove event listeners for certain views.
    Parameters: None
    Return Value: None
    Local Variables:
                compHandLL
                compMeldLL
                humanHandLL
                humanMeldLL
                help_icon
                save_icon
    Algorithm: For each component, set their onClickListeners to null.
    Assistance Received: none
    ********************************************************************* */
    public void clearListeners(){
        //Clear card listeners
        LinearLayout compHandLL = (LinearLayout)findViewById(R.id.compHandLL);
        LinearLayout compMeldLL = (LinearLayout)findViewById(R.id.compMeldLL);
        LinearLayout humanHandLL = (LinearLayout)findViewById(R.id.humanHandLL);
        LinearLayout humanMeldLL = (LinearLayout)findViewById(R.id.humanMeldLL);
        ImageView help_icon = (ImageView)findViewById(R.id.help_icon);
        ImageView save_icon = (ImageView)findViewById(R.id.save_icon);

        for (int i = 0; i < compHandLL.getChildCount(); i++){
            ImageView iv = (ImageView)compHandLL.getChildAt(i);
            iv.setOnClickListener(null);
        }

        for (int i = 0; i < compMeldLL.getChildCount(); i++){
            ImageView iv = (ImageView)compMeldLL.getChildAt(i);
            iv.setOnClickListener(null);
        }

        for (int i = 0; i < humanHandLL.getChildCount(); i++){
            ImageView iv = (ImageView)humanHandLL.getChildAt(i);
            iv.setOnClickListener(null);
        }

        for (int i = 0; i < humanMeldLL.getChildCount(); i++){
            ImageView iv = (ImageView)humanMeldLL.getChildAt(i);
            iv.setOnClickListener(null);
        }

        help_icon.setOnClickListener(null);
        save_icon.setOnClickListener(null);
    }

    /* *********************************************************************
    Name: createCardImage
    Purpose: To create card images for hands and capture piles.
    Parameters:
                currentCard
                canTouch
    Return Value: cardImage
    Local Variables:
                cardImage
                resourceFile
    Algorithm: Create a new cardImage for currentCard, set the resource image with a listener
                if needed, and return cardImage.
    Assistance Received: none
    ********************************************************************* */
    public ImageView createCardImage(Card currentCard, boolean canTouch){
        ImageView cardImage = new ImageView(this);
        String resourceFile = currentCard.getResourceName();
        updateCardImage(cardImage, resourceFile);
        if (canTouch){ setCardListener(cardImage, resourceFile); }
        return cardImage;
    }

    /* *********************************************************************
    Name: createCardImage
    Purpose: To create card images for melds.
    Parameters:
                currentCard
                canTouch
    Return Value: cardImage
    Local Variables:
                cardImage
                resourceFile
    Algorithm: Create a new cardImage for currentCard, set the resource image with a listener
                if needed, and return cardImage.
    Assistance Received: none
    ********************************************************************* */
    public ImageView createCardImage(String currentCard, boolean canTouch){
        ImageView cardImage = new ImageView(this);
        StringBuilder resourceFile = new StringBuilder();

        //Create resourceFile
        resourceFile.append(Character.toLowerCase(currentCard.charAt(1)));
        resourceFile.append('_');
        resourceFile.append(Character.toLowerCase(currentCard.charAt(0)));

        //Set image and listener
        updateCardImage(cardImage, resourceFile.toString());
        if (canTouch){ setCardListener(cardImage, resourceFile.toString()); }

        return cardImage;
    }

    /* *********************************************************************
    Name: renderBoard
    Purpose: To render the board.
    Parameters:
                players
                compScore
                totalCompScore
                humanScore
                totalHumanScore
                numRounds
                turnCount
                turnState
                turnFinished
                deckOfCards
                lead
                chase
    Return Value: None
    Local Variables:
                resourceFile
                compHandLL
                compCaptureLL
                compMeldLL
                humanHandLL
                humanCaptureLL
                humanMeldLL
                leadSpace
                chaseSpace
                comp_scores
                human_scores
                round_count
                turn_count
                help_icon
                save_icon
                log_view
                current_player_label
                trump_space
                stock_button
    Algorithm: For each necessary component, update their listeners/visibility depending on the
                game's state.
    Assistance Received: none
    ********************************************************************* */
    //Render the game board
    public void renderBoard(Vector<Player> players, int compScore, IntRef totalCompScore, int humanScore,
                            IntRef totalHumanScore, int numRounds, int turnCount, int turnState,
                            boolean turnFinished, Deck deckOfCards, Card lead, Card chase){
        //String for resource files
        String resourceFile;

        //Update computer hand, capture pile, and melds
        HorizontalScrollView compHandView = findViewById(R.id.compHandView);
        compHandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMove("");
            }
        });
        LinearLayout compHandLL = (LinearLayout)findViewById(R.id.compHandLL);
        LinearLayout compCaptureLL = (LinearLayout)findViewById(R.id.compCaptureLL);
        LinearLayout compMeldLL = (LinearLayout)findViewById(R.id.compMeldLL);
        updateCompHandView(compHandLL, players.get(1).getHand());
        updateCaptureView(compCaptureLL, players.get(1).getCapturePile());
        updateCompMeldView(compMeldLL, players.get(1).getMelds());

        //Update human hand, capture pile, and melds
        LinearLayout humanHandLL = (LinearLayout)findViewById(R.id.humanHandLL);
        LinearLayout humanCaptureLL = (LinearLayout)findViewById(R.id.humanCaptureLL);
        LinearLayout humanMeldLL = (LinearLayout)findViewById(R.id.humanMeldLL);
        updateHumanHandView(humanHandLL, players.get(0).getHand());
        updateCaptureView(humanCaptureLL, players.get(0).getCapturePile());
        updateHumanMeldView(humanMeldLL, players.get(0).getMelds());

        //Update lead and chase cards if necessary
        ImageView leadSpace = (ImageView)findViewById(R.id.leadSpace);
        ImageView chaseSpace = (ImageView)findViewById(R.id.chaseSpace);
        if (turnState > 0){
            resourceFile = lead.getResourceName();
        }
        else{
            resourceFile = "empty_black";
        }
        updateCardImage(leadSpace, resourceFile);

        if (turnFinished){
            resourceFile = chase.getResourceName();
        }
        else{
            resourceFile = "empty_red";
        }
        updateCardImage(chaseSpace, resourceFile);

        //Update comp_scores
        TextView comp_scores = (TextView)findViewById(R.id.comp_scores);
        updateScores(comp_scores, totalCompScore, compScore);

        //Update human_scores
        TextView human_scores = (TextView)findViewById(R.id.human_scores);
        updateScores(human_scores, totalHumanScore, humanScore);

        //Update round_count and turn_count text
        TextView round_count = (TextView)findViewById(R.id.round_count);
        TextView turn_count = (TextView)findViewById(R.id.turn_count);
        round_count.setText(Integer.toString(numRounds));
        turn_count.setText(Integer.toString(turnCount));

        //Update current_player_label text
        TextView currentPlayerLabel = (TextView)findViewById(R.id.current_player_label);
        if (!currentPlayer.getVal()){
            currentPlayerLabel.setText(getResources().getString(R.string.human_up));
            findViewById(R.id.tap_notice_label).setVisibility(View.INVISIBLE);
        }
        else{
            currentPlayerLabel.setText(getResources().getString(R.string.computer_up));
            findViewById(R.id.tap_notice_label).setVisibility(View.VISIBLE);
        }

        //Update log view
        TextView log_view = (TextView)findViewById(R.id.log_view);

        //Clear log and print info
        log_view.setText("");
        log_view.setMovementMethod(new ScrollingMovementMethod());
        for (String note : log){
            log_view.append(note + "\n");
        }

        //Update trump_space
        ImageView trump_space = (ImageView)findViewById(R.id.trump_space);
        if (!deckOfCards.getTrumpPopped()){
            resourceFile = deckOfCards.getTrumpCard().getResourceName();

        }
        else{
            resourceFile = deckOfCards.getTrumpSuitResource();
        }
        updateCardImage(trump_space, resourceFile);



        //Set onClickListener for help_icon if human is playing
        ImageView help_icon = (ImageView)findViewById(R.id.help_icon);
        if (!currentPlayer.getVal()) {
            help_icon.setVisibility(View.VISIBLE);
            help_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHelp();
                }
            });
        }
        else{
            help_icon.setVisibility(View.INVISIBLE);
            help_icon.setOnClickListener(null);
        }

        //Set onClickListener for save_icon if it's the lead turn
        ImageView save_icon = (ImageView)findViewById(R.id.save_icon);
        if (turnState == 0){
            save_icon.setVisibility(View.VISIBLE);
            save_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveGame();
                }
            });
        }
        else{
            save_icon.setVisibility(View.INVISIBLE);
            save_icon.setOnClickListener(null);
        }

        //Set onClickListener for stock_button
        Button stock_button = (Button)findViewById(R.id.stock_button);
        stock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStock();
            }
        });
    }

    /* *********************************************************************
    Name: resourceToCardStr
    Purpose: To convert a resource name to a card's toString equivalent.
    Parameters: resource
    Return Value: cardStr.toString()
    Local Variables: cardStr
    Algorithm: Append the third and first characters from resource into
                cardStr and return cardStr.toString()
    Assistance Received: none
    ********************************************************************* */
    public String resourceToCardStr(String resource){
        StringBuilder cardStr = new StringBuilder();
        cardStr.append(resource.charAt(2));
        cardStr.append(resource.charAt(0));
        return cardStr.toString();
    }

    /* *********************************************************************
    Name: roundEnded
    Purpose: To add human and computer scores to their total score variables
             and show winner.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Add human and computer scores to their total score variables
                and call showRoundWinner.
    Assistance Received: none
    ********************************************************************* */
    public void roundEnded(){
        //Add to total human and computer scores
        totalHumanScore.add(humanScore);
        totalCompScore.add(compScore);

        //Pop up displaying winner with total points and option to play again
        showRoundWinner();
    }


    /* *********************************************************************
    Name: setCardListener
    Purpose: onClickListeners for card ImageViews.
    Parameters:
             iv
             resource
    Return Value: None
    Local Variables: None
    Algorithm: Set an onClickListener for an ImageView.
    Assistance Received: none
    ********************************************************************* */
    private void setCardListener(final ImageView iv, final String resource){
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardStr = resourceToCardStr(resource);
                makeMove(cardStr);
            }
        });
    }


    /* *********************************************************************
    Name: turnEnded
    Purpose: To handle end-of-turn procedures.
    Parameters: none
    Return Value: None
    Local Variables: None
    Algorithm: Show the winner, declare melds if possible,
                increment turnCount, and reset turnState
    Assistance Received: none
    ********************************************************************* */
    public void turnEnded(){
        playerVal = boolToInt(currentPlayer.getVal());

        //Winner adds cards to capture pile
        players.get(playerVal).addCardsToCapturePile(new Card(lead), new Card(chase));

        //Display winner pop-up
        showTurnWinner();

        //Go through melding process
        if (turnCount != 24){
            players.get(playerVal).meld(currentRound, trumpSuit, this);
        }

        if (!deckOfCards.getTrumpPopped()){
            //Winner picks first card from deck
            poppedFromDeck = deckOfCards.popCard();
            players.get(boolToInt(currentPlayer.getVal())).addCard(poppedFromDeck);

            //Loser picks second card from deck
            poppedFromDeck = deckOfCards.popCard();
            players.get(boolToInt(!currentPlayer.getVal())).addCard(poppedFromDeck);
        }

        //Reset lead and chase slots
        //lead.setToEmpty();
        //chase.setToEmpty();

        //Increment turnCount and reset turnState
        newGame.incrementTurnCount();
        turnState = 0;
    }

    /* *********************************************************************
    Name: updateCardImage
    Purpose: To update a card's image resource.
    Parameters:
                cardView, resourceFile
    Return Value: None
    Local Variables: resID
    Algorithm: Get resourceFile's resource ID equivalent and set that as the
                card's image resource.
    Assistance Received: none
    ********************************************************************* */
    public void updateCardImage(ImageView cardView, String resourceFile){
        int resID = getResources().getIdentifier(resourceFile, "drawable", getPackageName());
        cardView.setImageResource(resID);
    }

    /* *********************************************************************
    Name: updateScores
    Purpose: To update a player's score view.
    Parameters:
                scoreView
                totalScore
                currentScore
    Return Value: None
    Local Variables: scores
    Algorithm: Build a string using totalScore and currentScore and set that string
                as scoreView's text.
    Assistance Received: none
    ********************************************************************* */
    public void updateScores(TextView scoreView, IntRef totalScore, int currentScore){
        StringBuilder scores = new StringBuilder();
        scores.append(totalScore.getVal() + " : " + currentScore);
        scoreView.setText(scores);
    }

    /* *********************************************************************
    Name: updateCompHandView
    Purpose: To update the computer's hand view.
    Parameters:
                cardView, cardList
    Return Value: None
    Local Variables: canTouch
    Algorithm: Clear all views from cardView, and re-insert one view per card in hand.
    Assistance Received: none
    ********************************************************************* */
    public void updateCompHandView(LinearLayout cardView, Vector<Card> cardList){
        boolean canTouch = currentPlayer.getVal();
        System.out.print("Com ");

        //Clear all views
        cardView.removeAllViews();

        for (Card currentCard : cardList){
            if (currentCard.howManyMelds() > 0){ continue; }
            ImageView iv = createCardImage(currentCard, canTouch);
            cardView.addView(iv);
        }
    }

    /* *********************************************************************
    Name: updateHumanHandView
    Purpose: To update the human's hand view.
    Parameters:
                cardView, cardList
    Return Value: None
    Local Variables: canTouch
    Algorithm: Clear all views from cardView, and re-insert one view per card in hand.
    Assistance Received: none
    ********************************************************************* */
    public void updateHumanHandView(LinearLayout cardView, Vector<Card> cardList){
        boolean canTouch = !currentPlayer.getVal();
        System.out.print("Your ");

        //Clear all views
        cardView.removeAllViews();

        for (Card currentCard : cardList){
            if (currentCard.howManyMelds() > 0){ continue; }
            ImageView iv = createCardImage(currentCard, canTouch);
            cardView.addView(iv);
        }
    }

    /* *********************************************************************
    Name: updateCaptureView
    Purpose: To update the player's capture pile view.
    Parameters:
                cardView, cardList
    Return Value: None
    Local Variables: None
    Algorithm: Clear all views from cardView, and re-insert one view per card in capture pile.
    Assistance Received: none
    ********************************************************************* */
    public void updateCaptureView(LinearLayout cardView, Vector<Card> cardList){
        //Clear all views
        cardView.removeAllViews();

        for (Card currentCard : cardList){
            ImageView iv = createCardImage(currentCard, false);
            cardView.addView(iv);;
        }
    }

    /* *********************************************************************
    Name: updateCompMeldView
    Purpose: To update the comp player's meld view.
    Parameters:
                cardView, melds
    Return Value: None
    Local Variables: canTouch
    Algorithm: Clear all views from cardView, and re-insert one view per card in meld,
                while adding dividers if necessary.
    Assistance Received: none
    ********************************************************************* */
    public void updateCompMeldView(LinearLayout cardView, Vector<Vector<String>> melds){
        boolean canTouch = currentPlayer.getVal();

        //Clear all views
        cardView.removeAllViews();

        for (int i = 0; i < melds.size(); i++){
            for (int j = 1; j < melds.get(i).size(); j++){
                ImageView iv = createCardImage(melds.get(i).get(j), canTouch);
                cardView.addView(iv);
            }

            if (melds.size() > 1 && i+1 != melds.size()){
                ImageView divider = new ImageView(this);
                int resID = getResources().getIdentifier("divider", "drawable", getPackageName());
                divider.setImageResource(resID);
                cardView.addView(divider);
            }
        }
    }

    /* *********************************************************************
    Name: updateMeldView
    Purpose: To update the human player's meld view.
    Parameters:
                cardView, melds
    Return Value: None
    Local Variables: canTouch
    Algorithm: Clear all views from cardView, and re-insert one view per card in meld,
                while adding dividers if necessary.
    Assistance Received: none
    ********************************************************************* */
    public void updateHumanMeldView(LinearLayout cardView, Vector<Vector<String>> melds){
        boolean canTouch = !currentPlayer.getVal();

        //Clear all views
        cardView.removeAllViews();

        for (int i = 0; i < melds.size(); i++){
            for (int j = 1; j < melds.get(i).size(); j++){
                ImageView iv = createCardImage(melds.get(i).get(j), canTouch);
                cardView.addView(iv);
            }

            if (melds.size() > 1 && i+1 != melds.size()){
                ImageView divider = new ImageView(this);
                int resID = getResources().getIdentifier("divider", "drawable", getPackageName());
                divider.setImageResource(resID);
                cardView.addView(divider);
            }
        }
    }

    /*----------------------------------------------------------------------------------------------
    SERIALIZATION
    ----------------------------------------------------------------------------------------------*/

    /* *********************************************************************
    Name: onActivityResult
    Purpose: To handle saving to a chosen file.
    Parameters:
                requestCode, resultCode, resultData
    Return Value: None
    Local Variables:
                 uri, pfd, saveFile, successBox,
    Algorithm: Algorithm
    Assistance Received: none
    ********************************************************************* */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if (resultData != null){
                uri = resultData.getData();
                try{
                    ParcelFileDescriptor pfd = MainGameActivity.this.getContentResolver()
                            .openFileDescriptor(uri, "w");
                    FileOutputStream saveFile = new FileOutputStream(pfd.getFileDescriptor());
                    writeToFile(saveFile);

                    AlertDialog.Builder successBox = new AlertDialog.Builder(this);
                    successBox.setMessage("The game has been saved!");
                    successBox.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });

                    successBox.create().show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* *********************************************************************
    Name: saveGame
    Purpose: To save the game's state to a file
    Parameters: None
    Return Value: None
    Local Variables: saveIntent
    Algorithm: Create an intent to save a save file to the device's file system and run it.
    Assistance Received: none
    ********************************************************************* */
    public void saveGame(){
        Intent saveIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        saveIntent.addCategory(Intent.CATEGORY_OPENABLE);
        saveIntent.setType("text/plain");
        saveIntent.putExtra(Intent.EXTRA_TITLE, "newGame");

        startActivityForResult(saveIntent, CREATE_FILE);
    }

    /* *********************************************************************
    Name: writeToFile
    Purpose: To write the game's state to a text file.
    Parameters: saveFile
    Return Value: None
    Local Variables: cardList
    Algorithm: Go through the game's data values and write them to a text file in
                a certain order.
    Assistance Received: none
    ********************************************************************* */
    public void writeToFile(FileOutputStream saveFile){
        //Vector to hold a list of cards
        Vector<Card> cardList;

        try{

            //Write round number
            saveFile.write(("Round: " + numRounds + "\n\n").getBytes());

            //Write computer scores
            saveFile.write(("Computer:\n").getBytes());
            saveFile.write(("   Score: " + totalCompScore.getVal() + " / "
                + compScore + "\n").getBytes());

            //Write computer hand
            saveFile.write(("   Hand:").getBytes());
            cardList = players.get(1).getHand();
            for (Card card : cardList){
                if (card.howManyMelds() == 0){
                    saveFile.write((" " + card.toString()).getBytes());
                }
            }
            saveFile.write(("\n").getBytes());

            //Write computer capture pile
            saveFile.write(("   Capture Pile:").getBytes());
            cardList = players.get(1).getCapturePile();
            for (Card card : cardList){
                saveFile.write((" " + card.toString()).getBytes());
            }
            saveFile.write(("\n").getBytes());

            //Write computer melds
            Vector<Vector<String>> compMelds = players.get(1).getMelds();
            saveFile.write(("   Melds:").getBytes());
            if (compMelds.size() != 0){
                saveFile.write((" ").getBytes());

                //Iterate through each meld vector
                for (int i = 0; i < compMelds.size(); i++){

                    //Write meld cards
                    for (int j = 1; j < compMelds.get(i).size(); j++){
                        saveFile.write((compMelds.get(i).get(j)).getBytes());

                        //If appropriate, write a space
                        if (j != compMelds.get(i).size()-1){
                            saveFile.write((" ").getBytes());
                        }
                    }

                    //If appropriate, write a comma before a space
                    if (i + 1 != compMelds.size()){
                        saveFile.write((", ").getBytes());
                    }

                }
                saveFile.write((" ").getBytes());
            }
            saveFile.write(("\n\n").getBytes());

            //Write human scores
            saveFile.write(("Human:\n").getBytes());
            saveFile.write(("   Score: " + totalHumanScore.getVal() + " / "
                    + humanScore + "\n").getBytes());

            //Write human hand
            saveFile.write(("   Hand:").getBytes());
            cardList = players.get(0).getHand();
            for (Card card : cardList){
                if (card.howManyMelds() == 0){
                    saveFile.write((" " + card.toString()).getBytes());
                }
            }
            saveFile.write(("\n").getBytes());

            //Write human capture pile
            saveFile.write(("   Capture Pile:").getBytes());
            cardList = players.get(0).getCapturePile();
            for (Card card : cardList){
                saveFile.write((" " + card.toString()).getBytes());
            }
            saveFile.write(("\n").getBytes());

            //Write human melds
            Vector<Vector<String>> humanMelds = players.get(0).getMelds();
            saveFile.write(("   Melds:").getBytes());
            if (humanMelds.size() != 0){
                saveFile.write((" ").getBytes());

                //Iterate through each meld vector
                for (int i = 0; i < humanMelds.size(); i++){

                    //Write meld cards
                    for (int j = 1; j < humanMelds.get(i).size(); j++){
                        saveFile.write((humanMelds.get(i).get(j)).getBytes());

                        //If appropriate, write a space
                        if (j != humanMelds.get(i).size()-1){
                            saveFile.write((" ").getBytes());
                        }
                    }

                    //If appropriate, write a comma before a space
                    if (i + 1 != humanMelds.size()){
                        saveFile.write((", ").getBytes());
                    }

                }
                saveFile.write((" ").getBytes());
            }
            saveFile.write(("\n\n").getBytes());

            //Write trump card/suit
            saveFile.write(("Trump Card: ").getBytes());
            if (!deckOfCards.getTrumpPopped()){
                saveFile.write((deckOfCards.getTrumpCard().toString() + "\n").getBytes());
            }
            else{
                saveFile.write((deckOfCards.getTrumpSuit() + "\n").getBytes());
            }


            //Write stock
            saveFile.write(("Stock:").getBytes());
            for (Card card : deckOfCards.getDeck()){
                saveFile.write((" " + card.toString()).getBytes());
            }
            saveFile.write(("\n\n").getBytes());

            //Write next player
            saveFile.write(("Next Player: ").getBytes());
            if (currentPlayer.getVal() == false) { saveFile.write(("Human").getBytes()); }
            else { saveFile.write(("Computer").getBytes()); }

            saveFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}