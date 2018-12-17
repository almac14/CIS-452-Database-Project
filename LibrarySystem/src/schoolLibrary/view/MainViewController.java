/**
 * CIS 452 Final Project (Fall 2018)
 * This is the Controller class for the top section, handles the home button click event.
 * Will also Support the click for the second button if time allows
 *
 * @author  Alex MacMillan
 * @since   12/14/18
 */
package schoolLibrary.view;

import java.io.IOException;

import javafx.fxml.FXML;
import schoolLibrary.Main;

public class MainViewController {

	private Main main;
	
	//Handles "Home" button click on the top BorderPane.
	@FXML
	private void goHome() throws IOException {
		main.showMainItems();
	}
	
}
