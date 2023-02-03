package ind.yl.tsuya.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TsuyaDeploymentEngine {

	private Map<String, List<Class>> classCategories = new HashMap<>();

	private static final String executeClassCategoryName = "EXECUTE";

	private static final String moduleClassCategoryName = "MODULES";

	public boolean returnOK() {
		return true;
	}

	public Map<String, List<Class>> getClassCategories() {
		// Execute class (should be only 1)
		List<Class> execClass = new ArrayList<>();
		execClass.add(ind.yl.tsuya.main.Main.class);
		// Module classes (any)
		List<Class> moduleClasses = new ArrayList<>();
		moduleClasses.add(ind.yl.tsuya.main.BaseCommandsListener.class);
		moduleClasses.add(ind.yl.tsuya.music.MusicCommandsListener.class);
		moduleClasses.add(ind.yl.tsuya.music.AudioPlayerSendHandler.class);
		moduleClasses.add(ind.yl.tsuya.music.GuildMusicManager.class);
		moduleClasses.add(ind.yl.tsuya.music.TrackScheduler.class);
		classCategories.put(executeClassCategoryName, execClass);
		classCategories.put(moduleClassCategoryName, moduleClasses);
		return classCategories;
	}
}
