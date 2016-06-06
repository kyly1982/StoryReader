package com.aries.storyreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aries.storyreader.adapter.chapterAdapter;
import com.aries.storyreader.bean.ChapterItem;

import java.util.ArrayList;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link chapterDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class chapterListActivity extends AppCompatActivity implements chapterAdapter.OnItemClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView chapterListView;
    private Toolbar toolbar;

    private chapterAdapter adapter;
    private ArrayList<ChapterItem> chapterItems;

    private final int permissionRequestCode = 5;
    private int count = 0;
    private ChapterItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        if (findViewById(R.id.chapter_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == chapterListView) {
            initView();
        }
        if (null == chapterItems) {
            getData();
        } else {
            showData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case permissionRequestCode:
                if (null != grantResults && permissions.length == grantResults.length) {
                    boolean result = true;
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            result = false;
                            break;
                        }
                    }
                    if (result) {
                        showNextActivity();
                    } else {
                        if (3 > count) {
                            count++;
                            checkPermissions();
                        } else {
                            Snackbar snackbar = Snackbar.make(toolbar, "未获取到权限！", Snackbar.LENGTH_LONG);
                            snackbar.setAction("忽略权限", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showNextActivity();
                                }
                            });
                            snackbar.show();
                        }
                    }
                }
                break;
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        chapterListView = (RecyclerView) findViewById(R.id.chapter_list);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setTitle("章节");

    }



    private void getData() {
        chapterItems = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ChapterItem item = new ChapterItem();
            item.setIndex(i + 1);
            item.setDescribe("这是第" + (i + 1) + "章");
            chapterItems.add(item);
        }
        showData();
    }


    private void showData() {
        if (null == adapter) {
            adapter = new chapterAdapter(this, this);
        }
        adapter.setData(chapterItems);
        chapterListView.setAdapter(adapter);
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT > 22) {
            int status = 0;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                status = status | 1;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                status = status | 2;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                status = status | 4;
            }


            switch (status) {
                case 1:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCode);
                    }
                    return false;
                case 2:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissionRequestCode);
                    }
                    return false;
                case 3:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCode);
                    }
                    return false;
                case 4:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, permissionRequestCode);
                    }
                    return false;
                case 5:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, permissionRequestCode);
                    }
                    return false;
                case 6:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, permissionRequestCode);
                    }
                    return false;
                case 7:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, permissionRequestCode);
                    }
                    return false;
                default:
                    return true;
            }

        } else {
            return true;
        }

    }


    @Override
    public void onItemClicked(ChapterItem item) {
        this.item = item;
        if (checkPermissions()) {
            showNextActivity();
        }
    }

    private void showNextActivity() {
        Intent intent = new Intent(this, chapterDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ChapterItem", item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
