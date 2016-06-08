package com.aries.storyreader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aries.storyreader.adapter.nodeAdapter;
import com.aries.storyreader.bean.ChapterItem;
import com.aries.storyreader.bean.Dub;
import com.aries.storyreader.bean.Node;
import com.aries.storyreader.bean.User;
import com.czt.mp3recorder.MP3Recorder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
public class chapterDetailActivity extends AppCompatActivity implements com.aries.storyreader.adapter.nodeAdapter.OnItemClickListener,OperationButton.OnStatusChangedListener {
    private Toolbar toolbar;
    private RecyclerView nodeListView;
    private OperationButton operation;
    private ImageView writerPortrait;
    private RelativeLayout header;
    private ImageView cover;
    private LinearLayout dubOwnerRoot;

    private ChapterItem chapterItem;
    private ArrayList<Node> nodeItems;
    private nodeAdapter nodeAdapter;
    private MP3Recorder recorder;
    private MediaPlayer player;
    private File file;
    private LinearLayoutManager manager;
    private ImageLoader loader;
    private DisplayImageOptions circleOption;

    private ArrayList<User> dubOwner;

    private int position=0;
    private Node currentItem;
    private boolean isInited = false;
    private boolean isChanged = false;


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
        //getMenuInflater().inflate(R.menu.menu_detail,menu);
        return super.onCreateOptionsMenu(menu);
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
                    currentItem = nodeItems.get(0);
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

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        nodeListView = (RecyclerView) findViewById(R.id.nodelist);
        operation = (OperationButton) findViewById(R.id.operation);
        header = (RelativeLayout) findViewById(R.id.header);
        cover = (ImageView) findViewById(R.id.cover);
        writerPortrait= (ImageView) findViewById(R.id.writerPortrait);
        dubOwnerRoot = (LinearLayout) findViewById(R.id.dubOwner);

        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        nodeListView.setLayoutManager(manager);


        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        toolbar.setTitle("");

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        operation.setOnStatusChangedListener(this);


        nodeListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isInited){
                    int first = manager.findFirstVisibleItemPosition();
                    int last = manager.findLastVisibleItemPosition();



                    int index = (manager.findFirstVisibleItemPosition() + manager.findLastVisibleItemPosition()) / 2;
                    if (position != index) {
                        position = index;
                        isChanged = true;
                        Log.e("scroll","current position="+position);
                        nodeAdapter.setChoiseNodePosition(position);
                        currentItem = nodeItems.get(position);
                        operation.setDub(currentItem.getDub());
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (0 == newState){
                    if (isChanged) {
                        playItem(false);
                        isChanged = false;
                    }
                }
            }
        });
    }

    private void getData(){
        nodeItems = Dategen.getNodeItems(this);
        dubOwner = Dategen.getDubOwner(this);
        read();
        showdata();
    }

    private void showdata(){
        if (null == nodeAdapter){
            nodeAdapter = new nodeAdapter(this,this);
            loader = ImageLoader.getInstance();
            circleOption = MyApplication.instence.getCircleOptions();
        }
        nodeAdapter.setData(nodeItems);
        nodeListView.setAdapter(nodeAdapter);
        loader.displayImage("http://p2.qhimg.com/t01870443f968f1e4bf.png",cover);
        loader.displayImage(getString(R.string.writerIcon),writerPortrait,circleOption);
        isInited = true;
    }

    @Override
    public void onItemClicked(int position,Node item) {
        this.position = position;
        currentItem = item;
        operation.setDub(currentItem.getDub());
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
        if (null != currentItem && null != currentItem.getDub()){
            if (null == player){
                player = new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        player.setVolume(0f,0f);
                        stopPlay();
                        operation.setPlayComplet();
                    }
                });
            } else {
                player.reset();
            }
            try {
                player.setDataSource(currentItem.getDub().getFile());
                player.prepare();
            } catch (IOException e) {
                stopPlay();
                operation.setPlayComplet();
                e.printStackTrace();
            }
            player.start();
            player.setVolume(1f,1f);
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

    private void addDubUser(User user){
        String tag = (String) dubOwnerRoot.getTag();
        //检查用户是否已存在
        if (null != tag){
            String[] ids = tag.split(",");
            for (int i = 0;i < ids.length;i++){
                if (ids[i].equals(user.getId()+"")){
                    return;
                }
            }
        }
        //添加用户
        ImageView imageView = new ImageView(this);
        int width = getResources().getDimensionPixelSize(R.dimen.dubOwnerCoverWidth);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
        params.setMargins(0,5,50,5);
        imageView.setImageResource(R.mipmap.ic_account_circle_black);
        dubOwnerRoot.addView(imageView,params);
        loader.displayImage(user.getPortrait(),imageView,circleOption);
        dubOwnerRoot.postInvalidate();

        if (null == tag){
            tag = user.getId()+"";
        } else {
            tag +=","+user.getId();
        }
        dubOwnerRoot.setTag(tag);
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

    /**
     * Operation按钮的相关回调
     */
    @Override
    public void startPlay() {
        switch (operation.getStatus()){
            case PAUSE:
                if (null != player ){
                    player.start();
                }
                break;
            case PLAYREADY:
                playItem(false);
                break;
        }
    }

    @Override
    public void pausePlay() {
        if (null != player && player.isPlaying()){
            player.pause();
        }
    }

    @Override
    public void stopPlay(){
        if (null != player && player.isPlaying()){
            player.stop();
        }
    }

    @Override
    public void startRecording() {
        if (null == recorder) {
            recorder = new MP3Recorder(getFile());
            try {
                recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    @Override
    public void stopRecording() {
        recorder.stop();
        Dub dub = new Dub(currentItem.getId(),operation.getTime(),file.getAbsolutePath());
        User duber = dubOwner.get((int)(Math.random() * 5));
        dub.setOwner(duber);
        addDubUser(duber);
        nodeItems.get(position).setDub(dub);
        operation.setDub(dub);
        nodeAdapter.setData(nodeItems);

        recorder = null;
    }


}
