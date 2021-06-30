package app.controllers.StudentCon;

import app.DAO.DBConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Finance implements Initializable {

    private String Username;
    private String amount;
    private String Std_id;
    Connection con;

    @FXML
    private Label FeePayed;

    @FXML
    private TitledPane PayFeePane;

    @FXML
    private Label Amount;

    @FXML
    private Label Ddate;

    @FXML
    private TableView<FeeReciepts> Reciepts;

    @FXML
    private TableColumn<FeeReciepts, String> P_date;

    @FXML
    private TableColumn<FeeReciepts, String> A_payed;

    ObservableList<FeeReciepts> Fee_r = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con= DBConn.getConnection();
        PayFeePane.setDisable(true);
    }

    public void setUname(String name){
        this.Username=name;
        String query= "SELECT s.STUDENT_ID FROM STUDENT s WHERE s.USERNAME = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,Username);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next()) {
                this.Std_id=rs.getString(1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        updateReciepts();
        getFee();
    }

    private void updateReciepts(){
        Fee_r.clear();
        try {
            con.createStatement();
            String query="SELECT f.PAYED_DATE , f.AMOUNT \n" +
                    "FROM FEE f INNER JOIN STUDENT s ON (s.STUDENT_ID=f.STUDENT_STUDENT_ID)\n" +
                    "WHERE s.USERNAME = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,Username);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                Fee_r.add(new FeeReciepts(rs.getString(1),rs.getString(2)));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        P_date.setCellValueFactory(new PropertyValueFactory<>("P_date"));
        A_payed.setCellValueFactory(new PropertyValueFactory<>("amount"));

        Reciepts.setItems(Fee_r);
    }

    private void getFee(){
        System.out.println("Username: "+Username);
        String query= "SELECT PAYED_DATE \n" +
                "FROM FEE f \n" +
                "INNER JOIN STUDENT s ON (f.STUDENT_STUDENT_ID=s.STUDENT_ID)\n" +
                "WHERE s.USERNAME = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,Username);
            ResultSet rs=pstmt.executeQuery();

            do{
                if(!rs.next()){
                    System.out.println("No date");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(Calendar.DATE, 10);
                    String duedate = dateFormat.format(c.getTime());
                    FeePayed.setText("Fee Not Payed");
                    Ddate.setText(duedate);
                    PayFeePane.setDisable(false);
                    generateFee();
                }
                else {
                    // Check if difference between today and this date is >=30 then generate Fee
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date today = new Date();
                    System.out.println("Today: " + dateFormat.format(today));
                    Date db = new Date();
                    db=rs.getDate(1);
                    System.out.println("DB: " + db);

                    Date date1 = dateFormat.parse(dateFormat.format(today));
                    System.out.println("DateFormat: "+dateFormat.format(today));
                    Date date2 = dateFormat.parse(db.toString());
                    long diff = date2.getTime() - date1.getTime();
                    long days=Math.abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if(days>=30){
                        Calendar c = Calendar.getInstance();
                        c.setTime(date2);
                        c.add(Calendar.DATE, 10);
                        String duedate = dateFormat.format(c.getTime());
                        FeePayed.setText("Fee Not Payed");
                        Ddate.setText(duedate);
                        PayFeePane.setDisable(false);
                        generateFee();
                    }else{
                        FeePayed.setText("Fee Payed");
                        updateReciepts();
                        FeePayed.setStyle("-fx-border-color: #1bff00; -fx-padding:4; -fx-border-radius:3");
                    }
                }
            }while(rs.next());

        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        }
    }

    private void generateFee(){
        String query= "SELECT count(*)\n" +
                "FROM student s\n" +
                "INNER JOIN ENROLLS e ON (s.STUDENT_ID=e.STUDENT_STUDENT_ID)\n" +
                "WHERE s.USERNAME = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,Username);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next()) {
                int fee = Integer.parseInt(rs.getString(1)) * 16000;
                Amount.setText(String.valueOf(fee));
                this.amount=String.valueOf(fee);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    private void PayFee(ActionEvent event){
        String query= "INSERT INTO FEE\n" +
                "(RECIEPT_NO, AMOUNT, STUDENT_STUDENT_ID, PAYED_DATE, FINE)\n" +
                "SELECT COALESCE(MAX(f.RECIEPT_NO),0)+1  ,?,?,?,? \n" +
                "FROM FEE f";
        PreparedStatement pstmt = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,amount);
            pstmt.setString(2,Std_id);
            pstmt.setDate(3, java.sql.Date.valueOf(dateFormat.format(today)));
            pstmt.setString(4,"0");
            ResultSet rs=pstmt.executeQuery();
            con.commit();
            PayFeePane.setDisable(true);
            FeePayed.setText("Fee Payed");
            FeePayed.setStyle("-fx-border-color: #1bff00; -fx-padding:4; -fx-border-radius:3");
            updateReciepts();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public class FeeReciepts{
        String P_date, amount;

        public FeeReciepts(String p_date, String amount) {
            P_date = p_date;
            this.amount = amount;
        }

        public String getP_date() {
            return P_date;
        }

        public String getAmount() {
            return amount;
        }
    }
}
