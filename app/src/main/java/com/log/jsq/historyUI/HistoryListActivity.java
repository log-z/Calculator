package com.log.jsq.historyUI;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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
import com.log.jsq.R;
import com.log.jsq.tool.HistoryListData;
import com.log.jsq.tool.Theme;
import com.log.jsq.tool.Time;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryListActivity extends AppCompatActivity
        implements HistoryListAdapter.OnItemClickListener,
        HistoryListAdapter.OnItemLongClickListener, HistoryListAdapter.OnCheckBoxClickListener,
        HistoryCallback.ItemTouchHelperAdapter, HistoryDialog.Callback,
        HistoryDecoration.DecorationCallback {

    /** 当前选项菜单 */
    private Menu mMenu;
    /** 历史记录详情弹窗 */
    private HistoryDialog mDialog;
    /** 历史记录数据集 */
    private ArrayList<HistoryListData.RowData> mDataset = new ArrayList<>();
    /** 将要被移除的数据集（规定为栈结构，index=0为栈顶） */
    private ArrayList<HistoryListData.RowData> mDeleted = new ArrayList<>();
    /** 已更新数据集 */
    private ArrayList<HistoryListData.RowData> mUpdated = new ArrayList<>();
    /** RecyclerView构造器 */
    private HistoryListAdapter mAdapter;
    /** 条目操作回调 */
    private HistoryCallback mCallback;
    /** 是否是从主页打开此Activity */
    private boolean mStartFromMain = true;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setTheme(this);
        setContentView(R.layout.activity_history_list);
        setTitle();

        new Thread() {
            @Override
            public void run() {
                // 当前Activity的引用
                final HistoryListActivity hla = HistoryListActivity.this;

                // 判断是否从主页启动
                String startFrom = getIntent().getStringExtra("startFrom");
                if (startFrom == null || !startFrom.equals(MainActivity.class.toString())) {
                    Log.w(getClass().toString(), "不是通过主页面启动!");
                    mStartFromMain = false;
                }

                recyclerView = findViewById(R.id.recycler_view);
                // 设置固定条目大小
                recyclerView.setHasFixedSize(true);
                // 设置布局管理器
                recyclerView.setLayoutManager(new LinearLayoutManager(hla));
                // 设置条目动画（增加、移除）
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                // 添加条目分割线
                recyclerView.addItemDecoration(new HistoryDecoration(
                        HistoryListActivity.this,
                        HistoryListActivity.this
                ));

                // 从数据库取出数据集
                mDataset = HistoryListData.exportAllFromSQLite(getApplicationContext());
                // 排序数据集
                Collections.sort(mDataset, new HistoryListData.SortByTimeDesc());

                for (HistoryListData.RowData data : mDataset) {
                    System.out.println("///////// " + data.getTime() + " /// " + data.getResult());
                }

                // 建立构造器
                mAdapter = new HistoryListAdapter(hla.mDataset, hla);
                // 绑定条目点击监听
                mAdapter.setOnItemClickListener(hla);
                // 绑定条目长按监听
                mAdapter.setOnItemLongClickListener(hla);
                // 绑定重要复选框监听
                mAdapter.setOnCheckBoxClickListener(hla);
                // 绑定构造器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(mAdapter);
                    }
                });

                // 建立条目操作回调
                mCallback = new HistoryCallback(hla);
                // 绑定条目操作回调
                final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                });

                // 初始化历史记录详情弹窗
                mDialog = HistoryDialog.getInstance();
                mDialog.setCallback(hla);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.init(hla);
                    }
                });

                // 显示列表
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();
    }

    /**
     * 设置标题栏
     */
    private void setTitle() {
        setTitle(getResources().getString(R.string.history));

        // 显示返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 当构建选项菜单时
     * @param menu  选项菜单
     * @return      是否显示选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(this.mMenu = menu);
    }

    /**
     * 当选项菜单被选择时
     * @param item  被选择的菜单项
     * @return      是否消耗掉选择
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 返回
                finishAfterTransition();
                return true;
            case R.id.undo:
                // 撤消
                undo();
                return true;
            case R.id.comeBreak:
                // 返回到计算器
                breakToMainActivity();
                return true;
            case R.id.deleteHistoryOf_all:
                // 删除所有历史记录
                batchDeletion(Time.Span.ALL, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_aWeekAgo:
                // 删除一周前的历史记录
                batchDeletion(Time.Span.A_WEEK, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_halfAMonthAgo:
                // 删除半个月前的历史记录
                batchDeletion(Time.Span.HALF_A_MONTH, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_aMonthAgo:
                // 删除一个月前的历史记录
                batchDeletion(Time.Span.A_MONTH, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_halfAYearAgo:
                // 删除半年前的历史记录
                batchDeletion(Time.Span.HALF_A_YEAR, item.getTitle());
                return true;
            case R.id.deleteHistoryOf_aYearAgo:
                // 删除一年前的历史记录
                batchDeletion(Time.Span.A_YEAR, item.getTitle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 当按下返回键时
     */
    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    /**
     * 当按键弹起时
     * @param keyCode   按键值
     * @param event     按键动作
     * @return          是否消耗掉按压操作
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 拦截MENU键
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyUp(keyCode, event);
    }

    /**
     * 当条目被点击时
     * @param view      条目视图
     * @param position  条目位置
     */
    @Override
    public void onItemClick(final View view, int position) {
        mDialog.show(this, mDataset.get(position));
    }

    /**
     * 当条目被长按时
     * @param view      条目视图
     * @param position  条目位置
     * @return          是否消耗掉按压操作
     */
    @Override
    public boolean onItemLongClick(View view, int position) {
        // 提取此条目的数据
        HistoryListData.RowData rowData = mDataset.get(position);
        String clipStr = rowData.getEquation() + FuHao.dengYu + rowData.getResult();
        // 实例化剪切板服务
        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("算式和结果", clipStr);
        // 添加算式和结果到剪切板
        if (myClipboard != null) {
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(this, R.string.copyAll, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.copyError, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    /**
     * 当条目被划掉后
     * @param position  条目位置
     */
    @Override
    public void onItemDismiss(final int position) {
        // 把条目数据移至mDeleted
        mDeleted.add(0, mDataset.remove(position));
        // 在构造器中移除该条目
        mAdapter.deleteItem(position);
        // 刷新菜单显示
        setMenuItemVisible();
    }

    /**
     * 当重要复选框被点击时
     * @param checkBox  条目视图
     * @param position  条目位置
     */
    @Override
    public void onCheckBoxClick(CheckBox checkBox, int position) {
        HistoryListData.RowData data = mDataset.get(position);
        // 更新数据
        data.setImportance(checkBox.isChecked());
        //对数据集重新排序
        Collections.sort(mDataset, new HistoryListData.SortByTimeDesc());
        // 添加数据到mUpdated
        addToUpdated(data);
        // 更新构造器中条目的位置
        mAdapter.moveItem(mDataset.indexOf(data), position);
    }

    /**
     * 执行撤消操作
     */
    private void undo() {
        // 取出mDeleted栈顶
        HistoryListData.RowData data = mDeleted.remove(0);
        // 把数据放回数据集
        mDataset.add(data);
        // 重新排序数据集
        Collections.sort(mDataset, new HistoryListData.SortByTimeDesc());
        // 在构造器中刷新显示
        mAdapter.addItem(mDataset.indexOf(data));
        // 刷新菜单显示
        setMenuItemVisible();
    }

    /**
     * 结束Activity
     */
    @Override
    public void finish() {
        // 如果不是从主页打开历史记录，而且主页已被构建，则回到主页
        if (!mStartFromMain && MainActivity.isCreated()) {
            breakToMainActivity();
        }

        super.finish();
    }

    /**
     * 当Activity被销毁时
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 数据库同步线程
        final Thread dbThread = new Thread() {
            @Override
            public void run() {
                // 更新数据
                updateDB();
                mUpdated = null;

                // 删除数据
                int len = mDeleted.size();
                long[] times = new long[len];
                for (int i = 0; i < len; i++) {
                    times[i] = mDeleted.get(i).getTime();
                }
                HistoryListData.deleteRow(times, getApplicationContext());
                mDeleted.clear();
                mDeleted = null;
            }
        };
        dbThread.start();

        // 释放构造器
        if (mAdapter != null) {
            mAdapter.release();
            mAdapter = null;
        }

        // 释放操作回调
        if (mCallback != null) {
            mCallback.release();
            mCallback = null;
        }

        // 释放数据集和菜单属性
        mDataset.clear();
        mDataset = null;
        mMenu = null;

        // 清理时间段缓存
        HistoryDecoration.TimeSpan.clear();

        // 如果不是从主页打开历史记录，而且主页没有被构建，则直接关闭程序
        if (!mStartFromMain && !MainActivity.isCreated()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        // 等待数据库同步结束
                        dbThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            }.start();
        }
    }

    /**
     * 返回主页
     */
    private void breakToMainActivity() {
        if (mStartFromMain) {
            finish();
        } else {
            // 重新打开主页
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent, new Bundle());
        }
    }

    /**
     * 更新数据库
     */
    private void updateDB() {
        HistoryListData.updateFromSQLite(mUpdated, getApplicationContext());
        mUpdated.clear();
    }

    /**
     * 批量删除操作
     * @param timeSpan      时间跨度
     * @param itemStr       弹窗标题
     */
    private void batchDeletion(final Time.Span timeSpan, CharSequence itemStr) {
        // 是否删除重要数据
        // final数组内部仍可更改
        final boolean[] deleteImportance = {false};
        // 确认删除弹窗
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.sure)
                        + getString(R.string.delete) + itemStr + "的记录?")
                // 同时删除重要记录复选框
                .setMultiChoiceItems(
                        new String[]{getString(R.string.deleteImportance)},
                        deleteImportance,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    deleteImportance[0] = isChecked;
                            }
                        })
                // 取消
                .setNegativeButton(getString((R.string.close)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                // 确定
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @SuppressLint("InflateParams")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater =
                                (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view;
                        if (inflater != null) {
                            view = inflater.inflate(R.layout.waiting_view, null);
                        } else {
                            new NullPointerException("inflater is null pointer")
                                    .printStackTrace();
                            return;
                        }

                        TextView waitingText = view.findViewById(R.id.waitingTitle);
                        waitingText.setText(R.string.deleting);

                        // 等待弹窗
                        final AlertDialog waitingDialog = new AlertDialog
                                .Builder(HistoryListActivity.this)
                                .setView(view)
                                // 不可取消弹窗
                                .setCancelable(false)
                                .create();
                        waitingDialog.show();

                        // 数据删除线程
                        final Thread thread = new Thread(){
                            @Override
                            public void run() {
                                // 先更新数据库
                                updateDB();

                                // 删除数据
                                long minTimeMillis = Time.getMinTime(timeSpan);
                                HistoryListData.deleteRow(minTimeMillis, deleteImportance[0],
                                        getApplicationContext());

                                // 清理所有数据存储
                                mDataset.clear();
                                mDeleted.clear();
                                mUpdated.clear();

                                // 重新获取数据
                                mDataset = HistoryListData
                                        .exportAllFromSQLite(getApplicationContext());
                                Collections.sort(mDataset, new HistoryListData.SortByTimeDesc());
                                mAdapter.setDataset(mDataset);

                                // 刷新菜单显示
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setMenuItemVisible();
                                    }
                                });
                            }
                        };
                        thread.start();

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    thread.join();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 删除完成
                                            Toast.makeText(HistoryListActivity.this,
                                                    getString(R.string.deleteSure),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 删除失败
                                            Toast.makeText(HistoryListActivity.this,
                                                    getString(R.string.deleteError),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });
                                } finally {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 取消弹窗
                                            waitingDialog.cancel();
                                            // 通知构造器数据集已更新
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }.start();
                    }
                })
                .create()
                .show();
    }

    /**
     * 当要载入到计算器时
     * @param result    要载入的结果
     * @param equation  要载入的算式
     */
    @Override
    public void onLoadToCalculator(String result, String equation) {
        SharedPreferences.Editor spe = getSharedPreferences("list", MODE_PRIVATE).edit();
        spe.putString("textView0", equation);
        spe.putString("numTextView0", FuHao.dengYu + result);
        spe.putBoolean("normal", false);
        spe.apply();
        breakToMainActivity();
    }

    /**
     * 当标签被修改时
     * @param data 被修改的数据组
     */
    @Override
    public void onTagChange(HistoryListData.RowData data) {
        // 刷新条目内容
        int position = mDataset.indexOf(data);
        mAdapter.notifyItemChanged(position);
        // 加入已更新数据集
        addToUpdated(data);
    }

    /**
     * 设置菜单选项显示状态
     */
    private void setMenuItemVisible() {
        int deletedSize = mDeleted.size();
        mMenu.findItem(R.id.undo).setVisible(deletedSize > 0);
        mMenu.findItem(R.id.comeBreak).setVisible(deletedSize == 0);
    }

    /**
     * 添加到已更新数据集
     * @param data  数据
     */
    private void addToUpdated(HistoryListData.RowData data) {
        if (!mUpdated.contains(data)) {
            mUpdated.add(data);
        }
    }

    /**
     * 获取条目时间
     * @param position  条目位置
     * @return          返回条目时间
     */
    @Override
    public long getItemTime(int position) {
        if (position < 0) {
            return -1;
        } else {
            return mDataset.get(position).getTime();
        }
    }

    /**
     * 确认是否为重要条目
     * @param position  条目位置
     * @return          返回指定条目是否为重要条目
     */
    @Override
    public boolean isImportantItem(int position) {
        return position >= 0 && mDataset.get(position).getImportance();
    }
}
