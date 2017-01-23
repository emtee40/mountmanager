package org.sudoforlunch.mountmanager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());
        setTitle(getResources().getString(R.string.about));

        TextView tv = (TextView) findViewById(R.id.abouttv);
        String abouttext = getResources().getString(R.string.abouttext).replace("BUILD_VERSION", BuildConfig.VERSION_NAME);

        /* I overuse the conditional ? I should change that : I guess it works well in many places */
        tv.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(abouttext, 0) : Html.fromHtml(abouttext));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
