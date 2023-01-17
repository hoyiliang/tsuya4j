package ind.yl.tsuya.gui.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.handlers.BotJarBrowseEventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class LeftGridPaneComponent {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(LeftGridPaneComponent.class);
	
	public GridPane getGridPane(final Stage stage) {
		GridPane leftGrid = new GridPane();
			Text botJarTitle = new Text("JAR file");
			TextField botJarDirField = new TextField();
				botJarDirField.setPrefWidth(400);
				botJarDirField.setEditable(false);
			Text botJarDirStatus = new Text("Please select a compatible bot JAR file.");
			Button botJarBrowse = new Button("Browse");
				botJarBrowse.setPrefWidth(125);
				FileChooser botJarChooser = new FileChooser();
					ExtensionFilter extFilterJar = new ExtensionFilter("JAR files (*.jar)", "*.jar");
				botJarChooser.getExtensionFilters().add(extFilterJar);
				BotJarBrowseEventHandler botJarBrowseEventHandler = new BotJarBrowseEventHandler();
				botJarBrowse.setOnMouseClicked(botJarBrowseEventHandler.getEventHandler(stage, botJarChooser, botJarDirField, botJarDirStatus));
			Text botTokenTitle = new Text("Bot Token");
			PasswordField botTokenField = new PasswordField();
				botTokenField.setPrefWidth(400);
			Button botTokenSet = new Button("Set");
				botTokenSet.setPrefWidth(125);
		leftGrid.setPadding(new Insets(10, 10, 10, 10));
		leftGrid.setHgap(5);	leftGrid.setVgap(5);
		leftGrid.add(botJarTitle, 0, 0);
		leftGrid.add(botJarDirField, 1, 0);
		leftGrid.add(botJarBrowse, 2, 0);
		leftGrid.add(botJarDirStatus, 1, 1);
		leftGrid.add(botTokenTitle, 0, 2);
		leftGrid.add(botTokenField, 1, 2);
		leftGrid.add(botTokenSet, 2, 2);
		return leftGrid;
	}
}
