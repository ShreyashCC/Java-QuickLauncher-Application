import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class Main extends Application {

    private Stage primaryStage;
    private ListView<Option> listView;
    private Map<String, Option> optionsMap;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        optionsMap = new LinkedHashMap<>();
        listView = new ListView<>();
        loadOptions();

        Label infoLabel = new Label();

        Button addOptionButton = new Button("Add Option");
        addOptionButton.setOnAction(e -> openAddOptionWindow());

        Button deleteButton = new Button("Delete Option");
        deleteButton.setOnAction(e -> deleteOption());

        VBox managementPane = new VBox(10, addOptionButton, deleteButton);
        managementPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(new VBox(20, listView, managementPane, infoLabel));

        Scene scene = new Scene(root, 600, 300);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Option Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Handle double-click to open selected option
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Option selectedOption = listView.getSelectionModel().getSelectedItem();
                if (selectedOption != null) {
                    handleSelection(selectedOption, infoLabel);
                } else {
                    infoLabel.setText("Please select an option from the list.");
                }
            }
        });
    }

    private void openAddOptionWindow() {
        Stage addOptionStage = new Stage();
        VBox addOptionPane = new VBox(10);

        TextField descField = new TextField();
        descField.setPromptText("Option Description");

        TextField cmdField = new TextField();
        cmdField.setPromptText("Command (comma separated)");

        TextField linkField = new TextField();
        linkField.setPromptText("Link (comma separated)");

        Button addOptionButton = new Button("Add Option");
        addOptionButton.setOnAction(e -> {
            String desc = descField.getText();
            String cmds = cmdField.getText();
            String links = linkField.getText();
            if (!desc.isEmpty()) {
                Option option = optionsMap.computeIfAbsent(desc, Option::new);
                if (!cmds.isEmpty()) {
                    for (String cmd : cmds.split(",")) {
                        option.addCommand(cmd.trim());
                    }
                }
                if (!links.isEmpty()) {
                    for (String link : links.split(",")) {
                        option.addLink(link.trim());
                    }
                }
                saveOptions();
                loadOptions();
                descField.clear();
                cmdField.clear();
                linkField.clear();
            }
        });

        addOptionPane.getChildren().addAll(descField, cmdField, linkField, addOptionButton);
        addOptionPane.setPadding(new Insets(10));

        Scene addOptionScene = new Scene(addOptionPane, 400, 200);
        addOptionStage.setTitle("Add Option");
        addOptionStage.setScene(addOptionScene);
        addOptionStage.show();
    }

    private void deleteOption() {
        Option selectedOption = listView.getSelectionModel().getSelectedItem();

        if (selectedOption == null) {
            return;
        }

        optionsMap.remove(selectedOption.getDescription());
        saveOptions();
        loadOptions();
    }

    private void loadOptions() {
        listView.getItems().clear();
        optionsMap.clear();

        try (BufferedReader cmdReader = new BufferedReader(new FileReader("commands.txt"));
             BufferedReader linkReader = new BufferedReader(new FileReader("links.txt"))) {

            String line;
            while ((line = cmdReader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    Option option = new Option(parts[0]);
                    for (String cmd : parts[1].split(",")) {
                        option.addCommand(cmd.trim());
                    }
                    optionsMap.put(option.getDescription(), option);
                }
            }
            
            while ((line = linkReader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    Option option = optionsMap.get(parts[0]);
                    if (option != null) {
                        for (String link : parts[1].split(",")) {
                            option.addLink(link.trim());
                        }
                    }
                }
            }

            listView.getItems().addAll(optionsMap.values());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveOptions() {
        try (BufferedWriter cmdWriter = new BufferedWriter(new FileWriter("commands.txt"));
             BufferedWriter linkWriter = new BufferedWriter(new FileWriter("links.txt"))) {

            for (Option option : optionsMap.values()) {
                String desc = option.getDescription();
                for (String cmd : option.getCommands()) {
                    cmdWriter.write(desc + "=" + cmd);
                    cmdWriter.newLine();
                }
                for (String link : option.getLinks()) {
                    linkWriter.write(desc + "=" + link);
                    linkWriter.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSelection(Option option, Label infoLabel) {
        new Thread(() -> {
            try {
                // Execute commands
                for (String command : option.getCommands()) {
                    String[] cmdParts = command.trim().split("\\s+");
                    if (cmdParts.length > 0) {
                        new ProcessBuilder(cmdParts).start();
                    }
                }

                // Open links in browser
                for (String link : option.getLinks()) {
                    openInBrowser(link.trim());
                }

                javafx.application.Platform.runLater(() -> primaryStage.setIconified(true));
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    infoLabel.setText("Error: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void openInBrowser(String url) {
        try {
            new ProcessBuilder("xdg-open", url).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
