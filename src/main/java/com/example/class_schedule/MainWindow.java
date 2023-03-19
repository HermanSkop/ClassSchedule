package com.example.class_schedule;

import parser.ScheduleHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("schedule.fxml"));
        Scene scene = new Scene(loader.load(), 1500, 700);
        stage.setTitle("PJAIT Schedule");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}