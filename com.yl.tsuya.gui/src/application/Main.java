package application;
	
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.components.LeftGridPaneComponent;
import ind.yl.tsuya.gui.components.RightGridPaneComponent;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


public class Main extends Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	// Window resolution
	private static double width = 1280;
	private static double height = 720;
	
	@Override
	public void start(Stage primaryStage) {
			// Using HTML indentation style
			BorderPane root = new BorderPane();
				// Left side (Deployment control view)
				LeftGridPaneComponent leftGridComponent = new LeftGridPaneComponent();
				GridPane leftGrid = leftGridComponent.getGridPane(primaryStage);
				// TODO Middle horizontal line separator

				// Right side (Console view)
				RightGridPaneComponent rightGridComponent = new RightGridPaneComponent();
				GridPane rightGrid = rightGridComponent.getGridPane();
			root.setLeft(leftGrid);
			root.setRight(rightGrid);

			// Code to show App Window (Always same)
			Scene scene = new Scene(root,width,height);
			LOGGER.info("Creating a GUI Application Window with resolution: " +(int)width +"x" +(int)height);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); TODO implement
			primaryStage.setTitle("Tsuya Bot Server Manager");
			primaryStage.setScene(scene);
			primaryStage.show();
			LOGGER.info("GUI Application finished loading.");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
