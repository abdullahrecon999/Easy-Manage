package app.controllers.TeacherCon;

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
    private Label username, Name, Email, Phone,dob, addr,Exp,TechDept,NumStd;


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

    }

    private void setFields() throws SQLException {
        Connection con= DBConn.getConnection();
        con.createStatement();
        String query="SELECT USERNAME, FIRST_NAME , LAST_NAME , EMAIL , PHONE_NO , DOB , HOUSE ,Street, Sector , EXPERIENCE, d.DEPT_NAME \n" +
                "FROM TEACHER t INNER JOIN DEPARTMENT d ON (d.DEPT_ID=t.DEPARTMENT_DEPT_ID)\n" +
                "WHERE USERNAME = ? ";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1,Username);
        ResultSet rs=pstmt.executeQuery();
        rs.next();
        System.out.println("Username: "+rs.getString(1));
        this.Exp.setText(rs.getString(10));
        this.Name.setText(rs.getString(2)+" "+rs.getString(3));
        this.Email.setText(rs.getString(4));
        this.Phone.setText(rs.getString(5));
        this.dob.setText(rs.getString(6));
        this.TechDept.setText(rs.getString(11));
        this.addr.setText(rs.getString(7)+", "+rs.getString(8)+", "+rs.getString(9));

        String query1="SELECT count(*) FROM \n" +
                "TEACHER t \n" +
                "INNER JOIN \n" +
                "COURSE c ON (t.TEACH_ID=c.TEACHER_TEACH_ID)\n" +
                "INNER JOIN \n" +
                "ENROLLS e ON (c.COURSE_ID=e.COURSE_COURSE_ID)\n" +
                "WHERE t.USERNAME = ?";
        pstmt = con.prepareStatement(query1);
        pstmt.setString(1,Username);
        rs=pstmt.executeQuery();
        rs.next();
        this.NumStd.setText(rs.getString(1));
    }
}

