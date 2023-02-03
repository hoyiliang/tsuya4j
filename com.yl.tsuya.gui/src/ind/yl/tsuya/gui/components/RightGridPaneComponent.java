package ind.yl.tsuya.gui.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class RightGridPaneComponent {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(LeftGridPaneUpperComponent.class);

	private TextArea console;

	public GridPane getGridPane() {
		GridPane rightGrid = new GridPane();
			Text consoleTitle = new Text("Console");
			console = new TextArea();
				console.setPrefSize(640,720);
				console.setEditable(false);
		rightGrid.setPadding(new Insets(10, 10, 10, 10));
		rightGrid.add(consoleTitle, 0, 0);
		rightGrid.add(console, 0, 1);
		return rightGrid;
	}

	public TextArea getConsole() {
		return console;
	}

	public void setConsole(TextArea console) {
		this.console = console;
	}
}
