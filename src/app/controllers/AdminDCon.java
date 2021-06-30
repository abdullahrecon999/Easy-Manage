package app.controllers;

import app.controllers.AdminCon.*;
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

public class AdminDCon implements Initializable {

    @FXML
    private AnchorPane MainPane;
    @FXML
    private Pane DisplayPane;
    private String Username="";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    private void LoadUi(String ui){
        try {
            FXMLLoader loader= new FXMLLoader(getClass().getResource("/app/FXML_Views/Admin/"+ui+".fxml"));
            Pane newpane = loader.load();           // execute .load before getController
            if(ui.equalsIgnoreCase("Profile")){
                Profile prf = loader.getController();
                prf.setUname(Username);
            }
            else if (ui.equalsIgnoreCase("Finance")){
                Finance crs = loader.getController();
                crs.setUname(Username);
            }
            else if (ui.equalsIgnoreCase("Controls")){
                Controls ctrl = loader.getController();
                ctrl.setUname(Username);
            }

            DisplayPane.getChildren().add(newpane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadU1(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Profile");
    }
    public void LoadU3(javafx.scene.input.MouseEvent mouseEvent) {
        LoadUi("Controls");
    }
    public void LoadU4(javafx.scene.input.MouseEvent mouseEvent) {
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
    }
}