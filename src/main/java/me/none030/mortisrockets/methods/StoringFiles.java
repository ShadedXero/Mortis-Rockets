package me.none030.mortisrockets.methods;

import java.io.File;

import static me.none030.mortisrockets.MortisRockets.plugin;

public class StoringFiles {

    public static void StoreFiles() {

        File file = new File("plugins/MortisRockets/", "config.yml");

        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }

    }
}
