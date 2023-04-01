package fr.arnaud.transfer_image_client.popups;

import android.widget.Toast;

import fr.arnaud.transfer_image_client.MainActivity;
import fr.arnaud.transfer_image_client.files.FileManager;

public class CreatedPopups {

    public final static PopupSettings.PopupSettingsConfig addressPopup = new PopupSettings.PopupSettingsConfig(
            "Changer l'ip locale", "Définissez l'ip locale du serveur.", "adresse locale",
            value -> {
                MainActivity.settings.setIp(value);
                FileManager.saveCurrentSettings();
                Toast.makeText(MainActivity.activity, "L'ip locale a été défini sur : " + value, Toast.LENGTH_SHORT).show();
            }
    );

    public final static PopupSettings.PopupSettingsConfig passwordPopup = new PopupSettings.PopupSettingsConfig(
            "Changer le mot de passe", "Définissez le mot de passe de laison avec le serveur.", "mot de passe",
            value -> {
                MainActivity.settings.setPassword(value);
                FileManager.saveCurrentSettings();
                Toast.makeText(MainActivity.activity, "Le mot de passe a été défini sur : " + value, Toast.LENGTH_SHORT).show();
            }
    );

    public final static PopupSettings.PopupSettingsConfig portPopup = new PopupSettings.PopupSettingsConfig(
            "Definissez le port", "Definissez le port à utiliser pour l'ip locale", "port",
            value -> {
                MainActivity.settings.setPort(Integer.parseInt(value));
                FileManager.saveCurrentSettings();
                Toast.makeText(MainActivity.activity, "Le port a bien été défini sur : " + value, Toast.LENGTH_SHORT).show();
            }
    );
}
