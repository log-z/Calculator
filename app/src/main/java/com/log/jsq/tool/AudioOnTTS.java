package com.log.jsq.tool;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.log.jsq.R;

import java.util.List;
import java.util.Locale;

public class AudioOnTTS implements TextToSpeech.OnInitListener {
    private Context context;
    private TextToSpeech tts;
    private Exceptional exceptional;

    public interface Exceptional {
        public void TTSExceptionalHandle(View view);
    }

    public AudioOnTTS(Context context, String ttsPackageName, Exceptional exceptional) {
        this.context = context;
        this.exceptional = exceptional;
        tts = new TextToSpeech(context, this, ttsPackageName);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setSpeechRate(0.8f);
            int result = tts.setLanguage(Locale.CHINA);

            if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE && result != TextToSpeech.LANG_AVAILABLE) {
                Toast.makeText(context, "当前TTS不支持简体中文，请到设置更换TTS程序！", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void speak(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, "speech");
    }

    public void speak(View view) {
        switch (view.getId()) {
            case R.id.b0:
                speak(context.getString(R.string._0));
                break;
            case R.id.b1:
                speak(context.getString(R.string._1));
                break;
            case R.id.b2:
                speak(context.getString(R.string._2));
                break;
            case R.id.b3:
                speak(context.getString(R.string._3));
                break;
            case R.id.b4:
                speak(context.getString(R.string._4));
                break;
            case R.id.b5:
                speak(context.getString(R.string._5));
                break;
            case R.id.b6:
                speak(context.getString(R.string._6));
                break;
            case R.id.b7:
                speak(context.getString(R.string._7));
                break;
            case R.id.b8:
                speak(context.getString(R.string._8));
                break;
            case R.id.b9:
                speak(context.getString(R.string._9));
                break;
            case R.id.bDian:
                speak("点");
                break;
            case R.id.bJia:
                speak("加");
                break;
            case R.id.bJian:
                speak("减");
                break;
            case R.id.bCheng:
                speak("乘以");
                break;
            case R.id.bChu:
                speak("除以");
                break;
            case R.id.bDengyu:
                speak("等于");
                break;
            case R.id.bKuoHaoTou:
                speak("括号");
                break;
            case R.id.bKuoHaoWei:
                speak("括号");
                break;
            case R.id.textViewNum:
                speak("负");
                break;
            case R.id.bShanChu:
                stop();
                exceptional.TTSExceptionalHandle(view);
                break;
            case R.id.bGuiLing:
                stop();
                exceptional.TTSExceptionalHandle(view);
                break;
        }
    }

    public void shutdown() {
        tts.stop();
        tts.shutdown();
        context = null;
    }

    public void stop() {
        tts.stop();
    }

    public static String[][] getEngines(Context context) {
        TextToSpeech tts = new TextToSpeech(context, null);
        List<TextToSpeech.EngineInfo> list = tts.getEngines();
        final int listLen = list.size();
        String[] label = new String[listLen];
        String[] name = new String[listLen];

        for (int i=0;i<list.size();i++) {
            TextToSpeech.EngineInfo info = list.get(i);
            label[i] = info.label + " (" + info.name + ")";
            name[i] = info.name;
        }

        return new String[][] {label, name};
    }
}
