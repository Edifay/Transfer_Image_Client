package fr.arnaud.transfer_image_client.transfer.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.util.Objects;

public class ImageDescriptor {

    public static final ImageDescriptor EMPTY = new ImageDescriptor("-1", -1, -1);
    public String path;
    public long size;
    public long createdAt = 0;

    public ImageDescriptor() {

    }

    public ImageDescriptor(final String path, final long size, final long createdAt) {
        this.path = path;
        this.size = size;
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public String getName() {
        return new File(path).getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDescriptor that = (ImageDescriptor) o;
        return size == that.size && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, size);
    }
}
