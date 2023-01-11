package ind.yl.tsuya.main;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BaseCommandsListener extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(BaseCommandsListener.class);

	private static final String ownerId = "238673285116723203";

	private JDA bot;

	public BaseCommandsListener(JDA bot) {
		this.bot = bot;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		LOGGER.info("BaseCommandsListener RECEIVED COMMAND: " +event.getName() +"\tFROM: " +event.getGuild());

		// Switch-case for multiple commands
		switch(event.getName()) {

		case "help":
			LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild());
			EmbedBuilder embedHelp = new EmbedBuilder();
			embedHelp.setTitle("List of available commands");
			embedHelp.setColor(Color.blue);
			embedHelp.setDescription("Shows all available commands for this bot.");
			embedHelp.addField("`help`", "> Shows this message.", false);
			embedHelp.addField("`ping`", "> Shows the bot's ping in ms.", false);
			embedHelp.addField("`about`", "> Bot version, Developers, and Testers.", false);
			event.replyEmbeds(embedHelp.build()).queue();
			break;

		case "ping":
			LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild());
			long time = System.currentTimeMillis();
            event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                 .flatMap(v ->
                      event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                 ).queue(); // Queue both reply and edit
            break;

		case "about":
			LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild());
			EmbedBuilder embedAbout = new EmbedBuilder();
			embedAbout.setTitle("About Tsuya bot");
			embedAbout.setDescription("Version: 0.0.1");
			embedAbout.addField("Developers", ">>> Stevehyl\nYukinaForever", false);
			embedAbout.addField("Testers", ">>> Belrys\nAUNTIE SLAYER\nHao\nDeathEnd1st\nRinka Lynx", false);
			event.replyEmbeds(embedAbout.build()).queue();
			break;
		}
	}

	// Shutdown command
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		MessageChannel channel = event.getChannel();
		Message message = event.getMessage();
        String content = message.getContentRaw();
        if (event.getAuthor().isBot()) return;
        if (content.equals("#shutdown")) {
			if (event.getAuthor().getId().equals(ownerId)) {
				LOGGER.info("EXEC COMMAND: shutdown");
				channel.sendMessage("Shutting down...").queue();
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					LOGGER.error("An error has occured whilist trying to shutdown the bot instance.");
					e.printStackTrace();
				}
				bot.shutdownNow();
				LOGGER.info("Bot instance shut down successfully.");
			} else {
				LOGGER.info("ALERT: Unauthorized shutdown request by " +event.getAuthor().getAsTag());
				channel.sendMessage("You not owner. This incident will be reported.").queue();
			}
        }
	}
}
