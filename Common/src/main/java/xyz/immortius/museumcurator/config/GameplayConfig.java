package xyz.immortius.museumcurator.config;

import xyz.immortius.museumcurator.config.system.Comment;
import xyz.immortius.museumcurator.config.system.Name;

public class GameplayConfig {
    @Name("individualChecklists")
    @Comment("Should there be individual checklists (rather than a single shared checklist)")
    private boolean individualChecklists = false;

    public boolean isIndividualChecklists(MuseumCuratorConfig museumCuratorConfig) {
        return individualChecklists;
    }

    public void setIndividualChecklists(boolean value) {
        this.individualChecklists = value;
    }
}
