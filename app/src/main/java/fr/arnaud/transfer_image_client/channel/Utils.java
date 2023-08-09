package fr.arnaud.transfer_image_client.channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utils {
    public static byte[] getByteForObject(final Object object) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(output);
        out.writeUnshared(object);
        return output.toByteArray();
    }

}
