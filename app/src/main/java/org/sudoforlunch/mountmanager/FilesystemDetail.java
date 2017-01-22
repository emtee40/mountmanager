package org.sudoforlunch.mountmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class FilesystemDetail extends AppCompatActivity {

    private Filesystem fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filesystem_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(FilesystemDetail.this).create();
            alertDialog.setTitle(getResources().getString(R.string.yousurebruh));
            alertDialog.setMessage(getResources().getString(R.string.moresure) + fs.getMountpoint() + getResources().getString(R.string.as) + (fs.isWritable() ? getResources().getString(R.string.readonly) : getResources().getString(R.string.readwrite)));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ohno),
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.cont),
                    (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), FilesystemRemount.class);
                        intent.putExtra(FilesystemList.EXTRA_FSPOS, FilesystemList.fslist.indexOf(fs));
                        startActivity(intent);
                    });
            alertDialog.setCancelable(true);
            alertDialog.show();
        });
        Intent intent = getIntent();
        int pos = intent.getIntExtra(FilesystemList.EXTRA_FSPOS, 0);
        fs = FilesystemList.fslist.get(pos);

        setFsDetails();

    }

    private void setFsDetails() {
        StringBuilder text = new StringBuilder();

        text.append("Filesystem Details:\nFilesystem: ");
        text.append(fs.getFilesystem());
        text.append("\nMountpoint: ");
        text.append(fs.getMountpoint());
        text.append("\n");
        text.append("Writable: ");
        text.append(fs.isWritable() ? getResources().getString(R.string.yeah) : getResources().getString(R.string.nope));
        text.append("\nType: ");
        text.append(fs.getType());
        text.append("\nMount Options:");
        text.append(fs.getOptions().toString());

        TextView textv = (TextView) findViewById(R.id.fstext);
        textv.setText(text.toString());

        setTitle(fs.getMountpoint());
    }

}
