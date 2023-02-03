package ind.yl.tsuya.gui.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.gui.components.RightGridPaneComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StopBotEventHandler {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BotTokenSetEventHandler.class);

	private EventHandler<ActionEvent> stopEventHandler;

	public EventHandler<ActionEvent> getStopEventHandler() {
		return stopEventHandler;
	}

	public void setStopEventHandler(final Process cmd, final RightGridPaneComponent rightGridComponent, final Button startButton, final Button stopButton) {
		this.stopEventHandler = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				cmd.destroy();
				rightGridComponent.getConsole().setText("");
				startButton.setDisable(false);
				stopButton.setDisable(true);
			}
		};
	}
}
