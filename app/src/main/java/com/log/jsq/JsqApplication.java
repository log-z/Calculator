package com.log.jsq;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;


public class JsqApplication extends Application {
    @Override
    public void onCreate() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        super.onCreate();
    }

    private static class CrashHandler implements UncaughtExceptionHandler {
        private Context context;
        private static CrashHandler INSTANCE = new CrashHandler();
        private UncaughtExceptionHandler defaultHandler;

        private CrashHandler(){}

        public static CrashHandler getInstance() {
            return INSTANCE;
        }

        public void init(Context context) {
            this.context = context;
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // 对未捕获异常处理
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            final String errorLog = baos.toString();

            Log.e("error", errorLog);

            ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);  //实例化剪切板服务
            ClipData myClip = ClipData.newPlainText("errorLog", errorLog);
            myClipboard.setPrimaryClip(myClip);

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(context, "< 发现未知错误 >\n崩溃日志已复制，请尽快反馈给作者，多谢！\n\n" + errorLog, Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(-1);
        }
    }
}
