package com.aries.storyreader;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.aries.storyreader.bean.Dub;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/6/3.
 */
public class OperationButton extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private AppCompatImageView statusView;
    private AppCompatImageView coverView;
    private AppCompatTextView timeView;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private int elapsedTime = 0;


    private Dub dub;
    private int width, height;
    private OperationStates states = OperationStates.RECODERREADY;
    private OnStatusChangedListener listener;

    public interface OnStatusChangedListener {
        void startRecording();

        void stopRecording();

        void startPlay();

        void pausePlay();

        void stopPlay();
    }

    public OperationButton(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public OperationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public OperationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        setOnClickListener(this);
    }

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        this.listener = listener;
    }


    public void setDub(@NonNull Dub dub){
        this.dub = dub;
        if (null != dub.getFile()){
            states = OperationStates.PLAYREADY;
        } else {
            states = OperationStates.RECODERREADY;
        }
        elapsedTime = dub.getTime();
        showData();
    }


    public AppCompatImageView getUserCoverView() {
        return coverView;
    }

    private void initView() {
        removeAllViews();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = getWidth();
                height = getHeight();
                addUserCvoerView();
                addStatesImageView();
                addTimerView();
            }
        });

    }

    private void addUserCvoerView() {
        coverView = new AppCompatImageView(context);
        RelativeLayout.LayoutParams params = new LayoutParams((int)(width * 0.4), (int)(height * 0.4));
        params.addRule(ALIGN_PARENT_RIGHT);
        coverView.setLayoutParams(params);
        coverView.setPadding(2,2,2,2);
        coverView.setBackgroundResource(R.drawable.bg_skyblue_without_storke);
        coverView.setImageResource(R.mipmap.ic_account_circle_black);
        addView(coverView);
    }

    private void addStatesImageView() {
        statusView = new AppCompatImageView(context);
        statusView.setId(R.id.statusViewId);
        RelativeLayout.LayoutParams params = new LayoutParams((int)(width * 0.4), (int)(width * 0.4));
        params.addRule(CENTER_IN_PARENT);
        statusView.setLayoutParams(params);
        statusView.setBackgroundResource(R.drawable.bg_skyblue);
        statusView.setBackgroundResource(R.mipmap.ic_mic_none_white);
        addView(statusView);
    }

    private void addTimerView() {
        timeView = new AppCompatTextView(context);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);


        timeView.setLayoutParams(params);
        if (Build.VERSION.SDK_INT == 23){
            timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.operationTimeTextSize));
        } else {
            timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.operationTimeTextSize));
        }
        timeView.setPadding(0,0,0,10);
        timeView.setTextColor(getResources().getColor(R.color.colorBtnOperationLength));
        timeView.setText("00:00");
        addView(timeView);
    }

    private void showData(){
        if (null == loader){
            loader = ImageLoader.getInstance();
            options = MyApplication.instence.getCircleOptions();
        }

        if ( null != dub.getOwner()  && null != dub.getOwner().getPortrait()){
            if (null == coverView.getTag() || !(coverView.getTag().equals(dub.getOwner().getPortrait()))) {
                loader.displayImage(dub.getOwner().getPortrait(), coverView, options);
                coverView.setTag(dub.getOwner().getPortrait());
            }
        } else {
            coverView.setImageResource(R.mipmap.ic_account_circle_black);
        }

        timeView.setText(getTime());
    }

    private void pause(){

    }

    private void resumer(){

    }

    private String getTime(){
        if (elapsedTime > 59){
            return (elapsedTime / 60) + ":" + (elapsedTime % 60);
        } else {
            return "00:" + elapsedTime;
        }
    }

    public enum OperationStates {
        RECODERREADY,
        RECODERING,
        PLAYREADY,
        PLAYING,
        PAUSE,
        END
    }

    @Override
    public void onClick(View v) {
        if (null != listener) {
            switch (states) {
                case RECODERREADY:
                    states = OperationStates.RECODERING;
                    statusView.setBackgroundResource(R.mipmap.ic_stop_white);
                    //timeView.setBase(SystemClock.elapsedRealtime());
                    //timeView.start();
                    listener.startRecording();
                    break;
                case RECODERING:
                    states = OperationStates.PLAYREADY;
                    statusView.setBackgroundResource(R.mipmap.ic_play);
                    //timeView.stop();
                    listener.stopRecording();
                    break;
                case PLAYREADY:
                    states = OperationStates.PLAYING;
                    statusView.setBackgroundResource(R.mipmap.ic_pause);
                    listener.startPlay();
                    break;
                case PLAYING:
                    states = OperationStates.PAUSE;
                    statusView.setBackgroundResource(R.mipmap.ic_play);
                    listener.pausePlay();
                    break;
                case PAUSE:
                    states = OperationStates.PLAYING;
                    statusView.setBackgroundResource(R.mipmap.ic_pause);
                    listener.startPlay();
                    break;
            }
        }
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 99241:
                    if (states == OperationStates.RECODERING){
                        elapsedTime++;
                        handler.sendMessageDelayed(handler.obtainMessage(99241),1000);
                    } else if (states == OperationStates.PLAYING){
                        if (0 == elapsedTime){
                            handler.removeMessages(99241);
                        } else {
                            elapsedTime--;
                            handler.sendMessageDelayed(handler.obtainMessage(99241),1000);
                        }
                    }
                    showData();
                    break;
                case 99240:
                    handler.removeMessages(99241);
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
