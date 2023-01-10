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
			EmbedBuilder ebHelp = new EmbedBuilder();
			ebHelp.setTitle("List of available commands");
			ebHelp.setColor(Color.blue);
			ebHelp.setDescription("Shows all available commands for this bot.");
			ebHelp.addField("`help`", "> Shows this message.", false);
			ebHelp.addField("`ping`", "> Shows the bot's ping in ms.", false);
			event.replyEmbeds(ebHelp.build()).queue();
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
			EmbedBuilder ebAbout = new EmbedBuilder();
			ebAbout.setTitle("About Tsuya bot");
			ebAbout.setDescription("Version: 0.0.1");
			ebAbout.addField("Developer", "> Stevehyl", false);
			ebAbout.addField("Testers", ">>> AUNTIE SLAYER\nHao\nDeathEnd1st\nRinka Lynx\nYukinaForever", false);
			event.replyEmbeds(ebAbout.build()).queue();
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
