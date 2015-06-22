package com.boelroy.arrowpopwindows.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.boelroy.arrowpopwindows.lib.*;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrowPopWindows arrowPopWindows = new ArrowPopWindows(MainActivity.this, R.layout.layout, new ArrowPopWindows.OnViewCreateListener() {
                    @Override
                    public void onViewCreate(ViewGroup viewGroup) {

                    }
                });
                arrowPopWindows.show(view, ArrowPopWindows.SHOW_RIGHT);
            }
        });
    }

}
