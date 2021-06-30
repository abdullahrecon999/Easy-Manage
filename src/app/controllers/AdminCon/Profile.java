package app.controllers.AdminCon;

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
    Connection con;
    @FXML
    private Label username, Name, Email, Phone,dob, addr;

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
        con= DBConn.getConnection();
    }

    private void setFields() throws SQLException {
        con.createStatement();
        String query="SELECT USERNAME, FIRST_NAME , LAST_NAME , EMAIL , PHONE_NO , DOB , HOUSE ,Street, Sector\n" +
                "FROM ADMIN a \n" +
                "WHERE USERNAME = ? ";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1,Username);
        ResultSet rs=pstmt.executeQuery();
        rs.next();
        System.out.println("Username: "+rs.getString(1));
        this.Name.setText(rs.getString(2)+" "+rs.getString(3));
        this.Email.setText(rs.getString(4));
        this.Phone.setText(rs.getString(5));
        this.dob.setText(rs.getString(6));
        this.addr.setText(rs.getString(7)+", "+rs.getString(8)+", "+rs.getString(9));
    }
}

