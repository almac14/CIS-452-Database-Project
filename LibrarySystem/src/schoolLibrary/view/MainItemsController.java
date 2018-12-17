/**
 * CIS 452 Final Project (Fall 2018)
 * This is the Controller Class for the 3 buttons on the main menu.
 *
 * @author  Alex MacMillan
 * @since   12/14/18
 */
package schoolLibrary.view;

import java.io.IOException;

import javafx.fxml.FXML;
import schoolLibrary.Main;

public class MainItemsController {
	
	private Main main;
	
	//Handles "Search Book" button click on the main menu.
	@FXML
	private void goSearchBook() throws IOException {
		main.showSearchBookScene();
	}
	
	//]Handles "Borrow Book" button click on the main menu.
	@FXML
	private void goBorrowBook() throws IOException {
		main.showBorrowBookScene();
	}
	
	//Handles "Return Book" button click on the main menu.
	@FXML
	private void goReturnBook() throws IOException {
		main.showReturnBookScene();
	}
	
}
