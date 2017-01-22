package org.sudoforlunch.mountmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


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
                    confirmbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            }
        });
    }



}
