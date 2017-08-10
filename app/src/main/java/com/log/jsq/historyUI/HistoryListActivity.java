package com.log.jsq.historyUI;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.log.jsq.library.FuHao;
import com.log.jsq.mainUI.MainActivity;
import com.log.jsq.tool.HistoryListData;

import com.log.jsq.R;
import com.log.jsq.tool.HistoryListSqlite;
import com.log.jsq.tool.TextHandler;
import com.log.jsq.tool.Theme;
import com.log.jsq.tool.Time;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryListActivity
        extends AppCompatActivity
        implements HistoryListAdapter.MyItemClickListener,
        HistoryCallback.ItemTouchHelperAdapter,
        HistoryListAdapter.MyCheckBoxClickListener {
    private Menu menu;
    private ArrayList<HistoryListData.RowData> arrayList;
    private ArrayList<HistoryListData.RowData> arrayRecycler;
    private ArrayList<HistoryListData.RowData> arrayUpdate;
    private HistoryListAdapter adapter;
    private HistoryCallback callback;
    private Activity thisActivity = this;
    private int recycler = 0;
    private boolean startFromMainActivity = true;

    @Override
    protected void finalize() throws Throwable {
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        super.finalize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String startFrom = getIntent().getStringExtra("startFrom");
        if (startFrom == null || !startFrom.equals(MainActivity.class.toString())) {
            Log.w(getClass().toString(), "不是通过主页面启动!");
            startFromMainActivity = false;
        }

        super.onCreate(savedInstanceState);
        Theme.setTheme(this);
        setContentView(R.layout.activity_history_list);

        setTitle();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = HistoryListData.exportAllFromSQLite(getApplicationContext());
        Collections.sort(arrayList);
        adapter = new HistoryListAdapter(arrayList, this);
        adapter.setOnItemClickListener(this);
        adapter.setOnCheckBoxClickListener(this);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        callback = new HistoryCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setTitle() {
        setTitle(getResources().getString(R.string.history));

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_menu, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.recycler:
                dataRecycler();
                return true;
            case R.id.comeBreak:
                breakToActivity(MainActivity.class);
                return true;
            case R.id.deleteHistoryOf_all:
                batchDeletion(Time.time.ALL, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_aWeekAgo:
                batchDeletion(Time.time.A_WEEK, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_halfAMonthAgo:
                batchDeletion(Time.time.HALF_A_MONTH, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_aMonthAgo:
                batchDeletion(Time.time.A_MONTH, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_halfAYearAgo:
                batchDeletion(Time.time.HALF_A_YEAR, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_aYearAgo:
                batchDeletion(Time.time.A_YEAR, item.getTitle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU) {  //MENU键
            return true;       //监控/拦截菜单键
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        final HistoryListData.RowData rowData = arrayList.get(position);
        final String result = rowData.getResult();
        final String equation = rowData.getEquation();

        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        final CharSequence equationHtml = TextHandler.setStyle(equation, value.data);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View particularView = inflater.inflate(R.layout.history_particular, null);
        ((TextView) particularView.findViewById(R.id.history_particular_title)).setText(result);
        ((TextView) particularView.findViewById(R.id.history_particular_body)).setText(equationHtml);

        new AlertDialog.Builder(this)
                .setView(particularView)
                .setPositiveButton("载入到计算器", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor spe = getSharedPreferences("list", MODE_PRIVATE).edit();
                        spe.putString("textView0", equation);
                        spe.putString("numTextView0", FuHao.dengYu + result);
                        spe.putBoolean("normal", false);
                        spe.apply();

                        breakToActivity(MainActivity.class);
                    }
                })
                .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        HistoryListData.RowData rowData = arrayList.get(position);
        String clipStr = rowData.getEquation() + FuHao.dengYu + rowData.getResult();
        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);  //实例化剪切板服务
        ClipData myClip = ClipData.newPlainText("复制的算式", clipStr);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(this, "算式及结果已复制", Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        if (arrayRecycler == null) {
            arrayRecycler = new ArrayList<>();
        }

        arrayRecycler.add(arrayList.get(position).setPosition(position));
        arrayList.remove(position);
        adapter.deleteItem(position);
        recycler++;
        menu.findItem(R.id.recycler).setVisible(recycler > 0);
        menu.findItem(R.id.comeBreak).setVisible(recycler == 0);
    }

    @Override
    public void onCheckBoxClick(View view, int position) {
        CheckBox checkBox = (CheckBox) view;
        HistoryListData.RowData rowData = arrayList.get(position);

        if (arrayUpdate == null) {
            arrayUpdate = new ArrayList<HistoryListData.RowData>();
        }

        rowData.setImportance(checkBox.isChecked());
        //对arrayList重新排序
        Collections.sort(arrayList);

        arrayUpdate.remove(rowData);
        arrayUpdate.add(rowData);
        adapter.updateItem(arrayList.indexOf(rowData), position);
    }

    private void dataRecycler() {
        HistoryListData.RowData rowData = arrayRecycler.get(arrayRecycler.size() - 1);
        arrayList.add(rowData.getPosition(), rowData);
        arrayRecycler.remove(arrayRecycler.size() - 1);
        recycler--;
        adapter.recoverItem(rowData.getPosition());
        menu.findItem(R.id.recycler).setVisible(recycler > 0);
        menu.findItem(R.id.comeBreak).setVisible(recycler == 0);
    }

    @Override
    public void finish() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                updateRowFromSql();

                if (arrayRecycler != null) {
                    int len = arrayRecycler.size();
                    long[] times = new long[len];

                    for (int i = 0; i < len; i++) {
                        times[i] = arrayRecycler.get(i).getTime();
                    }

                    HistoryListData.deleteRow(HistoryListSqlite.TABLE_NAME, times, getApplicationContext());
                    arrayRecycler.clear();
                    arrayRecycler = null;
                }
            }
        };
        thread.start();

        if (adapter != null) {
            adapter.release();
            adapter = null;
        }

        if (callback != null) {
            callback.release();
            callback = null;
        }

        if (arrayList != null) {
            arrayList.clear();
            arrayList = null;
        }

        menu = null;
        thisActivity = null;

        super.finish();

        if (!startFromMainActivity) {
            if (MainActivity.isOnCreated()) {
                breakToActivity(MainActivity.class);
                return;
            }

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }
    }

    private void breakToActivity(Class mClass) {
        if (!startFromMainActivity) {
            Intent intent = new Intent(getApplicationContext(), mClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent, new Bundle());
        }

        finish();
    }

    private void updateRowFromSql() {
        if (arrayUpdate != null) {
            int len = arrayUpdate.size();

            if (len > 0) {
                HistoryListData.RowData[] rowDates = new HistoryListData.RowData[len];

                for (int i = 0; i < len; i++) {
                    rowDates[i] = arrayUpdate.get(i);
                }

                HistoryListData.updateFromSQLite(HistoryListSqlite.TABLE_NAME, rowDates, getApplicationContext());
                arrayUpdate.clear();
                arrayUpdate = null;
            }
        }
    }

    private void batchDeletion(final Time.time time, CharSequence title) {
        final boolean[] deleteImportance = {false};

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.sure) + getString(R.string.delete) + title + "的记录?")
                .setMultiChoiceItems(
                        new String[]{getString(R.string.deleteImportance)},
                        new boolean[]{false},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    deleteImportance[0] = isChecked;
                            }
                        })
                .setNegativeButton(getString((R.string.close)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.waiting_view, null);
                        TextView waitingText = (TextView) view.findViewById(R.id.waitingTitle);
                        waitingText.setText(thisActivity.getString(R.string.deleting));

                        AlertDialog waitingDialog = new AlertDialog.Builder(thisActivity)
                                .setView(view)
                                .setCancelable(false)
                                .create();
                        waitingDialog.show();

                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                long minTimeMillis = Time.getMinTime(time);
                                updateRowFromSql();
                                HistoryListData.deleteRow(HistoryListSqlite.TABLE_NAME, minTimeMillis, deleteImportance[0], getApplicationContext());

                                arrayList.clear();
                                if (arrayRecycler != null) {
                                    arrayRecycler.clear();
                                }
                                if (arrayUpdate != null) {
                                    arrayUpdate.clear();
                                }

                                arrayList = HistoryListData.exportAllFromSQLite(getApplicationContext());
                                Collections.sort(arrayList);
                                adapter.setmDataset(arrayList);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        menu.findItem(R.id.recycler).setVisible(false);
                                        menu.findItem(R.id.comeBreak).setVisible(true);
                                    }
                                });
                            }
                        };
                        thread.start();

                        try {
                            thread.join();
                            Toast.makeText(thisActivity, getString(R.string.deleteSure), Toast.LENGTH_SHORT).show();
                        } catch (InterruptedException e) {
                            Toast.makeText(thisActivity, getString(R.string.deleteError), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } finally {
                            waitingDialog.cancel();
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .create()
                .show();
    }
}
