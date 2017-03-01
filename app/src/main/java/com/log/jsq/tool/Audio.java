package com.log.jsq.tool;

import android.app.Activity;
import android.media.SoundPool;
import android.view.View;

import com.log.jsq.R;
import com.log.jsq.library.Nums;
import com.log.jsq.mainUI.MainActivity;

import java.util.ArrayList;

public class Audio {
    private Activity activity = null;
    private MainActivity ma = null;
    private SoundPool sp = null;
    private final static int MAX_STREAMS = 2;
    private final static int NUM_LEN = 10;
    private final static int FUHAO_LEN = 10;
    private final static int CTRL_LEN = 2;
    private final static int ZH_LEN = 5;
    private int[] numSoundID = new int[NUM_LEN];
    private int[] numAudioID = new int[NUM_LEN];
    private int[] fuHaoSoundID = new int[FUHAO_LEN];
    private int[] ctrlSoundID = new int[CTRL_LEN];
    private int[] zhSoundID = new int[ZH_LEN];
    private SoundSequenceThread soundSequenceThread = null;
    private int soundID;

    private class AudioIO {
        private int soundID;
        private long time;
        public static final long NUM_0_TIME = 490;
        public static final long NUM_1_TIME = 404;
        public static final long NUM_2_TIME = 356;
        public static final long NUM_3_TIME = 412;
        public static final long NUM_4_TIME = 412;
        public static final long NUM_5_TIME = 410;
        public static final long NUM_6_TIME = 412;
        public static final long NUM_7_TIME = 450;
        public static final long NUM_8_TIME = 382;
        public static final long NUM_9_TIME = 440;
        public static final long FU_TIME = 427;
        public static final long DIAN_TIME = 314;
        public static final long DENG_YU_TIME = 720;
        public static final long DEFAULT_TIME = 500;
        public static final long TEN_POWER_TIME = 1200;
        public static final long ZH_SHI_TIME = 455;
        public static final long ZH_BAI_TIME = 330;
        public static final long ZH_QIAN_TIME = 467;
        public static final long ZH_WAN_TIME = 431;
        public static final long ZH_YI_TIME = 394;

        public AudioIO(int soundID, long time) {
            this.soundID = soundID;
            this.time = time;
        }

        public int getSoundID() {
            return soundID;
        }

        public long getTime() {
            return time;
        }
    }

    private class SoundSequenceThread extends Thread {
        private boolean stop = false;
        private ArrayList<AudioIO> sequence;
        public static final float V1 = (float) 0.6;
        public static final float V2 = (float) 0.7;
        public static final float V3 = (float) 0.8;
        public static final float V4 = (float) 0.9;
        public static final float V5 = 1;
        public static final float V6 = (float) 1.1;
        public static final float V7 = (float) 1.2;
        public static final float V8 = (float) 1.3;
        public static final float V9 = (float) 1.4;

        public SoundSequenceThread(ArrayList<AudioIO> sequence) {
            this.sequence = sequence;
        }

        @Override
        public void run() {
            for (AudioIO audioIO: sequence) {
                if (stop) {
                    break;
                }

                soundID = sp.play(audioIO.getSoundID(), 1, 1, 0, 0, 1);

                try {
                    sleep((long) (audioIO.getTime() * V4));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sequence.clear();
        }

        public void onStop() {
            stop = true;
            sp.stop(soundID);
        }
    }

    public Audio(Activity activity){
        this.activity = activity;
        this.ma = (MainActivity)activity;
    }

    public void loading(){
        sp = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .build();

        for(int i=0;i<NUM_LEN;i++) {
            numAudioID[i] = activity.getResources().getIdentifier("r" + i, "raw", "com.log.jsq");
            numSoundID[i] = sp.load(activity, numAudioID[i], 1);
        }

        fuHaoSoundID[0] = sp.load(activity, R.raw.dian, 1);
        fuHaoSoundID[1] = sp.load(activity, R.raw.jia, 1);
        fuHaoSoundID[2] = sp.load(activity, R.raw.jian, 1);
        fuHaoSoundID[3] = sp.load(activity, R.raw.cheng_yi, 1);
        fuHaoSoundID[4] = sp.load(activity, R.raw.chu_yi, 1);
        fuHaoSoundID[5] = sp.load(activity, R.raw.deng_yu, 1);
        fuHaoSoundID[6] = sp.load(activity, R.raw.kuo_hao, 1);
        fuHaoSoundID[7] = sp.load(activity, R.raw.fu, 1);
        fuHaoSoundID[8] = sp.load(activity, R.raw.cheng_yi_10, 1);
        fuHaoSoundID[9] = sp.load(activity, R.raw.ci_fang, 1);

        ctrlSoundID[0] = sp.load(activity, R.raw.back, 1);
        ctrlSoundID[1] = sp.load(activity, R.raw.cls, 1);

        zhSoundID[0] = sp.load(activity, R.raw.zh_shi, 1);
        zhSoundID[1] = sp.load(activity, R.raw.zh_bai, 1);
        zhSoundID[2] = sp.load(activity, R.raw.zh_qian, 1);
        zhSoundID[3] = sp.load(activity, R.raw.zh_wan, 1);
        zhSoundID[4] = sp.load(activity, R.raw.zh_yi, 1);
    }

    private void defaultPlay(int id){
        stopSoundThread();
        sp.stop(soundID);
        soundID = sp.play(id, 1, 1, 0, 0, 1);
    }

    private void sequencePlay(ArrayList<AudioIO> sequence){
        stopSoundThread();
        soundSequenceThread = new SoundSequenceThread(sequence);
        soundSequenceThread.start();
    }

    private void onPlay(int[] playID){
        if (playID != null) {
            int id = playID[1];

            if (playID[0] == 0) {
                defaultPlay(numSoundID[id]);
            } else if (playID[0] == 1) {
                defaultPlay(fuHaoSoundID[id]);
            } else if (playID[0] == 2) {
                defaultPlay(ctrlSoundID[id]);
            }
        }
    }

    public void play(View view){
        if( ma.isOnYuYin() ) {
            int[] playID;

            switch (view.getId()) {
                case R.id.b0:
                    playID = new int[]{0, 0};
                    break;
                case R.id.b1:
                    playID = new int[]{0, 1};
                    break;
                case R.id.b2:
                    playID = new int[]{0, 2};
                    break;
                case R.id.b3:
                    playID = new int[]{0, 3};
                    break;
                case R.id.b4:
                    playID = new int[]{0, 4};
                    break;
                case R.id.b5:
                    playID = new int[]{0, 5};
                    break;
                case R.id.b6:
                    playID = new int[]{0, 6};
                    break;
                case R.id.b7:
                    playID = new int[]{0, 7};
                    break;
                case R.id.b8:
                    playID = new int[]{0, 8};
                    break;
                case R.id.b9:
                    playID = new int[]{0, 9};
                    break;
                case R.id.bDian:
                    playID = new int[]{1, 0};
                    break;
                case R.id.bJia:
                    playID = new int[]{1, 1};
                    break;
                case R.id.bJian:
                    playID = new int[]{1, 2};
                    break;
                case R.id.bCheng:
                    playID = new int[]{1, 3};
                    break;
                case R.id.bChu:
                    playID = new int[]{1, 4};
                    break;
                case R.id.bDengyu:
                    playID = new int[]{1, 5};
                    break;
                case R.id.bKuoHaoTou:
                    playID = new int[]{1, 6};
                    break;
                case R.id.bKuoHaoWei:
                    playID = new int[]{1, 6};
                    break;
                case R.id.textViewNum:
                    playID = new int[]{1, 7};
                    break;
                case R.id.bShanChu:
                    playID = new int[]{2, 0};
                    break;
                case R.id.bGuiLing:
                    playID = new int[]{2, 1};
                    break;
                default:
                    playID = null;
            }

            onPlay(playID);
        }
    }

    public void play(int[] sequence) {
        ArrayList<AudioIO> arrayList = new ArrayList<>();
        arrayList.add(new AudioIO(fuHaoSoundID[5], AudioIO.DENG_YU_TIME));

        for (int i=0;i < sequence.length;i++) {
            int soundID;
            long time = AudioIO.DEFAULT_TIME;

            switch (sequence[i]) {
                case 0:
                    soundID = numSoundID[0];
                    time = AudioIO.NUM_0_TIME;
                    break;
                case 1:
                    soundID = numSoundID[1];
                    time = AudioIO.NUM_1_TIME;
                    break;
                case 2:
                    soundID = numSoundID[2];
                    time = AudioIO.NUM_2_TIME;
                    break;
                case 3:
                    soundID = numSoundID[3];
                    time = AudioIO.NUM_3_TIME;
                    break;
                case 4:
                    soundID = numSoundID[4];
                    time = AudioIO.NUM_4_TIME;
                    break;
                case 5:
                    soundID = numSoundID[5];
                    time = AudioIO.NUM_5_TIME;
                    break;
                case 6:
                    soundID = numSoundID[6];
                    time = AudioIO.NUM_6_TIME;
                    break;
                case 7:
                    soundID = numSoundID[7];
                    time = AudioIO.NUM_7_TIME;
                    break;
                case 8:
                    soundID = numSoundID[8];
                    time = AudioIO.NUM_8_TIME;
                    break;
                case 9:
                    soundID = numSoundID[9];
                    time = AudioIO.NUM_9_TIME;
                    break;
                case Nums.FU:
                    soundID = fuHaoSoundID[7];
                    time = AudioIO.FU_TIME;
                    break;
                case Nums.DIAN:
                    soundID = fuHaoSoundID[0];
                    time = AudioIO.DIAN_TIME;
                    break;
                case Nums.TEN_POWER:
                    soundID = fuHaoSoundID[8];
                    time = AudioIO.TEN_POWER_TIME;
                    break;
                case Nums.TEN_POWER_END:
                    soundID = fuHaoSoundID[9];
                    break;
                case Nums.ZH_SHI:
                    soundID = zhSoundID[0];
                    time = AudioIO.ZH_SHI_TIME;
                    break;
                case Nums.ZH_BAI:
                    soundID = zhSoundID[1];
                    time = AudioIO.ZH_BAI_TIME;
                    break;
                case Nums.ZH_QIAN:
                    soundID = zhSoundID[2];
                    time = AudioIO.ZH_QIAN_TIME;
                    break;
                case Nums.ZH_WAN:
                    soundID = zhSoundID[3];
                    time = AudioIO.ZH_WAN_TIME;
                    break;
                case Nums.ZH_YI:
                    soundID = zhSoundID[4];
                    time = AudioIO.ZH_YI_TIME;
                    break;
                default:
                    continue;
            }

            AudioIO io = new AudioIO(soundID, time);
            arrayList.add(io);
        }

        sequencePlay(arrayList);
    }

    public void stopSoundThread() {
        if (soundSequenceThread != null) {
            soundSequenceThread.onStop();
            soundSequenceThread = null;
        }
    }

    public void release() {
        stopSoundThread();
        numSoundID = null;
        numAudioID = null;
        fuHaoSoundID = null;
        ctrlSoundID = null;
        ma = null;
        activity = null;
        sp.release();
    }

}
