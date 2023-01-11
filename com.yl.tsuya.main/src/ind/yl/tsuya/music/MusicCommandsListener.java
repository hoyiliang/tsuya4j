package ind.yl.tsuya.music;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommandsListener extends ListenerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(MusicCommandsListener.class);

	private static final String OPTION_URL = "url";

	private static final String OPTION_VOLUME = "value";

	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;

	public MusicCommandsListener() {
		this.musicManagers = new HashMap<>();
		this.playerManager = new DefaultAudioPlayerManager();

		AudioSourceManagers.registerRemoteSources(playerManager);
	    AudioSourceManagers.registerLocalSource(playerManager);
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		LOGGER.info("MusicCommandsListener RECEIVED COMMAND: " +event.getName() +"\tFROM: " +event.getGuild());
		final String userTagged = event.getUser().getAsTag();
		GuildMusicManager musicManager = musicManagers.get(Long.parseLong(event.getGuild().getId()));
		AudioChannelUnion voiceChannel = event.getMember().getVoiceState().getChannel();
		switch(event.getName()) {

			case "play":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				final String url = event.getOption(OPTION_URL).getAsString();
				if (voiceChannel != null) { 
					loadAndPlay(event, url, voiceChannel);
				} else {
					event.reply("Please join a voice channel first.").queue();
				}
				break;

			case "volume":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				final int volume = event.getOption(OPTION_VOLUME).getAsInt();
				musicManager.player.setVolume(volume);
				event.reply("Volume is now " +volume +"%").queue();
				break;

			case "pause":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				if (musicManager.player.isPaused() == true) {
					event.reply("Music is already Paused, do you mean */resume*?");
				} else {
					musicManager.player.setPaused(true);
					event.reply("Music is Paused.");
				}
				break;

			case "resume":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				if (musicManager.player.isPaused() == false) {
					event.reply("Music is currently Playing, do you mean */pause*?");
				} else {
					musicManager.player.setPaused(false);
					event.reply("Music is now Resumed.");
				}
				break;

			case "stop":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				musicManager.player.stopTrack();
				musicManager.player.checkCleanup(0);
				event.getGuild().getAudioManager().closeAudioConnection();
				event.reply("Music is Stopped.").queue();
				break;

			case "skip":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				event.reply("Skipping track: " +musicManager.player.getPlayingTrack().getInfo().title).queue();
				musicManager.scheduler.nextTrack();
				if (musicManager.player.getPlayingTrack() == null) {
					event.reply("That was the last track! Leaving voice channel...").queue();
					musicManager.player.checkCleanup(0);
					event.getGuild().getAudioManager().closeAudioConnection();
				}
				break;
		}
	}

	private void loadAndPlay(final SlashCommandInteractionEvent event, final String url, final AudioChannelUnion voiceChannel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

		playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {

			@Override
		    public void trackLoaded(AudioTrack track) {
				event.reply("Adding to queue " + track.getInfo().title).queue();

		      	play(event.getGuild(), musicManager, voiceChannel, track);
		    }

		    @Override
		    public void playlistLoaded(AudioPlaylist playlist) {
		    	AudioTrack firstTrack = playlist.getSelectedTrack();

		    	if (firstTrack == null) {
		    		firstTrack = playlist.getTracks().get(0);
		    	}

		    	event.reply("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

		    	play(event.getGuild(), musicManager, voiceChannel, firstTrack);
		    }

		    @Override
		    public void noMatches() {
		    	event.reply("Nothing found by " + url).queue();
		    }

		    @Override
		    public void loadFailed(FriendlyException exception) {
		    	event.reply("Could not play: " + exception.getMessage()).queue();
		    }
		});
	}

	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		long guildId = Long.parseLong(guild.getId());
	    GuildMusicManager musicManager = musicManagers.get(guildId);

	    if (musicManager == null) {
	    	musicManager = new GuildMusicManager(playerManager);
	    	musicManagers.put(guildId, musicManager);
	    }

	    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

	    return musicManager;
	}
	
	private void play(Guild guild, GuildMusicManager musicManager, AudioChannelUnion voiceChannel, AudioTrack track) {
	    connectToUserVoiceChannel(guild.getAudioManager(), voiceChannel);

	    musicManager.scheduler.queue(track);
	}

	private static void connectToFirstVoiceChannel(AudioManager audioManager) {
	    if (!audioManager.isConnected()) {
	    	for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
	    		audioManager.openAudioConnection(voiceChannel);
	    		break;
	    	}
	    }
	}
	
	private static void connectToUserVoiceChannel(AudioManager audioManager, AudioChannelUnion voiceChannel) {
		if (!audioManager.isConnected()) {
			audioManager.openAudioConnection(voiceChannel);
		}
	}
}
