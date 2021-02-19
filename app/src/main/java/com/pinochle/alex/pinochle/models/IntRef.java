package com.pinochle.alex.pinochle.models;

import java.io.Serializable;

public class IntRef implements Serializable {
    private int value;

    //Constructor
    public IntRef(int value){
        this.value = value;
    }

    /* *********************************************************************
    Name: add
    Purpose: To add a number to value.
    Parameters: value
    Return Value: None
    Local Variables: None
    Algorithm: Add value to this.value.
    Assistance Received: none
    ********************************************************************* */
    public void add(int value){
        this.value += value;
    }

    /* *********************************************************************
    Name: getVal
    Purpose: To return value.
    Parameters: None
    Return Value: value
    Local Variables: None
    Algorithm: Return value.
    Assistance Received: none
    ********************************************************************* */
    public int getVal(){
        return value;
    }

    /* *********************************************************************
    Name:setVal
    Purpose: To set value.
    Parameters: value
    Return Value: None
    Local Variables: None
    Algorithm: Set this.value to value.
    Assistance Received: none
    ********************************************************************* */
    public void setVal(int value){
        this.value = value;
    }
}