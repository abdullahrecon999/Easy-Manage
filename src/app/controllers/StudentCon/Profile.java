package app.controllers.StudentCon;

import app.DAO.DBConn;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Profile implements Initializable {

    private String Username="";
    @FXML
    private Label username,Roll, Name, Email, Phone,dob, addr;


    public void setUname(String name){
        this.Username=name;
        this.username.setText(Username);
        try {
            setFields();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Profile Opened for Username: "+Username);
    }

    private void setFields() throws SQLException {
        Connection con= DBConn.getConnection();
        con.createStatement();
        String query="SELECT * FROM Student WHERE USERNAME=?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1,Username);
        ResultSet rs=pstmt.executeQuery();
        rs.next();
        System.out.println("Username: "+rs.getString(6));
        this.Roll.setText(rs.getString(6));
        this.Name.setText(rs.getString(4)+" "+rs.getString(5));
        this.Email.setText(rs.getString(10));
        this.Phone.setText(rs.getString(14));
        this.dob.setText(rs.getString(9));
        this.addr.setText(rs.getString(11)+", "+rs.getString(12)+", "+rs.getString(13));
    }
}
