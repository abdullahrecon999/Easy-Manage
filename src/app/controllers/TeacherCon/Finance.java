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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Finance implements Initializable {

    private String Username;
    Connection con;

    @FXML
    private Label Banner;

    @FXML
    private Label P_amount;

    @FXML
    private Label P_date;

    @FXML
    private Label P_type;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con=DBConn.getConnection();
    }

    public void setUname(String name){
        this.Username=name;
        checkPay();
    }

    private void checkPay(){
        try {
            con.createStatement();
            String query="SELECT s.AMOUNT , s.PAYMENT_DATE , s.CHEQUE_CASH \n" +
                    "FROM SALARY s \n" +
                    "INNER JOIN \n" +
                    "TEACHER t ON (t.TEACH_ID=s.TEACHER_TEACH_ID)\n" +
                    "WHERE t.USERNAME =?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,this.Username);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                if(rs.getString(2)==null){
                    System.out.println("Not Payed");
                    Banner.setText("Not Payed");
                    Banner.setStyle("-fx-background-color: red; " +
                            "-fx-border-width: 4; " +
                            "-fx-border-color: #2298f2; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 4;");
                }
                else{
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date today = new Date();

                    Date date1 = dateFormat.parse(dateFormat.format(today));
                    System.out.println("DateFormat: "+dateFormat.format(today));
                    Date date2 = dateFormat.parse(rs.getString(2));
                    long diff = date2.getTime() - date1.getTime();
                    long days=Math.abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if(Math.abs(days)>=30){
                        System.out.println("Not Payed");
                        Banner.setText("Not Payed");
                        Banner.setStyle("-fx-background-color: red; " +
                                "-fx-border-width: 4; " +
                                "-fx-border-color: #2298f2; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 4;");
                    }
                    else{
                        System.out.println("Payed");
                        P_amount.setText(rs.getString(1));
                        P_date.setText(rs.getString(2));
                        P_type.setText(rs.getString(3));
                    }
                }
            }

        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        }
    }
}
