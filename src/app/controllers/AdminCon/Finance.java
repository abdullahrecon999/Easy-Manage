package app.controllers.AdminCon;

import app.DAO.DBConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Finance implements Initializable {

    private String Username;
    Connection con;

    @FXML
    private Label income;

    @FXML
    private Label cost;

    @FXML
    private TableView<SalaryData> SalTable;

    @FXML
    private TableColumn<SalaryData, String> T_name;

    @FXML
    private TableColumn<SalaryData, String> T_amount;

    @FXML
    private TableColumn<SalaryData, String> PayedDate;


    ObservableList<SalaryData> SalDat = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con= DBConn.getConnection();
        GetIncome();
        GetCost();
        GetSalaries();
        PaySalary();
    }

    public void setUname(String name){
        this.Username=name;
    }

    private void GetSalaries(){
        try {
            con.createStatement();
            String query="SELECT t.FIRST_NAME ||' '|| t.LAST_NAME \"Name\", s.AMOUNT, s.PAYMENT_DATE, t.Username\n" +
                    "FROM SALARY s INNER JOIN TEACHER t ON (t.TEACH_ID=s.TEACHER_TEACH_ID)";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();
            SalDat.clear();
            while(rs.next()){
                SalDat.add(new SalaryData(rs.getString(1),rs.getString(3),rs.getString(2),rs.getString(4)));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        T_name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        PayedDate.setCellValueFactory(new PropertyValueFactory<>("P_Date"));
        T_amount.setCellValueFactory(new PropertyValueFactory<>("Amount"));

        SalTable.setItems(SalDat);
    }

    private void PaySalary(){
        TableColumn<SalaryData, Void> colBtn = new TableColumn("Pay Salary");

        Callback<TableColumn<SalaryData, Void>, TableCell<SalaryData, Void>> cellFactory = new Callback<TableColumn<SalaryData, Void>, TableCell<SalaryData, Void>>() {
            @Override
            public TableCell<SalaryData, Void> call(final TableColumn<SalaryData, Void> param) {
                final TableCell<SalaryData, Void> cell = new TableCell<SalaryData, Void>() {

                    private final Button btn = new Button("Pay");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            SalaryData data = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Pay Salary to "+data.getName()+" ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                            alert.showAndWait();
                            if(alert.getResult() == ButtonType.YES){
                                try {
                                    con.createStatement();
                                    String query="UPDATE SALARY s SET PAYMENT_DATE = TO_DATE(sysdate)\n" +
                                            "WHERE s.TEACHER_TEACH_ID \n" +
                                            "IN (SELECT t.TEACH_ID FROM TEACHER t WHERE s.TEACHER_TEACH_ID=t.TEACH_ID AND t.USERNAME=?)";
                                    PreparedStatement pstmt = con.prepareStatement(query);
                                    pstmt.setString(1,data.getUname());
                                    ResultSet rs=pstmt.executeQuery();
                                    System.out.println("Uname: "+data.getUname());
                                    con.commit();
                                    GetSalaries();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                // Check if salary Payed recently
                            SalaryData data = getTableView().getItems().get(getIndex());

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date today = new Date();

                            Date date1 = null;
                            try {
                                if(data.getP_Date()==null){
                                    btn.setDisable(false);
                                }
                                else{
                                    date1 = dateFormat.parse(dateFormat.format(today));
                                    System.out.println("DateFormat: "+dateFormat.format(today));
                                    Date date2 = dateFormat.parse(data.getP_Date());
                                    long diff = date2.getTime() - date1.getTime();
                                    long days=Math.abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                                    System.out.println("Days: "+ days);
                                    if(Math.abs(days)<=30){
                                        btn.setDisable(true);
                                    }
                                    else{
                                        btn.setDisable(false);
                                    }
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        SalTable.getColumns().add(colBtn);
    }

    private void GetIncome(){
        try {
            con.createStatement();
            String query="SELECT UNIQUE sum(AMOUNT) \n" +
                        "FROM FEE f ";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                income.setText(NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(rs.getString(1))).toString());
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void GetCost(){
        try {
            con.createStatement();
            String query="SELECT UNIQUE sum(Amount) FROM SALARY s WHERE s.PAYMENT_DATE IS NOT null";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();

            if(rs.next()){
                cost.setText(NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(rs.getString(1))));
            }
            else{
                cost.setText("00");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public class SalaryData{
        String Name, P_Date, Amount,T_Username;

        public SalaryData(String name, String p_Date, String amount,String Uname) {
            Name = name;
            P_Date = p_Date;
            Amount = amount;
            T_Username=Uname;
        }

        public String getName() {
            return Name;
        }

        public String getUname() {
            return T_Username;
        }

        public String getP_Date() {
            return P_Date;
        }

        public String getAmount() {
            return Amount;
        }
    }
}
