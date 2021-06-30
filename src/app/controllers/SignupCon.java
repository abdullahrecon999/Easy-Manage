package app.controllers;

import app.DAO.DBConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupCon implements Initializable {

    @FXML
    private TextField email;

    @FXML
    private TextField firstname;

    @FXML
    private TextField password;

    @FXML
    private TextField lastname;

    @FXML
    private TextField username;

    @FXML
    private TextField Mmarks;

    @FXML
    private TextField Imarks;

    @FXML
    private TextField house;

    @FXML
    private TextField street;

    @FXML
    private TextField sector;

    @FXML
    private TextField phone;

    @FXML
    private TextField dob;

    Connection con;
    public static final Pattern EmailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con= DBConn.getConnection();
        System.out.println(checkUsername("May"));
        System.out.println(checkUsername("May123"));
    }

    public void Submit(ActionEvent actionEvent) {
        int count=11;
        Matcher matcher=EmailRegex.matcher(email.getText());
        if(!firstname.getText().matches("[a-z A-Z]+")){
            firstname.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            firstname.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!lastname.getText().matches("[a-z A-Z]+")){
            lastname.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            lastname.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!matcher.find()){
            email.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            email.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!username.getText().matches("[a-z A-Z 0-9]+") || !checkUsername(username.getText())){
            username.setStyle("-fx-border-color: #FF0000;");
            Alert alert1 = new Alert(Alert.AlertType.WARNING, "Username Already Taken", ButtonType.OK);
            alert1.showAndWait();
            username.clear();
            count--;
        }
        else{
            username.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!password.getText().matches("[a-z A-Z 0-9]+")){
            password.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            password.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!Mmarks.getText().matches("[0-9]+")){
            Mmarks.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            Mmarks.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!Imarks.getText().matches("[0-9]+")){
            Imarks.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            Imarks.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!house.getText().matches("[a-z A-Z 0-9]+")){
            house.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            house.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!sector.getText().matches("[a-z A-Z 0-9 -/]+")){
            sector.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            sector.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!street.getText().matches("[a-z A-Z 0-9]+")){
            street.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            street.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(!phone.getText().matches("[0-9]+")){
            phone.setStyle("-fx-border-color: #FF0000;");
            count--;
        }
        else{
            phone.setStyle("-fx-border-color: #DEDEE4;");
        }
        if(count==11){
            System.out.println("All Working");
            count=11;
            PreparedStatement pstmt;
            ResultSet rs;
            try {
                con.createStatement();
                String query = "INSERT INTO STDADMISSION\n" +
                        "(STD_ID, USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, DOB, EMAIL, METRIC_MARKS, INTER_MARKS, HOUSE, STREET, SECTOR, PHONE)\n" +
                        "SELECT coalesce(MAX(s.STD_ID), 0)+1,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?\n" +
                        "FROM STDADMISSION s ";
                pstmt = con.prepareStatement(query);
                pstmt.setString(1, username.getText());
                pstmt.setString(2, password.getText());
                pstmt.setString(3, firstname.getText());
                pstmt.setString(4, lastname.getText());
                pstmt.setString(5, dob.getText());
                pstmt.setString(6, email.getText());
                pstmt.setString(7, Mmarks.getText());
                pstmt.setString(8, Imarks.getText());
                pstmt.setString(9, house.getText());
                pstmt.setString(10, street.getText());
                pstmt.setString(11, sector.getText());
                pstmt.setString(12, phone.getText());
                rs= pstmt.executeQuery();
                con.commit();
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION, "Form Submitted", ButtonType.OK);
                alert1.showAndWait();
                username.clear();
                password.clear();
                firstname.clear();
                lastname.clear();
                email.clear();
                dob.clear();
                Mmarks.clear();
                Imarks.clear();
                house.clear();
                sector.clear();
                street.clear();
                phone.clear();
            }catch(SQLException e){
                System.out.println("Application Still Pending");
            }
        }
    }

    private boolean checkUsername(String name){
        String query="SELECT * FROM (\n" +
                "SELECT s.USERNAME FROM STDADMISSION s \n" +
                "UNION\n" +
                "SELECT s2.USERNAME FROM STUDENT s2 \n" +
                ")\n" +
                "WHERE USERNAME = ?";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            con.createStatement();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,name);
            rs= pstmt.executeQuery();
            if(!rs.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
