public abstract class Customer {
    private int customerID;
    private String name;
    private String email;
    private String password;
    private String address;

    public Customer(int customerID, String name, String email, String password, String address) {
        this.customerID = customerID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public abstract void purchaseTicket(Game game, Seat seat);

    protected void simulatePurchase(Game game, Seat seat) {
        System.out.println("Ticket purchased by customer " + getName() + ":");
        System.out.println("Game: " + game.getGameID());
        System.out.println("Seat: " + seat.getSeatID());
    }
}
