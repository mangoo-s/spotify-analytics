package com.example.spotifyscrobble;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {
    ApplicationModules modules = ApplicationModules.of(SpotifyScrobbleApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void printsModuleStructureForDebugging() {
        modules.forEach(System.out::println); // lists each module and its detected dependencies
    }

    @Test
    void writeDocumentationSnippets() {
        new Documenter(modules)
                .writeModulesAsPlantUml()      // generates a diagram per module
                .writeIndividualModulesAsPlantUml();
    }
}
