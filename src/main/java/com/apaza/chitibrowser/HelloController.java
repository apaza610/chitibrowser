package com.apaza.chitibrowser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @FXML
    void resizeVentana(ActionEvent event) {
        System.out.println("resizing the ventana");
        
        String currentUrl = wvNavegador.getEngine().getLocation();
        if (currentUrl == null || currentUrl.isEmpty()) {
            System.out.println("no page loaded");
            return;
        }
        
        String lowerUrl = currentUrl.toLowerCase();
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg"};
        boolean isImage = false;
        for (String ext : imageExtensions) {
            if (lowerUrl.endsWith(ext) || lowerUrl.contains(ext + "?")) {
                isImage = true;
                break;
            }
        }
        
        if (!isImage) {
            System.out.println("current content is not an image");
            return;
        }
        
        // Get the image file, properly decoding URL-encoded paths (e.g., %20 for spaces)
        File imageFile = null;
        if (currentUrl.startsWith("file://")) {
            try {
                imageFile = new File(new URL(currentUrl).toURI());
            } catch (Exception e) {
                // Fallback: simple substring if URI parsing fails
                String imagePath = currentUrl.substring(7);
                int queryIndex = imagePath.indexOf('?');
                if (queryIndex != -1) {
                    imagePath = imagePath.substring(0, queryIndex);
                }
                imageFile = new File(imagePath);
            }
        }
        
        double imageWidth = -1;
        double imageHeight = -1;

        try {
            // Special handling for SVG: parse viewBox attribute
            if (lowerUrl.endsWith(".svg")) {
                String svgContent;
                if (imageFile != null) {
                    if (!imageFile.exists()) {
                        System.out.println("svg file not found: " + imageFile.getAbsolutePath());
                        return;
                    }
                    svgContent = new String(Files.readAllBytes(imageFile.toPath()));
                } else {
                    try (InputStream is = new URL(currentUrl).openStream()) {
                        svgContent = new String(is.readAllBytes());
                    }
                }
                // Parse viewBox="x y width height"
                Pattern viewBoxPattern = Pattern.compile("viewBox\\s*=\\s*[\"']([^\"']+)[\"']");
                Matcher matcher = viewBoxPattern.matcher(svgContent);
                if (matcher.find()) {
                    String[] parts = matcher.group(1).trim().split("\\s+");
                    if (parts.length >= 4) {
                        imageWidth = Double.parseDouble(parts[2]);
                        imageHeight = Double.parseDouble(parts[3]);
                    }
                }
                // Fallback: check width/height attributes on <svg> element
                if (imageWidth <= 0 || imageHeight <= 0) {
                    Pattern svgTagPattern = Pattern.compile("<svg[^>]*>");
                    Matcher svgMatcher = svgTagPattern.matcher(svgContent);
                    if (svgMatcher.find()) {
                        String svgTag = svgMatcher.group();
                        Pattern widthPattern = Pattern.compile("width\\s*=\\s*[\"']([^\"']+)[\"']");
                        Pattern heightPattern = Pattern.compile("height\\s*=\\s*[\"']([^\"']+)[\"']");
                        Matcher wMatcher = widthPattern.matcher(svgTag);
                        Matcher hMatcher = heightPattern.matcher(svgTag);
                        if (wMatcher.find() && imageWidth <= 0) {
                            String w = wMatcher.group(1).replace("px", "");
                            imageWidth = Double.parseDouble(w);
                        }
                        if (hMatcher.find() && imageHeight <= 0) {
                            String h = hMatcher.group(1).replace("px", "");
                            imageHeight = Double.parseDouble(h);
                        }
                    }
                }
            } else {
                // Non-SVG images: use JavaFX Image class
                Image image;
                if (imageFile != null) {
                    if (!imageFile.exists()) {
                        System.out.println("image file not found: " + imageFile.getAbsolutePath());
                        return;
                    }
                    try (InputStream is = new FileInputStream(imageFile)) {
                        image = new Image(is);
                    }
                } else {
                    image = new Image(new URL(currentUrl).openStream());
                }
                imageWidth = image.getWidth();
                imageHeight = image.getHeight();
            }

            if (imageWidth > 0 && imageHeight > 0) {
                // Add padding for the toolbar (approximately 30px)
                double toolbarHeight = 30;
                wvNavegador.getScene().getWindow().setWidth(imageWidth + 10);
                wvNavegador.getScene().getWindow().setHeight(imageHeight + toolbarHeight + 10);
                System.out.println("window resized to: " + (imageWidth + 10) + "x" + (imageHeight + toolbarHeight + 10));
            }
        } catch (IOException e) {
            System.out.println("error loading image: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("error parsing svg dimensions: " + e.getMessage());
        }
    }
}
