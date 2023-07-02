package xyz.immortius.museumcurator.config;

import xyz.immortius.museumcurator.config.system.Comment;
import xyz.immortius.museumcurator.config.system.Name;

/**
 * Root configuration
 */
public class MuseumCuratorConfig {

    private static final MuseumCuratorConfig instance = new MuseumCuratorConfig();

    public static MuseumCuratorConfig get() {
        return instance;
    }

    @Name("Gameplay")
    public final GameplayConfig gameplayConfig = new GameplayConfig();

    public GameplayConfig getGameplayConfig() {
        return gameplayConfig;
    }
}
