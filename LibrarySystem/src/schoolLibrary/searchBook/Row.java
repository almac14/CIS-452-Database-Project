/**
* CIS 452 Final Project (Fall 2018)
* Class to hold the data of each row in Search Book.
*
* @author  Alex MacMillan
* @since   12/14/18
*/
package schoolLibrary.searchBook;

import javafx.beans.property.SimpleStringProperty;

public class Row {

	private SimpleStringProperty isbn;
	private SimpleStringProperty title;
	private SimpleStringProperty author;
	
	public Row(String isbn, String title, String author) {
		this.isbn = new SimpleStringProperty(isbn);
		this.title = new SimpleStringProperty(title);
		this.author = new SimpleStringProperty(author);
	}
	
	public SimpleStringProperty getIsbn() {
		return isbn;
	}
	public SimpleStringProperty getTitle() {
		return title;
	}
	public SimpleStringProperty getAuthor() {
		return author;
	}
	
}
