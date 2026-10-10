package com.apaza.chitibrowser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;

public class HelloController {

    @FXML    private TextField tfPath;
    @FXML    private WebView wvNavegador;

    public void setTfPath(String path) {
        if (path != null) {
            tfPath.setText(path);
        }
    }

    @FXML
    void cargarSitio(ActionEvent event) {
        WebEngine we = wvNavegador.getEngine();
//        we.load("https://www.google.com");
        String path = tfPath.getText().trim();
        if (path.isEmpty()) {
            return;
        }
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            we.load(file.toURI().toString());
        } else {
            we.load(path);
        }
    }
}
