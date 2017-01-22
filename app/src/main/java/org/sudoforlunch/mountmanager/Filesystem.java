package org.sudoforlunch.mountmanager;

import java.util.ArrayList;
import java.util.Arrays;

public class Filesystem {
    private String filesystem;
    private String mountpoint;
    private String type;
    private ArrayList<String> options;
    private boolean writable;

    public Filesystem(String mount) throws InvalidMountLineException {
        String split[] = mount.split(" ");
        if (split.length != 6)
            throw new InvalidMountLineException("6 columns not found!");
        this.filesystem = split[0];
        this.mountpoint = split[1];
        this.type = split[2];
        this.options = new ArrayList<>(Arrays.asList(split[3].split(",")));
        this.writable = true;
        if (this.options.contains("ro")) this.writable = false;
    }

    public String getFilesystem() {
        return filesystem;
    }

    public String getMountpoint() {
        return mountpoint;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public boolean isWritable() {
        return writable;
    }
}
