package model;


/**
 * Class State which stores all the data for the client.
 *
 * @author Wouter Folkertsma
 */
public class State extends AbstractModel {
    private String state;

    public void setState(String newState){
        state = newState;
    }

    public String getState(){
        return state;
    }
}


