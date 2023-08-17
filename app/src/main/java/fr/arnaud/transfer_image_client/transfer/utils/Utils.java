package fr.arnaud.transfer_image_client.transfer.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.arnaud.transfer_image_client.MainActivity;

public class Utils {

    public static ArrayList<String> getAllMedias() {
        ArrayList<String> imageList = getAllImages(MainActivity.activity);
        ArrayList<String> videoList = getAllVideos(MainActivity.activity);

        return (ArrayList<String>) Stream.concat(imageList.stream(), videoList.stream()).collect(Collectors.toList());
    }

    public static ArrayList<String> getAllImages(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        ArrayList<String> listOfAllImages = new ArrayList<String>();

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    public static ArrayList<String> getAllVideos(Activity activity) {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = activity.getBaseContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        return downloadedList;
    }

    public static InputStream openStreamFile(final ImageDescriptor descriptor) throws IOException {
        return new BufferedInputStream(Files.newInputStream(Paths.get(descriptor.path)));
    }

    public static float byteToMegaByte(long value) {
        return value / 1000000f;
    }


    public static boolean isSnapchatFile(final File f) {
        return f.getName().contains("Snapchat") && f.getName().contains(".jpg");
    }

    public static long getDateFromSnap(final File f) {
        return f.lastModified();
    }

    public static Date getDateFrom(final String data) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd HH:mm:ss yyyy");
        return format.parse(getExtractShittyFormat(data));
    }

    public static String getExtractShittyFormat(final String data) {
        return data.substring(4, 20) + data.substring(27, 31);
    }

}
