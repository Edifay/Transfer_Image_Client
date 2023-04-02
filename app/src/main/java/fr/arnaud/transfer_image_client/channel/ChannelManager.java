package fr.arnaud.transfer_image_client.channel;

import static fr.arnaud.transfer_image_client.MainActivity.runUi;
import static fr.arnaud.transfer_image_client.transfer.TransferManager.sleepABit;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import apifornetwork.tcp.RunnableParamPacket;
import apifornetwork.tcp.Secure.SecureClientTCP;
import fr.arnaud.transfer_image_client.MainActivity;
import fr.arnaud.transfer_image_client.channel.utils.CertLoader;
import fr.arnaud.transfer_image_client.channel.utils.PType;
import fr.arnaud.transfer_image_client.databinding.FragmentFirstBinding;
import fr.arnaud.transfer_image_client.files.Settings;
import fr.arnaud.transfer_image_client.transfer.utils.ImageDescriptor;

public class ChannelManager {

    private RunnableParamPacket quitEvent;

    private final Settings settings;
    private final ArrayList<ImageDescriptor> owned;
    private final SecureClientTCP channel;
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

        this.channel = new SecureClientTCP(settings.getIp(), settings.getPort(), config.stream, config.password);
        setupQuitEvent();
        this.channel.addPacketEvent(quitEvent);
    }

    public void exchange(final FragmentFirstBinding binding) {
        this.channel.startListen();

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
                this.channel.close();
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
                channel.waitForPacket(PType.CLOSE);
            channel.removePacketEvent(this.quitEvent);

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
            if (packet.getPacketNumber() == PType.CLOSE) {
                try {
                    channel.stopListen();
                    channel.close();
                    System.out.println("Channel closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
