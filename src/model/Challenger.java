package model;

/**
 * Class State which stores all the data for the client.
 *
 * @author Wouter Folkertsma
 */
public class Challenger extends AbstractModel {
    private String challenger;
    private String challengeNumber;
    private String gameType;

    public String getChallenger() {
        return challenger;
    }

    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public String getChallengeNumber() {
        return challengeNumber;
    }

    public void setChallengeNumber(String challengeNumber) {
        this.challengeNumber = challengeNumber;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}
