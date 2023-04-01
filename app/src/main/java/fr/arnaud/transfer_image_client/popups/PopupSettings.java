package fr.arnaud.transfer_image_client.popups;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import fr.arnaud.transfer_image_client.R;

public class PopupSettings extends Dialog {

    private Button save_button;
    private ImageButton close_button;
    private TextView title;
    private TextView description;
    private TextInputLayout textInput;

    public PopupSettings(@NonNull Context context, final PopupSettingsConfig config) {
        super(context);
        setContentView(R.layout.popup_settings);

        retrieveComponents();

        this.title.setText(config.title);
        this.description.setText(config.description);

        this.textInput.setHint(config.titleField);
        this.textInput.getEditText().setText(config.currentValue);

        this.close_button.setOnClickListener(v -> this.dismiss());
        this.save_button.setOnClickListener(v -> {
            config.actionOnSave.save(this.textInput.getEditText().getText().toString());
            this.dismiss();
        });

    }


    public void retrieveComponents() {
        this.title = findViewById(R.id.settings_title);
        this.description = findViewById(R.id.settings_description);
        this.textInput = findViewById(R.id.text_input);
        this.save_button = findViewById(R.id.save_button);
        this.close_button = findViewById(R.id.close_popup);
    }

    public static class PopupSettingsConfig {

        private final String title;
        private final String description;
        private final String titleField;
        private SaveRunnable actionOnSave;
        private String currentValue;


        public PopupSettingsConfig(final String title, final String description, final String titleField, final SaveRunnable runnable) {
            this.title = title;
            this.description = description;
            this.titleField = titleField;
            this.actionOnSave = runnable;
        }

        public PopupSettingsConfig currentValue(final String currentValue) {
            this.currentValue = currentValue;
            return this;
        }

        public PopupSettingsConfig actionOnSave(final SaveRunnable runnable) {
            this.actionOnSave = runnable;
            return this;
        }

    }

    public interface SaveRunnable {
        void save(final String value);
    }
}
