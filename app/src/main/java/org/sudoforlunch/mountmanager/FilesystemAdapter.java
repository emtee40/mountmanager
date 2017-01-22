package org.sudoforlunch.mountmanager;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


class FilesystemAdapter extends ArrayAdapter<Filesystem> {
    private ArrayList<Filesystem> fslist;

    FilesystemAdapter(Context context, int textViewResourceId, ArrayList<Filesystem> objects) {
        super(context, textViewResourceId, objects);
        this.fslist = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_view_items, null);
        }

        Filesystem fs = fslist.get(position);

        if (fs != null) {
            TextView textView = (TextView) v.findViewById(R.id.itemText);
            textView.setText(fs.getMountpoint());
            ImageView imageView = (ImageView) v.findViewById(R.id.itemIcon);
            if (fs.getOptions().contains("ro"))
                imageView.setImageResource(R.drawable.ic_ro);
            else
                imageView.setImageResource(R.drawable.ic_rw);
        }

        return v;
    }

}
