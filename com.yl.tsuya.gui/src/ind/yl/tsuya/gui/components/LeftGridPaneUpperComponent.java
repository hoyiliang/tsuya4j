package ind.yl.tsuya.gui.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.handlers.BotJarBrowseEventHandler;
import ind.yl.tsuya.gui.handlers.BotTokenSetEventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class LeftGridPaneUpperComponent {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(LeftGridPaneUpperComponent.class);

	private BotJarBrowseEventHandler botJarBrowseEventHandler = new BotJarBrowseEventHandler();

	private BotTokenSetEventHandler botTokenSetEventHandler = new BotTokenSetEventHandler();

	public GridPane getGridPane(final Stage stage) {
		GridPane leftUpperGrid = new GridPane();
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
				botJarBrowse.setOnMouseClicked(botJarBrowseEventHandler.getEventHandler(stage, botJarChooser, botJarDirField, botJarDirStatus));
			Text botTokenTitle = new Text("Bot Token");
			PasswordField botTokenField = new PasswordField();
				botTokenField.setPrefWidth(400);
			Text botTokenStatus = new Text("Paste your Bot Token here.");
			Button botTokenSet = new Button("Set");
				botTokenSet.setPrefWidth(125);
				botTokenSet.setOnMouseClicked(botTokenSetEventHandler.getEventHandler(botTokenField, botTokenStatus));
		leftUpperGrid.setPadding(new Insets(10, 10, 10, 10));
		leftUpperGrid.setHgap(5);	leftUpperGrid.setVgap(5);
		leftUpperGrid.add(botJarTitle, 0, 0);
		leftUpperGrid.add(botJarDirField, 1, 0);
		leftUpperGrid.add(botJarBrowse, 2, 0);
		leftUpperGrid.add(botJarDirStatus, 1, 1);
		leftUpperGrid.add(botTokenTitle, 0, 2);
		leftUpperGrid.add(botTokenField, 1, 2);
		leftUpperGrid.add(botTokenSet, 2, 2);
		leftUpperGrid.add(botTokenStatus, 1, 3);
		return leftUpperGrid;
	}

	public BotJarBrowseEventHandler getBotJarBrowseEventHandler() {
		return botJarBrowseEventHandler;
	}

	public void setBotJarBrowseEventHandler(BotJarBrowseEventHandler botJarBrowseEventHandler) {
		this.botJarBrowseEventHandler = botJarBrowseEventHandler;
	}

	public BotTokenSetEventHandler getBotTokenSetEventHandler() {
		return botTokenSetEventHandler;
	}

	public void setBotTokenSetEventHandler(BotTokenSetEventHandler botTokenSetEventHandler) {
		this.botTokenSetEventHandler = botTokenSetEventHandler;
	}
}
