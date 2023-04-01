package fr.arnaud.transfer_image_client.files;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import fr.arnaud.transfer_image_client.MainActivity;

public class FileManager {

    private static final String SETTINGS_FILE = "/settings.txt";

    public static Settings loadSettings(final File file) {
        final File settings = new File(file.getAbsolutePath() + SETTINGS_FILE);

        System.out.println("LOADING SETTINGS IN : " + settings.getAbsolutePath());

        try {
            return new ObjectMapper().readValue(settings, new TypeReference<Settings>() {
            }).path(file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Unable to load settings ! Default settings loaded.\n " + e.getMessage());
        }

        return new Settings("localhost", "default", 5656, file.getAbsolutePath());

    }


    public static void saveSettings(final File file, final Settings settingsToSave) {
        final File settings = new File(file.getAbsolutePath() + SETTINGS_FILE);

        try {
            new ObjectMapper().writeValue(settings, settingsToSave);
        } catch (IOException e) {
            System.err.println("Unable to save settings !" + e.getMessage());
        }
    }

    public static void saveCurrentSettings() {
        System.out.println("Saving : "+ MainActivity.settings);
        saveSettings(new File(MainActivity.settings.settingsPath), MainActivity.settings);
    }


}
