/**
 * CIS 452 Final Project (Fall 2018)
 * This is a controller class to display detailed book information after the user has selected a book.
 *
 * @author  Alex MacMillan
 * @since   12/14/18
 */

package schoolLibrary.bookDetails;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import schoolLibrary.Main;
import schoolLibrary.borrowBook.BorrowBookController;

public class BookDetailsController {
	
	@FXML
	private Label isbnLbl;
	@FXML
	private Label titleLbl;
	@FXML
	private Label authorLbl;
	@FXML
	private Label pubLbl;
	@FXML
	private Label yearLbl;
	@FXML
	private Label desLbl;
	@FXML
	private Label catLbl;
	@FXML
	private Label ratingLbl;
	
	@FXML
	private Button selectBtn;
	
	private Connection connection;
	private Main main;
	
	//Variable to store Book ISBN
	private static String selectedIsbn = null;
    /*
     * This is a static method to accept the ISBN before initializing the scene.
     */
	public static void setIsbn(String isbn) {
		selectedIsbn = isbn;
	}
	
    @FXML
    private void initialize() throws SQLException {
    	//Load the sqlite-JDBC
    	try {
    		Class.forName( "org.sqlite.JDBC" );
    	} catch (ClassNotFoundException e1) {
    	}
    	
        //Connect to the database
        try {
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
            /*
             * Generate parameterized sql to get the book information
             * Also calculate avg of the rating.
             */
			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author," + 
							" Book.Publisher AS book_pub, Book.Year AS book_year, Book.Description AS book_des," + 
							" (Book.CategoryId || ' - ' || Category.CategoryName) AS book_cat," +
							" ifnull(ROUND(AVG(Borrow.Rating), 2), 'Not rated') AS rating"+
							" FROM Book" + 
							" JOIN Category USING (CategoryId)" +
							" LEFT JOIN Borrow USING (ISBN)" +
							" WHERE Book.ISBN = ?;";
				
			//Prepared statement
			PreparedStatement stmt= connection.prepareStatement( sql );
			stmt.setString( 1, selectedIsbn);
						
			//Get results
			ResultSet res = stmt.executeQuery();
			
			//Put data into label
			isbnLbl.setText(selectedIsbn);
			
			//Update labels from result
			if (res.next()) {
				titleLbl.setText(res.getString("book_title"));
				authorLbl.setText(res.getString("book_author"));
				pubLbl.setText(res.getString("book_pub"));
				yearLbl.setText(res.getString("book_year"));
				desLbl.setText(res.getString("book_des"));
				catLbl.setText(res.getString("book_cat"));
				ratingLbl.setText(res.getString("rating"));
			}
        } catch (SQLException e) {
			handleError(e);
		} finally {
			if (connection != null) {
				try {
		    	//Avoid SQLite_BUSY Error - database is locked
					connection.close();
				} catch (SQLException e) {
					handleError(e);
				}
			}
		} 
    }

	private void handleError(Exception e) {
		//Alert the user when things aren't right
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		
        //Close the program when the user clicks close on the alert
		alert.setOnCloseRequest(event -> Platform.exit());
		
        //Shows the alert
		alert.show();
	}
	
	//Method to handle button click to Borrow this book.
	@FXML
	private void clickSelect() throws IOException {
		//Passing ISBN to borrow book controller. 
		BorrowBookController.setIsbn(selectedIsbn);
		main.showBorrowBookScene();
	}

}
