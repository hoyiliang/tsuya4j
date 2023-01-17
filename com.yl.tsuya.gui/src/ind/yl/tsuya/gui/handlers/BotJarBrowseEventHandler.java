package ind.yl.tsuya.gui.handlers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.components.LeftGridPaneComponent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class BotJarBrowseEventHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LeftGridPaneComponent.class);
	
	public EventHandler<MouseEvent> getEventHandler(final Stage stage, final FileChooser botJarChooser, final TextField botJarDirField, final Text botJarDirStatus) {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				File file = botJarChooser.showOpenDialog(stage);
				if (file != null) {
					botJarDirField.setText(file.getAbsolutePath());
					try {
						URLClassLoader checkClassLoader = new URLClassLoader(new URL[] {file.toURI().toURL()}, this.getClass().getClassLoader());
						@SuppressWarnings({ "unused", "rawtypes" })
						Class checkClass = Class.forName("ind.yl.tsuya.main.TsuyaDeploymentEngine", false, checkClassLoader);
						LOGGER.info("Class TsuyaDeploymentEngine --- PASS");
						botJarDirStatus.setText("JAR OK");
						botJarDirStatus.setStyle("-fx-fill: green;");
					} catch (MalformedURLException e) {
						LOGGER.error("An error occurred whilist trying to create a class loader!");
						botJarDirStatus.setText("APPLICATION NG! Please report this Bug.");
						botJarDirStatus.setStyle("-fx-fill: red;");
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						LOGGER.info("Invalid Tsuya deployment JAR loaded!");
						botJarDirStatus.setText("JAR NG!");
						botJarDirStatus.setStyle("-fx-fill: red;");
					}
				}
			}
		};
	}
}
