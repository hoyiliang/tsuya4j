package ind.yl.tsuya.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MiscCommandsListener extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MiscCommandsListener.class);

	private static final String OPTION_MENTION = "mention";
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		LOGGER.info("MiscCommandsListener RECEIVED COMMAND: " +event.getName() +"\tFROM: " +event.getGuild());
		final String userTagged = event.getUser().getAsTag();

		switch (event.getName()) {
		case "avatar":
			LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
			final User thisUser = event.getOption(OPTION_MENTION).getAsUser();
			EmbedBuilder avatarEmbed = new EmbedBuilder();
			avatarEmbed.setTitle("Avatar");
			avatarEmbed.setDescription("This is the avatar of this user: " +thisUser.getAsTag());
			avatarEmbed.setImage(thisUser.getAvatar().getUrl());
			event.replyEmbeds(avatarEmbed.build()).queue();
			break;
		}
	}
}
