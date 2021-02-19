package com.pinochle.alex.pinochle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pinochle.alex.pinochle.models.Game;

public class CoinFlipActivity extends AppCompatActivity {
    //New game object
    Game newGame = new Game();

    /* *********************************************************************
    Name: onCreate
    Purpose: To initialize CoinFlipActivity.
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
        setContentView(R.layout.activity_coin_flip);
    }

    /* *********************************************************************
    Name: sendCall
    Purpose: To send a coin toss to newGame.tossCoin().
    Parameters: view
    Return Value: None
    Local Variables: headsButton, tailsButton, startButton
    Algorithm: Have the player choose heads or tails, process the toss,
                then set startButton to visible while hiding headsButton
                and tailsButton.
    Assistance Received: none
    ********************************************************************* */
    public void sendCall(View view){
        switch(view.getId()){
            case R.id.heads:
                newGame.tossCoin('h');
                break;
            case R.id.tails:
                newGame.tossCoin('t');
                break;
        }
        printToss(view);

        Button headsButton = (Button)findViewById(R.id.heads),
            tailsButton = (Button)findViewById(R.id.tails),
            startButton = (Button)findViewById(R.id.startGame);
        headsButton.setVisibility(View.GONE);
        tailsButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
    }

    /* *********************************************************************
    Name: printToss
    Purpose: To show the results of the toss.
    Parameters:
                view
    Return Value: None
    Local Variables:
                coinResult, firstPlayer, results
    Algorithm: Get the results pair from newGame.tossCoin() and set them
                as the text for coinResult and firstPlayer respectively.
    Assistance Received: none
    ********************************************************************* */
    public void printToss(View view){
        TextView coinResult = (TextView)findViewById(R.id.coinResult),
            firstPlayer = (TextView)findViewById(R.id.firstPlayer);
        Pair<String, String> results = newGame.getCoinResults();
        coinResult.setText(results.first);
        firstPlayer.setText(results.second);
    }

    /* *********************************************************************
    Name: startGameActivity
    Purpose: To start MainGameActivity with the current newGame object.
    Parameters:
                view
    Return Value: None
    Local Variables: intent
    Algorithm: Set the loaded value in newGame to false, then start an intent
                for MainGameActivity with newGame passed as an extra.
    Assistance Received: none
    ********************************************************************* */
    public void startGameActivity(View view){
        newGame.setLoaded(false);
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra("GameObject", newGame);
        startActivity(intent);
    }
}
