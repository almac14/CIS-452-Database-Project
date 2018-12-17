/*
 CIS 452 Final Project (Fall 2018)
 This is a controller class to return books
 
 @author Alex MacMillan
 @since 12/14/18
 */

package schoolLibrary.returnBook;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import schoolLibrary.Main;
import javafx.scene.control.Button;


public class ReturnBookController {
	
	@FXML
	private TextField memberIdTxtfield;

	@FXML
	private Button selectBtn;
	
	@FXML
	private ComboBox<String> ratingCombox;
	
	@FXML
	private TableView<Row> table;

    /*
     TableColumn maps a row type to a cell type
     For this, we map Row to a String
     */
	@FXML
	private TableColumn<Row,String> isbn;
	@FXML
	private TableColumn<Row,String> title;
	@FXML
	private TableColumn<Row,String> author;
	@FXML
	private TableColumn<Row,String> dueDate;

	//The list of rows to put into the table
	private ObservableList<Row> data;
	
	private Connection connection;
	private Main main;
	private boolean validMemId;
	
    //This method will be automatically callsed aftetr the FXML file is loaded
    @FXML
    private void initialize() throws SQLException {
    	try {
    		Class.forName("org.sqlite.JDBC");
    	} 
    	catch (ClassNotFoundException e1) {
    	}
    	
    	//Gets the Rating value into ComboBox.
        ratingCombox.getItems().addAll(
        		"1",
        		"2",
        		"3",
        		"4",
        		"5",
        		"6",
        		"7",
        		"8",
        		"9",
        		"10"
        		);
    	
        /*
         Tells JavaFX how to map each Row data to each column
         Initilizes the table with 4 columns
         */
    	isbn.setCellValueFactory(cellData -> cellData.getValue().getIsbn());
    	title.setCellValueFactory(cellData -> cellData.getValue().getTitle());
    	author.setCellValueFactory(cellData -> cellData.getValue().getAuthor());
    	dueDate.setCellValueFactory(cellData -> cellData.getValue().getDueDate());
        
        // Whenever the user types in something, update
        // Registering event listener
        // obs is query.textProperty()
        // oldText and newText are old and new values of query.getText()
        memberIdTxtfield.textProperty().addListener(      (obs, oldText, newText) -> {update();}    );
        
        update();
    }
    
   //Call this method when the user changes the text
	@FXML
	private void update() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			validMemId = false;
        
			String param = memberIdTxtfield.getText();
			
			data = FXCollections.observableArrayList();
			
			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author, Borrow.DueDate AS book_due" +
						" FROM Borrow" +
						" JOIN Book USING (ISBN)"+
						" WHERE Borrow.MemberId = ? AND Borrow.ReturnedDate IS NULL" +
						" ORDER BY book_isbn";
			
			PreparedStatement stmt = connection.prepareStatement( sql );
			
            //In SQLite 1 is the first place holder, 2 is the second place holder
			stmt.setString( 1, param.trim() );				

			ResultSet res = stmt.executeQuery();
			while ( res.next() ) {
				Row row = new Row(res.getString("book_isbn"), res.getString("book_title"), res.getString("book_author"), res.getString("book_due"));
				data.add(row);
				validMemId = true;
			}
            table.setItems(data);
			
		} catch (SQLException e) {
			handleError(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					handleError(e);
				}
			}
		} 
	}
		
	@FXML
	private void clickSelect() throws IOException {
        try {
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			Row selectedRow = table.getSelectionModel().getSelectedItem();
            
			if (!validMemId)
			{
				AlertMessage(AlertType.ERROR, "Incorrect Member ID.", "Please make sure the Member Id is correct");
				return;
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date currDate = new Date();     		
	        
			String sql = "UPDATE Borrow SET ReturnedDate = ?, Rating = ?"+
						" WHERE ISBN = ? AND MemberId = ? AND ReturnedDate IS NULL";
				
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			stmt.setString( 1, dateFormat.format(currDate));
			stmt.setString( 2, ratingCombox.getValue());
			stmt.setString( 3, selectedRow.getIsbn().getValue());
			stmt.setString( 4, memberIdTxtfield.getText().trim());	
			
			int res = stmt.executeUpdate();
			
            //If a single record is updated through the borrow table, book return was successful
            if (res == 1) {
				AlertMessage(AlertType.INFORMATION, "Thank You for returning the book!", 
						"Returned accepted on:  " + dateFormat.format(currDate) + ".");
			}
			else {
				AlertMessage(AlertType.ERROR, "There is an Error!", "Please ask an librarian for help.");
			}
			update ();
		} 
		catch (SQLException e) {
			handleError(e);
		}
		catch (NullPointerException e) {
			AlertMessage(AlertType.ERROR, "Please select a book.", e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					handleError(e);
				}
			}
		} 
	
	}
	
	private void handleError(Exception e) {
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		alert.setOnCloseRequest(event -> Platform.exit());
		alert.show();
	}
    
	private void AlertMessage(AlertType type, String headerMessage, String message) {
		Alert alert = new Alert(type, message, ButtonType.CLOSE);
		alert.setHeaderText(headerMessage);
		alert.show();
	}
}

