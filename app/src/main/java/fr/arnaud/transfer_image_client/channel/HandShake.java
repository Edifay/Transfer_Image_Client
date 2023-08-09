package fr.arnaud.transfer_image_client.channel;

import static fr.arnaud.transfer_image_client.channel.Utils.getByteForObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import fr.arnaud.transfer_image_client.channel.utils.PType;
import fr.arnaud.transfer_image_client.files.Settings;
import fr.arnaud.transfer_image_client.transfer.utils.ImageDescriptor;
import fr.jazer.session.RPacket;
import fr.jazer.session.SPacket;
import fr.jazer.session.Session;

public class HandShake {

    private final Session session;
    private final Settings settings;
    private final ArrayList<ImageDescriptor> owned;

    public HandShake(final Session session, final Settings settings, final ArrayList<ImageDescriptor> owned) {
        this.session = session;
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
        return (boolean) new ObjectInputStream(new ByteArrayInputStream(session.read(PType.PASSWORD_PACKET).getData())).readUnshared();
    }

    private void sendPassword() throws IOException {
        session.send(new SPacket(PType.PASSWORD_PACKET).writeString(settings.getPassword()));
    }


    private ArrayList<ImageDescriptor> receiveImagesDescriptors() throws InterruptedException, IOException, ClassNotFoundException {
        final RPacket packetDescriptorList = session.read(PType.EXCHANGING_IMAGES_DESCRIPTOR);
        final String json = packetDescriptorList.readString();

        return new ObjectMapper().readValue(json, new TypeReference<ArrayList<ImageDescriptor>>() {
        });
    }

    private void writeImagesDescriptors(final ArrayList<ImageDescriptor> descriptors) throws IOException {
        final String json = new ObjectMapper().writeValueAsString(descriptors);
        session.send(new SPacket(PType.EXCHANGING_IMAGES_DESCRIPTOR).writeString(json));
    }

    private void sendAllOk() throws IOException {
        session.send(new SPacket(PType.COMFIRM, getByteForObject(true)));
    }
}
