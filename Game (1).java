import java.util.Date;

public class Game {
    private int gameID;
    private String[] teamsPlaying;
    private Date date;
    private String venue;
    private String time;
    private int outfieldSeats;
    private int firstBaseSeats;
    private int thirdBaseSeats;

    public Game(int gameID, String[] teamsPlaying, Date date, String venue, String time, int outfieldSeats, int firstBaseSeats, int thirdBaseSeats) {
        this.gameID = gameID;
        this.teamsPlaying = teamsPlaying;
        this.date = date;
        this.venue = venue;
        this.time = time;
        this.outfieldSeats = outfieldSeats;
        this.firstBaseSeats = firstBaseSeats;
        this.thirdBaseSeats = thirdBaseSeats;
    }

    // Getters and setters
    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String[] getTeamsPlaying() {
        return teamsPlaying;
    }

    public void setTeamsPlaying(String[] teamsPlaying) {
        this.teamsPlaying = teamsPlaying;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getOutfieldSeats() {
        return outfieldSeats;
    }

    public void setOutfieldSeats(int outfieldSeats) {
        this.outfieldSeats = outfieldSeats;
    }

    public int getFirstBaseSeats() {
        return firstBaseSeats;
    }

    public void setFirstBaseSeats(int firstBaseSeats) {
        this.firstBaseSeats = firstBaseSeats;
    }

    public int getThirdBaseSeats() {
        return thirdBaseSeats;
    }

    public void setThirdBaseSeats(int thirdBaseSeats) {
        this.thirdBaseSeats = thirdBaseSeats;
    }

    // Method to subtract seats from the outfield section
    public void subtractOutfieldSeats(int numSeats) {
        outfieldSeats -= numSeats;
    }

    // Method to subtract seats from the first base section
    public void subtractFirstBaseSeats(int numSeats) {
        firstBaseSeats -= numSeats;
    }

    // Method to subtract seats from the third base section
    public void subtractThirdBaseSeats(int numSeats) {
        thirdBaseSeats -= numSeats;
    }
}
