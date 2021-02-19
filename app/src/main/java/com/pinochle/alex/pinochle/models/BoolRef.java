package com.pinochle.alex.pinochle.models;

import java.io.Serializable;

public class BoolRef implements Serializable {
    private boolean value;

    //Constructor
    public BoolRef(boolean value){
        this.value = value;
    }

    /* *********************************************************************
    Name: flipVal
    Purpose: To flip value.
    Parameters: None
    Return Value: None
    Local Variables: None
    Algorithm: Set value to !value.
    Assistance Received: none
    ********************************************************************* */
    public void flipVal(){
        this.value = !value;
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
    public boolean getVal(){
        return value;
    }

    /* *********************************************************************
    Name: setVal
    Purpose: To set value.
    Parameters: value
    Return Value: None
    Local Variables: None
    Algorithm: Set this.value to value.
    Assistance Received: none
    ********************************************************************* */
    public void setVal(boolean value){
        this.value = value;
    }
}
