package app.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private VBox vbox;
    private static Parent fxml;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        TranslateTransition t = new TranslateTransition(Duration.seconds(0.5),vbox);
        t.setToX(vbox.getLayoutX()*40);
        t.play();
        t.setOnFinished((e)->{
            try{
                fxml= FXMLLoader.load(getClass().getResource("/app/FXML_Views/Login.fxml"));
                vbox.getChildren().removeAll();
                vbox.getChildren().setAll(fxml);
            }catch (IOException ex){
                System.out.println("Error in Init: "+ex.getMessage());
            }
        });
    }

    @FXML
    private void open_login(ActionEvent event){
        TranslateTransition t = new TranslateTransition(Duration.seconds(0.5),vbox);
        t.setToX(vbox.getLayoutX()*40);
        t.play();
        t.setOnFinished((e)->{
            try{
                fxml= FXMLLoader.load(getClass().getResource("/app/FXML_Views/Login.fxml"));
                vbox.getChildren().removeAll();
                vbox.getChildren().setAll(fxml);
            }catch (IOException ex){
                System.out.println("Error in Login: "+ex.getMessage());
            }
        });
    }

    @FXML
    private void open_signup(ActionEvent event){
        TranslateTransition t = new TranslateTransition(Duration.seconds(0.5),vbox);
        t.setToX(2);
        t.play();
        t.setOnFinished((e)->{
            try{
                fxml= FXMLLoader.load(getClass().getResource("/app/FXML_Views/Signup.fxml"));
                vbox.getChildren().removeAll();
                vbox.getChildren().setAll(fxml);
            }catch (IOException ex){
                System.out.println("Error in Sign up: "+ex.getMessage());
            }
        });
    }

    @FXML
    private void close(ActionEvent event){
        Stage stg= (Stage) fxml.getScene().getWindow();
        stg.close();
    }

    public static void CloseMain(){
        Stage stg= (Stage) fxml.getScene().getWindow();
        stg.close();
    }

}
