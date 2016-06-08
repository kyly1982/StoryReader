package com.aries.storyreader;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aries.storyreader.bean.Dub;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by kyly on 2016/6/3.
 */
public class OperationButton extends FrameLayout implements View.OnClickListener {
    private ImageView statusView;
    private ImageView coverView;
    private TextView timeView;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private int elapsedTime = 0;


    private Dub dub;
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
        this(context,null,0);
    }

    public OperationButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OperationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        this.listener = listener;
    }

    public int getTime(){
        return elapsedTime;
    }

    public void setPlayComplet(){
        states = OperationStates.PLAYREADY;
        showData(3);
    }


    public void setDub(@NonNull Dub dub){
        this.dub = dub;
        if (null != dub && null != dub.getFile()){
            elapsedTime = dub.getTime();
            states = OperationStates.PLAYREADY;
        } else {
            states = OperationStates.RECODERREADY;
            elapsedTime = 0;
        }
        showData(0);
    }

    public OperationStates getStatus(){
        return states;
    }


    private void initView() {
        View view = inflate(getContext(),R.layout.btn_operation,null);
        statusView = (ImageView) view.findViewById(R.id.statusView);
        coverView = (ImageView) view.findViewById(R.id.userCover);
        timeView = (TextView) view.findViewById(R.id.timeView);
        addView(view);
        setClickable(true);
        setOnClickListener(this);
    }

    /**
     * 根据传入的参数刷新相应的部分
     * @param what 要刷新的部分，0：刷新全部；1：只刷新头像；2：只刷新时间；3：只刷新状态
     */
    private void showData(int what){
        if (0 == what || 1 == what) {
            if (null == loader) {
                loader = ImageLoader.getInstance();
                options = MyApplication.instence.getCircleOptions();
            }

            if (null != dub && null != dub.getOwner() && null != dub.getOwner().getPortrait()) {
                if (null == coverView.getTag() || !(coverView.getTag().equals(dub.getOwner().getPortrait()))) {
                    loader.displayImage(dub.getOwner().getPortrait(), coverView, options);
                    coverView.setTag(dub.getOwner().getPortrait());
                }
            } else {
                coverView.setImageResource(R.mipmap.ic_account_circle_black);
            }
        }

        if (0 == what || 2 == what) {
            timeView.setText(getTimeString());
        }

        if (0 == what || 3 == what) {
            switch (states) {
                case RECODERREADY:
                    statusView.setImageResource(R.mipmap.ic_mic_none_white);
                    break;
                case RECODERING:
                    statusView.setImageResource(R.mipmap.ic_stop_white);
                    break;
                case PLAYREADY:
                    statusView.setImageResource(R.mipmap.ic_play);
                    break;
                case PLAYING:
                    statusView.setImageResource(R.mipmap.ic_pause);
                    break;
                case PAUSE:
                    statusView.setImageResource(R.mipmap.ic_play);
                    break;
            }
        }
    }

    private void pause(){

    }

    private void resumer(){

    }

    private String getTimeString(){
        if (elapsedTime > 59){
            return (int)(elapsedTime / 60) + ":" + (elapsedTime % 60);
        } else {
            if (elapsedTime > 9) {
                return "00:" + elapsedTime;
            } else {
                return "00:0"+elapsedTime;
            }
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
                    listener.startRecording();
                    handler.sendMessageDelayed(handler.obtainMessage(99241),1000);
                    states = OperationStates.RECODERING;
                    break;
                case RECODERING:
                    listener.stopRecording();
                    handler.sendMessage(handler.obtainMessage(99240));
                    states = OperationStates.PLAYREADY;
                    break;
                case PLAYREADY:
                    listener.startPlay();
                    handler.sendMessageDelayed(handler.obtainMessage(99241),1000);
                    states = OperationStates.PLAYING;
                    break;
                case PLAYING:
                    listener.pausePlay();
                    handler.sendMessage(handler.obtainMessage(99240));
                    states = OperationStates.PAUSE;
                    break;
                case PAUSE:
                    listener.startPlay();
                    handler.sendMessage(handler.obtainMessage(99240));
                    states = OperationStates.PLAYING;
                    break;
            }
            showData(3);
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
                            setPlayComplet();
                        } else {
                            elapsedTime--;
                            handler.sendMessageDelayed(handler.obtainMessage(99241),1000);
                        }
                    }
                    showData(2);
                    break;
                case 99240:
                    handler.removeMessages(99241);
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
