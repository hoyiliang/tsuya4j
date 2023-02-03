package application;
	
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.components.LeftGridPaneLowerComponent;
import ind.yl.tsuya.gui.components.LeftGridPaneUpperComponent;
import ind.yl.tsuya.gui.components.RightGridPaneComponent;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Main extends Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	// Window resolution
	private static double width = 1280;
	private static double height = 720;

	@Override
	public void start(Stage primaryStage) {
			BorderPane root = new BorderPane();
				// Right side (Console view)
				RightGridPaneComponent rightGridComponent = new RightGridPaneComponent();
				GridPane rightGrid = rightGridComponent.getGridPane();
				rightGrid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
				// Left side (Management view)
				BorderPane leftBorderPane = new BorderPane();
					// Left Upper side (Field Inputs view)
					LeftGridPaneUpperComponent leftGridUpperComponent = new LeftGridPaneUpperComponent();
					GridPane leftUpperGrid = leftGridUpperComponent.getGridPane(primaryStage);
					// Left Center side (Deploy & Start Buttons view)
					LeftGridPaneLowerComponent leftGridLowerComponent = new LeftGridPaneLowerComponent();
					Node leftCenterGrid = leftGridLowerComponent.getGridPane(primaryStage, leftGridUpperComponent, rightGridComponent);
					// Left Bottom side (Components view)
				leftBorderPane.setTop(leftUpperGrid);
				leftBorderPane.setCenter(leftCenterGrid);
				leftBorderPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
				// TODO Middle horizontal line separator


			root.setId("rootPane");
			root.setLeft(leftBorderPane);
			root.setRight(rightGrid);

			// Code to show Application Window (Always same)
			Scene scene = new Scene(root,width,height);
			LOGGER.info("Creating a GUI Application Window with resolution: " +(int)width +"x" +(int)height);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Tsuya Bot Server Manager");
			primaryStage.setScene(scene);
			primaryStage.show();
			LOGGER.info("GUI Application finished loading.");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
