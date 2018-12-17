/**
 * CIS 452 Final Project (Fall 2018)
 * A School Library Database System using SQLite, support features for searching,
 * borrowing, and returning books.
 *
 * Also includes an onclick function for the home button as well as adding in another onclick button if time allows
 *
 * @author  Alex MacMillan
 * @since   12/14/18
 */
package schoolLibrary;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private static BorderPane mainLayout;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Library System DataBase Version 1.0");

		showMainView();
		showMainItems();
	}

	//MainView is the top panel with a Home button, Add User button, and the Library name.
	private void showMainView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/MainView.fxml"));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	//MainItem is the center section with 3 buttons
	public static void showMainItems() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/MainItems.fxml"));
		BorderPane mainItems = loader.load();
		mainLayout.setCenter(mainItems);
	}
	
	//SearchBookScene
	public static void showSearchBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("searchBook/SearchBook.fxml"));
		BorderPane searchBook = loader.load();
		mainLayout.setCenter(searchBook);
	}
	
	//BorrowBookScene
	public static void showBorrowBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("borrowBook/BorrowBook.fxml"));
		BorderPane borrowBook = loader.load();
		mainLayout.setCenter(borrowBook);
	}
	
	//ReturnBookScene
	public static void showReturnBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("returnBook/ReturnBook.fxml"));
		BorderPane returnBook = loader.load();
		mainLayout.setCenter(returnBook);
	}
	
	//BookDetailsScene
	public static void showBookDetailsScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("bookDetails/BookDetails.fxml"));
		BorderPane bookDetails = loader.load();
		mainLayout.setCenter(bookDetails);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
