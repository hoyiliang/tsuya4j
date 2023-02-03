package ind.yl.tsuya.gui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.components.LeftGridPaneUpperComponent;
import ind.yl.tsuya.gui.components.RightGridPaneComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;

public class DeployBotEventHandler {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BotTokenSetEventHandler.class);

	private static final String executeClassCategoryName = "EXECUTE";

	private static final String moduleClassCategoryName = "MODULES";

	private String BOT_TOKEN;

	private URLClassLoader botJar;

	private Map<String, List<Class>> classCategories;

	private List<Class> execClass;

	private List<Class> moduleClasses;

	private StartBotEventHandler startButtonEventHandler = new StartBotEventHandler();

	private boolean isDeploySuccess = false;

	public EventHandler<ActionEvent> getEventHandler(final LeftGridPaneUpperComponent leftGridUpperComponent, final RightGridPaneComponent rightGridComponent, final TextField execClassField, final TextArea moduleClassesField, final Button startButton, final Button stopButton) {
		return new EventHandler<ActionEvent>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (leftGridUpperComponent.getBotJarBrowseEventHandler().isDone()) {
					if (leftGridUpperComponent.getBotTokenSetEventHandler().isDone()) {
						botJar = leftGridUpperComponent.getBotJarBrowseEventHandler().getBotJar();
						BOT_TOKEN = leftGridUpperComponent.getBotTokenSetEventHandler().getBOT_TOKEN();
						LOGGER.info("Begin Deployment for bot JAR...");
						try {
							Class deploymentEngine = Class.forName("ind.yl.tsuya.main.TsuyaDeploymentEngine", true, botJar);
							Method getModules = deploymentEngine.getMethod("getClassCategories", null);
							classCategories = (Map<String, List<Class>>) getModules.invoke(deploymentEngine.newInstance(), null);
							execClass = classCategories.get(executeClassCategoryName);
							moduleClasses = classCategories.get(moduleClassCategoryName);
							LOGGER.info("==== [ Execute Class ] ================");
							for (Class c : execClass) {
								execClassField.setText(c.getName());
								LOGGER.info(c.getName());
							}
							LOGGER.info("==== [ Module Classes ] ===============");
							moduleClassesField.setText("");
							for (Class c : moduleClasses) {
								moduleClassesField.appendText(c.getName() +"\n");
								LOGGER.info(c.getName());
							}
							startButtonEventHandler.setStartEventHandler(leftGridUpperComponent, rightGridComponent, startButton, stopButton);
							startButton.setOnAction(startButtonEventHandler.getStartEventHandler());
							startButton.setDisable(false);
							isDeploySuccess = true;
							LOGGER.info("Deployment Successful!");
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Deployment Engine Class not found.");
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Get Modules Method not found.");
						} catch (SecurityException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Security Violation.");
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Illegal Access of Methods.");
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Wrong Arguments given for target Method");
						} catch (InvocationTargetException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Target Method returns Error.");
						} catch (InstantiationException e) {
							e.printStackTrace();
							LOGGER.error("Deployment Failed! Reason: Fail to instantiate Deployment Engine Class.");
						}
					} 
				} else {
					Dialog<String> missingRequirementsPopup = new Dialog<String>();
					missingRequirementsPopup.setTitle("Error");
					missingRequirementsPopup.setContentText("Bot JAR or Bot TOKEN is invalid!");
						ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
					missingRequirementsPopup.getDialogPane().getButtonTypes().add(okButton);
						AudioClip dameSound = new AudioClip(DeployBotEventHandler.class.getResource("Dame.mp3").toExternalForm());
						dameSound.play();
					missingRequirementsPopup.showAndWait();
				}
			}
		};
	}

	public String getBOT_TOKEN() {
		return BOT_TOKEN;
	}

	public void setBOT_TOKEN(String BOT_TOKEN) {
		this.BOT_TOKEN = BOT_TOKEN;
	}

	public URLClassLoader getBotJar() {
		return botJar;
	}

	public void setBotJar(URLClassLoader botJar) {
		this.botJar = botJar;
	}

	public boolean isDeploySuccess() {
		return isDeploySuccess;
	}

	public void setDeploySuccess(boolean isDeploySuccess) {
		this.isDeploySuccess = isDeploySuccess;
	}

	public List<Class> getExecClass() {
		return execClass;
	}

	public void setExecClass(List<Class> execClass) {
		this.execClass = execClass;
	}

	public List<Class> getModuleClasses() {
		return moduleClasses;
	}

	public void setModuleClasses(List<Class> moduleClasses) {
		this.moduleClasses = moduleClasses;
	}
}
