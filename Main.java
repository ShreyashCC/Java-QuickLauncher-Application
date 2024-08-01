import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main extends Application {

    private Stage primaryStage; // Store the primary stage

    @Override
    public void start(Stage stage) {
        primaryStage = stage; // Initialize the primary stage
        // Create UI components
        ListView<String> listView = new ListView<>();
        listView.setPrefSize(300, 160); // Set preferred size for the ListView
        listView.setMaxHeight(Region.USE_PREF_SIZE); // Restrict the maximum height

        Label infoLabel = new Label();

        // Define options
        listView.getItems().addAll(
            "1. DSA",
            "2. Development",
            "3. Placement",
            "4. AI/ML",
            "5. Linux Customization",
            "6. Exit"
        );

        // Handle Enter key to open selected option
        listView.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    String selected = listView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        handleSelection(selected, infoLabel);
                    } else {
                        infoLabel.setText("Please select an option from the list.");
                    }
                    break;
                default:
                    break;
            }
        });

        // Handle double-click to open selected option
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    handleSelection(selected, infoLabel);
                } else {
                    infoLabel.setText("Please select an option from the list.");
                }
            }
        });

        // Layout
        VBox root = new VBox(5, listView, infoLabel); // Reduced spacing
        root.setPadding(new Insets(0)); // Remove padding
        root.setSpacing(5); // Minimal spacing between components
        Scene scene = new Scene(root, 320, 240); // Adjusted scene size

        // Apply CSS
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Option Selector");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleSelection(String selection, Label infoLabel) {
        new Thread(() -> {
            try {
                switch (selection) {
                    case "1. DSA":
                        openInBrowser("https://leetcode.com/problemset/");
                        openInBrowser("https://takeuforward.org/interviews/strivers-sde-sheet-top-coding-interview-problems");
                        break;
                    case "2. Development":
                        openVisualStudioCode();
                        openInBrowser("https://www.youtube.com");
                        openInBrowser("https://chatgpt.com/");
                        break;
                    case "3. Placement":
                        openInBrowser("https://www.linkedin.com/feed/");
                        openInBrowser("https://app.pod.ai/");
                        break;
                    case "4. AI/ML":
                        openInBrowser("https://drive.google.com/drive/folders/1f7zLp-VkUbswDvY9FkemMLMJzloFDxJY");
                        openInBrowser("https://colab.research.google.com/?utm_source=scs-index");
                        break;
                    case "5. Linux Customization":
                        openInBrowser("https://www.reddit.com/r/unixporn/");
                        openInBrowser("https://www.gnome-look.org/browse/");
                        break;
                    case "6. Exit":
                        System.exit(0);
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected value: " + selection);
                }
                // Minimize the stage after selection
                javafx.application.Platform.runLater(() -> primaryStage.setIconified(true));
            } catch (IOException | URISyntaxException e) {
                javafx.application.Platform.runLater(() -> {
                    infoLabel.setText("Error: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void openVisualStudioCode() throws IOException {
        // Adjust the path to the VSCode executable as needed
        String command = "code"; // Ensure 'code' command is available in the PATH
        new ProcessBuilder(command).start();
    }

    private void openInBrowser(String url) throws IOException, URISyntaxException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } else {
            throw new UnsupportedOperationException("Desktop is not supported on this system.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
