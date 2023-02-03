package ind.yl.tsuya.gui.components;

import java.util.List;

import org.controlsfx.tools.Borders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.handlers.DeployBotEventHandler;
import ind.yl.tsuya.gui.handlers.StartBotEventHandler;
import ind.yl.tsuya.gui.handlers.StopBotEventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LeftGridPaneLowerComponent {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(LeftGridPaneLowerComponent.class);

	private DeployBotEventHandler deployButtonEventHandler = new DeployBotEventHandler();

	public BorderPane getGridPane(final Stage stage, final LeftGridPaneUpperComponent leftGridUpperComponent, final RightGridPaneComponent rightGridComponent) {
		BorderPane leftLowerBorderPane = new BorderPane();
			GridPane leftBottomGrid = new GridPane();
				Label execClassLabel = new Label("Main Executable Component");
				TextField execClassField = new TextField();
					execClassField.setEditable(false);
				Label moduleClassesLabel = new Label("Bot Modules");
				TextArea moduleClassesField = new TextArea();
					moduleClassesField.setEditable(false);
			leftBottomGrid.setPadding(new Insets(10, 10, 10, 10));
			leftBottomGrid.setHgap(5);	leftBottomGrid.setVgap(5);
			leftBottomGrid.add(execClassLabel, 0, 0);
			leftBottomGrid.add(execClassField, 0, 1);
			leftBottomGrid.add(moduleClassesLabel, 0, 2);
			leftBottomGrid.add(moduleClassesField, 0, 3);
			Node componentsWrapper = Borders.wrap(leftBottomGrid)
					.lineBorder()
					.title("Bot Components")
					.color(Color.BLACK)
					.thickness(1)
					.buildAll();
			GridPane leftCenterGrid = new GridPane();
				Button deployButton = new Button("Deploy Bot JAR");
					deployButton.setPrefWidth(550);
				Button startButton = new Button("Start Bot");
					startButton.setPrefWidth(550);
					startButton.setDisable(true);
				Button stopButton = new Button("Stop Bot");
					stopButton.setPrefWidth(550);
					stopButton.setDisable(true);
				// Dependent with each other Buttons EventHandlers
				deployButton.setOnAction(deployButtonEventHandler.getEventHandler(leftGridUpperComponent, rightGridComponent, execClassField, moduleClassesField, startButton, stopButton));
			leftCenterGrid.setPadding(new Insets(10, 10, 10, 10));
			leftCenterGrid.setHgap(5);	leftCenterGrid.setVgap(5);
			leftCenterGrid.add(deployButton, 0, 0);
			leftCenterGrid.add(startButton, 0, 1);
			leftCenterGrid.add(stopButton, 0, 2);
			Node buttonsWrapper = Borders.wrap(leftCenterGrid)
					.lineBorder()
					.title("Deploy & Start")
					.color(Color.BLACK)
					.thickness(1)
					.buildAll();
		leftLowerBorderPane.setTop(buttonsWrapper);
		leftLowerBorderPane.setBottom(componentsWrapper);
		return leftLowerBorderPane;
	}
}
