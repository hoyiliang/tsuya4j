package ind.yl.tsuya.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

	private static String BOT_TOKEN;

	public static void main(String[] args) {
		try {
			File env = new File("C:\\Users\\yilia\\OneDrive\\Desktop\\Tsuya4J\\com.yl.tsuya.main\\src\\ind\\yl\\tsuya\\main\\.env");
			Scanner readEnv = new Scanner(env);
			BOT_TOKEN = readEnv.nextLine();
			readEnv.close();
		} catch (FileNotFoundException e) {
			// Missing BOT_TOKEN!
			e.printStackTrace();
		}
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
	}

	public static void initCommands(final JDA bot) {
		LOGGER.info("ENTERING: initCommands()");
		bot.updateCommands().addCommands(
			//// Base Section /////////////////////////////////////////////////////
			Commands.slash("help", "Shows all available commands for the bot."),
			Commands.slash("ping", "Calculate ping of the bot."),
			Commands.slash("about", "About Tsuya bot."),

			//// Music Section ///////////////////////////////////////////////////
			Commands.slash("play", "Plays a music with a URL provided.")
				.setGuildOnly(true)
				.addOption(OptionType.STRING, "url", "Write YouTube URL here.", true),
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
				.setGuildOnly(true)
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
