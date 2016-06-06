package com.aries.storyreader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.aries.storyreader.adapter.nodeAdapter;
import com.aries.storyreader.bean.ChapterItem;
import com.aries.storyreader.bean.Node;
import com.czt.mp3recorder.MP3Recorder;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * An activity representing a single chapter detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link chapterListActivity}.
 */
public class chapterDetailActivity extends AppCompatActivity implements com.aries.storyreader.adapter.nodeAdapter.OnItemClickListener {
    private Toolbar toolbar;
    private RecyclerView nodeListView;
    private OperationButton operation;

    private ChapterItem chapterItem;
    private ArrayList<Node> nodeItems;
    private nodeAdapter nodeAdapter;
    private MP3Recorder recorder;
    private MediaPlayer player;
    private File file;
    private LinearLayoutManager manager;

    private int mFirst = 0;
    private int mLast = 0;
    private int position=0;
    private int height = 0;

    private Node playItem;

//    private int permissionRequestCode = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);

        if (null == chapterItem && null != getIntent() && null != getIntent().getExtras()){
            chapterItem = (ChapterItem) getIntent().getExtras().getSerializable("ChapterItem");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == toolbar){
            initView();
        }
        if (null == nodeItems){
            getData();
        } else {
            showdata();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != player){
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        nodeListView = (RecyclerView) findViewById(R.id.nodelist);
        operation = (OperationButton) findViewById(R.id.operation);

        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        nodeListView.setLayoutManager(manager);


        setSupportActionBar(toolbar);
        toolbar.setTitle(chapterItem.getDescribe());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        operation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = operation.getHeight();
                operation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        nodeListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int first = manager.findFirstVisibleItemPosition();
                int end = manager.findLastVisibleItemPosition();
                if (first != mFirst || end != mLast) {
                    mFirst = first;
                    mLast = end;
                    nodeAdapter.setChoiseNodeIndex(((mFirst + mLast) / 2));
                    position = ((mFirst + mLast) / 2);
                    //moveToPosition(((mFirst + mLast) / 2));
                }
                scrollToPosition(dy);


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, chapterListActivity.class));
                save();
                return true;
            case R.id.action_play:
                if (null != nodeItems){
                    playItem = nodeItems.get(0);
                    playItem(true);
                }
                break;
            case R.id.action_save:
                if (null != nodeAdapter) {
                    save();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        nodeItems = Dategen.getNodeItems(this);
        read();
        showdata();
    }

    private void showdata(){
        if (null == nodeAdapter){
            nodeAdapter = new nodeAdapter(this,this);
        }
        nodeAdapter.setData(nodeItems);
        nodeListView.setAdapter(nodeAdapter);
        int first = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        nodeAdapter.setChoiseNodeIndex(((first + end) / 2));
    }

    /*@Override
    public void onItemPlayClicked(NodeItem item) {
        if (null != item && null != item.getDub()) {
            playItem = item;
            playItem(false);
        }
    }

    @Override
    public void onItemRecordClicked(NodeItem item) {
        if (null == recorder) {
            recorder = new MP3Recorder(getFile());
            try {
                nodeAdapter.setChoiseNodeIndex(item.getIndex());
                recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            recorder.stop();
            nodeAdapter.setChoiseNodeIndex(-1);
            for (NodeItem nodeItem:nodeItems){
                if (nodeItem.getIndex() == item.getIndex()){
                    nodeItem.setDub(file.getAbsolutePath());
                    nodeAdapter.setData(nodeItems);
                    break;
                }
            }
            recorder = null;
        }
    }*/

    @Override
    public void onItemClicked(int position,Node item) {
        Log.e("onItemClicked","posotion="+position +"\tmFirst="+mFirst+"\tmLast="+mLast);
        if (0 == mFirst || nodeItems.size() - 1 == mLast){
            nodeAdapter.setChoiseNodeIndex(position);
            Log.e("onItemClicked","边界设置");
        }
    }

    private File getFile(){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmSS");

        String fileName = sdf.format(new Date(System.currentTimeMillis()));
        file = new File(Environment.getExternalStorageDirectory()+"/StoryReader/","rec_"+fileName + ".mp3");
        if (!file.getParentFile().exists() ||  !file.getParentFile().isDirectory()){
            file.getParentFile().mkdirs();
        }
        return file;
    }

    private void playItem(final boolean playToEnd){
        if (null != playItem && null != playItem.getDub()){
            if (null == player){
                player = new MediaPlayer();
                /*if (playToEnd){
                    player.setLooping(false);
                }*/
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        player.setVolume(0f,0f);
                        stopPlay();
                        if (playToEnd){
                            boolean hasNext = false;
                            for (Node nodeItem:nodeItems){
                                if (playItem.getId() < nodeItem.getId() && null != nodeItem.getDub()){
                                    playItem = nodeItem;
                                    hasNext = true;
                                    break;
                                }
                            }
                            if (!hasNext){
                                nodeAdapter.setChoiseNodeIndex(-1);
                                return;
                            } else {
                                handler.sendMessageDelayed(handler.obtainMessage(1),800);
                            }
                        } else {
                            nodeAdapter.setChoiseNodeIndex(-1);
                        }
                    }
                });
            } else {
                player.reset();
            }
            try {
                player.setDataSource(playItem.getDub().getFile());
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            nodeAdapter.setChoiseNodeId(playItem.getId());
            player.start();
            player.setVolume(1f,1f);
        }
    }

    private void stopPlay(){
        if (null != player && player.isPlaying()){
            player.stop();
        }
    }

    private void read(){
        SharedPreferences preferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
        if(preferences.getInt("AUDIO_COUNT",0) > 0) {
            for (Node item : nodeItems) {

                //item.setDub(preferences.getString("AUDIOINDEX_" + item.getIndex(), null));
            }
        }
    }

    private void save(){
        SharedPreferences preferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int count = 0;
        for (Node item:nodeItems) {
            if (null != item.getDub()){
                //editor.putString("AUDIOINDEX_"+item.getIndex(),item.getDub());
                count++;
            }
        }
        editor.putInt("AUDIO_COUNT",count);
        editor.commit();
    }

    private void moveToPosition(int position){
        View view = manager.findViewByPosition(position);
        int top = view.getTop();
        //operation.setLayoutParams(getOperationLayoutParams(top,false));
       // operation.setTop(top);
        //operation.postInvalidate();
    }

    private void scrollToPosition(int dy){
        Log.e("scrollToPosition","dy="+dy + "\toperationTop="+operation.getTop()+"\theight="+height);
        //operation.startAnimation(getScrollAnimation(dy));
//        operation.scrollBy(operation.getLeft(),operation.getTop() - dy);
       // operation.setLayoutParams(getOperationLayoutParams(dy,true));
        View view = manager.findViewByPosition(position);
        int top = view.getTop();
        operation.setTop(top);
        operation.setBottom(operation.getTop() + height);
        //operation.postInvalidate();
    }

    private CoordinatorLayout.LayoutParams getOperationLayoutParams(int dy,boolean isScroll){
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) operation.getLayoutParams();
        if (isScroll) {
            Log.e("scroll", "beforTop=" + operation.getTop() + "\ttopMargin=" + params.topMargin + "\tdy=" + dy);
            params.topMargin = params.topMargin == 0 ? operation.getTop() + dy : params.topMargin - dy;
        } else {
            Log.e("change", "beforTop=" + operation.getTop() + "\ttopMargin=" + params.topMargin + "\tdy=" + dy);
            params.topMargin = dy;
            operation.setTop(0);
            operation.setY(0);
        }
        return params;
    }


    private Animation getScrollOperationButtonAnimation(int top){
        Animation animation = new TranslateAnimation(0,0,-operation.getTop(),-top);
        animation.setFillAfter(true);
        animation.setDuration(20);
        return animation;
    }

    private Animation getScrollAnimation(int top){
        Animation animation = new TranslateAnimation(0,0,-operation.getTop(),top);
        animation.setFillAfter(true);
        animation.setDuration(20);
        return animation;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    playItem(true);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
