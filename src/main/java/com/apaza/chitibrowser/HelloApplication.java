package com.apaza.chitibrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private String tfPath;

    @Override
    public void start(Stage stage) throws IOException {
        var rawArgs = getParameters().getRaw();
        if (!rawArgs.isEmpty()) {
            tfPath = rawArgs.getFirst();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        HelloController controller = fxmlLoader.getController();
        controller.setTfPath(tfPath);

        stage.setTitle("chitiBrowser");
        stage.setScene(scene);
        stage.show();
    }
}
