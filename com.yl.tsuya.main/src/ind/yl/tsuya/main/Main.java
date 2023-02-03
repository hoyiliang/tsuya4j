package ind.yl.tsuya.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.yl.tsuya.misc.MiscCommandsListener;
import ind.yl.tsuya.music.MusicCommandsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	private static String BOT_TOKEN;

	public static void main(String[] args) {
		BOT_TOKEN = args[0];
		final JDA bot = JDABuilder.createDefault(BOT_TOKEN)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
					.setActivity(Activity.playing("New to JDA!"))
					.build();

		initEventListeners(bot);
		initCommands(bot);
	}

	// Init methods
	public static void initEventListeners(final JDA bot) {
		bot.addEventListener(new BaseCommandsListener(bot));
		bot.addEventListener(new MusicCommandsListener());
		bot.addEventListener(new MiscCommandsListener());
	}

	public static void initCommands(final JDA bot) {
		LOGGER.info("ENTERING: initCommands()");
		bot.updateCommands().addCommands(
			//// Base Section /////////////////////////////////////////////////////
			Commands.slash("help", "Shows all available commands for the bot."),
			Commands.slash("ping", "Calculate ping of the bot."),
			Commands.slash("about", "About Tsuya bot."),
			
			//// Music Section ///////////////////////////////////////////////////
			Commands.slash("play", "Plays a music with an URL or keyword.")
				.setGuildOnly(true)
				.addOption(OptionType.STRING, "args", "Write an URL or keyword here.", true),
			Commands.slash("volume", "Change volume of the music player.")
				.setGuildOnly(true)
				.addOption(OptionType.INTEGER, "value", "Range: 0-100", true),
			Commands.slash("pause", "Pause the music.")
				.setGuildOnly(true),
			Commands.slash("resume", "Resumes the music.")
				.setGuildOnly(true),
			Commands.slash("stop", "Stops the player and leaves the voice channel.")
				.setGuildOnly(true),
			Commands.slash("skip", "Skips the current track.")
				.setGuildOnly(true),
			Commands.slash("queue", "Get track queue.")
				.setGuildOnly(true),
			Commands.slash("nowplaying", "Get info for currently playing track.")
				.setGuildOnly(true),
			
			//// Misc Section ///////////////////////////////////////////////////
			Commands.slash("avatar", "Get your own avatar!")
				.addOption(OptionType.MENTIONABLE, "mention", "@mention someone and get their avatar!", false)
		).queue();

		LOGGER.info("EXITING: initCommands()");
	}
}
