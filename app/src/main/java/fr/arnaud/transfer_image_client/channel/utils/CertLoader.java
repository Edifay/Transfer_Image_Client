package fr.arnaud.transfer_image_client.channel.utils;

import java.io.InputStream;
import java.util.Scanner;

import fr.arnaud.transfer_image_client.MainActivity;

public class CertLoader {

    private static final String CLE_PUBLIC = "CLE_PUBLIC.bks";

    private static final String PASSWORD_CLE_PUBLIC = "mdp_public.txt";

    private static final String LOCAL_PATH = "/assets/keyStore/";

    public CertConfiguration getConfig(){
        final Scanner scanner = new Scanner(MainActivity.class.getResourceAsStream(LOCAL_PATH + PASSWORD_CLE_PUBLIC));


        final InputStream stream = MainActivity.class.getResourceAsStream(LOCAL_PATH + CLE_PUBLIC);
        final String password = scanner.nextLine();

        System.out.println("Password : " + password);
        System.out.println("JKS " + LOCAL_PATH + CLE_PUBLIC);

        scanner.close();

        return new CertConfiguration(stream, password);
    }


    public static class CertConfiguration {
        public final InputStream stream;
        public final String password;

        public CertConfiguration(final InputStream stream, final String password) {
            this.stream = stream;
            this.password = password;
        }
    }

}
