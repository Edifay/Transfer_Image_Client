package fr.arnaud.transfer_image_client.transfer;

import static fr.arnaud.transfer_image_client.transfer.utils.Utils.getDateFromSnap;
import static fr.arnaud.transfer_image_client.transfer.utils.Utils.isSnapchatFile;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import fr.arnaud.transfer_image_client.MainActivity;
import fr.arnaud.transfer_image_client.R;
import fr.arnaud.transfer_image_client.channel.ChannelManager;
import fr.arnaud.transfer_image_client.channel.utils.CertLoader;
import fr.arnaud.transfer_image_client.databinding.FragmentFirstBinding;
import fr.arnaud.transfer_image_client.transfer.utils.ImageDescriptor;
import fr.arnaud.transfer_image_client.transfer.utils.Utils;

public class TransferManager {

    private static ChannelManager manager;

    public static void transfer(final FragmentFirstBinding binding) {
        binding.current.setText(0 + "");
        binding.total.setText(0 + "");
        binding.progressBar.setProgress(0, true);

        if (!canTransfer()) {
            System.err.println("Already transferring !");
            MainActivity.activity.runOnUiThread(() -> Toast.makeText(MainActivity.activity, "Transfer en cour...", Toast.LENGTH_SHORT).show());
            return;
        }

        manager = new ChannelManager();

        MainActivity.activity.askPerms();
        binding.status.setText("Loading Images...");

        System.out.println("Fetching all medias.");
        ArrayList<ImageDescriptor> descriptors = getDescriptorsFromMedias(Utils.getAllMedias());
        System.out.println("Total of " + descriptors.size() + " medias.");

        binding.status.setText(descriptors.size() + " images detected.");

        sleepABit();

        final CertLoader loader = new CertLoader();
        try {
            manager = new ChannelManager(MainActivity.settings, loader.getConfig(), descriptors);
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException |
                 KeyManagementException e) {
            System.err.println("Impossible de ce connecter au serveur ! " + e.getMessage());
            e.printStackTrace();
            manager = null;
        }

        if (manager == null)
            return;

        manager.exchange(binding);

        binding.progressBar.setProgress(100, true);

    }

    public static void sleepABit() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static ArrayList<ImageDescriptor> getDescriptorsFromMedias(final ArrayList<String> medias) {
        ArrayList<ImageDescriptor> descriptors = new ArrayList<>();
        File f;
        for (String s : medias) {
            f = new File(s);
            descriptors.add(new ImageDescriptor(s, (int) f.length(), getDateFromSnap(f)));
        }
        return descriptors;
    }

    public static boolean canTransfer() {
        return manager == null || !manager.isTransferring();
    }

}