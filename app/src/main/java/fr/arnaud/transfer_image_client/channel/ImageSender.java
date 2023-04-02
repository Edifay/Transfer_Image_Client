package fr.arnaud.transfer_image_client.channel;


import static fr.arnaud.transfer_image_client.MainActivity.runUi;
import static fr.arnaud.transfer_image_client.transfer.utils.Utils.byteToMegaByte;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import apifornetwork.data.packets.SendSecurePacket;
import apifornetwork.tcp.SocketMake;
import fr.arnaud.transfer_image_client.channel.utils.PType;
import fr.arnaud.transfer_image_client.databinding.FragmentFirstBinding;
import fr.arnaud.transfer_image_client.transfer.utils.ImageDescriptor;
import fr.arnaud.transfer_image_client.transfer.utils.Utils;

public class ImageSender {

    private final int PACKET_SIZE = 10000000;

    private final SocketMake socket;
    private final ArrayList<ImageDescriptor> owned;
    private final ArrayList<ImageDescriptor> needed;

    public ImageSender(final SocketMake socket, final ArrayList<ImageDescriptor> owner, final ArrayList<ImageDescriptor> needed) {
        this.socket = socket;
        this.owned = owner;
        this.needed = needed;
    }

    private static int totalRead = 0;
    private static long currentReset;

    public void send(final FragmentFirstBinding binding) throws IOException, InterruptedException, ClassNotFoundException {
        final AtomicInteger i = new AtomicInteger(0);

        new Thread(() -> {
            try {
                currentReset = System.currentTimeMillis();
                while (i.get() + 1 < needed.size()) {
                    Thread.sleep(1000);
                    System.out.println("SETTING : " + totalRead);
                    if (i.get() + 1 < needed.size())
                        runUi(() -> {
                            try {
                                binding.status.setText((int) (byteToMegaByte(totalRead) / ((System.currentTimeMillis() - currentReset) / 1000f)) + " MB/s");
                                totalRead = 0;
                                currentReset = System.currentTimeMillis();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        for (; i.get() < needed.size(); i.incrementAndGet()) {

            runUi(() -> {
                binding.current.setText((i.get() + 1) + "");
                binding.progressBar.setProgress((int) (((float) (i.get() + 1) / needed.size()) * 100), true);
            });

            if (owned.contains(needed.get(i.get())))
                sendImage(needed.get(i.get()));
            else
                System.exit(-1);
        }
    }

    private void sendImage(final ImageDescriptor descriptor) throws IOException {
        final InputStream in = Utils.openStreamFile(descriptor);
        final int size = in.available();
        byte[] buf = new byte[size < PACKET_SIZE ? 0 : PACKET_SIZE];

        if (size >= PACKET_SIZE) printStatus(descriptor, size, size);

        while (in.available() > PACKET_SIZE) {
            in.read(buf);
            sendBuff(buf);
            printStatus(descriptor, in.available(), size);
            totalRead += PACKET_SIZE;
        }

        buf = new byte[in.available()];
        in.read(buf);
        sendBuff(buf);
        printStatus(descriptor, in.available(), size);
        totalRead += buf.length;

    }

    public void sendBuff(final byte[] buff) throws IOException {
        socket.send(new SendSecurePacket(PType.RECEIVING_DATA, buff));
    }

    public static void printStatus(final ImageDescriptor descriptor, final int current, final int total) {
        System.out.printf(descriptor.getName() + "\t -> \t" + "%.0f" + "/" + "%.0f" + "MB", byteToMegaByte(total - current), byteToMegaByte(total)).println();
    }


}
