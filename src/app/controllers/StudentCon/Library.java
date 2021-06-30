package app.controllers.StudentCon;

import app.DAO.DBConn;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Library implements Initializable {

    private String Username;
    private String Member_id;
    Connection con;

    @FXML
    private JFXButton Register;

    @FXML
    private Label NumberofBooks;

    @FXML
    private TableView<BooksObj> Abooks;

    @FXML
    private TableColumn<BooksObj,String> isbn;
    @FXML
    private TableColumn<BooksObj,String> title;
    @FXML
    private TableColumn<BooksObj,String> edt;
    @FXML
    private TableColumn<BooksObj,String> author;
    @FXML
    private TableColumn<BooksObj,String> publ;

    @FXML
    private TableView<inventory> inventory;
    @FXML
    private TableColumn<inventory, String> InvTitle;
    @FXML
    private TableColumn<inventory, String> Inv_idate;
    @FXML
    private TableColumn<inventory, String> Inv_ddate;

    ObservableList<BooksObj> Books = FXCollections.observableArrayList();
    ObservableList<inventory> Borrowed_books = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con= DBConn.getConnection();
        Abooks.setVisible(false);
        inventory.setVisible(false);
    }

    public void setUname(String name){
        this.Username=name;
        checkReg();
        LoadBooks();
        updateInventory();
        addReturnButton();
    }

    @FXML
    private void Register(){
        String query1= "INSERT INTO MEMBERSHIP \n" +
                "SELECT s.STUDENT_ID ,'20', 'Student' FROM STUDENT s \n" +
                "WHERE s.USERNAME = ? ";
        String query2= "UPDATE Student s SET MEMBERSHIP_MEMBER_ID = s.STUDENT_ID WHERE s.USERNAME = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(query1);
            pstmt.setString(1,Username);
            ResultSet rs=pstmt.executeQuery();
            con.commit();
            pstmt = con.prepareStatement(query2);
            pstmt.setString(1,Username);
            ResultSet rs1=pstmt.executeQuery();
            con.commit();
            Register.setVisible(false);
            Abooks.setVisible(true);
            inventory.setVisible(true);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void checkReg(){
        String query="SELECT MEMBERSHIP_MEMBER_ID from STUDENT s WHERE s.USERNAME = ? \n";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,Username);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                if(rs.getString(1)!="" && rs.getString(1)!=null){
                    System.out.println("Registered");
                    Member_id=rs.getString(1);
                    Register.setVisible(false);
                    Abooks.setVisible(true);
                    inventory.setVisible(true);
                }
                else{
                    System.out.println("Not registered");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void LoadBooks(){
        try {
            con.createStatement();
            String query="SELECT b.ISBN , b.TITLE , b.EDITION , b.AUTHOR , p.PUB_NAME \n" +
                    "FROM BOOKS b \n" +
                    "INNER JOIN \n" +
                    "PUBLISHER p ON (p.PUB_ID=b.PUBLISHER_PUB_ID)";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                Books.add(new BooksObj(rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5)));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        isbn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        edt.setCellValueFactory(new PropertyValueFactory<>("edition"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        publ.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        Abooks.setItems(Books);
        addButton();
    }

    public void addButton(){
        TableColumn<BooksObj, Void> colBtn = new TableColumn("Borrow");

        Callback<TableColumn<BooksObj, Void>, TableCell<BooksObj, Void>> cellFactory = new Callback<TableColumn<BooksObj, Void>, TableCell<BooksObj, Void>>() {
            @Override
            public TableCell<BooksObj, Void> call(final TableColumn<BooksObj, Void> param) {
                final TableCell<BooksObj, Void> cell = new TableCell<BooksObj, Void>() {

                    private final Button btn = new Button("+");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            BooksObj data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to Borrow "+data.getTitle()+" ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                            alert.showAndWait();
                            if(alert.getResult() == ButtonType.YES){
                                try {
                                    con.createStatement();
                                    String query="INSERT INTO BORROW (MEMBERSHIP_MEMBER_ID,BOOKS_ISBN,ISSUE_DATE,DUE_DATE,RETURN_DATE,FINE)\n" +
                                            "\tVALUES (?,?,sysdate,TO_DATE(sysdate)+30,NULL,0)";
                                    PreparedStatement pstmt = con.prepareStatement(query);
                                    System.out.println("Member id: "+Member_id);
                                    pstmt.setString(1,Member_id);
                                    pstmt.setString(2,data.getISBN());
                                    ResultSet rs=pstmt.executeQuery();
                                    con.commit();
                                    updateInventory();
                                } catch (SQLException throwables) {
                                    Alert alert1 = new Alert(Alert.AlertType.WARNING, "Already Borrowed!", ButtonType.OK);
                                    alert1.showAndWait();
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
        Abooks.getColumns().add(colBtn);
    }

    public void addReturnButton(){
        TableColumn<inventory, Void> colBtn = new TableColumn("Return");

        Callback<TableColumn<inventory, Void>, TableCell<inventory, Void>> cellFactory = new Callback<TableColumn<inventory, Void>, TableCell<inventory, Void>>() {
            @Override
            public TableCell<inventory, Void> call(final TableColumn<inventory, Void> param) {
                final TableCell<inventory, Void> cell = new TableCell<inventory, Void>() {

                    private final Button btn = new Button("-");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            inventory data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to return "+data.getTitle()+" ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                            alert.showAndWait();
                            if(alert.getResult() == ButtonType.YES){
                                try {
                                    con.createStatement();
                                    String query="DELETE FROM BORROW b\n" +
                                            "WHERE b.MEMBERSHIP_MEMBER_ID = ? AND b.BOOKS_ISBN = ? ";
                                    PreparedStatement pstmt = con.prepareStatement(query);
                                    System.out.println("Member id: "+Member_id);
                                    pstmt.setString(1,Member_id);
                                    pstmt.setString(2,data.getIsbn());
                                    ResultSet rs=pstmt.executeQuery();
                                    con.commit();
                                    updateInventory();
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
        inventory.getColumns().add(colBtn);
    }

    private void updateInventory(){
        try {
            Borrowed_books.clear();
            con.createStatement();
            String query="SELECT b2.TITLE , b.ISSUE_DATE , b.DUE_DATE, b2.ISBN\n" +
                    "FROM BORROW b INNER JOIN BOOKS b2 ON (b.BOOKS_ISBN=b2.ISBN)\n" +
                    "WHERE MEMBERSHIP_MEMBER_ID = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,Member_id);
            ResultSet rs=pstmt.executeQuery();

            while(rs.next()){
                Borrowed_books.add(new inventory(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)));
                System.out.println(rs.getString(1));
            }
            InvTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            Inv_idate.setCellValueFactory(new PropertyValueFactory<>("issue_date"));
            Inv_ddate.setCellValueFactory(new PropertyValueFactory<>("due_date"));

            inventory.setItems(Borrowed_books);
            // Get Number of books

            con.createStatement();
            query="SELECT count(*) FROM BORROW b WHERE b.MEMBERSHIP_MEMBER_ID = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,Member_id);
            ResultSet rs2=pstmt.executeQuery();

            while(rs2.next()){
                NumberofBooks.setText(rs2.getString(1));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public class BooksObj{
        String ISBN, title, edition, author, publisher;

        public BooksObj(String ISBN, String title, String edition, String author, String publisher) {
            this.ISBN = ISBN;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.edition = edition;
        }

        public String getISBN() {
            return ISBN;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getPublisher() {
            return publisher;
        }

        public String getEdition() {
            return edition;
        }
    }

    public class inventory{
        String title, issue_date, due_date,isbn;

        public inventory(String title, String issue_date, String due_date,String isbn) {
            this.title = title;
            this.issue_date = issue_date;
            this.due_date = due_date;
            this.isbn = isbn;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitle() {
            return title;
        }

        public String getIssue_date() {
            return issue_date;
        }

        public String getDue_date() {
            return due_date;
        }
    }
}
