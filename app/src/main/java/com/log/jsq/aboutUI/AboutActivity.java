package com.log.jsq.aboutUI;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.log.jsq.R;
import com.log.jsq.mainUI.MainActivity;
import com.log.jsq.tool.Open;

public class AboutActivity extends AppCompatActivity {

    private class ItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.itemHelp:
                    openHelp(v.getContext());
                    break;
                case R.id.itemUpdateHistrory:
                    String updateHistoryStr = Open.openTxt(getApplicationContext(), R.raw.update_log) + Open.openTxt(getApplicationContext(), R.raw.update_history);
                    new AlertDialog.Builder(v.getContext())
                            .setTitle(getString(R.string.updateHistory))
                            .setMessage(updateHistoryStr)
                            .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                    break;
                             case R.id.itemOpenSource:
                    String url = getString(R.string.openSourceLink);
                    Open.openLink(v.getContext(), null, url, false);
                    break;
                case R.id.itemScoreAndFeedback:
                    Open.openApplicationMarket(getPackageName(), "com.coolapk.market", v.getContext());
                    break;
                case R.id.itemDonate:
                    String alipayUrl = getString(R.string.alipayUrl);
                    Open.openLink(v.getContext(), null, alipayUrl, false);
                    ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);  //实例化剪切板服务
                    ClipData myClip = ClipData.newPlainText("donateLink", alipayUrl);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getApplicationContext(), getString(R.string.donateToast), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setTheme(this);
        setContentView(R.layout.activity_about);
        setActionBar();

        ItemOnClickListener clickListener = new ItemOnClickListener();
        findViewById(R.id.itemHelp).setOnClickListener(clickListener);
        findViewById(R.id.itemUpdateHistrory).setOnClickListener(clickListener);
        findViewById(R.id.itemOpenSource).setOnClickListener(clickListener);
        findViewById(R.id.itemScoreAndFeedback).setOnClickListener(clickListener);
        findViewById(R.id.itemDonate).setOnClickListener(clickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setActionBar() {
        setTitle(getResources().getString(R.string.about));

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void openHelp(Context context) {
        String helpStr = Open.openTxt(context, R.raw.help);
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.helpWord))
                .setMessage(helpStr)
                .setPositiveButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

}
