//javac -cp "libs\mysql-connector-j-9.5.0.jar;libs\javafx.base.jar;libs\javafx.controls.jar;libs\javafx.graphics.jar" -d out management\*.java

//java --module-path libs --add-modules javafx.controls,javafx.graphics,javafx.base -cp "out;libs\mysql-connector-j-9.5.0.jar" RydexFx

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.collections.*;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RydexFx extends Application {

    // Colors & Fonts
    private static final Color PRIMARY_COLOR = Color.rgb(0x6A, 0x1B, 0x9A);
    private static final Color PRIMARY_LIGHT = Color.rgb(0x9C, 0x4D, 0xCC);
    private static final Color PRIMARY_DARK = Color.rgb(0x4A, 0x14, 0x8C);
    private static final Color BACKGROUND_COLOR = Color.rgb(0xFA, 0xFA, 0xFB);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = PRIMARY_DARK;
    private static final Color TEXT_SECONDARY = PRIMARY_COLOR;

    private static final Font FONT_TITLE = Font.font("Segoe UI", FontWeight.BOLD, 30);
    private static final Font FONT_HEADER = Font.font("Segoe UI", FontWeight.BOLD, 22);
    private static final Font FONT_SUBHEADER = Font.font("Segoe UI", FontWeight.BOLD, 16);
    private static final Font FONT_BODY = Font.font("Segoe UI", FontWeight.NORMAL, 13);
    private static final Font FONT_SMALL = Font.font("Segoe UI", FontWeight.NORMAL, 12);
    private static final Font FONT_BUTTON = Font.font("Segoe UI", FontWeight.BOLD, 13);

    private StackPane mainContainer;
    private Map<String, Pane> namedCards = new HashMap<>();
    private Stage primaryStage;

    // DAOs
    private UserDAO userDAO = new UserDAO();
    private RideDAO rideDAO = new RideDAO();
    private RouteDAO routeDAO = new RouteDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    private DriverShiftDAO shiftDAO = new DriverShiftDAO();
    private RideAssistantDAO assistantDAO = new RideAssistantDAO();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Rydex");

        mainContainer = new StackPane();
        mainContainer.setStyle("-fx-background-color: #FAFAFB;");

        Pane welcome = createWelcomePanel();
        Pane loading = createLoadingPanel();

        namedCards.put("WELCOME", welcome);
        namedCards.put("LOADING", loading);

        mainContainer.getChildren().addAll(welcome, loading);
        welcome.setVisible(true);
        loading.setVisible(false);

        Scene scene = new Scene(mainContainer, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            DatabaseConfig.closeConnection();
            Platform.exit();
        });

        testDatabaseConnection();
        primaryStage.show();
    }

    // ----------------- Base UI (Welcome + Loading + helpers) -----------------

    private Pane createWelcomePanel() {
        BorderPane panel = new BorderPane();
        panel.setStyle("-fx-background-color: #FAFAFB;");

        Pane header = createGradientHeader("Rydex", "Your reliable campus transportation partner");

        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(30, 24, 30, 24));
        center.setStyle("-fx-background-color: #FAFAFB;");

        Pane featureCards = createFeatureCards();

        HBox buttonRow = new HBox(28);
        buttonRow.setAlignment(Pos.CENTER);

        VBox leftButtons = createButtonGroup("Driver",
                new String[] { "Register as Driver", "Login as Driver" },
                new Runnable[] { () -> showRegistrationForm("Driver"), () -> showLoginForm("Driver") },
                PRIMARY_COLOR);

        VBox rightButtons = createButtonGroup("Rider",
                new String[] { "Register as Rider", "Login as Rider" },
                new Runnable[] { () -> showRegistrationForm("Rider"), () -> showLoginForm("Rider") },
                PRIMARY_LIGHT);

        buttonRow.getChildren().addAll(leftButtons, rightButtons);

        center.getChildren().addAll(featureCards, buttonRow);

        panel.setTop(header);
        panel.setCenter(center);
        panel.setBottom(createFooter());

        return panel;
    }

    private Pane createGradientHeader(String title, String subtitle) {
        StackPane header = new StackPane();
        header.setPrefHeight(120);
        header.setMinHeight(120);
        header.setMaxHeight(120);
        header.setPadding(new Insets(18, 28, 18, 28));

        // Gradient background
        Rectangle gradient = new Rectangle();
        gradient.setManaged(false);
        gradient.widthProperty().bind(header.widthProperty());
        gradient.heightProperty().bind(header.heightProperty());
        LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_DARK), new Stop(1, PRIMARY_LIGHT));
        gradient.setFill(lg);

        VBox textBox = new VBox(5);
        textBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setTextFill(Color.WHITE);

        Label subLbl = new Label(subtitle);
        subLbl.setFont(FONT_BODY);
        subLbl.setTextFill(Color.rgb(255, 255, 255, 0.8));

        textBox.getChildren().addAll(titleLbl, subLbl);

        header.getChildren().addAll(gradient, textBox);
        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);

        return header;
    }

    private Pane createFeatureCards() {
        HBox panel = new HBox(18);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10));

        panel.getChildren().addAll(
                createFeatureCard("Quick Rides", "Get to classes on time with reliable campus drivers"),
                createFeatureCard("Affordable", "Student-friendly pricing and flexible payments"),
                createFeatureCard("Rate Drivers", "Share feedback to improve service"));

        return panel;
    }

    private Pane createFeatureCard(String title, String desc) {
        VBox card = createRoundedCard(12);
        card.setPadding(new Insets(16));
        card.setSpacing(8);
        card.setPrefWidth(280);

        Label t = new Label(title);
        t.setFont(FONT_SUBHEADER);
        t.setTextFill(PRIMARY_COLOR);
        t.setMaxWidth(Double.MAX_VALUE);
        t.setAlignment(Pos.CENTER);

        Label d = new Label(desc);
        d.setFont(FONT_BODY);
        d.setWrapText(true);
        d.setAlignment(Pos.CENTER);

        card.getChildren().addAll(t, d);
        return card;
    }

    private VBox createButtonGroup(String title, String[] buttonTexts, Runnable[] actions, Color color) {
        VBox p = new VBox(12);
        p.setAlignment(Pos.CENTER);
        p.setPadding(new Insets(8));

        Label lbl = new Label(title);
        lbl.setFont(FONT_SUBHEADER);
        lbl.setTextFill(TEXT_PRIMARY);

        VBox btns = new VBox(10);
        btns.setAlignment(Pos.CENTER);

        for (int i = 0; i < buttonTexts.length; i++) {
            Button b = createModernButton(buttonTexts[i], color);
            final Runnable action = actions[i];
            b.setOnAction(e -> action.run());
            btns.getChildren().add(b);
        }

        p.getChildren().addAll(lbl, btns);
        return p;
    }

    private Pane createFooter() {
        BorderPane footer = new BorderPane();
        footer.setStyle("-fx-background-color: #4A148C;");
        footer.setPadding(new Insets(10, 18, 10, 18));
        footer.setPrefHeight(52);

        Label c = new Label("© 2025 Rydex - Made by Jawad Ahmad");
        c.setFont(FONT_BODY);
        c.setTextFill(Color.rgb(255, 255, 255, 0.7));

        Button exit = createModernButton("Exit", PRIMARY_DARK);
        exit.setPrefSize(90, 36);
        exit.setOnAction(e -> {
            DatabaseConfig.closeConnection();
            Platform.exit();
        });

        footer.setLeft(c);
        footer.setRight(exit);
        return footer;
    }

    private Pane createLoadingPanel() {
        BorderPane panel = new BorderPane();
        panel.setStyle("-fx-background-color: #FAFAFB;");

        VBox center = new VBox(8);
        center.setAlignment(Pos.CENTER);

        Label loading = new Label("Connecting to Database...");
        loading.setFont(FONT_HEADER);
        loading.setTextFill(TEXT_PRIMARY);

        ProgressBar pb = new ProgressBar();
        pb.setPrefWidth(320);
        pb.setPrefHeight(18);

        center.getChildren().addAll(loading, pb);
        panel.setCenter(center);

        return panel;
    }

    private void testDatabaseConnection() {
        showLoadingScreen();
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                try {
                    Class<?> cfg = Class.forName("DatabaseConfig");
                    try {
                        java.lang.reflect.Method testM = cfg.getMethod("testConnection");
                        Object res = testM.invoke(null);
                        if (res instanceof Boolean)
                            return (Boolean) res;
                    } catch (NoSuchMethodException ns) {
                        try {
                            java.lang.reflect.Method getConn = cfg.getMethod("getConnection");
                            Object conn = getConn.invoke(null);
                            return conn != null;
                        } catch (NoSuchMethodException ns2) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void succeeded() {
                boolean ok = getValue();
                if (!ok)
                    showDatabaseError();
                else
                    showWelcomeScreen();
            }

            @Override
            protected void failed() {
                showDatabaseError();
            }
        };
        new Thread(task).start();
    }

    private void showLoadingScreen() {
        showCard("LOADING");
    }

    private void showWelcomeScreen() {
        showCard("WELCOME");
    }

    private void showCard(String cardName) {
        Platform.runLater(() -> {
            System.out.println("[UI] Switching to card: " + cardName);
            System.out
                    .println("[UI] mainContainer size: " + mainContainer.getWidth() + "x" + mainContainer.getHeight());
            for (Map.Entry<String, Pane> entry : namedCards.entrySet()) {
                boolean visible = entry.getKey().equals(cardName);
                Pane p = entry.getValue();
                p.setVisible(visible);

                System.out.println("[UI] Card '" + entry.getKey() + "' visible=" + visible + " class="
                        + p.getClass().getSimpleName() + " style=" + p.getStyle() + " bounds=" + p.getBoundsInParent());

                if (visible) {
                    p.toFront();
                    // add a visible debug border so we can see which pane is active
                    String existing = p.getStyle() == null ? "" : p.getStyle();
                    p.setStyle(existing + "; -fx-border-color: rgba(255,255,255,1); -fx-border-width: 4;");
                } else {
                    // remove debug border for non-visible panes
                    String s = p.getStyle();
                    if (s != null) {
                        s = s.replaceAll("-fx-border-color:[^;]+;", "").replaceAll("-fx-border-width:[^;]+;", "");
                        p.setStyle(s);
                    }
                }
            }
        });
    }

    private void showDatabaseError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Database Connection Failed");
            alert.setContentText("Please check your DB and DatabaseConfig. java");
            alert.showAndWait();
            showWelcomeScreen();
        });
    }

    private Button createModernButton(String text, Color color) {
        Button button = new Button(text);
        button.setFont(FONT_BUTTON);
        button.setTextFill(Color.WHITE);
        button.setPrefWidth(340);
        button.setPrefHeight(48);

        String colorHex = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        button.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            Color lighter = color.brighter();
            String lighterHex = String.format("#%02X%02X%02X",
                    (int) (lighter.getRed() * 255),
                    (int) (lighter.getGreen() * 255),
                    (int) (lighter.getBlue() * 255));
            button.setStyle(
                    "-fx-background-color: " + lighterHex + ";" +
                            "-fx-background-radius: 16;" +
                            "-fx-cursor:  hand;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + colorHex + ";" +
                            "-fx-background-radius: 16;" +
                            "-fx-cursor: hand;");
        });

        return button;
    }

    private Button createLargePrimaryButton(String text, Color color) {
        Button btn = createModernButton(text, color);
        btn.setPrefSize(340, 48);
        return btn;
    }

    private VBox createStatCard(String title, String value, Color accent) {
        VBox card = createRoundedCard(12);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setSpacing(8);

        Label t = new Label(title);
        t.setFont(FONT_SUBHEADER);
        t.setTextFill(accent);

        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        v.setTextFill(TEXT_PRIMARY);

        card.getChildren().addAll(t, v);
        return card;
    }

    private VBox createRoundedCard(int radius) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: " + radius + ";" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        return card;
    }

    // ----------------- Registration & Login -----------------

    private void showRegistrationForm(String userType) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Register as " + userType);
        dialog.initOwner(primaryStage);

        VBox content = new VBox(12);
        content.setPadding(new Insets(18));
        content.setStyle("-fx-background-color: #FAFAFB;");

        Label header = new Label("Create " + userType + " Account");
        header.setFont(FONT_HEADER);
        header.setTextFill(PRIMARY_COLOR);

        VBox form = new VBox(8);
        form.setStyle(
                "-fx-background-color: white; -fx-border-color: rgba(0,0,0,0.3); -fx-border-radius: 5; -fx-background-radius: 5;");
        form.setPadding(new Insets(8));

        TextField nameField = createStyledTextField();
        TextField emailField = createStyledTextField();
        PasswordField passwordField = createStyledPasswordField();

        form.getChildren().addAll(
                createFormRow("Full Name", nameField),
                createFormRow("Email", emailField),
                createFormRow("Password", passwordField));

        TextField licenseField = null;
        if (userType.equals("Driver")) {
            licenseField = createStyledTextField();
            form.getChildren().add(createFormRow("License Number", licenseField));
        }

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        Button registerBtn = createModernButton("Register", PRIMARY_LIGHT);
        registerBtn.setPrefWidth(150);
        Button cancelBtn = createModernButton("Cancel", PRIMARY_DARK);
        cancelBtn.setPrefWidth(150);

        TextField finalLicenseField = licenseField;
        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showMessage("Error", "All fields are required", PRIMARY_DARK);
                return;
            }
            if (!isValidEmail(email)) {
                showMessage("Invalid Email", "Please enter a valid email address (e.g.  user@example.com)",
                        PRIMARY_DARK);
                return;
            }
            if (!isValidPassword(password)) {
                showMessage("Weak Password", "Password must be at least 6 characters long.", PRIMARY_DARK);
                return;
            }
            if (userType.equals("Driver")
                    && (finalLicenseField == null || finalLicenseField.getText().trim().isEmpty())) {
                showMessage("Error", "License number required for drivers", PRIMARY_DARK);
                return;
            }

            boolean success;
            if (userType.equals("Driver"))
                success = userDAO.registerDriver(name, email, password, finalLicenseField.getText().trim());
            else
                success = userDAO.registerRider(name, email, password);

            if (success)
                showMessage("Success", "Registration successful", PRIMARY_COLOR);
            else
                showMessage("Error", "Registration failed (email may exist)", PRIMARY_DARK);
            dialog.close();
        });

        cancelBtn.setOnAction(e -> dialog.close());
        btns.getChildren().addAll(registerBtn, cancelBtn);
        form.getChildren().add(btns);

        content.getChildren().addAll(header, form);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private void showLoginForm(String userType) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Login as " + userType);
        dialog.initOwner(primaryStage);

        VBox content = new VBox(12);
        content.setPadding(new Insets(18));
        content.setStyle("-fx-background-color: #FAFAFB;");

        Label header = new Label(userType + " Login");
        header.setFont(FONT_HEADER);
        header.setTextFill(PRIMARY_COLOR);

        VBox form = new VBox(8);
        form.setStyle(
                "-fx-background-color: white; -fx-border-color: rgba(0,0,0,0.3); -fx-border-radius: 5; -fx-background-radius: 5;");
        form.setPadding(new Insets(8));

        TextField emailField = createStyledTextField();
        PasswordField passwordField = createStyledPasswordField();

        form.getChildren().addAll(
                createFormRow("Email", emailField),
                createFormRow("Password", passwordField));

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        Button loginBtn = createModernButton("Login", PRIMARY_COLOR);
        loginBtn.setPrefWidth(150);
        Button cancelBtn = createModernButton("Cancel", PRIMARY_DARK);
        cancelBtn.setPrefWidth(150);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Error", "Email and password required", PRIMARY_DARK);
                return;
            }
            if (!isValidEmail(email)) {
                showMessage("Invalid Email", "Please enter a valid email address", PRIMARY_DARK);
                return;
            }
            if (!isValidPassword(password)) {
                showMessage("Weak Password", "Password must be at least 6 characters long.", PRIMARY_DARK);
                return;
            }

            if (userType.equals("Driver")) {
                Driver driver = userDAO.loginDriver(email, password);
                if (driver != null) {
                    Vehicle v = vehicleDAO.getVehicleByDriverId(driver.getUserId());
                    if (v != null)
                        driver.setVehicle(v);
                    showDriverDashboard(driver);
                    dialog.close();
                } else {
                    showMessage("Error", "Invalid credentials", PRIMARY_DARK);
                }
            } else {
                Rider rider = userDAO.loginRider(email, password);
                if (rider != null) {
                    showRiderDashboard(rider);
                    dialog.close();
                } else {
                    showMessage("Error", "Invalid credentials", PRIMARY_DARK);
                }
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());
        btns.getChildren().addAll(loginBtn, cancelBtn);
        form.getChildren().add(btns);

        content.getChildren().addAll(header, form);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private VBox createFormRow(String label, Control field) {
        VBox row = new VBox(6);
        row.setStyle("-fx-background-color: white;");

        Label l = new Label(label);
        l.setFont(FONT_BODY);

        row.getChildren().addAll(l, field);
        return row;
    }

    private TextField createStyledTextField() {
        TextField f = new TextField();
        f.setFont(FONT_BODY);
        f.setPrefHeight(36);
        f.setStyle(
                "-fx-border-color: rgba(0,0,0,0.4); -fx-border-radius: 5; -fx-background-radius:  5; -fx-padding: 6 8;");
        return f;
    }

    private void applyPlaceholder(TextField f, String placeholder) {
        f.setPromptText(placeholder);
    }

    private PasswordField createStyledPasswordField() {
        PasswordField f = new PasswordField();
        f.setFont(FONT_BODY);
        f.setPrefHeight(36);
        f.setStyle(
                "-fx-border-color: rgba(0,0,0,0.4); -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 6 8;");
        return f;
    }

    private void showMessage(String title, String message, Color color) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color:  #FAFAFB;");

        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        if (email == null)
            return false;
        return java.util.regex.Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    private boolean isValidPassword(String password) {
        if (password == null)
            return false;
        return password.length() >= 6;
    }

    // ----------------- Dashboards (Driver & Rider) -----------------

    private void showDriverDashboard(Driver driver) {
        if (namedCards.containsKey("DRIVER_DASH")) {
            mainContainer.getChildren().remove(namedCards.get("DRIVER_DASH"));
            namedCards.remove("DRIVER_DASH");
        }
        Pane panel = createDriverDashboardPanel(driver);
        namedCards.put("DRIVER_DASH", panel);
        mainContainer.getChildren().add(panel);
        showCard("DRIVER_DASH");
    }

    private Pane createDriverDashboardPanel(Driver driver) {
        BorderPane panel = new BorderPane();
        panel.setStyle("-fx-background-color: #FAFAFB;");

        // Header
        StackPane header = new StackPane();
        header.setPrefHeight(110);
        header.setPadding(new Insets(14, 20, 14, 20));

        Rectangle gradient = new Rectangle();
        gradient.setManaged(false);
        gradient.setMouseTransparent(true);
        gradient.widthProperty().bind(header.widthProperty());
        gradient.heightProperty().bind(header.heightProperty());
        LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_DARK), new Stop(1, PRIMARY_LIGHT));
        gradient.setFill(lg);

        VBox textBox = new VBox(5);
        textBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Driver Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Welcome, " + driver.getName());
        subtitle.setFont(FONT_BODY);
        subtitle.setTextFill(Color.rgb(255, 255, 255, 0.8));

        textBox.getChildren().addAll(title, subtitle);
        header.getChildren().addAll(gradient, textBox);
        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);

        // Center
        VBox center = new VBox(18);
        center.setPadding(new Insets(18));
        center.setStyle("-fx-background-color: #FAFAFB;");

        // Stats row
        HBox statsRow = new HBox(18);
        statsRow.setAlignment(Pos.CENTER);

        int ridesCompleted = 0;
        try {
            List<String> rides = rideDAO.getRidesByDriver(driver.getUserId());
            ridesCompleted = rides == null ? 0 : rides.size();
        } catch (Exception ignored) {
        }

        String earnings = String.format("PKR %.2f", driver.getTotalEarnings());
        statsRow.getChildren().addAll(
                createStatCard("Earnings", earnings, PRIMARY_COLOR),
                createStatCard("Rides Completed", String.valueOf(ridesCompleted), PRIMARY_LIGHT),
                createStatCard("Vehicle", driver.getVehicle() != null ? driver.getVehicle().getModel() : "No vehicle",
                        PRIMARY_DARK));

        // Action area
        HBox actions = new HBox(18);
        actions.setAlignment(Pos.CENTER);

        // Left: quick actions
        VBox leftCol = new VBox(18);
        VBox quickCard = createRoundedCard(12);
        quickCard.setPadding(new Insets(16));
        quickCard.setSpacing(10);

        Label quickTitle = new Label("Quick Actions");
        quickTitle.setFont(FONT_SUBHEADER);
        quickTitle.setTextFill(TEXT_PRIMARY);

        GridPane actionGrid = new GridPane();
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);

        Button startBtn = createLargePrimaryButton("Start Ride (Prompt)", PRIMARY_COLOR);
        startBtn.setOnAction(e -> startRideAction(driver));

        Button completeBtn = createLargePrimaryButton("Complete Ride (Prompt)", PRIMARY_LIGHT);
        completeBtn.setOnAction(e -> completeRideAction(driver));

        Button shiftsBtn = createLargePrimaryButton("View Shifts", PRIMARY_DARK);
        shiftsBtn.setOnAction(e -> viewDriverShifts(driver));

        Button addShiftQuick = createLargePrimaryButton("Add Shift", PRIMARY_LIGHT);
        addShiftQuick.setOnAction(e -> addDriverShift(driver));

        actionGrid.add(startBtn, 0, 0);
        actionGrid.add(completeBtn, 1, 0);
        actionGrid.add(shiftsBtn, 0, 1);
        actionGrid.add(addShiftQuick, 1, 1);

        quickCard.getChildren().addAll(quickTitle, actionGrid);
        leftCol.getChildren().add(quickCard);

        // Right: profile
        VBox rightCol = new VBox(18);
        VBox profileCard = createRoundedCard(12);
        profileCard.setPadding(new Insets(16));
        profileCard.setSpacing(8);

        Label pfTitle = new Label("Profile");
        pfTitle.setFont(FONT_SUBHEADER);
        pfTitle.setTextFill(TEXT_PRIMARY);

        TextArea profileArea = new TextArea();
        profileArea.setEditable(false);
        profileArea.setText("Name: " + driver.getName() + "\nEmail: " + driver.getEmail() + "\nLicense: "
                + driver.getLicenseNumber());
        profileArea.setFont(FONT_BODY);
        profileArea.setPrefRowCount(4);
        profileArea.setWrapText(true);

        Button manageVeh = createModernButton("Manage Vehicle", PRIMARY_LIGHT);
        manageVeh.setPrefWidth(200);
        manageVeh.setOnAction(e -> addOrUpdateVehicle(driver));

        profileCard.getChildren().addAll(pfTitle, profileArea, manageVeh);
        rightCol.getChildren().add(profileCard);

        actions.getChildren().addAll(leftCol, rightCol);

        center.getChildren().addAll(statsRow, actions);

        // Footer
        HBox footerArea = new HBox(10);
        footerArea.setAlignment(Pos.CENTER_RIGHT);
        footerArea.setPadding(new Insets(10));

        Button delete = createModernButton("Delete Profile", Color.rgb(0xAA, 0x11, 0x11));
        delete.setPrefWidth(150);
        delete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setContentText("Delete your profile and all related data?  This cannot be undone.");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean ok = userDAO.deleteUser(driver.getUserId());
                if (ok) {
                    showMessage("Deleted", "Profile deleted.", PRIMARY_COLOR);
                    showWelcomeScreen();
                } else {
                    showMessage("Error", "Failed to delete profile.", PRIMARY_DARK);
                }
            }
        });

        Button viewRidesBtn = createModernButton("View My Rides", PRIMARY_LIGHT);
        viewRidesBtn.setPrefWidth(150);
        viewRidesBtn.setOnAction(e -> viewDriverRides(driver));

        Button logout = createModernButton("Logout", PRIMARY_DARK);
        logout.setPrefWidth(150);
        logout.setOnAction(e -> showWelcomeScreen());

        footerArea.getChildren().addAll(delete, viewRidesBtn, logout);

        panel.setTop(header);
        panel.setCenter(center);
        panel.setBottom(footerArea);

        return panel;
    }

    private void showRiderDashboard(Rider rider) {
        if (namedCards.containsKey("RIDER_DASH")) {
            mainContainer.getChildren().remove(namedCards.get("RIDER_DASH"));
            namedCards.remove("RIDER_DASH");
        }
        Pane riderPanel = createRiderDashboardPanel(rider);
        namedCards.put("RIDER_DASH", riderPanel);
        mainContainer.getChildren().add(riderPanel);
        showCard("RIDER_DASH");
    }

    private Pane createRiderDashboardPanel(Rider rider) {
        BorderPane panel = new BorderPane();
        panel.setStyle("-fx-background-color: #FAFAFB;");

        // Header
        StackPane header = new StackPane();
        header.setPrefHeight(110);
        header.setPadding(new Insets(14, 20, 14, 20));

        Rectangle gradient = new Rectangle();
        gradient.setManaged(false);
        gradient.setMouseTransparent(true);
        gradient.widthProperty().bind(header.widthProperty());
        gradient.heightProperty().bind(header.heightProperty());
        LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_DARK), new Stop(1, PRIMARY_LIGHT));
        gradient.setFill(lg);

        VBox textBox = new VBox(5);
        textBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Rider Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Welcome, " + rider.getName());
        subtitle.setFont(FONT_BODY);
        subtitle.setTextFill(Color.rgb(255, 255, 255, 0.8));

        textBox.getChildren().addAll(title, subtitle);
        header.getChildren().addAll(gradient, textBox);
        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);

        // Center
        VBox center = new VBox(18);
        center.setPadding(new Insets(18));
        center.setStyle("-fx-background-color: #FAFAFB;");

        // Stats row
        HBox statsRow = new HBox(18);
        statsRow.setAlignment(Pos.CENTER);

        int ridesCount = 0;
        try {
            List<String> rides = rideDAO.getRidesByRider(rider.getUserId());
            ridesCount = rides == null ? 0 : rides.size();
        } catch (Exception ignored) {
        }

        statsRow.getChildren().addAll(
                createStatCard("Wallet", String.format("PKR %.2f", rider.getBalance()), PRIMARY_COLOR),
                createStatCard("My Rides", String.valueOf(ridesCount), PRIMARY_LIGHT),
                createStatCard("Saved Routes", String.valueOf(routeDAO.getAllRoutes().size()), PRIMARY_DARK));

        // Action area
        HBox actions = new HBox(18);
        actions.setAlignment(Pos.CENTER);

        // Book card
        VBox bookCard = createRoundedCard(12);
        bookCard.setPadding(new Insets(16));
        bookCard.setSpacing(12);
        bookCard.setPrefWidth(280);

        Label bkTitle = new Label("Book a Ride");
        bkTitle.setFont(FONT_SUBHEADER);
        bkTitle.setTextFill(TEXT_PRIMARY);

        Label bkDesc = new Label(
                "Find a nearby driver, choose a route and pay with wallet, card or cash.  Fast and secure.");
        bkDesc.setWrapText(true);
        bkDesc.setFont(FONT_BODY);

        Button bookBtn = createLargePrimaryButton("Start Booking (Wizard)", PRIMARY_COLOR);
        bookBtn.setPrefWidth(240);
        bookBtn.setOnAction(e -> openBookingWizard(rider));

        bookCard.getChildren().addAll(bkTitle, bkDesc, bookBtn);

        // Wallet card
        VBox walletCard = createRoundedCard(12);
        walletCard.setPadding(new Insets(16));
        walletCard.setSpacing(12);
        walletCard.setPrefWidth(280);

        Label wTitle = new Label("Wallet");
        wTitle.setFont(FONT_SUBHEADER);
        wTitle.setTextFill(TEXT_PRIMARY);

        Label wDesc = new Label("Top up your wallet for faster, cashless payments.");
        wDesc.setWrapText(true);
        wDesc.setFont(FONT_BODY);

        Button topUpBtn = createLargePrimaryButton("Top-up Wallet", PRIMARY_LIGHT);
        topUpBtn.setPrefWidth(240);
        topUpBtn.setOnAction(e -> addMoneyToWallet(rider));

        walletCard.getChildren().addAll(wTitle, wDesc, topUpBtn);

        // Feedback card
        VBox fbCard = createRoundedCard(12);
        fbCard.setPadding(new Insets(16));
        fbCard.setSpacing(12);
        fbCard.setPrefWidth(280);

        Label fTitle = new Label("Feedback");
        fTitle.setFont(FONT_SUBHEADER);
        fTitle.setTextFill(TEXT_PRIMARY);

        Label fDesc = new Label("Rate drivers and submit feedback to improve service quality.");
        fDesc.setWrapText(true);
        fDesc.setFont(FONT_BODY);

        Button fbBtn = createLargePrimaryButton("Submit Feedback", PRIMARY_LIGHT);
        fbBtn.setPrefWidth(240);
        fbBtn.setOnAction(e -> submitFeedback(rider));

        fbCard.getChildren().addAll(fTitle, fDesc, fbBtn);

        actions.getChildren().addAll(bookCard, walletCard, fbCard);

        center.getChildren().addAll(statsRow, actions);

        // Footer
        HBox footerArea = new HBox(10);
        footerArea.setAlignment(Pos.CENTER_RIGHT);
        footerArea.setPadding(new Insets(10));

        Button delete = createModernButton("Delete Profile", Color.rgb(0xAA, 0x11, 0x11));
        delete.setPrefWidth(150);
        delete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setContentText("Delete your profile and all related data? This cannot be undone.");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean ok = userDAO.deleteUser(rider.getUserId());
                if (ok) {
                    showMessage("Deleted", "Profile deleted.", PRIMARY_COLOR);
                    showWelcomeScreen();
                } else {
                    showMessage("Error", "Failed to delete profile.", PRIMARY_DARK);
                }
            }
        });

        Button viewRides = createModernButton("View My Rides", PRIMARY_LIGHT);
        viewRides.setPrefWidth(150);
        viewRides.setOnAction(e -> viewRiderRides(rider));

        Button logout = createModernButton("Logout", PRIMARY_DARK);
        logout.setPrefWidth(150);
        logout.setOnAction(e -> showWelcomeScreen());

        footerArea.getChildren().addAll(delete, viewRides, logout);

        panel.setTop(header);
        panel.setCenter(center);
        panel.setBottom(footerArea);

        return panel;
    }

    // ----------------- Booking Wizard -----------------

    private void openBookingWizard(Rider rider) {
        BookingWizard wizard = new BookingWizard(rider);
        wizard.showDialog();
    }

    private class BookingWizard {
        private final Stage dialog;
        private final StackPane stepsPanel = new StackPane();
        private final Rider rider;

        // Step components
        private ListView<String> driversList;
        private ObservableList<String> driversModel;

        private ListView<String> routesList;
        private ObservableList<String> routesModel;
        private Label farePreview;

        private RadioButton rbCash, rbCard, rbWallet;
        private ToggleGroup paymentGroup;
        private TextField cardField;
        private Label walletBalanceLabel;

        private CheckBox assistantCheck;
        private TextField assistantNameField;

        private Button backBtn, nextBtn, cancelBtn;

        // internal selections
        private int selectedDriverId = -1;
        private Route selectedRoute = null;
        private String selectedPaymentMethod = "Cash";
        private double computedFare = 0.0;
        private boolean walletOk = false;

        private int currentStep = 0;
        private Pane[] steps = new Pane[4];

        BookingWizard(Rider rider) {
            this.rider = rider;
            dialog = new Stage();
            dialog.initOwner(primaryStage);
            dialog.setTitle("Book a Ride - Wizard");
            dialog.setWidth(620);
            dialog.setHeight(520);
            dialog.initModality(Modality.APPLICATION_MODAL);
            initSteps();

            BorderPane layout = new BorderPane();
            layout.setCenter(stepsPanel);
            layout.setBottom(createWizardControls());

            Scene scene = new Scene(layout);
            dialog.setScene(scene);
        }

        private void initSteps() {
            steps[0] = createStep1_SelectDriver();
            steps[1] = createStep2_SelectRoute();
            steps[2] = createStep3_Payment();
            steps[3] = createStep4_AssistantReview();

            stepsPanel.getChildren().addAll(steps);
            showStep(0);
        }

        private void showStep(int step) {
            currentStep = step;
            for (int i = 0; i < steps.length; i++) {
                steps[i].setVisible(i == step);
            }
            backBtn.setDisable(step == 0);
            nextBtn.setText(step == 3 ? "Confirm" : "Next");
        }

        private Pane createStep1_SelectDriver() {
            VBox p = createRoundedCard(10);
            p.setPadding(new Insets(16));
            p.setSpacing(12);

            Label title = new Label("Step 1: Select Driver");
            title.setFont(FONT_SUBHEADER);
            title.setTextFill(TEXT_PRIMARY);

            driversModel = FXCollections.observableArrayList();
            driversList = new ListView<>(driversModel);
            driversList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            List<String> drivers = vehicleDAO.getAllDriversWithVehicles();
            if (drivers.isEmpty())
                driversModel.add("No drivers available");
            else
                driversModel.addAll(drivers);

            driversList.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
                if (val != null) {
                    selectedDriverId = extractDriverIdFromListItem(val);
                }
            });

            p.getChildren().addAll(title, driversList);
            VBox.setVgrow(driversList, Priority.ALWAYS);
            return p;
        }

        private Pane createStep2_SelectRoute() {
            VBox p = createRoundedCard(10);
            p.setPadding(new Insets(16));
            p.setSpacing(12);

            Label title = new Label("Step 2: Select Route & Preview Fare");
            title.setFont(FONT_SUBHEADER);
            title.setTextFill(TEXT_PRIMARY);

            routesModel = FXCollections.observableArrayList();
            routesList = new ListView<>(routesModel);
            routesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            List<Route> currentRoutes = routeDAO.getAllRoutes();
            if (currentRoutes.isEmpty()) {
                routeDAO.addRoute("Main Campus", "Engineering Block", 2.5);
                routeDAO.addRoute("Library", "Student Center", 1.8);
                routeDAO.addRoute("Hostel A", "Science Building", 3.2);
            }
            final List<Route> routes = routeDAO.getAllRoutes();
            for (Route r : routes)
                routesModel.add(r.toString());

            farePreview = new Label("Fare:  PKR 0.00");
            farePreview.setFont(FONT_BODY);
            farePreview.setTextFill(TEXT_SECONDARY);

            routesList.getSelectionModel().selectedIndexProperty().addListener((obs, old, val) -> {
                int idx = val.intValue();
                if (idx >= 0 && idx < routes.size()) {
                    selectedRoute = routes.get(idx);
                    computedFare = calculateFare(selectedRoute.getDistanceKm());
                    farePreview.setText(String.format("Fare: PKR %.2f", computedFare));
                }
            });

            p.getChildren().addAll(title, routesList, farePreview);
            VBox.setVgrow(routesList, Priority.ALWAYS);
            return p;
        }

        private Pane createStep3_Payment() {
            VBox p = createRoundedCard(10);
            p.setPadding(new Insets(16));
            p.setSpacing(12);

            Label title = new Label("Step 3: Payment");
            title.setFont(FONT_SUBHEADER);
            title.setTextFill(TEXT_PRIMARY);

            rbCash = new RadioButton("Cash");
            rbCard = new RadioButton("Card");
            rbWallet = new RadioButton("Wallet");

            paymentGroup = new ToggleGroup();
            rbCash.setToggleGroup(paymentGroup);
            rbCard.setToggleGroup(paymentGroup);
            rbWallet.setToggleGroup(paymentGroup);
            rbCash.setSelected(true);

            HBox pmPanel = new HBox(10);
            pmPanel.getChildren().addAll(rbCash, rbCard, rbWallet);

            Label cardLbl = new Label("Card Number:");
            cardField = createStyledTextField();
            cardField.setDisable(true);

            Label walletLbl = new Label("Wallet Balance:");
            walletBalanceLabel = new Label("PKR " + String.format("%.2f", rider.getBalance()));
            Button topUpBtn = createModernButton("Top-up", PRIMARY_LIGHT);
            topUpBtn.setPrefWidth(90);
            topUpBtn.setPrefHeight(30);

            HBox walletPanel = new HBox(10);
            walletPanel.getChildren().addAll(walletBalanceLabel, topUpBtn);

            rbCard.setOnAction(e -> cardField.setDisable(false));
            rbCash.setOnAction(e -> cardField.setDisable(true));
            rbWallet.setOnAction(e -> cardField.setDisable(true));

            topUpBtn.setOnAction(e -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Top-up");
                inputDialog.setHeaderText("Enter amount to add to wallet (PKR):");
                Optional<String> result = inputDialog.showAndWait();
                if (result.isPresent()) {
                    try {
                        double a = Double.parseDouble(result.get().trim());
                        if (a <= 0) {
                            showMessage("Error", "Invalid amount", PRIMARY_DARK);
                            return;
                        }
                        rider.addBalance(a);
                        userDAO.updateRiderBalance(rider.getUserId(), rider.getBalance());
                        walletBalanceLabel.setText("PKR " + String.format("%. 2f", rider.getBalance()));
                        showMessage("Success", "Wallet topped up", PRIMARY_COLOR);
                    } catch (Exception ex) {
                        showMessage("Error", "Invalid number", PRIMARY_DARK);
                    }
                }
            });

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(cardLbl, 0, 0);
            grid.add(cardField, 1, 0);
            grid.add(walletLbl, 0, 1);
            grid.add(walletPanel, 1, 1);

            p.getChildren().addAll(title, pmPanel, grid);
            return p;
        }

        private TextArea reviewTextAreaRef;

        private Pane createStep4_AssistantReview() {
            VBox p = createRoundedCard(10);
            p.setPadding(new Insets(16));
            p.setSpacing(12);

            Label title = new Label("Step 4: Assistant & Review");
            title.setFont(FONT_SUBHEADER);
            title.setTextFill(TEXT_PRIMARY);

            assistantCheck = new CheckBox("Add an assistant/companion");
            assistantNameField = createStyledTextField();
            assistantNameField.setDisable(true);

            assistantCheck.selectedProperty().addListener((obs, old, val) -> {
                assistantNameField.setDisable(!val);
            });

            reviewTextAreaRef = new TextArea();
            reviewTextAreaRef.setEditable(false);
            reviewTextAreaRef.setWrapText(true);
            reviewTextAreaRef.setPrefRowCount(8);

            p.getChildren().addAll(title, assistantCheck, assistantNameField, reviewTextAreaRef);
            VBox.setVgrow(reviewTextAreaRef, Priority.ALWAYS);
            return p;
        }

        private Pane createWizardControls() {
            BorderPane controls = new BorderPane();
            controls.setPadding(new Insets(10, 12, 10, 12));
            controls.setStyle("-fx-background-color: #FAFAFB;");

            Label stepHint = new Label("Wizard:  1 → 2 → 3 → 4");
            stepHint.setFont(FONT_SMALL);

            HBox right = new HBox(10);
            right.setAlignment(Pos.CENTER_RIGHT);

            backBtn = createModernButton("Back", PRIMARY_LIGHT);
            backBtn.setPrefWidth(100);
            nextBtn = createModernButton("Next", PRIMARY_COLOR);
            nextBtn.setPrefWidth(100);
            cancelBtn = createModernButton("Cancel", PRIMARY_DARK);
            cancelBtn.setPrefWidth(100);

            backBtn.setDisable(true);

            backBtn.setOnAction(e -> stepBack());
            nextBtn.setOnAction(e -> stepNext());
            cancelBtn.setOnAction(e -> dialog.close());

            right.getChildren().addAll(backBtn, nextBtn, cancelBtn);

            controls.setLeft(stepHint);
            controls.setRight(right);
            return controls;
        }

        void showDialog() {
            dialog.showAndWait();
        }

        private void stepBack() {
            if (currentStep > 0) {
                showStep(currentStep - 1);
                updateReviewIfNeeded();
            }
        }

        private void stepNext() {
            if (currentStep == 0) {
                // validate driver selection
                if (driversList.getSelectionModel().getSelectedIndex() < 0 || driversModel.isEmpty()
                        || driversList.getSelectionModel().getSelectedItem().startsWith("No drivers")) {
                    showMessage("Error", "Please select a driver.", PRIMARY_DARK);
                    return;
                }
                selectedDriverId = extractDriverIdFromListItem(driversList.getSelectionModel().getSelectedItem());

                if (!driverHasVehicle(selectedDriverId)) {
                    showMessage("Unavailable",
                            "Selected driver has no vehicle registered.  Please choose another driver.", PRIMARY_DARK);
                    return;
                }
                if (!driverHasActiveShiftNow(selectedDriverId)) {
                    showMessage("Unavailable",
                            "Selected driver is not on shift right now. Please choose another driver.", PRIMARY_DARK);
                    return;
                }
                if (driverHasInProgressRide(selectedDriverId)) {
                    showMessage("Busy",
                            "Selected driver currently has an in-progress ride. Please choose another driver.",
                            PRIMARY_DARK);
                    return;
                }
                showStep(1);

            } else if (currentStep == 1) {
                if (routesList.getSelectionModel().getSelectedIndex() < 0) {
                    showMessage("Error", "Please select a route.", PRIMARY_DARK);
                    return;
                }
                showStep(2);

            } else if (currentStep == 2) {
                // validate payment
                if (rbCard.isSelected()) {
                    selectedPaymentMethod = "Card";
                    String cardNum = cardField.getText().trim();
                    if (cardNum.isEmpty()) {
                        showMessage("Error", "Enter card number.", PRIMARY_DARK);
                        return;
                    }
                } else if (rbWallet.isSelected()) {
                    selectedPaymentMethod = "Wallet";
                    walletOk = rider.getBalance() >= computedFare;
                    if (!walletOk) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Wallet Low");
                        confirm.setContentText("Insufficient wallet balance. Top-up now?");
                        Optional<ButtonType> result = confirm.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            TextInputDialog inputDialog = new TextInputDialog();
                            inputDialog.setTitle("Top-up");
                            inputDialog.setHeaderText("Amount to add (PKR):");
                            Optional<String> amtResult = inputDialog.showAndWait();
                            if (amtResult.isPresent()) {
                                try {
                                    double a = Double.parseDouble(amtResult.get().trim());
                                    if (a <= 0) {
                                        showMessage("Error", "Invalid amount", PRIMARY_DARK);
                                        return;
                                    }
                                    rider.addBalance(a);
                                    userDAO.updateRiderBalance(rider.getUserId(), rider.getBalance());
                                    walletBalanceLabel.setText("PKR " + String.format("%.2f", rider.getBalance()));
                                    walletOk = rider.getBalance() >= computedFare;
                                    if (!walletOk) {
                                        showMessage("Error", "Still insufficient balance", PRIMARY_DARK);
                                        return;
                                    }
                                } catch (Exception ex) {
                                    showMessage("Error", "Invalid number", PRIMARY_DARK);
                                    return;
                                }
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    selectedPaymentMethod = "Cash";
                }
                showStep(3);
                updateReviewIfNeeded();

            } else if (currentStep == 3) {
                // Confirm booking
                if (selectedDriverId <= 0 || selectedRoute == null || computedFare <= 0) {
                    showMessage("Error", "Invalid booking state", PRIMARY_DARK);
                    return;
                }

                if (!isDriverAvailableNow(selectedDriverId)) {
                    showMessage("Unavailable", "Driver is not available at the moment.  Please choose another driver.",
                            PRIMARY_DARK);
                    return;
                }

                String assistantName = assistantCheck.isSelected() ? assistantNameField.getText().trim() : null;

                int rideId = rideDAO.createRide(rider.getUserId(), selectedDriverId, selectedRoute.getRouteId(),
                        computedFare);
                if (rideId <= 0) {
                    showMessage("Error", "Failed to create ride", PRIMARY_DARK);
                    return;
                }

                boolean paymentSuccess = false;
                if ("Wallet".equals(selectedPaymentMethod)) {
                    if (walletOk && rider.deductBalance(computedFare)) {
                        userDAO.updateRiderBalance(rider.getUserId(), rider.getBalance());
                        paymentSuccess = true;
                    }
                } else if ("Card".equals(selectedPaymentMethod)) {
                    paymentSuccess = true;
                } else {
                    paymentSuccess = true;
                }

                String payStatus = paymentSuccess ? "Completed" : "Failed";
                int payId = paymentDAO.createPayment(rideId, computedFare, selectedPaymentMethod, payStatus);

                if (assistantName != null && !assistantName.isEmpty()) {
                    assistantDAO.addAssistant(rideId, rider.getUserId(), assistantName);
                }

                String msg = String.format("Ride booked successfully! ID: %d\nPayment status: %s\nPayment record:  %s",
                        rideId, payStatus, payId > 0 ? ("ID " + payId) : "Not recorded");
                showMessage("Success", msg, PRIMARY_COLOR);
                dialog.close();
            }
        }

        private void updateReviewIfNeeded() {
            if (reviewTextAreaRef == null)
                return;
            StringBuilder sb = new StringBuilder();
            sb.append("Driver: ").append(selectedDriverId > 0 ? selectedDriverId : "Not selected").append("\n");
            sb.append("Route: ").append(selectedRoute != null ? selectedRoute.toString() : "Not selected").append("\n");
            sb.append(String.format("Fare: PKR %.2f\n", computedFare));
            sb.append("Payment:  ").append(selectedPaymentMethod).append("\n");
            sb.append("Wallet Balance: PKR ").append(String.format("%.2f", rider.getBalance())).append("\n");
            sb.append("Assistant: ")
                    .append((assistantCheck != null && assistantCheck.isSelected() && assistantNameField != null
                            && !assistantNameField.getText().trim().isEmpty()) ? assistantNameField.getText().trim()
                                    : "None")
                    .append("\n");
            reviewTextAreaRef.setText(sb.toString());
        }
    }

    private int extractDriverIdFromListItem(String item) {
        if (item == null)
            return -1;
        try {
            String digits = item.replaceAll("^\\D*(\\d+).*", "$1");
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return -1;
        }
    }

    private double calculateFare(double km) {
        return 100 + km * 50;
    }

    // ----------------- Availability helpers -----------------
    private boolean driverHasVehicle(int driverId) {
        try {
            Vehicle v = vehicleDAO.getVehicleByDriverId(driverId);
            return v != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean driverHasInProgressRide(int driverId) {
        try {
            return rideDAO.hasInProgressRideForDriver(driverId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean driverHasActiveShiftNow(int driverId) {
        try {
            var shifts = shiftDAO.getShiftsByDriver(driverId);
            if (shifts == null || shifts.isEmpty())
                return false;
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            for (Object o : shifts) {
                try {
                    DriverShift s = (DriverShift) o;
                    java.sql.Date sd = s.getShiftDate();
                    java.sql.Time st = s.getStartTime();
                    java.sql.Time et = s.getEndTime();
                    if (sd == null || st == null || et == null)
                        continue;
                    LocalDate shiftDate = sd.toLocalDate();
                    LocalTime start = st.toLocalTime();
                    LocalTime end = et.toLocalTime();
                    if (today.equals(shiftDate) && (!now.isBefore(start) && !now.isAfter(end)))
                        return true;
                } catch (Exception ignored) {
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDriverAvailableNow(int driverId) {
        if (!driverHasVehicle(driverId))
            return false;
        if (!driverHasActiveShiftNow(driverId))
            return false;
        if (driverHasInProgressRide(driverId))
            return false;
        return true;
    }

    // ----------------- Action methods (stubs - implement as needed)
    // -----------------

    private void showLargeText(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(640, 480);
        alert.showAndWait();
    }

    private void addOrUpdateVehicle(Driver driver) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add / Update Vehicle");
        dialog.initOwner(primaryStage);

        VBox content = new VBox(12);
        content.setPadding(new Insets(18));
        content.setStyle("-fx-background-color: #FAFAFB;");

        TextField modelF = createStyledTextField();
        TextField plateF = createStyledTextField();
        TextField capF = createStyledTextField();
        TextField colorF = createStyledTextField();

        content.getChildren().addAll(
                new Label("Model:"), modelF,
                new Label("Plate Number:"), plateF,
                new Label("Capacity:"), capF,
                new Label("Color:"), colorF);

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        Button save = createModernButton("Save Vehicle", PRIMARY_COLOR);
        save.setPrefWidth(150);
        Button cancel = createModernButton("Cancel", PRIMARY_DARK);
        cancel.setPrefWidth(150);

        save.setOnAction(e -> {
            try {
                String model = modelF.getText().trim();
                String plate = plateF.getText().trim();
                int cap = Integer.parseInt(capF.getText().trim());
                String color = colorF.getText().trim();

                if (model.isEmpty() || plate.isEmpty() || color.isEmpty()) {
                    showMessage("Error", "All fields required", PRIMARY_DARK);
                    return;
                }

                int vid = vehicleDAO.addVehicle(driver.getUserId(), model, plate, cap, color);
                if (vid > 0) {
                    driver.setVehicle(new Vehicle(vid, model, plate, cap, color));
                    showMessage("Success", "Vehicle saved.", PRIMARY_COLOR);
                    dialog.close();
                    showDriverDashboard(driver);
                } else {
                    showMessage("Error", "Failed to save vehicle.", PRIMARY_DARK);
                }
            } catch (NumberFormatException nfe) {
                showMessage("Error", "Capacity must be a number", PRIMARY_DARK);
            }
        });

        cancel.setOnAction(e -> dialog.close());
        btns.getChildren().addAll(save, cancel);
        content.getChildren().add(btns);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private void startRideAction(Driver driver) {
        int did = driver.getUserId();
        if (!driverHasVehicle(did)) {
            showMessage("Unavailable", "You must add a vehicle before starting rides.", PRIMARY_DARK);
            return;
        }
        if (!driverHasActiveShiftNow(did)) {
            showMessage("Unavailable",
                    "You are not scheduled for a shift right now.  Add a shift or try within your shift times.",
                    PRIMARY_DARK);
            return;
        }
        if (driverHasInProgressRide(did)) {
            showMessage("Busy", "You already have an in-progress ride. Complete it before starting another.",
                    PRIMARY_DARK);
            return;
        }

        var candidates = rideDAO.getPendingOrConfirmedRidesForDriver(did);
        if (candidates == null || candidates.isEmpty()) {
            showMessage("Info", "No pending/confirmed rides available to start.", PRIMARY_DARK);
            return;
        }

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(candidates.get(0), candidates);
        choiceDialog.setTitle("Select Ride");
        choiceDialog.setHeaderText("Select ride to start");
        choiceDialog.setContentText("Ride:");

        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            String sel = result.get();
            int rideId = extractDriverIdFromListItem(sel);
            boolean ok = rideDAO.startRideTransaction(rideId, driver.getUserId());
            showMessage(ok ? "Success" : "Error",
                    ok ? "Ride marked In Progress." : "Failed to mark ride as In Progress.",
                    ok ? PRIMARY_COLOR : PRIMARY_DARK);
            showDriverDashboard(driver);
        }
    }

    private void completeRideAction(Driver driver) {
        int did = driver.getUserId();
        var inProg = rideDAO.getInProgressRidesForDriver(did);

        if (inProg == null || inProg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("No In-Progress Rides");
            alert.setContentText("No started rides found. Start a ride now?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                startRideAction(driver);
            }
            return;
        }

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(inProg.get(0), inProg);
        choiceDialog.setTitle("Complete Ride");
        choiceDialog.setHeaderText("Select in-progress ride to complete");
        choiceDialog.setContentText("Ride:");

        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            String sel = result.get();
            int rideId = extractDriverIdFromListItem(sel);
            double fare = rideDAO.getFareByRideId(rideId);

            if (fare < 0) {
                showMessage("Error", "Could not determine fare for ride.", PRIMARY_DARK);
                return;
            }

            boolean ok = rideDAO.completeRideTransaction(rideId, driver.getUserId(), fare);
            if (ok) {
                driver.addEarnings(fare);
                showMessage("Success", "Ride completed and earnings updated.", PRIMARY_COLOR);
                showDriverDashboard(driver);
            } else {
                showMessage("Error", "Failed to complete ride.", PRIMARY_DARK);
            }
        }
    }

    private void viewDriverShifts(Driver driver) {
        var shifts = shiftDAO.getShiftsByDriver(driver.getUserId());
        if (shifts == null || shifts.isEmpty()) {
            showMessage("Shifts", "No shifts set.", PRIMARY_DARK);
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Your Shifts");
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TableView<DriverShift> table = new TableView<>();

        TableColumn<DriverShift, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("shiftId"));

        TableColumn<DriverShift, java.sql.Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("shiftDate"));

        TableColumn<DriverShift, java.sql.Time> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<DriverShift, java.sql.Time> endCol = new TableColumn<>("End");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        table.getColumns().addAll(idCol, dateCol, startCol, endCol);

        ObservableList<DriverShift> data = FXCollections.observableArrayList();
        for (Object o : shifts) {
            data.add((DriverShift) o);
        }
        table.setItems(data);

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        btns.setPadding(new Insets(10));

        Button endBtn = createModernButton("End Selected Shift Now", PRIMARY_COLOR);
        endBtn.setPrefWidth(200);
        Button closeBtn = createModernButton("Close", PRIMARY_DARK);
        closeBtn.setPrefWidth(100);

        endBtn.setOnAction(e -> {
            DriverShift selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showMessage("Error", "Please select a shift to end.", PRIMARY_DARK);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm End Shift");
            confirm.setContentText("End the selected shift now?  This will set its end time to the current time.");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                java.sql.Time now = java.sql.Time.valueOf(LocalTime.now());
                boolean ok = shiftDAO.endShift(selected.getShiftId(), now);

                if (ok) {
                    showMessage("Success", "Shift ended at " + now.toString(), PRIMARY_COLOR);
                    // Refresh table
                    var refreshed = shiftDAO.getShiftsByDriver(driver.getUserId());
                    data.clear();
                    for (Object o : refreshed) {
                        data.add((DriverShift) o);
                    }
                } else {
                    showMessage("Error", "Failed to end shift.", PRIMARY_DARK);
                }
            }
        });

        closeBtn.setOnAction(e -> stage.close());
        btns.getChildren().addAll(endBtn, closeBtn);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(table, btns);
        VBox.setVgrow(table, Priority.ALWAYS);

        Scene scene = new Scene(layout, 700, 420);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void addDriverShift(Driver driver) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Shift");
        dialog.initOwner(primaryStage);

        VBox content = new VBox(12);
        content.setPadding(new Insets(18));
        content.setStyle("-fx-background-color: #FAFAFB;");

        TextField dateF = createStyledTextField();
        applyPlaceholder(dateF, "YYYY-MM-DD");
        TextField startF = createStyledTextField();
        applyPlaceholder(startF, "HH:MM: SS");
        TextField endF = createStyledTextField();
        applyPlaceholder(endF, "HH:MM:SS");

        content.getChildren().addAll(
                new Label("Shift Date (YYYY-MM-DD):"), dateF,
                new Label("Start Time (HH:MM:SS):"), startF,
                new Label("End Time (HH:MM: SS):"), endF);

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        Button add = createModernButton("Add", PRIMARY_COLOR);
        add.setPrefWidth(100);
        Button cancel = createModernButton("Cancel", PRIMARY_DARK);
        cancel.setPrefWidth(100);

        add.setOnAction(e -> {
            try {
                java.sql.Date date = java.sql.Date.valueOf(dateF.getText().trim());
                java.sql.Time start = java.sql.Time.valueOf(startF.getText().trim());
                java.sql.Time end = java.sql.Time.valueOf(endF.getText().trim());

                boolean ok = shiftDAO.addShift(driver.getUserId(), date, start, end);
                showMessage(ok ? "Success" : "Error",
                        ok ? "Shift added." : "Failed to add shift.",
                        ok ? PRIMARY_COLOR : PRIMARY_DARK);

                if (ok) {
                    dialog.close();
                    showDriverDashboard(driver);
                }
            } catch (Exception ex) {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle("Fallback Input");
                inputDialog.setHeaderText("Invalid format. Enter shift as:  YYYY-MM-DD HH:MM:SS-HH:MM:SS");
                Optional<String> result = inputDialog.showAndWait();

                if (result.isPresent()) {
                    try {
                        String combined = result.get().trim();
                        String[] parts = combined.split("\\s+");
                        if (parts.length != 2)
                            throw new IllegalArgumentException("Bad format");

                        java.sql.Date date = java.sql.Date.valueOf(parts[0]);
                        String[] times = parts[1].split("-");
                        if (times.length != 2)
                            throw new IllegalArgumentException("Bad time range");

                        java.sql.Time start = java.sql.Time.valueOf(times[0]);
                        java.sql.Time end = java.sql.Time.valueOf(times[1]);

                        boolean ok = shiftDAO.addShift(driver.getUserId(), date, start, end);
                        showMessage(ok ? "Success" : "Error",
                                ok ? "Shift added." : "Failed to add shift.",
                                ok ? PRIMARY_COLOR : PRIMARY_DARK);

                        if (ok) {
                            dialog.close();
                            showDriverDashboard(driver);
                        }
                    } catch (Exception ex2) {
                        showMessage("Error", "Invalid date/time format.", PRIMARY_DARK);
                    }
                }
            }
        });

        cancel.setOnAction(e -> dialog.close());
        btns.getChildren().addAll(add, cancel);
        content.getChildren().add(btns);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private void addMoneyToWallet(Rider rider) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Top-up Wallet");
        dialog.setHeaderText("Enter amount to add (PKR):");
        dialog.setContentText("Amount:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double a = Double.parseDouble(result.get().trim());
                if (a <= 0) {
                    showMessage("Error", "Invalid amount", PRIMARY_DARK);
                    return;
                }

                rider.addBalance(a);
                userDAO.updateRiderBalance(rider.getUserId(), rider.getBalance());
                showMessage("Success",
                        "Added successfully.  New balance: PKR " + String.format("%.2f", rider.getBalance()),
                        PRIMARY_COLOR);
                showRiderDashboard(rider);
            } catch (Exception e) {
                showMessage("Error", "Invalid number", PRIMARY_DARK);
            }
        }
    }

    private void viewRiderRides(Rider rider) {
        List<String> rides = rideDAO.getRidesByRider(rider.getUserId());
        if (rides == null || rides.isEmpty()) {
            showMessage("My Rides", "No rides yet!", PRIMARY_DARK);
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("My Rides");
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TableView<RideTableRow> table = new TableView<>();

        TableColumn<RideTableRow, String> idCol = new TableColumn<>("Ride ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("rideId"));

        TableColumn<RideTableRow, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));

        TableColumn<RideTableRow, String> otherCol = new TableColumn<>("Driver");
        otherCol.setCellValueFactory(new PropertyValueFactory<>("other"));

        TableColumn<RideTableRow, String> fareCol = new TableColumn<>("Fare");
        fareCol.setCellValueFactory(new PropertyValueFactory<>("fare"));

        TableColumn<RideTableRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<RideTableRow, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        table.getColumns().addAll(idCol, routeCol, otherCol, fareCol, statusCol, timeCol);

        ObservableList<RideTableRow> data = FXCollections.observableArrayList();

        Runnable populate = () -> {
            data.clear();
            for (String s : rideDAO.getRidesByRider(rider.getUserId())) {
                try {
                    String[] parts = s.split(" \\| ");
                    if (parts.length < 6)
                        continue;

                    String id = parts[0].replace("Ride#", "").trim();
                    String route = parts[1].trim();
                    String other = parts[2].contains(": ") ? parts[2].split(":", 2)[1].trim() : parts[2].trim();
                    String fare = parts[3].replace("Fare:  PKR", "").trim();
                    String status = parts[4].replace("Status:", "").trim();
                    String time = parts[5].replace("Time:", "").trim();

                    data.add(new RideTableRow(id, route, other, "PKR " + fare, status, time));
                } catch (Exception ex) {
                    // skip malformed
                }
            }
        };

        populate.run();
        table.setItems(data);

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        btns.setPadding(new Insets(10));

        Button cancelBtn = createModernButton("Cancel Selected Ride", PRIMARY_DARK);
        cancelBtn.setPrefWidth(180);
        Button close = createModernButton("Close", PRIMARY_DARK);
        close.setPrefWidth(100);

        cancelBtn.setOnAction(e -> {
            RideTableRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showMessage("Error", "Please select a ride to cancel.", PRIMARY_DARK);
                return;
            }

            String status = selected.getStatus();
            if (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("In Progress")) {
                showMessage("Cannot Cancel", "Ride is already In Progress or Completed.", PRIMARY_DARK);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancel");
            confirm.setContentText("Cancel ride #" + selected.getRideId() + "?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(selected.getRideId());
                    boolean ok = rideDAO.cancelRide(id);
                    if (ok) {
                        showMessage("Cancelled", "Ride cancelled.", PRIMARY_COLOR);
                        populate.run();
                    } else {
                        showMessage("Error", "Failed to cancel ride (it may be already in-progress/completed).",
                                PRIMARY_DARK);
                    }
                } catch (Exception ex) {
                    showMessage("Error", "Invalid ride id.", PRIMARY_DARK);
                }
            }
        });

        close.setOnAction(e -> stage.close());
        btns.getChildren().addAll(cancelBtn, close);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(table, btns);
        VBox.setVgrow(table, Priority.ALWAYS);

        Scene scene = new Scene(layout, 760, 420);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void viewDriverRides(Driver driver) {
        List<String> rides = rideDAO.getRidesByDriver(driver.getUserId());
        if (rides == null || rides.isEmpty()) {
            showMessage("My Rides", "No rides yet!", PRIMARY_DARK);
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("My Rides");
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        TableView<RideTableRow> table = new TableView<>();

        TableColumn<RideTableRow, String> idCol = new TableColumn<>("Ride ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("rideId"));

        TableColumn<RideTableRow, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));

        TableColumn<RideTableRow, String> otherCol = new TableColumn<>("Rider");
        otherCol.setCellValueFactory(new PropertyValueFactory<>("other"));

        TableColumn<RideTableRow, String> fareCol = new TableColumn<>("Fare");
        fareCol.setCellValueFactory(new PropertyValueFactory<>("fare"));

        TableColumn<RideTableRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<RideTableRow, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        table.getColumns().addAll(idCol, routeCol, otherCol, fareCol, statusCol, timeCol);

        ObservableList<RideTableRow> data = FXCollections.observableArrayList();

        Runnable populate = () -> {
            data.clear();
            for (String s : rideDAO.getRidesByDriver(driver.getUserId())) {
                try {
                    String[] parts = s.split(" \\| ");
                    if (parts.length < 6)
                        continue;

                    String id = parts[0].replace("Ride#", "").trim();
                    String route = parts[1].trim();
                    String other = parts[2].contains(":") ? parts[2].split(":", 2)[1].trim() : parts[2].trim();
                    String fare = parts[3].replace("Fare: PKR", "").trim();
                    String status = parts[4].replace("Status:", "").trim();
                    String time = parts[5].replace("Time:", "").trim();

                    data.add(new RideTableRow(id, route, other, "PKR " + fare, status, time));
                } catch (Exception ex) {
                    // skip malformed
                }
            }
        };

        populate.run();
        table.setItems(data);

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        btns.setPadding(new Insets(10));

        Button confirmBtn = createModernButton("Confirm Selected Ride", PRIMARY_COLOR);
        confirmBtn.setPrefWidth(180);
        Button cancelBtn = createModernButton("Cancel Selected Ride", Color.rgb(0xAA, 0x11, 0x11));
        cancelBtn.setPrefWidth(180);
        Button close = createModernButton("Close", PRIMARY_DARK);
        close.setPrefWidth(100);

        confirmBtn.setOnAction(e -> {
            RideTableRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showMessage("Error", "Please select a ride to confirm.", PRIMARY_DARK);
                return;
            }

            String status = selected.getStatus();
            if (!status.equalsIgnoreCase("Pending")) {
                showMessage("Cannot Confirm", "Only Pending rides can be confirmed.", PRIMARY_DARK);
                return;
            }

            try {
                int id = Integer.parseInt(selected.getRideId());
                boolean ok = rideDAO.confirmRide(id);
                if (ok) {
                    showMessage("Confirmed", "Ride confirmed.", PRIMARY_COLOR);
                    populate.run();
                } else {
                    showMessage("Error", "Failed to confirm ride.", PRIMARY_DARK);
                }
            } catch (Exception ex) {
                showMessage("Error", "Invalid ride id.", PRIMARY_DARK);
            }
        });

        cancelBtn.setOnAction(e -> {
            RideTableRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showMessage("Error", "Please select a ride to cancel.", PRIMARY_DARK);
                return;
            }

            String status = selected.getStatus();
            if (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("In Progress")) {
                showMessage("Cannot Cancel", "Ride is already In Progress or Completed.", PRIMARY_DARK);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancel");
            confirm.setContentText("Cancel ride #" + selected.getRideId() + "?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(selected.getRideId());
                    boolean ok = rideDAO.cancelRide(id);
                    if (ok) {
                        showMessage("Cancelled", "Ride cancelled.", PRIMARY_COLOR);
                        populate.run();
                    } else {
                        showMessage("Error", "Failed to cancel ride.", PRIMARY_DARK);
                    }
                } catch (Exception ex) {
                    showMessage("Error", "Invalid ride id.", PRIMARY_DARK);
                }
            }
        });

        close.setOnAction(e -> stage.close());
        btns.getChildren().addAll(confirmBtn, cancelBtn, close);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(table, btns);
        VBox.setVgrow(table, Priority.ALWAYS);

        Scene scene = new Scene(layout, 820, 460);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void submitFeedback(Rider rider) {
        List<String> rides = rideDAO.getRidesByRider(rider.getUserId());
        if (rides == null || rides.isEmpty()) {
            showMessage("Feedback", "No rides to provide feedback for.", PRIMARY_DARK);
            return;
        }

        showLargeText("Your Rides", String.join("\n", rides));

        TextInputDialog rideDialog = new TextInputDialog();
        rideDialog.setTitle("Submit Feedback");
        rideDialog.setHeaderText("Enter Ride ID to submit feedback for:");
        rideDialog.setContentText("Ride ID:");

        Optional<String> ridResult = rideDialog.showAndWait();
        if (!ridResult.isPresent())
            return;

        try {
            int rideId = Integer.parseInt(ridResult.get().trim());

            TextInputDialog ratingDialog = new TextInputDialog();
            ratingDialog.setTitle("Rating");
            ratingDialog.setHeaderText("Rating (1-5):");
            ratingDialog.setContentText("Rating:");

            Optional<String> ratingResult = ratingDialog.showAndWait();
            if (!ratingResult.isPresent())
                return;

            int rating = Integer.parseInt(ratingResult.get().trim());
            if (rating < 1 || rating > 5) {
                showMessage("Error", "Rating must be between 1 and 5", PRIMARY_DARK);
                return;
            }

            TextInputDialog commentsDialog = new TextInputDialog();
            commentsDialog.setTitle("Comments");
            commentsDialog.setHeaderText("Comments:");
            commentsDialog.setContentText("Comments:");

            Optional<String> commentsResult = commentsDialog.showAndWait();
            String comments = commentsResult.orElse("");

            int fbId = feedbackDAO.createFeedback(rideId, rating, comments);
            if (fbId > 0) {
                showMessage("Success", "Feedback submitted.  ID: " + fbId, PRIMARY_COLOR);
            } else {
                showMessage("Error", "Failed to submit feedback.", PRIMARY_DARK);
            }
        } catch (Exception e) {
            showMessage("Error", "Invalid input.", PRIMARY_DARK);
        }
    }

    // Helper class for TableView
    public static class RideTableRow {
        private final SimpleStringProperty rideId;
        private final SimpleStringProperty route;
        private final SimpleStringProperty other;
        private final SimpleStringProperty fare;
        private final SimpleStringProperty status;
        private final SimpleStringProperty time;

        public RideTableRow(String rideId, String route, String other, String fare, String status, String time) {
            this.rideId = new SimpleStringProperty(rideId);
            this.route = new SimpleStringProperty(route);
            this.other = new SimpleStringProperty(other);
            this.fare = new SimpleStringProperty(fare);
            this.status = new SimpleStringProperty(status);
            this.time = new SimpleStringProperty(time);
        }

        public String getRideId() {
            return rideId.get();
        }

        public String getRoute() {
            return route.get();
        }

        public String getOther() {
            return other.get();
        }

        public String getFare() {
            return fare.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getTime() {
            return time.get();
        }
    }

    // ----------------- Main -----------------

    public static void main(String[] args) {
        launch(args);
    }
}