package com.chrisb97.warehouse;

import com.chrisb97.warehouse.config.AppContext;
import com.chrisb97.warehouse.util.AlertUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            AppContext.getInstance().verifyDatabaseConnection();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(Main.class.getResource("/css/application.css").toExternalForm());
            stage.setTitle("Warehouse Inventory Manager");
            stage.setMinWidth(1050);
            stage.setMinHeight(680);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            AlertUtil.showFatalError("Application startup failed", exception.getMessage());
        } catch (RuntimeException exception) {
            AlertUtil.showFatalError(
                    "Database connection failed",
                    exception.getMessage() + "\n\nCopy database.properties.example to database.properties and configure MySQL."
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
