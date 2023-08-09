package fr.arnaud.transfer_image_client.channel;

import static fr.arnaud.transfer_image_client.MainActivity.runUi;
import static fr.arnaud.transfer_image_client.transfer.TransferManager.sleepABit;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import fr.arnaud.transfer_image_client.channel.utils.CertLoader;
import fr.arnaud.transfer_image_client.channel.utils.PType;
import fr.arnaud.transfer_image_client.databinding.FragmentFirstBinding;
import fr.arnaud.transfer_image_client.files.Settings;
import fr.arnaud.transfer_image_client.transfer.utils.ImageDescriptor;
import fr.jazer.session.RPacket;
import fr.jazer.session.Session;
import fr.jazer.session.stream.Receiver;
import fr.jazer.session.utils.crypted.CertFormat;
import fr.jazer.session.utils.crypted.ClientCertConfig;
import fr.jazer.session.utils.crypted.SecureType;

public class ChannelManager {

    private Receiver<RPacket> quitEvent;

    private final Settings settings;
    private final ArrayList<ImageDescriptor> owned;
    private final Session channel;
    private final CertLoader.CertConfiguration config;


    public ChannelManager() {
        this.settings = null;
        this.owned = null;
        this.channel = null;
        this.config = null;
    }

    public ChannelManager(final Settings settings, final CertLoader.CertConfiguration config, final ArrayList<ImageDescriptor> descriptors) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.settings = settings.clone();
        this.config = config;
        this.owned = descriptors;

        this.channel = new Session();
        ClientCertConfig options = new ClientCertConfig(config.stream, config.password, SecureType.TLSv1_2, CertFormat.BKS);
        this.channel.connect(settings.getIp(), settings.getPort(), options);
        setupQuitEvent();
        this.channel.addPacketListener(PType.CLOSE, quitEvent);
    }

    public void exchange(final FragmentFirstBinding binding) {
        try {
            runUi(() -> binding.status.setText("Shaking Server..."));

            System.out.println("Starting HandShake.");
            HandShake handShake = new HandShake(this.channel, settings, owned);
            HandShake.HandShakeData data = handShake.shake();
            System.out.println("HandShake Finished.");

            if (data.isAuth)
                System.out.println("HandShake success");
            else {
                System.err.println("HandShake failed ! Wrong password.");
                runUi(() -> binding.status.setText("Wrong password."));
                this.channel.destroy();
                return;
            }

            runUi(() -> {
                binding.status.setText(data.needed.size() + " images needed.");
                binding.total.setText(data.needed.size() + "");
            });
            sleepABit();

            System.out.println("Transfer will fetch " + data.needed.size() + " files.");

            try {
                ImageSender sender = new ImageSender(channel, owned, data.needed);
                sender.send(binding);
            } catch (Exception e) {
                runUi(() -> binding.status.setText("Erreur d'envois : " + e.getMessage()));
                e.printStackTrace();
            }
            System.out.println("Transfer complete.");

            if (!channel.getSocket().isClosed())
                channel.read(PType.CLOSE);
            channel.removePacketListener(this.quitEvent);

            runUi(() -> binding.status.setText("Transfer complete."));

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            runUi(() -> binding.status.setText("Internal Error : " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public boolean isTransferring() {
        return this.channel == null || !this.channel.getSocket().isClosed();
    }

    public void setupQuitEvent() {
        this.quitEvent = packet -> {
            channel.destroy();
            System.out.println("Channel closed.");
        };
    }

}
