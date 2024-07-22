package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;

    /**
     * Az alkalmazás fo ablakának a létrehozásáért és megnyitásáért felel.
     * @param stage Kezdo ablak. Ez fog megnyílni eloszor.
     * @throws IOException Ha az alkalmazás ablakát leíró fxml fájl nem található, akkor dobódik.
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 940, 540);
        stage.setScene(scene);
        stage.setTitle("XML converter");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))));
        stage.show();
    }

    /**
     * Az adott FXML fájl betoltéséért és annak tartalmának inicializálásáért felelős metódus.
     *
     * @param fxml Az FXML fájl elérési útja (a fájlnév kivételével).
     * @return Az inicializált Java objektum, amely a betöltött FXML fájl tartalmát reprezentálja.
     * @throws IOException Ha valamilyen hiba torténik az FXML fájl betoltése kozben.
     */
    static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}