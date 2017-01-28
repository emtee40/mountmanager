package org.sudoforlunch.mountmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;


import static org.sudoforlunch.mountmanager.FilesystemList.DO_NOT_CALL_BATSHIT_INSANE_FS;


public class FilesystemRemount extends AppCompatActivity {

    private Filesystem fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        fs = FilesystemList.fslist.get(intent.getIntExtra(FilesystemList.EXTRA_FSPOS, 0));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filesystem_remount);
        SeekBar seekbar = (SeekBar) findViewById(R.id.slider);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private static final int THUMB_MAX_MOVE = 5;
            private int currentProgress = 0;
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 10) {
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentProgress = 0;
                seekBar.setProgress(0);

            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // check if we've jumped too far
                if (Math.abs(currentProgress - progress) > THUMB_MAX_MOVE) {

                    // if so, revert to last progress and return
                    seekBar.setProgress(currentProgress);
                    return;
                }

                currentProgress = progress;
                if (currentProgress == 100) {
                    Button confirmbutton = (Button) findViewById(R.id.confirmbutton);
                    TextView conftext = (TextView) findViewById(R.id.conftext);
                    conftext.setVisibility(TextView.INVISIBLE);
                    confirmbutton.setVisibility(TextView.VISIBLE);
                    seekBar.setVisibility(SeekBar.INVISIBLE);
                    confirmbutton.setEnabled(true);
                    confirmbutton.setOnClickListener(v -> {
                        try {
                            Process su = Runtime.getRuntime().exec("su");
                            BufferedWriter mountswriter = new BufferedWriter(new OutputStreamWriter(su.getOutputStream()));
                            mountswriter.write("su\n");
                            mountswriter.write("mount -o remount,r" + (fs.isWritable() ? "o " : "w ") + fs.getMountpoint() +"\n");
                            mountswriter.write("exit\n");
                            mountswriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                            AlertDialog alertDialog = new AlertDialog.Builder(FilesystemRemount.this).create();
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
                        try {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            boolean blEnabled = prefs.getBoolean(getString(R.string.blEnabled), true);
                            FilesystemList.populateFilesystems(blEnabled);
                        } catch (ProcMountsReadException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), FilesystemList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(FilesystemList.EXTRA_REFR, fs.getMountpoint());
                        startActivity(intent);
                    });
                }

            }
        });

        setTitle(fs.getMountpoint());

        TextView warn1 = (TextView) findViewById(R.id.warn1);
        TextView warn2 = (TextView) findViewById(R.id.warn2);
        TextView fsremounting = (TextView) findViewById(R.id.fsremouning);
        TextView roorrw = (TextView) findViewById(R.id.roorrw);

        for (String fsp: DO_NOT_CALL_BATSHIT_INSANE_FS){
            if (fs.getMountpoint().startsWith(fsp)) {
                warn1.setVisibility(TextView.INVISIBLE);
                warn2.setVisibility(TextView.INVISIBLE);
            }
        }

        fsremounting.setText(fs.getMountpoint());
        roorrw.setText(fs.isWritable() ? getResources().getString(R.string.toro) : getResources().getString(R.string.torw));

    }



}
