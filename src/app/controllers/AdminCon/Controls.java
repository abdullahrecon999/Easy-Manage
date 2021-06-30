package app.controllers.AdminCon;

import app.DAO.DBConn;
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

public class Controls implements Initializable {

    private String Username;
    Connection con;

    @FXML
    private TableView<admission> AdmissionTable;

    @FXML
    private TableColumn<admission, String> name;

    @FXML
    private TableColumn<admission, String> dob;

    @FXML
    private TableColumn<admission, String> email;

    @FXML
    private TableColumn<admission, String> Mmarks;

    @FXML
    private TableColumn<admission, String> Imarks;

    @FXML
    private TextField isbn;

    @FXML
    private TextField author;

    @FXML
    private TextField title;

    @FXML
    private TextField edition;

    @FXML
    private TextField publisher;

    @FXML
    private TableView<BooksObj> Abooks;

    @FXML
    private TableColumn<BooksObj, String> isbn1;

    @FXML
    private TableColumn<BooksObj, String> title1;

    @FXML
    private TableColumn<BooksObj, String> edt;

    @FXML
    private TableColumn<BooksObj, String> author1;

    @FXML
    private TableColumn<BooksObj, String> publ;

    ObservableList<BooksObj> Books = FXCollections.observableArrayList();
    ObservableList<admission> students = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con= DBConn.getConnection();
        loadBooks();
        loadStudents();
    }

    public void setUname(String name){
        this.Username=name;
    }

    private void loadStudents(){
        try {
            con.createStatement();
            String query="SELECT (s.FIRST_NAME ||' '|| s.LAST_NAME)AS name ,s.dob , s.EMAIL , s.METRIC_MARKS , s.INTER_MARKS, s.username FROM STDADMISSION s ";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next()){
                students.add(new admission(rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5),rs.getString(6)));
                System.out.println(rs.getString(6));
            }

            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            dob.setCellValueFactory(new PropertyValueFactory<>("dob"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            Mmarks.setCellValueFactory(new PropertyValueFactory<>("Mmarks"));
            Imarks.setCellValueFactory(new PropertyValueFactory<>("Imarks"));

            AdmissionTable.setItems(students);
            addButton();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addButton(){
        TableColumn<admission, Void> colBtn = new TableColumn("Add Student");

        Callback<TableColumn<admission, Void>, TableCell<admission, Void>> cellFactory = new Callback<TableColumn<admission, Void>, TableCell<admission, Void>>() {
            @Override
            public TableCell<admission, Void> call(final TableColumn<admission, Void> param) {
                final TableCell<admission, Void> cell = new TableCell<admission, Void>() {

                    private final Button btn = new Button("+");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            admission data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getUsername());
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Admit "+data.getName()+ "?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                            alert.showAndWait();
                            if(alert.getResult() == ButtonType.YES){
                                try {
                                    con.createStatement();
                                    String query="INSERT INTO STUDENT (STUDENT_ID, USERNAME, PASSWORD, FIRST_NAME, LAST_NAME,ROLL, GRADE, SEMESTER, DOB, EMAIL, HOUSE, STREET, SECTOR)\n" +
                                            "SELECT coalesce(MAX(s2.STUDENT_ID), 0)+1,s.USERNAME ,s.PASSWORD ,s.FIRST_NAME ,s.LAST_NAME ,('F-'||TO_CHAR(count(*)+1)),null,1, s.DOB ,s.EMAIL ,s.HOUSE ,s.STREET ,s.SECTOR \n" +
                                            "FROM STDADMISSION s , student s2\n" +
                                            "WHERE s.USERNAME = ?\n" +
                                            "GROUP BY s.STD_ID, s.USERNAME, s.PASSWORD, s.FIRST_NAME, s.LAST_NAME, \n" +
                                            "s.DOB, s.EMAIL, s.HOUSE, s.STREET, s.SECTOR, \n" +
                                            "null, 1 ";
                                    PreparedStatement pstmt = con.prepareStatement(query);
                                    pstmt.setString(1,data.getUsername());
                                    ResultSet rs=pstmt.executeQuery();
                                    con.commit();

                                    String query1="DELETE FROM STDADMISSION s \n" +
                                            "WHERE USERNAME = ?";
                                    pstmt = con.prepareStatement(query1);
                                    pstmt.setString(1,data.getUsername());
                                    rs=pstmt.executeQuery();
                                    con.commit();

                                    // Update the Materialized view (LoginView)
                                    String query2="SELECT * from student;\n" +
                                            "BEGIN\n" +
                                            "DBMS_SNAPSHOT.REFRESH('LoginView');\n" +
                                            "END";
                                    pstmt = con.prepareStatement(query2);
                                    rs=pstmt.executeQuery();
                                    con.commit();

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
        AdmissionTable.getColumns().add(colBtn);
    }

    private void loadBooks(){
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

        isbn1.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        title1.setCellValueFactory(new PropertyValueFactory<>("title"));
        edt.setCellValueFactory(new PropertyValueFactory<>("edition"));
        author1.setCellValueFactory(new PropertyValueFactory<>("author"));
        publ.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        Abooks.setItems(Books);
    }

    private void UpdateBooks(){
        try{
            PreparedStatement pstmt;
            ResultSet rs;
            try {
                con.createStatement();
                String query = "INSERT INTO PUBLISHER(PUB_ID,PUB_NAME)\n" +
                        "SELECT coalesce(MAX(PUB_ID), 0)+1,? FROM PUBLISHER p2\n" +
                        "WHERE NOT EXISTS (SELECT PUB_NAME FROM PUBLISHER p WHERE upper(p.PUB_NAME)=upper(?))";
                pstmt = con.prepareStatement(query);
                pstmt.setString(1, publisher.getText());
                pstmt.setString(2, publisher.getText());
                rs= pstmt.executeQuery();
                con.commit();
            }catch(SQLException e){
                //Publisher Exists
            }
            String query1="INSERT INTO BOOKS (ISBN, AUTHOR, TITLE, EDITION, PUBLISHER_PUB_ID, ADMIN_ADMIN_ID)\n" +
                    "SELECT ?,?,?,?,p.PUB_ID ,1 \n" +
                    "FROM PUBLISHER p,BOOKS b \n" +
                    "WHERE p.PUB_NAME =?\n" +
                    "GROUP BY p.PUB_ID ";
            pstmt=con.prepareStatement(query1);
            pstmt.setString(1,isbn.getText());
            pstmt.setString(2,author.getText());
            pstmt.setString(3,title.getText());
            pstmt.setString(4,edition.getText());
            pstmt.setString(5,publisher.getText());
            rs=pstmt.executeQuery();
            con.commit();
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION, "Book Added!", ButtonType.OK);
            alert1.showAndWait();
            isbn.clear();
            title.clear();
            author.clear();
            edition.clear();
            publisher.clear();

        } catch (SQLException throwables) {
            Alert alert1 = new Alert(Alert.AlertType.WARNING, "Book Already Exists", ButtonType.OK);
            alert1.showAndWait();
            throwables.printStackTrace();
        }
    }

    public void AddBook(ActionEvent actionEvent) {
        if(isbn.getText()==null || !isbn.getText().matches("[0-9]+")){
            System.out.println("No ISBN");
            isbn.setStyle("-fx-border-color:red;");
        }
        else if(author.getText()==null || !author.getText().matches("[a-z A-Z .]+")){
            System.out.println("No Author");
            author.setStyle("-fx-border-color:red;");
        }
        else if(title.getText()==null || !title.getText().matches("[a-z A-Z 0-9 ']+")){
            System.out.println("No title");
            title.setStyle("-fx-border-color:red;");
        }
        else if(edition.getText()==null || !edition.getText().matches("[0-9]+")){
            System.out.println("No Edt");
            edition.setStyle("-fx-border-color:red;");
        }
        else if(publisher.getText()==null || !publisher.getText().matches("[a-z A-Z]+")){
            System.out.println("No Publisher");
            publisher.setStyle("-fx-border-color:red;");
        }
        else{
            publisher.setStyle("");
            edition.setStyle("");
            title.setStyle("");
            author.setStyle("");
            isbn.setStyle("");
            UpdateBooks();
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

    public class admission{
        String name,dob,email,Mmarks,Imarks,username;

        public admission(String name, String dob, String email, String mmarks, String imarks,String uname) {
            this.name = name;
            this.dob = dob;
            this.email = email;
            Mmarks = mmarks;
            Imarks = imarks;
            this.username=uname;
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getDob() {
            return dob;
        }

        public String getEmail() {
            return email;
        }

        public String getMmarks() {
            return Mmarks;
        }

        public String getImarks() {
            return Imarks;
        }
    }
}
