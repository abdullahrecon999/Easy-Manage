package app.controllers.StudentCon;

import app.DAO.DBConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CourseCon implements Initializable {

    private String Username="";

    @FXML
    private Label AvailButton;
    @FXML
    private TableView<CourseObj> CourseTable;
    @FXML
    private TableColumn<CourseObj, String> Cname;
    @FXML
    private TableColumn<CourseObj, String> Ccode;
    @FXML
    private TableColumn<CourseObj, String> Chr;
    @FXML
    private TableColumn<CourseObj, String> Cteach;
    @FXML
    private TableColumn<CourseObj, String> Cdept;

    @FXML
    private TableView<AvailableCourse> CAvailable;
    @FXML
    private TableColumn<AvailableCourse, String> Acname;
    @FXML
    private TableColumn<AvailableCourse, String> Accode;
    @FXML
    private TableColumn<AvailableCourse, String> Achrs;

    ObservableList<CourseObj> Cobjlist = FXCollections.observableArrayList();
    ObservableList<AvailableCourse> CAvaillist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AvailButton.setDisable(false);
    }

    public void setUname(String name){
        this.Username=name;
        RegisteredCourse();
    }

    private void RegisteredCourse(){
        Connection con= DBConn.getConnection();
        try {
            con.createStatement();
            String query="SELECT c.COURSE_NAME, c.CRDT_HR , c.COURSE_CODE , t.FIRST_NAME, t.LAST_NAME , d.DEPT_NAME \n" +
                    "FROM STUDENT s \n" +
                    "INNER JOIN \n" +
                    "ENROLLS e ON (s.STUDENT_ID=e.STUDENT_STUDENT_ID)\n" +
                    "INNER JOIN \n" +
                    "COURSE c ON (c.COURSE_ID=e.COURSE_COURSE_ID)\n" +
                    "INNER JOIN \n" +
                    "TEACHER t ON (t.TEACH_ID=c.TEACHER_TEACH_ID)\n" +
                    "INNER JOIN \n" +
                    "DEPARTMENT d ON (d.DEPT_ID=t.DEPARTMENT_DEPT_ID)\n" +
                    "WHERE s.USERNAME = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,this.Username);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                Cobjlist.add(new CourseObj(rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4)+" "+rs.getString(5),rs.getString(6)));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Cname.setCellValueFactory(new PropertyValueFactory<>("c_name"));
        Ccode.setCellValueFactory(new PropertyValueFactory<>("c_code"));
        Chr.setCellValueFactory(new PropertyValueFactory<>("crdt_hr"));
        Cteach.setCellValueFactory(new PropertyValueFactory<>("c_teacher"));
        Cdept.setCellValueFactory(new PropertyValueFactory<>("c_dept"));

        CourseTable.setItems(Cobjlist);
    }

    public void ShowAvailable(MouseEvent mouseEvent) {
        AvailButton.setDisable(true);
        Connection con= DBConn.getConnection();
        try {
            con.createStatement();
            String query="SELECT c.COURSE_NAME, c.COURSE_CODE ,c.CRDT_HR  \n" +
                    "FROM COURSE c \n" +
                    "MINUS \n" +
                    "SELECT c.COURSE_NAME, c.COURSE_CODE ,c.CRDT_HR  \n" +
                    "FROM STUDENT s \n" +
                    "INNER JOIN ENROLLS e ON (s.STUDENT_ID=e.STUDENT_STUDENT_ID)\n" +
                    "INNER JOIN COURSE c ON (c.COURSE_ID=e.COURSE_COURSE_ID)\n" +
                    "WHERE USERNAME = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,this.Username);
            System.out.println("Received Username: "+this.Username);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                CAvaillist.add(new AvailableCourse(rs.getString(1),rs.getString(2),rs.getString(3)));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Acname.setCellValueFactory(new PropertyValueFactory<>("c_name"));
        Accode.setCellValueFactory(new PropertyValueFactory<>("c_code"));
        Achrs.setCellValueFactory(new PropertyValueFactory<>("crd_hr"));

        CAvailable.setItems(CAvaillist);

        addButton();
    }

    public void addButton(){
        TableColumn<AvailableCourse, Void> colBtn = new TableColumn("Register Course");

        Callback<TableColumn<AvailableCourse, Void>, TableCell<AvailableCourse, Void>> cellFactory = new Callback<TableColumn<AvailableCourse, Void>, TableCell<AvailableCourse, Void>>() {
            @Override
            public TableCell<AvailableCourse, Void> call(final TableColumn<AvailableCourse, Void> param) {
                final TableCell<AvailableCourse, Void> cell = new TableCell<AvailableCourse, Void>() {

                    private final Button btn = new Button("Register");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            AvailableCourse data = getTableView().getItems().get(getIndex());
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to Register "+data.getC_name()+" ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                            alert.showAndWait();
                            if(alert.getResult() == ButtonType.YES){
                                System.out.println("Add course");
                                Connection con= DBConn.getConnection();
                                try {
                                    con.createStatement();
                                    String query="INSERT INTO ENROLLS \n" +
                                            "SELECT UNIQUE s.STUDENT_ID,c.COURSE_ID \n" +
                                            "FROM STUDENT s,COURSE c \n" +
                                            "WHERE s.USERNAME =? \n" +
                                            "AND c.COURSE_NAME =?";
                                    PreparedStatement pstmt = con.prepareStatement(query);
                                    pstmt.setString(1,Username);
                                    pstmt.setString(2,data.c_name);
                                    ResultSet rs=pstmt.executeQuery();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }
                            else{
                                System.out.println("No changes");
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
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        CAvailable.getColumns().add(colBtn);
    }
}

