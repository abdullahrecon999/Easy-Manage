package app.controllers.StudentCon;

import app.DAO.DBConn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Transport implements Initializable {

    private String Username;
    Connection con;
    String Bus_id;
    String Bus_no;

    @FXML
    private JFXButton BusReg;

    @FXML
    private JFXButton Cancel;

    @FXML
    private JFXButton Submit;

    @FXML
    private TitledPane RegData;

    @FXML
    private JFXComboBox<String> Routes;

    @FXML
    private JFXComboBox<String> Sizes;

    @FXML
    private Label RegLable;

    @FXML
    private Label location;

    @FXML
    private Label ptime;

    @FXML
    private Label dtime;

    @FXML
    private Label dname;

    @FXML
    private Label busno;
    @FXML
    private Label busno1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RegData.setDisable(true);
        Submit.setDisable(true);
        con= DBConn.getConnection();
    }

    public void setUname(String name) {
        this.Username=name;
        if(checkReg(name)==true){
            busno1.setText(Bus_no);
        }
    }

    @FXML
    private void Register(ActionEvent event){
        RegData.setDisable(false);

        try {
            con.createStatement();
            String query="SELECT UNIQUE ROUTE_NUM FROM ROUTE";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();
            ArrayList<String> routes=new ArrayList<>();
            routes.clear();
            while(rs.next()){
                routes.add(rs.getString(1));
            }
            Routes.getItems().clear();
            Routes.getItems().addAll(FXCollections.observableArrayList(routes));

            EventHandler<ActionEvent> Comboevent = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e) {
                            String query="SELECT b.BUS_SIZE,r.ROUTE_NUM FROM\n" +
                                    "ROUTE r INNER JOIN BUS b ON (r.ROUTE_ID=b.ROUTE_ROUTE_ID)\n" +
                                    "WHERE b.ROUTE_ROUTE_ID = ?";
                            try {
                                PreparedStatement pstmt = con.prepareStatement(query);
                                pstmt.setString(1,Routes.getValue());
                                ResultSet rs=pstmt.executeQuery();
                                ArrayList<String> sizes=new ArrayList<>();
                                sizes.clear();
                                while(rs.next()){
                                    sizes.add(rs.getString(1));
                                }
                                Sizes.getItems().clear();
                                Sizes.getItems().addAll(FXCollections.observableArrayList(sizes));

                                EventHandler<ActionEvent> Sizeevent = new EventHandler<ActionEvent>() {
                                            public void handle(ActionEvent e) {
                                                Submit.setDisable(false);
                                                try {
                                                    setFields(Routes.getValue(),Sizes.getValue());
                                                } catch (SQLException throwables) {
                                                    throwables.printStackTrace();
                                                }
                                            }
                                        };
                                Sizes.setOnAction(Sizeevent);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    };
            Routes.setOnAction(Comboevent);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setFields(String route, String size) throws SQLException {

        String query="SELECT r.LOCATION, r.PICKUP, r.DROP_OFF, d.FIRST_NAME, d.LAST_NAME, b.BUS_ID,b.Bus_no \n" +
                "FROM \n" +
                "ROUTE r \n" +
                "INNER JOIN \n" +
                "DRIVER d ON (d.BUS_BUS_ID=r.BUS_BUS_ID) \n" +
                "INNER JOIN \n" +
                "BUS b ON (b.BUS_ID=r.BUS_BUS_ID)\n" +
                "WHERE ROUTE_NUM = ? AND b.BUS_SIZE = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1,route);
        pstmt.setString(2,size);
        ResultSet rs=pstmt.executeQuery();
        while(rs.next()){
            location.setText(rs.getString(1));
            ptime.setText(rs.getString(2));
            dtime.setText(rs.getString(3));
            dname.setText(rs.getString(4)+" "+rs.getString(5));
            Bus_id=rs.getString(6);
            busno.setText(rs.getString(7));
            busno1.setText(rs.getString(7));
            Bus_no=rs.getString(7);
        }
        System.out.println(Username);
    }

    @FXML
    private void SubmitRecord(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you Sure You want to Register for Bus?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if(alert.getResult()==ButtonType.YES){
            String query="UPDATE STUDENT s SET s.Bus_Bus_ID=? WHERE s.Username= ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,Bus_id);
            pstmt.setString(2,this.Username);
            ResultSet rs=pstmt.executeQuery();
            con.commit();
            RegLable.setText("Registered for Bus");
            RegLable.setStyle("-fx-border-color: #1bff00; -fx-text-fill:#1bff00");

            BusReg.setDisable(true);
            RegData.setDisable(true);
            Cancel.setDisable(false);
        }
    }

    private boolean checkReg(String name) {
        String query="SELECT s.BUS_BUS_ID,b.BUS_NO FROM STUDENT s \n" +
                "INNER JOIN BUS b ON (b.BUS_ID=s.BUS_BUS_ID) \n" +
                "WHERE s.USERNAME = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,name);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next()){
                if(rs.getString(1)!="" && rs.getString(1)!=null){
                    System.out.println("Registered");
                    Bus_no=rs.getString(2);
                    RegData.setDisable(true);
                    BusReg.setDisable(true);
                    Cancel.setDisable(false);
                    RegLable.setText("Registered for Bus");
                    RegLable.setStyle("-fx-border-color: #1bff00; -fx-text-fill:#1bff00;-fx-padding:4");
                    return true;
                }
                else{
                    System.out.println("Not registered");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    @FXML
    private void RemoveBus(ActionEvent event) throws SQLException {
        String query="UPDATE STUDENT SET BUS_BUS_ID = '' WHERE USERNAME = ?";
        PreparedStatement pstmt = null;
        pstmt = con.prepareStatement(query);
        pstmt.setString(1,this.Username);
        ResultSet rs=pstmt.executeQuery();
        con.commit();

        RegData.setDisable(false);
        BusReg.setDisable(false);
        Cancel.setDisable(true);
        BusReg.fire();
        RegLable.setText("Bus Not Registered");
        RegLable.setStyle("-fx-border-color: #ff7676; -fx-text-fill:#ff7676;-fx-padding:4");

    }
}
