package app.controllers;

import app.controllers.StudentCon.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentDCon implements Initializable {

    @FXML
    private AnchorPane MainPane;
    @FXML
    private Pane DisplayPane;
    private String Username="";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //System.out.println("Student Controller");
    }

    private void LoadUi(String ui){
        try {
            //DisplayPane.getChildren().removeAll();
            FXMLLoader loader= new FXMLLoader(getClass().getResource("/app/FXML_Views/Student/"+ui+".fxml"));
            Pane newpane = loader.load();           // execute .load before getController
            if(ui.equalsIgnoreCase("Profile")){
                Profile prf = loader.getController();
                prf.setUname(Username);
            }
            else if (ui.equalsIgnoreCase("Course")){
                CourseCon crs = loader.getController();
                crs.setUname(Username);
            }
            else if (ui.equalsIgnoreCase("Transport")){
                Transport trs = loader.getController();
                trs.setUname(Username);
            }
            else if (ui.equalsIgnoreCase("Library")){
                Library lib = loader.getController();
                lib.setUname(Username);
            }
            else if (ui.equalsIgnoreCase("Finance")){
                Finance fin = loader.getController();
                fin.setUname(Username);
            }
            DisplayPane.getChildren().add(newpane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadU1(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Profile");
    }
    public void LoadU2(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Course");
    }
    public void LoadU4(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Library");
    }
    public void LoadU5(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Transport");
    }
    public void LoadU6(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Finance");
    }

    @FXML
    private void CloseSD(javafx.scene.input.MouseEvent event){
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setUname(String name){
        this.Username=name;
        //System.out.println("Username: "+Username);
    }
}