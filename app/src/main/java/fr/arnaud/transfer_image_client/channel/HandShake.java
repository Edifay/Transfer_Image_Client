package fr.arnaud.transfer_image_client.channel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import apifornetwork.data.packets.Packet;
import apifornetwork.data.packets.ReceiveSecurePacket;
import apifornetwork.data.packets.SendSecurePacket;
import apifornetwork.tcp.SocketMake;
import fr.arnaud.transfer_image_client.channel.utils.PType;
import fr.arnaud.transfer_image_client.files.Settings;
import fr.arnaud.transfer_image_client.transfer.utils.ImageDescriptor;

public class HandShake {

    private final SocketMake socket;
    private final Settings settings;
    private final ArrayList<ImageDescriptor> owned;

    public HandShake(final SocketMake socket, final Settings settings, final ArrayList<ImageDescriptor> owned) {
        this.socket = socket;
        this.settings = settings;
        this.owned = owned;
    }

    public HandShakeData shake() throws IOException, InterruptedException, ClassNotFoundException {
        sendPassword();
        boolean authResult = authResult();

        if (!authResult)
            return new HandShakeData(false, null);

        writeImagesDescriptors(owned);

        ArrayList<ImageDescriptor> needed = receiveImagesDescriptors();

        sendAllOk();

        return new HandShakeData(true, needed);
    }

    public static class HandShakeData {
        public final boolean isAuth;
        public final ArrayList<ImageDescriptor> needed;

        public HandShakeData(final boolean isAuth, final ArrayList<ImageDescriptor> needed) {
            this.isAuth = isAuth;
            this.needed = needed;
        }

    }

    private boolean authResult() throws InterruptedException, IOException, ClassNotFoundException {
        return (boolean) new ObjectInputStream(new ByteArrayInputStream(socket.waitForPacket(PType.PASSWORD_PACKET).getBytesData())).readUnshared();
    }

    private void sendPassword() throws IOException {
        socket.send(new SendSecurePacket(PType.PASSWORD_PACKET, Packet.getByteForObject(settings.getPassword())));
    }


    private ArrayList<ImageDescriptor> receiveImagesDescriptors() throws InterruptedException, IOException, ClassNotFoundException {
        final ReceiveSecurePacket packetDescriptorList = socket.waitForPacket(PType.EXCHANGING_IMAGES_DESCRIPTOR);
        final String json = (String) new ObjectInputStream(new ByteArrayInputStream(packetDescriptorList.getBytesData())).readUnshared();

        return new ObjectMapper().readValue(json, new TypeReference<ArrayList<ImageDescriptor>>() {
        });
    }

    private void writeImagesDescriptors(final ArrayList<ImageDescriptor> descriptors) throws IOException {
        final String json = new ObjectMapper().writeValueAsString(descriptors);
        socket.send(new SendSecurePacket(PType.EXCHANGING_IMAGES_DESCRIPTOR, Packet.getByteForObject(json)));
    }

    private void sendAllOk() throws IOException {
        socket.send(new SendSecurePacket(PType.COMFIRM, Packet.getByteForObject(true)));
    }
}
