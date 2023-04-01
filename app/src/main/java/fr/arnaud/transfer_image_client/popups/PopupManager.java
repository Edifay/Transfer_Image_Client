package fr.arnaud.transfer_image_client.popups;

import android.content.Context;

public class PopupManager {

    public static boolean showPopup(final Context context, final PopupSettings.PopupSettingsConfig config) {
        PopupSettings popup = new PopupSettings(context, config);
        popup.show();
        return true;
    }


}
