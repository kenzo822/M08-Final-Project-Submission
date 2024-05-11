public class Seat {
    private int seatID;
    private String section;
    private int row;
    private int seatNumber;
    private double price;
    private boolean availabilityStatus;

    public Seat(int seatID, String section, int row, int seatNumber, double price, boolean availabilityStatus) {
        this.seatID = seatID;
        this.section = section;
        this.row = row;
        this.seatNumber = seatNumber;
        this.price = price;
        this.availabilityStatus = availabilityStatus;
    }

    public int getSeatID() {
        return seatID;
    }

    public String getSection() {
        return section;
    }

    public int getRow() {
        return row;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailabilityStatus() {
        return availabilityStatus;
    }
}