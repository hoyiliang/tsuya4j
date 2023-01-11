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
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommandsListener extends ListenerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MusicCommandsListener.class);
	
	private static final String OPTION_URL = "url";
	
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
		
		switch(event.getName()) {
		
		case "play":
			final String userTagged = event.getUser().getAsTag();
			LOGGER.info("EXEC COMMAND: " +event.getName() +"\tFROM: " +event.getGuild() +"\tBY: " +userTagged);
			final String url = event.getOption(OPTION_URL).getAsString();
			loadAndPlay(event, url);
		}
	}
	
	private void loadAndPlay(final SlashCommandInteractionEvent event, final String url) {
		GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
		
		playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {

			@Override
		    public void trackLoaded(AudioTrack track) {
				event.reply("Adding to queue " + track.getInfo().title).queue();

		      	play(event.getGuild(), musicManager, track);
		    }

		    @Override
		    public void playlistLoaded(AudioPlaylist playlist) {
		    	AudioTrack firstTrack = playlist.getSelectedTrack();

		    	if (firstTrack == null) {
		    		firstTrack = playlist.getTracks().get(0);
		    	}

		    	event.reply("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

		    	play(event.getGuild(), musicManager, firstTrack);
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
	
	private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
	    connectToFirstVoiceChannel(guild.getAudioManager());

	    musicManager.scheduler.queue(track);
	}

	private void skipTrack(TextChannel channel) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
	    musicManager.scheduler.nextTrack();

	    channel.sendMessage("Skipped to next track.").queue();
	}

	private static void connectToFirstVoiceChannel(AudioManager audioManager) {
	    if (!audioManager.isConnected()) {
	    	for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
	    		audioManager.openAudioConnection(voiceChannel);
	    		break;
	    	}
	    }
	}
}
