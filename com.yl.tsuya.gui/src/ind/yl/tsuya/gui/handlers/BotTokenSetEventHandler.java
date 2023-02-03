package ind.yl.tsuya.gui.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class BotTokenSetEventHandler {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BotTokenSetEventHandler.class);
	
	private String BOT_TOKEN = "";
	
	private boolean isDone = false;
	
	public EventHandler<MouseEvent> getEventHandler(final PasswordField botTokenField, final Text botTokenStatus) {
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if (botTokenField.getText().isBlank()) {
					LOGGER.error("Bot Token field is blank!");
					botTokenStatus.setText("Token is EMPTY!");
					botTokenStatus.setStyle("-fx-fill: red;");
				} else {
					setBOT_TOKEN(botTokenField.getText());
					LOGGER.info("Bot Token set successfully!");
					botTokenStatus.setText("Token SET!");
					botTokenStatus.setStyle("-fx-fill: green;");
					setDone(true);
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

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
}
