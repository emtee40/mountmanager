package org.sudoforlunch.mountmanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilesystemList extends AppCompatActivity {

    public static final String EXTRA_FSPOS = "org.sudoforlunch.mountmanager.FILESYSTEM_POSITION";
    public static final String EXTRA_REFR = "org.sudoforlunch.mountmanager.REFRESH";
    private static final String END_OF_PROC_MOUNTS = "---END OF PROC MOUNTS---";
    private static final String PROC_MOUNTS = "/proc/mounts";
    private static final List<String> IGNORE_FS_DIRS = Arrays.asList(
            "/dev",
            "/proc"
    );
    private static final List<String> IGNORE_FS_PATHS = Arrays.asList(
            "none",
            "tmpfs",
            "sysfs"
    );
    public static final List<String> DO_NOT_CALL_BATSHIT_INSANE_FS = Arrays.asList(
            "/system",
            "/mnt",
            "/storage",
            "/storage",
            "/cache"
    );
    public static ArrayList<Filesystem> fslist = new ArrayList<>();
    private static FilesystemAdapter adp;

    public static void populateFilesystems() throws ProcMountsReadException {
        try {

            System.out.flush();
            fslist.clear();
            Process su = Runtime.getRuntime().exec("su");
            BufferedWriter mountswriter = new BufferedWriter(new OutputStreamWriter(su.getOutputStream()));
            BufferedReader mountsreader = new BufferedReader(new InputStreamReader(su.getInputStream()));
            mountswriter.write("su\n");
            mountswriter.write("cat " + PROC_MOUNTS + "\n");
            mountswriter.write("exit\n");
            mountswriter.write("echo " + END_OF_PROC_MOUNTS + "\n");
            mountswriter.flush();

            String piece;
            while (true) {
                piece = mountsreader.readLine();

                if (piece == null) throw new ProcMountsReadException();
                if (piece.equals(END_OF_PROC_MOUNTS)) break;

                try {
                    Filesystem sys = new Filesystem(piece);
                    if (fsIsBlacklisted(sys)) continue;
                    fslist.add(sys);
                    adp.notifyDataSetChanged();
                } catch (InvalidMountLineException e) {
                    throw new ProcMountsReadException("Invalid Mount Line");
                }
            }
            mountsreader.close();
            mountsreader.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new ProcMountsReadException("IO Error");
        }
    }

    private static boolean fsIsBlacklisted(Filesystem sys) {
        if (IGNORE_FS_PATHS.contains((sys.getFilesystem()))) return true;
        for (String dir : IGNORE_FS_DIRS) if (sys.getMountpoint().startsWith(dir)) return true;
        return false;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filesystem_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            try {
                populateFilesystems();
                Snackbar.make(view, "Refreshed!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            } catch (ProcMountsReadException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(FilesystemList.this).create();
                alertDialog.setTitle(getResources().getString(R.string.fr_error));
                alertDialog.setMessage(getResources().getString(R.string.fr_noroot));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.okay),
                        (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
                alertDialog.setCancelable(false);
                alertDialog.show();

            }

        });


        final ListView fslistview = (ListView) findViewById(R.id.fslist);

        adp = new FilesystemAdapter(this, R.layout.list_view_items, fslist);
        fslistview.setAdapter(adp);

        fslistview.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), FilesystemDetail.class);
            intent.putExtra(EXTRA_FSPOS, position);
            startActivity(intent);
        });
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_REFR)) {
                Snackbar.make(fslistview, getResources().getString(R.string.wasremounted) + intent.getStringExtra(EXTRA_REFR), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }

        try {
            populateFilesystems();


        } catch (ProcMountsReadException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(FilesystemList.this).create();
            alertDialog.setTitle(getResources().getString(R.string.fr_error));
            alertDialog.setMessage(getResources().getString(R.string.fr_noroot));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.okay),
                    (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
            alertDialog.setCancelable(false);
            alertDialog.show();

        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if (!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.apply();
            AlertDialog alertDialog = new AlertDialog.Builder(FilesystemList.this).create();
            alertDialog.setTitle(getResources().getString(R.string.fr_warning));
            alertDialog.setMessage(getResources().getString(R.string.fr_text));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.okay),
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
            alertDialog.show();
        }

    }


}
