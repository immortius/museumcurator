package xyz.immortius.museumcurator.interop;

import xyz.immortius.museumcurator.common.MuseumCuratorConstants;

import java.util.ServiceLoader;

public class Services {

    public static final MCPlatformHelper PLATFORM = load(MCPlatformHelper.class);

    public static GroupHelper GROUP_HELPER = new NoopGroupHelper();

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        MuseumCuratorConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
