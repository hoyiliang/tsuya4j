package ind.yl.tsuya.gui.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.components.LeftGridPaneUpperComponent;
import ind.yl.tsuya.gui.components.RightGridPaneComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StartBotEventHandler {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BotTokenSetEventHandler.class);

	private EventHandler<ActionEvent> startEventHandler;

	private StopBotEventHandler stopButtonEventHandler = new StopBotEventHandler();

	private BufferedReader output;

	private Thread consoleThread;

	public Thread getConsoleThread() {
		return consoleThread;
	}

	public EventHandler<ActionEvent> getStartEventHandler() {
		return startEventHandler;
	}

	public void setStartEventHandler(final LeftGridPaneUpperComponent leftGridUpperComponent, final RightGridPaneComponent rightGridComponent, final Button startButton, final Button stopButton) {
		this.startEventHandler = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String jarPath = leftGridUpperComponent.getBotJarBrowseEventHandler().getJarPath();
				String BOT_TOKEN = leftGridUpperComponent.getBotTokenSetEventHandler().getBOT_TOKEN();
				try {
					LOGGER.info("Starting Bot...");
					Process cmd = Runtime.getRuntime().exec("java -jar " +jarPath +" " +BOT_TOKEN);
					output = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
					stopButtonEventHandler.setStopEventHandler(cmd, rightGridComponent, startButton, stopButton);
					stopButton.setOnAction(stopButtonEventHandler.getStopEventHandler());
					startButton.setDisable(true);
					stopButton.setDisable(false);
					LOGGER.info("Starting Console Thread...");
					consoleThread = new Thread(new Runnable() {
						@Override
						public void run() {
							String line;
							try {
								while (cmd.isAlive()) {
									while ((line = output.readLine()) != null) {
										rightGridComponent.getConsole().appendText(line +"\n");
									}
									TimeUnit.SECONDS.sleep(1);
								}
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					consoleThread.start();
					LOGGER.info("Bot start SUCCESS!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
