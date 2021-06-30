package app.controllers;

import app.DAO.DBConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class loginCon implements Initializable {

    @FXML
    private TextField loginPass;
    @FXML
    private TextField loginEmail;
    @FXML
    private Button CloseButton;

    private double offset_x;
    private double offset_y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void Login(ActionEvent event1) throws SQLException {
         if(!loginEmail.getText().isEmpty() && !loginPass.getText().isEmpty()){

             try{
                 Connection con= DBConn.getConnection();
                 con.createStatement();
                 String query="SELECT * FROM LoginView WHERE USERNAME=? AND PASSWORD=?";
                 PreparedStatement pstmt = con.prepareStatement(query);
                 pstmt.setString(1,loginEmail.getText());
                 pstmt.setString(2,loginPass.getText());
                 ResultSet rs=pstmt.executeQuery();

                 if(rs.next()){
                     String type=rs.getString(1);
                     if(rs.getString(1).equalsIgnoreCase("student")){
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/FXML_Views/Student/StudentDashboard.fxml"));
                         Parent root1 = (Parent) fxmlLoader.load();
                         StudentDCon std = fxmlLoader.getController();
                         std.setUname(loginEmail.getText());
                         Stage stage = new Stage();
                         stage.initModality(Modality.APPLICATION_MODAL);
                         stage.initStyle(StageStyle.UNDECORATED);
                         Scene scene =new Scene(root1);
                         scene.setOnMousePressed(event->{
                             offset_x = event.getSceneX();
                             offset_y = event.getSceneY();
                         });
                         scene.setOnMouseDragged(event -> {
                             stage.setX(event.getScreenX() - offset_x);
                             stage.setY(event.getScreenY() - offset_y);
                         });
                         stage.setScene(scene);
                         stage.show();
                         Controller.CloseMain();
                     }
                     else if(rs.getString(1).equalsIgnoreCase("teacher")){
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/FXML_Views/Teacher/TeacherDashboard.fxml"));
                         Parent root1 = (Parent) fxmlLoader.load();
                         TeacherDCon td = fxmlLoader.getController();
                         td.setUname(loginEmail.getText());
                         Stage stage = new Stage();
                         stage.initModality(Modality.APPLICATION_MODAL);
                         stage.initStyle(StageStyle.UNDECORATED);
                         Scene scene =new Scene(root1);
                         scene.setOnMousePressed(event->{
                             offset_x = event.getSceneX();
                             offset_y = event.getSceneY();
                         });
                         scene.setOnMouseDragged(event -> {
                             stage.setX(event.getScreenX() - offset_x);
                             stage.setY(event.getScreenY() - offset_y);
                         });
                         stage.setScene(scene);
                         stage.show();
                         Controller.CloseMain();
                     }
                     else if(rs.getString(1).equalsIgnoreCase("admin")){
                         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/FXML_Views/Admin/AdminDashboard.fxml"));
                         Parent root1 = (Parent) fxmlLoader.load();
                         AdminDCon td = fxmlLoader.getController();
                         td.setUname(loginEmail.getText());
                         Stage stage = new Stage();
                         stage.initModality(Modality.APPLICATION_MODAL);
                         stage.initStyle(StageStyle.UNDECORATED);
                         Scene scene =new Scene(root1);
                         scene.setOnMousePressed(event->{
                             offset_x = event.getSceneX();
                             offset_y = event.getSceneY();
                         });
                         scene.setOnMouseDragged(event -> {
                             stage.setX(event.getScreenX() - offset_x);
                             stage.setY(event.getScreenY() - offset_y);
                         });
                         stage.setScene(scene);
                         stage.show();
                         Controller.CloseMain();
                     }
                     else{
                         Alert alert1 = new Alert(Alert.AlertType.WARNING, "Incorrect Credentials", ButtonType.OK);
                         alert1.showAndWait();
                     }
                 }
                 else{
                     Alert alert1 = new Alert(Alert.AlertType.WARNING, "Incorrect Credentials", ButtonType.OK);
                     alert1.showAndWait();
                 }

             }catch(SQLException | IOException e) {
                 e.printStackTrace();
             }
             loginEmail.setStyle("-fx-border-color: #DEDEE4;");
             loginPass.setStyle("-fx-border-color: #DEDEE4;");
         }
         else if (loginEmail.getText().isEmpty() && !loginPass.getText().isEmpty()){
             loginEmail.setStyle("-fx-border-color: #FF0000;");
             loginPass.setStyle("-fx-border-color: #DEDEE4;");
         }
         else if(!loginEmail.getText().isEmpty() && loginPass.getText().isEmpty()){
             loginEmail.setStyle("-fx-border-color: #DEDEE4;");
             loginPass.setStyle("-fx-border-color: #FF0000;");
         }
         else{
             loginEmail.setStyle("-fx-border-color: #FF0000;");
             loginPass.setStyle("-fx-border-color: #FF0000;");
         }
    }
}
