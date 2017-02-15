package com.log.jsq.historyUI;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.log.jsq.library.FuHao;
import com.log.jsq.library.RowData;
import com.log.jsq.mainUI.MainActivity;
import com.log.jsq.tool.HistoryListData;

import com.log.jsq.R;
import com.log.jsq.tool.HistoryListSqlite;
import com.log.jsq.tool.TextColorStyles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HistoryListActivity
        extends AppCompatActivity
        implements HistoryListAdapter.MyItemClickListener,
        HistoryCallback.ItemTouchHelperAdapter,
        HistoryListAdapter.MyCheckBoxClickListener {
    private HashMap<String, MenuItem> hashMap = new HashMap<String, MenuItem>();
    private ArrayList<RowData> arrayList;
    private ArrayList<RowData> arrayRecycler;
    private ArrayList<RowData> arrayUpdate;
    private HistoryListAdapter adapter;
    private HistoryCallback callback;
    private Activity thisActivity = this;
    private int recycler = 0;

    @Override
    protected void finalize() throws Throwable {
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        super.finalize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setTheme(this);
        setContentView(R.layout.activity_history_list);

        setTitle();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(thisActivity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = HistoryListData.exportAllFromSQLite(thisActivity);
        Collections.sort(arrayList);
        adapter = new HistoryListAdapter(arrayList, thisActivity);
        adapter.setOnItemClickListener((HistoryListAdapter.MyItemClickListener) thisActivity);
        adapter.setOnCheckBoxClickListener((HistoryListAdapter.MyCheckBoxClickListener) thisActivity);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(thisActivity, DividerItemDecoration.VERTICAL));

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
        final String recyclerStr = getString(R.string.recycler);
        final String homeItemStr = getString(R.string.app_name);
        final String allDeleteStr = getString(R.string.allDeleteHistory);

        MenuItem recyclerItem = menu.add(recyclerStr);
        recyclerItem.setIcon(R.drawable.undo_iocn);
        recyclerItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        recyclerItem.setVisible(recycler > 0);
        hashMap.put(recyclerStr, recyclerItem);

        MenuItem allDeleteItem = menu.add(allDeleteStr);
        hashMap.put(allDeleteStr, allDeleteItem);

        MenuItem homeItem = menu.add("回到 " + homeItemStr);
        homeItem.setIcon(R.drawable.home_iocn);
        homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        hashMap.put(homeItemStr, homeItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
            return true;
        } else if (item == hashMap.get(getString(R.string.recycler))) {
            dataRecycler();
        } else if (item == hashMap.get(getResources().getString(R.string.app_name))) {
            finishAfterTransition();
            return true;
        } else if (item == hashMap.get(getString(R.string.allDeleteHistory))) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.allDeleteHistory))
                    .setMessage(getString(R.string.sureDelete))
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
                                    HistoryListData.deleteRow(HistoryListSqlite.TABLE_NAME,
                                            new long[] {HistoryListData.ALL_TIME},
                                            thisActivity
                                    );

                                    arrayList.clear();
                                    if (arrayRecycler != null) {
                                        arrayRecycler.clear();
                                    }
                                    if (arrayUpdate != null) {
                                        arrayUpdate.clear();
                                    }

                                    thisActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hashMap.get(getString(R.string.recycler)).setVisible(false);
                                        }
                                    });
                                }
                            };
                            thread.start();

                            try {
                                thread.join();
                                Toast.makeText(thisActivity, "历史记录已清空", Toast.LENGTH_SHORT).show();
                            } catch (InterruptedException e) {
                                Toast.makeText(thisActivity, "删除失败，请重试...", Toast.LENGTH_SHORT).show();
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

        return super.onOptionsItemSelected(item);
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
        final RowData rowData = arrayList.get(position);
        final String result = rowData.getResult();
        final String equation = rowData.getEquation();

        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        final CharSequence equationHtml = TextColorStyles.run(equation, value.data);

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
                        spe.apply();

                        finishAfterTransition();
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
        RowData rowData = arrayList.get(position);
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
            arrayRecycler = new ArrayList<RowData>();
        }

        arrayRecycler.add(arrayList.get(position).setPosition(position));
        arrayList.remove(position);
        adapter.deleteItem(position);
        recycler++;
        hashMap.get(getString(R.string.recycler)).setVisible(recycler > 0);
    }

    @Override
    public void onCheckBoxClick(View view, int position) {
        CheckBox checkBox = (CheckBox) view;
        RowData rowData = arrayList.get(position);

        if (arrayUpdate == null) {
            arrayUpdate = new ArrayList<RowData>();
        }

        rowData.setImportance(checkBox.isChecked());
        //对arrayList重新排序
        Collections.sort(arrayList);

        arrayUpdate.remove(rowData);
        arrayUpdate.add(rowData);
        adapter.updateItem(arrayList.indexOf(rowData), position);
    }

    private void dataRecycler() {
        RowData rowData = arrayRecycler.get(arrayRecycler.size() - 1);
        arrayList.add(rowData.getPosition(), rowData);
        arrayRecycler.remove(arrayRecycler.size() - 1);
        recycler--;
        adapter.recoverItem(rowData.getPosition());
        hashMap.get(getString(R.string.recycler)).setVisible(recycler > 0);
    }

    @Override
    public void finish() {
        new Thread() {
            @Override
            public void run() {
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

                if (arrayUpdate != null) {
                    int len = arrayUpdate.size();
                    RowData[] rowDates = new RowData[len];

                    for (int i = 0; i < len; i++) {
                        rowDates[i] = arrayUpdate.get(i);
                    }

                    HistoryListData.updateFromSQLite(HistoryListSqlite.TABLE_NAME, rowDates, getApplicationContext());
                    arrayUpdate.clear();
                    arrayUpdate = null;
                }

            }
        }.start();

        if (adapter != null) {
            adapter.release();
            adapter = null;
        }

        if (callback != null) {
            callback.release();
            callback = null;
        }

        arrayList.clear();
        arrayList = null;
        hashMap.clear();
        thisActivity = null;

        super.finish();
    }
}
