/*
 CIS 452 Final Project (Fall 2018)
 This is a controller class to borrow books by inserting a SQLite query for a new borrow
 
 @author Alex MacMillan
 @since 12/14/18
 */
package schoolLibrary.borrowBook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class BorrowBookController {
		
	@FXML
	private TextField memberIdTxtfield;
	@FXML
	private TextField isbnTxtfield;
	
	@FXML
	private Label nameLbl;
	@FXML
	private Label bookLbl;
	@FXML
	private Label dueDateLbl;
	
	@FXML
	private Button borrowBtn;

	private Connection connection;
	private static long DAY_IN_MS = 1000 * 60 * 60 * 24;
	private boolean validMemId;
	private boolean validIsbn;
	
	//Variable to store the Books ISBN
	private static String selectedIsbn = null;
	
    /*
     This static method will accept the ISBN before loading the scene
     @param isbn The book ISBN will be in string format
     */
	public static void setIsbn(String isbn) {
		selectedIsbn = isbn;
	}
	
    /*
     Inititlaizes this controller class
     This method will be automatically called after the fxml scene is loaded
     Will @throws SQLException
     */
    @FXML
    private void initialize() throws SQLException {
    	//Loads SQLite-JDBC driver
    	try {
    		Class.forName("org.sqlite.JDBC");
    	} catch (ClassNotFoundException e1) {
    	}
    	        
        /*
         * Whenever the user types in something, update
         * Registering event listener
         * obs is query.textProperty()
         * oldText and newText are old and new values of query.getText()
         */
    	memberIdTxtfield.textProperty().addListener(      (obs, oldText, newText) -> {updateName();}    );
    	isbnTxtfield.textProperty().addListener(      (obs, oldText, newText) -> { updateBook(); }    );
        
    	//Time to add an event handler for the button
    	borrowBtn.setOnAction((e)-> { clickBorrow(); } );
        
        //Update
        updateName();
        
        //If the ISBN is received, then it will display in the txt box
        if (selectedIsbn == null)
        	updateBook();
        else
        	isbnTxtfield.setText(selectedIsbn);
        	
        /*
         Add the date for the book to be due
         Can change how many days later it will be due
         I have it coded for 7 days
         */
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis() + (7 * DAY_IN_MS));
        dueDateLbl.setText(dateFormat.format(date));
    }	// end of initialize()
    
    //Call this method whenever the user wants to change the TextField's
	@FXML
	private void updateName() {
		try {
			//Connect to the Database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			validMemId = false;
			nameLbl.setText("");
			String param = memberIdTxtfield.getText();
						
			//SQL for member information by the Member ID
			String sql = "SELECT Member.FirstName AS fName, Member.LastName AS lName, Student.Major AS major, Faculty.Department As dept" + 
							" FROM Member" +
							" LEFT JOIN Student USING (memberId)" +
							" LEFT JOIN Faculty USING (memberId)" +
							" WHERE Member.MemberId = ?;";

			
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			stmt.setString( 1, param.trim().replaceAll("[^\\d]", ""));
			
			
			ResultSet res = stmt.executeQuery();
			
			if ( res.next() ) {
				String memberInfo = "";
				if (res.getString("major")==null)
					memberInfo += String.format("[Faculty - %s] ", res.getString("dept"));
				else
					memberInfo += String.format("[Student - %s] ", res.getString("major"));
				
				memberInfo += res.getString("fName") + " " + res.getString("lName");
				
				nameLbl.setText(memberInfo);
		        validMemId = true;
			}

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
	private void updateBook() {
		try {
			//Connecting to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			validIsbn = false;
			
			bookLbl.setText("");
			String param = isbnTxtfield.getText();
						
			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
					" FROM Book" +
					" WHERE Book.ISBN = ?;";

			
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			stmt.setString( 1, param.trim().replaceAll("[^\\d]", ""));
			
			ResultSet res = stmt.executeQuery();
        
			if ( res.next() ) {
				bookLbl.setText(res.getString("book_title") + " by " + res.getString("book_author"));
				validIsbn = true;
			}

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
	} //Ends
	
	@FXML
	private void clickBorrow() {
		try {
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			String paramMemId = memberIdTxtfield.getText();
			String paramIsbn = isbnTxtfield.getText();
						
            //This section will check if a book is available or not
            
            //Parameterized sql, notAvailable will be a 1 for a not returned book and 0 for a book that is available to borrow
			String sqlAvailable = "SELECT COUNT(*) AS notAvailable FROM Borrow" +
									" WHERE Borrow.ISBN = ? AND Borrow.ReturnedDate IS NULL;";
				
			
			PreparedStatement stmtAvailable = connection.prepareStatement( sqlAvailable );
						
			stmtAvailable.setString( 1, paramIsbn.trim().replaceAll("[^\\d]", ""));
						
			ResultSet resAvailable = stmtAvailable.executeQuery();
			
			//Case if a member's ID or the ISBN is not found
			if (!validMemId ||!validIsbn){
				AlertMessage(AlertType.ERROR, "Incorrect Member ID or ISBN.", "Please make sure the Member Id and ISBN is correct");
				return;
			}
			else if (resAvailable.getString("notAvailable").equals("1") ) {
				AlertMessage(AlertType.ERROR, "This book is currently unavailable to borrow.", "Please look for another book.");
				return;
			}
			
            //Code for borrowing books
            
            //DateFormat is to be stored in the database as 2018/12/18 14:03:00
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date currDate = new Date();
	        Date dueDate = new Date(System.currentTimeMillis() + (7 * DAY_IN_MS));	     		
	        
			
			String sql = "INSERT INTO Borrow (ISBN, MemberId, BorrowDate, DueDate, ReturnedDate, Rating)" +
							" VALUES" +
							" (?, ?, ?, ?, NULL, NULL);";
	
			
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			stmt.setString( 1, paramIsbn.trim().replaceAll("[^\\d]", ""));
			stmt.setString( 2, paramMemId.trim().replaceAll("[^\\d]", ""));
			stmt.setString( 3, dateFormat.format(currDate));
			stmt.setString( 4, dateFormat.format(dueDate));
			
			
			int res = stmt.executeUpdate();
			
			//If a single record is inserted to the borrow table then borrow is a success
			if (res == 1) {
				//Confirmation Message
				AlertMessage(AlertType.INFORMATION, "Thank You for borrowing!", 
						"Please return the book by " + dateFormat.format(dueDate) + ".");
			}
			else {
				//Show an error message
				AlertMessage(AlertType.ERROR, "There is an Error!", "Please ask an librarian for help.");
			}

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
		
	} //Ends

	private void handleError(Exception e) {
		//Alerts user when things aren't right
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		//CLoses prohgram when the user clicks to close this alert messge
		alert.setOnCloseRequest(event -> Platform.exit());
		alert.show();
	}
	
	private void AlertMessage(AlertType type, String headerMessage, String message) {
		Alert alert = new Alert(type, message, ButtonType.CLOSE);
		alert.setHeaderText(headerMessage);
		// show the alert
		alert.show();
	}

}
