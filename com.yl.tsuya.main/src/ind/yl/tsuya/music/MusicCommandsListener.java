package ind.yl.tsuya.music;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommandsListener extends ListenerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MusicCommandsListener.class);

	private static final String OPTION_PLAY_ARGS = "args";

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
		AudioManager audioManager = event.getGuild().getAudioManager();
		AudioChannelUnion voiceChannel = event.getMember().getVoiceState().getChannel();
		switch(event.getName()) {

			case "play":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				final String url = event.getOption(OPTION_PLAY_ARGS).getAsString();
				if (!audioManager.isConnected()) {
					if (voiceChannel != null) {
						loadAndPlay(event, url, voiceChannel);
					} else {
						event.reply("Please join a voice channel first.").queue();
					}
				} else {
					if (audioManager.getConnectedChannel().equals(voiceChannel)) {
						loadAndPlay(event, url, voiceChannel);
					} else {
						event.reply("I am already playing music in another voice channel! Please join the voice channel before queuing new music!").queue();
					}
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

			case "queue":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				buildQueue(event, musicManager);
				break;
				
			case "nowplaying":
				LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
				buildNowPlaying(event, musicManager);
				break;
		}
	}

	private void loadAndPlay(final SlashCommandInteractionEvent event, final String args, final AudioChannelUnion voiceChannel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
		AudioLoadResultHandler resultHandler = new AudioLoadResultHandler() {

			@Override
		    public void trackLoaded(AudioTrack track) {
				event.reply("Adding to queue: [ *" + track.getInfo().title +"* ]").queue();

		      	play(event, musicManager, voiceChannel, track);
		    }

		    @Override
		    public void playlistLoaded(AudioPlaylist playlist) {
		    	AudioTrack firstTrack = playlist.getSelectedTrack();
		    	if (firstTrack == null) {
		    		firstTrack = playlist.getTracks().get(0);
		    	}

		    	event.reply("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

		    	play(event, musicManager, voiceChannel, firstTrack);
		    }

		    @Override
		    public void noMatches() {
		    	event.reply("Nothing found by " + args).queue();
		    }

		    @Override
		    public void loadFailed(FriendlyException exception) {
		    	event.reply("Could not play: " + exception.getMessage()).queue();
		    }
		};

		// Check if to use URL or Search
		try {
			URL checkUrl = new URL(args);
			playerManager.loadItemOrdered(musicManager, args, resultHandler);
		} catch (MalformedURLException e) {
			LOGGER.info("Not a valid URL, search by keyword instead.");
			AudioReference ref = new AudioReference("ytsearch:" +args, null);
			playerManager.loadItemOrdered(musicManager, ref, resultHandler);
		}
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
	
	private void play(final SlashCommandInteractionEvent event, GuildMusicManager musicManager, AudioChannelUnion voiceChannel, AudioTrack track) {
	    connectToUserVoiceChannel(event, event.getGuild().getAudioManager(), voiceChannel);
	    musicManager.scheduler.queue(track);
	    musicManager.player.setVolume(10);
	}

	private static void connectToUserVoiceChannel(final SlashCommandInteractionEvent event, AudioManager audioManager, AudioChannelUnion voiceChannel) {
		if (!audioManager.isConnected()) {
			audioManager.openAudioConnection(voiceChannel);
		} else {
			event.reply("I am already in another voice channel!");
		}
	}

	private void buildNowPlaying(final SlashCommandInteractionEvent event, final GuildMusicManager musicManager) {
		EmbedBuilder embedNowPlaying = new EmbedBuilder();
		long positionMillis = musicManager.player.getPlayingTrack().getPosition();
		long durationMillis = musicManager.player.getPlayingTrack().getDuration();
		String position = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(positionMillis),
				TimeUnit.MILLISECONDS.toMinutes(positionMillis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(positionMillis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(positionMillis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(positionMillis)));
		String duration = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(durationMillis),
				TimeUnit.MILLISECONDS.toMinutes(durationMillis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationMillis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(durationMillis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));
		embedNowPlaying.setTitle("Currently Playing: ");
		embedNowPlaying.setDescription("*" +musicManager.player.getPlayingTrack().getInfo().title +"*\nTimestamp: " +position +" / " +duration);
		embedNowPlaying.setThumbnail("https://img.youtube.com/vi/" + musicManager.player.getPlayingTrack().getIdentifier() + "/hqdefault.jpg");
		event.replyEmbeds(embedNowPlaying.build()).queue();
	}

	private void buildQueue(final SlashCommandInteractionEvent event, final GuildMusicManager musicManager) {
		if (musicManager != null) {
			if (musicManager.player.getPlayingTrack() != null) {
				EmbedBuilder embedQueue = new EmbedBuilder();
				embedQueue.addField("Music Player Queue", "These are all the tracks currently in the music queue:", false);
				// TODO implement
				List<AudioTrack> queue = musicManager.scheduler.getQueue();
				int idx = 0;
				String trackQueueString = ">>> ";
				for (AudioTrack track : queue) {
					idx++;
					trackQueueString.concat(idx +" | " +track.getInfo().title +"\n");
				}
				embedQueue.addField("Tracks: ", trackQueueString, false);
				event.replyEmbeds(embedQueue.build()).queue();
			} else {
				event.reply("No tracks are queued.").queue();
			}
		} else {
			event.reply("No tracks are queued.").queue();
		}
	}
}
