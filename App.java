import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private boolean loggedIn = false;
    private String loggedInUsername; // Tracks logged-in user
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Map<String, Map<String, Integer>> gameTicketsMap = new HashMap<>(); // Tracks tickets sold per game
    private Map<String, String> registeredUsers = new HashMap<>();
    private ComboBox<String> gameComboBox = new ComboBox<>();
    private ComboBox<String> seatComboBox = new ComboBox<>();
    private ComboBox<Integer> ticketComboBox = new ComboBox<>();
    private TextArea ticketTextArea = new TextArea();
    private static final String USER_FILE_PATH = "registered_users.txt";
    private static final String TICKET_FILE_PATH = "purchased_tickets.txt";
    private static final String GAME_TICKETS_FILE_PATH = "game_tickets_tracking.txt";

    // Seat limits
    private static final int MAX_OUTFIELD_SEATS = 15000;
    private static final int MAX_FIRST_BASE_SEATS = 10000;
    private static final int MAX_THIRD_BASE_SEATS = 10000;

    // Ticket prices
    private static final int OUTFIELD_PRICE = 50;
    private static final int BASE_SIDE_PRICE = 100;

    @Override
    public void start(Stage primaryStage) {
        loadRegisteredUsers();
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        Scene scene = new Scene(root, 400, 400);

        displayLogin(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Baseball Ticket App");
        primaryStage.show();
    }

    private void displayLogin(VBox root) {
        Label titleLabel = new Label("Welcome to Baseball Ticket App");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.setOnAction(event -> login(root));
        registerButton.setOnAction(event -> register());

        root.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel, passwordField,
                loginButton, registerButton);
        root.setAlignment(Pos.CENTER);
    }

    private void login(VBox root) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (registeredUsers.containsKey(username) && registeredUsers.get(username).equals(password)) {
            loggedIn = true;
            loggedInUsername = username; // Set logged-in user
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, " + username + "!");
            usernameField.clear();
            passwordField.clear();
            root.getChildren().clear();
            displayTicketMenu(root);
            addViewTicketsButton(root);
            addLogoutButton(root);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    private void displayTicketMenu(VBox root) {
        Label titleLabel = new Label("Ticket Menu");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        gameComboBox.getItems().addAll(
                "Atlanta Braves vs Chicago Cubs - May 9, 2024, 3:30 PM",
                "Atlanta Braves vs Chicago Cubs - May 10, 2024, 3:30 PM",
                "Atlanta Braves vs Miami Marlins - May 11, 2024, 7:30 PM",
                "Atlanta Braves vs Miami Marlins - May 12, 2024, 7:30 PM",
                "Atlanta Braves vs Miami Marlins - May 13, 2024, 7:30 PM",
                "Atlanta Braves vs Houston Astros - May 14, 2024, 5:30 PM",
                "Atlanta Braves vs Houston Astros - May 15, 2024, 5:30 PM"
        );
        gameComboBox.setPromptText("Select a game");

        seatComboBox.getItems().addAll("Outfield - $50", "First Base Side - $100", "Third Base Side - $100");
        seatComboBox.setPromptText("Select seat area");

        ticketComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8);
        ticketComboBox.setPromptText("Select number of tickets");

        Button purchaseButton = new Button("Purchase Tickets");
        purchaseButton.setOnAction(event -> showTicketPrice());

        VBox ticketMenu = new VBox(10);
        ticketMenu.getChildren().addAll(titleLabel, gameComboBox, seatComboBox, ticketComboBox, purchaseButton);
        ticketMenu.setAlignment(Pos.CENTER);

        root.getChildren().add(ticketMenu);
    }

    private void addViewTicketsButton(VBox root) {
        Button viewTicketsButton = new Button("View Purchased Tickets");
        viewTicketsButton.setOnAction(event -> viewPurchasedTickets());

        root.getChildren().add(viewTicketsButton);
    }

    private void addLogoutButton(VBox root) {
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> logout(root));

        root.getChildren().add(logoutButton);
    }

    private void showTicketPrice() {
        String game = gameComboBox.getValue();
        String seat = seatComboBox.getValue();
        int tickets = ticketComboBox.getValue();

        if (seat == null || tickets == 0 || game == null) {
            showAlert(Alert.AlertType.ERROR, "Purchase Error", "Please select a game, seat, and number of tickets.");
            return;
        }

        int availableTickets = getAvailableTickets(game, seat);
        if (availableTickets < tickets) {
            showAlert(Alert.AlertType.ERROR, "Purchase Error", "Not enough tickets available for this game and seat.");
            return;
        }

        int totalCost = calculateTotalCost(seat, tickets);
        String confirmationMessage = "The price for " + tickets + " ticket(s) is $" + totalCost + ".\n\nDo you want to purchase the tickets?";
        showConfirmationDialog(confirmationMessage, totalCost);
    }

    private int calculateTotalCost(String seat, int tickets) {
        int pricePerTicket;
        if (seat.equals("Outfield - $50")) {
            pricePerTicket = OUTFIELD_PRICE;
        } else {
            pricePerTicket = BASE_SIDE_PRICE;
        }
        return tickets * pricePerTicket;
    }

    private void showConfirmationDialog(String message, int totalCost) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Purchase");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeYes) {
                purchaseTickets(totalCost);
            }
        });
    }

    private void purchaseTickets(int totalCost) {
        String game = gameComboBox.getValue();
        String seat = seatComboBox.getValue();
        int tickets = ticketComboBox.getValue();

        // Updates the sold tickets count for the selected game and seat area
        updateSoldTickets(game, seat, tickets);

        // Saves purchased tickets with the associated username
        String purchasedTicket = loggedInUsername + "," + game + "," + seat + "," + tickets;
        savePurchasedTicket(purchasedTicket);

        showAlert(Alert.AlertType.INFORMATION, "Ticket Purchase", "Tickets successfully purchased for a total of $" + totalCost + "!");
    }

    private void updateSoldTickets(String game, String seat, int tickets) {
        Map<String, Integer> ticketsSold = gameTicketsMap.getOrDefault(game, new HashMap<>());
        int currentSoldTickets = ticketsSold.getOrDefault(seat, 0);
        ticketsSold.put(seat, currentSoldTickets + tickets);
        gameTicketsMap.put(game, ticketsSold);
        saveGameTicketsTracking(); // Save tracking data after updating
    }

    private int getAvailableTickets(String game, String seat) {
        int totalTickets;
        switch (seat) {
            case "Outfield - $50":
                totalTickets = MAX_OUTFIELD_SEATS;
                break;
            case "First Base Side - $100":
                totalTickets = MAX_FIRST_BASE_SEATS;
                break;
            case "Third Base Side - $100":
                totalTickets = MAX_THIRD_BASE_SEATS;
                break;
            default:
                totalTickets = 0;
        }

        Map<String, Integer> ticketsSold = gameTicketsMap.getOrDefault(game, new HashMap<>());
        int soldTickets = ticketsSold.getOrDefault(seat, 0);
        return totalTickets - soldTickets;
    }

    private void viewPurchasedTickets() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TICKET_FILE_PATH))) {
            StringBuilder tickets = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(loggedInUsername)) {
                    String[] gameParts = parts[1].split(" - ");
                    String gameTitle = gameParts[0];
                    String dateAndYear = gameParts[1];
                    tickets.append("Game: ").append(gameTitle).append("\n")
                            .append("Date: ").append(dateAndYear).append(" ").append(parts[2]).append("\n")
                            .append("Time: ").append(parts[3]).append("\n")
                            .append("Seat: ").append(parts[4]).append("\n")
                            .append("Number of Tickets: ").append(parts[5]).append("\n\n");
                }
            }
            if (tickets.length() == 0) {
                showAlert(Alert.AlertType.INFORMATION, "No Tickets", "You have not purchased any tickets yet.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Purchased Tickets", tickets.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout(VBox root) {
        loggedIn = false;
        root.getChildren().clear();
        displayLogin(root);
    }

    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (!isValidUsername(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Invalid username.");
        } else if (password.length() < 5) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Password must be at least 5 characters.");
        } else if (registeredUsers.containsKey(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists. Please choose another username.");
        } else {
            registeredUsers.put(username, password);
            saveRegisteredUsers();
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "You have successfully registered!");
            usernameField.clear();
            passwordField.clear();
        }
    }

    private boolean isValidUsername(String username) {
        return username.length() >= 4;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadRegisteredUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                registeredUsers.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRegisteredUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
            for (Map.Entry<String, String> entry : registeredUsers.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePurchasedTicket(String purchasedTicket) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TICKET_FILE_PATH, true))) {
            writer.write(purchasedTicket);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveGameTicketsTracking() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GAME_TICKETS_FILE_PATH))) {
            // Loop through a list of all games
            for (String game : gameComboBox.getItems()) {
                Map<String, Integer> ticketsSold = gameTicketsMap.getOrDefault(game, new HashMap<>());
                int outfieldTicketsSold = ticketsSold.getOrDefault("Outfield - $50", 0);
                int firstBaseTicketsSold = ticketsSold.getOrDefault("First Base Side - $100", 0);
                int thirdBaseTicketsSold = ticketsSold.getOrDefault("Third Base Side - $100", 0);

                int outfieldTicketsLeft = MAX_OUTFIELD_SEATS - outfieldTicketsSold;
                int firstBaseTicketsLeft = MAX_FIRST_BASE_SEATS - firstBaseTicketsSold;
                int thirdBaseTicketsLeft = MAX_THIRD_BASE_SEATS - thirdBaseTicketsSold;

                writer.write("Game: " + game + "\n");
                writer.write("Outfield Tickets Left: " + outfieldTicketsLeft + "\n");
                writer.write("First Base Side Tickets Left: " + firstBaseTicketsLeft + "\n");
                writer.write("Third Base Side Tickets Left: " + thirdBaseTicketsLeft + "\n");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
