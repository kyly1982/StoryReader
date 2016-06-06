package com.aries.storyreader;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Chronometer;
import android.widget.RelativeLayout;

import com.aries.storyreader.bean.Dub;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/6/3.
 */
public class OperationButton extends RelativeLayout implements View.OnClickListener,Chronometer.OnChronometerTickListener {
    private Context context;
    private AppCompatImageView statusView;
    private AppCompatImageView coverView;
    private Chronometer timeView;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private long lastPauseTime = 0;


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
        showData();
    }


    public AppCompatImageView getUserCoverView() {
        return coverView;
    }

    private void initView() {
        removeAllViews();
        setBackgroundResource(R.drawable.bg_skyblue);
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
        coverView.setPadding(4,4,4,4);
        coverView.setBackgroundResource(R.drawable.bg_skyblue_without_storke);
        coverView.setImageResource(R.mipmap.ic_account_circle_black);
        addView(coverView);
    }

    private void addStatesImageView() {
        statusView = new AppCompatImageView(context);
        RelativeLayout.LayoutParams params = new LayoutParams(80, 80);
        params.addRule(CENTER_IN_PARENT);
        statusView.setLayoutParams(params);
        statusView.setBackgroundResource(R.drawable.bg_skyblue);
        statusView.setBackgroundResource(R.mipmap.ic_mic_none_white);
        addView(statusView);
    }

    private void addTimerView() {
        timeView = new Chronometer(context);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(BELOW,statusView.getId());
        params.addRule(CENTER_HORIZONTAL);
        timeView.setLayoutParams(params);
        timeView.setTextColor(context.getResources().getColor(R.color.colorBtnOperationLength));
        timeView.setFormat("MM:SS");
        timeView.setText("00:00");
        timeView.setOnChronometerTickListener(this);
        addView(statusView);
    }

    private void showData(){
        if (null == loader){
            loader = ImageLoader.getInstance();
            options = MyApplication.instence.getCircleOptions();
        }

        if ( null != dub.getOwner()  && null != dub.getOwner().getPortrait()){
            if (null == coverView.getTag() || (null != coverView.getTag() && !coverView.getTag().equals(dub.getOwner().getPortrait()))) {
                loader.displayImage(dub.getOwner().getPortrait(), coverView, options);
                coverView.setTag(dub.getOwner().getPortrait());
            }
        } else {
            coverView.setImageResource(R.mipmap.ic_account_circle_black);
        }
    }

    private void pause(){

    }

    private void resumer(){

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
                    timeView.setBase(SystemClock.elapsedRealtime());
                    timeView.start();
                    listener.startRecording();
                    break;
                case RECODERING:
                    states = OperationStates.PLAYREADY;
                    statusView.setBackgroundResource(R.mipmap.ic_play);
                    timeView.stop();
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

    @Override
    public void onChronometerTick(Chronometer chronometer) {

    }
}
