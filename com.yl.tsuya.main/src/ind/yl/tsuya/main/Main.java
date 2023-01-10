package ind.yl.tsuya.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.music.MusicCommandsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final String BOT_TOKEN = 
			"MTA1MzExMjY2NDM2MjA2NTkzMA.GiYr-3.xd8x3-EfzwbOvKmmVuTK3uPv0RQ57taPjCbZow";

	private static final JDA bot = JDABuilder.createDefault(BOT_TOKEN)
			.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
			.setActivity(Activity.playing("New to JDA!"))
			.build();

	public static void main(String[] args) {
		initEventListeners(bot);
		initCommands();
	}

	// Init methods
	public static void initEventListeners(JDA bot) {
		bot.addEventListener(new BaseCommandsListener(bot));
		bot.addEventListener(new MusicCommandsListener());
	}

	public static void initCommands() {
		LOGGER.info("ENTERING: initCommands()");
		bot.updateCommands().addCommands(
			//// Base Section /////////////////////////////////////////////////////
			Commands.slash("help", "Shows all available commands for the bot."),
			Commands.slash("ping", "Calculate ping of the bot."),
			Commands.slash("about", "About Tsuya bot."),
			
			//// Music Section ///////////////////////////////////////////////////
			Commands.slash("play", "Plays a music with a URL provided.").setGuildOnly(true)
			.addOption(OptionType.STRING, "url", "Write YouTube URL here.")
			).queue();
		LOGGER.info("EXITING: initCommands()");
	}

	public static void initDoujinCommands() {
		// TODO implement 
	}

	public static void initAdminCommands() {
		// TODO implement
	}
}
