package com.aries.storyreader;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.aries.storyreader.bean.User;
import com.aries.storyreader.dao.DaoMaster;
import com.aries.storyreader.dao.DaoSession;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/3.
 */
public class MyApplication extends Application {

    private DisplayImageOptions circleOptions;
    public static MyApplication instence;
    public ArrayList<User> users;
    public DaoSession session;



    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        instence = this;
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory() // 对于同一url只缓存一个图
                .memoryCache(new UsingFreqLimitedMemoryCache(8 * 1024 * 1024))
                .memoryCacheSize(8 * 1024 * 1024)
                .diskCache(new UnlimitedDiskCache(new File(getImageCacheDir()), null, new HashCodeFileNameGenerator()))
                .diskCacheExtraOptions(1080, 1080, null)
                .diskCacheSize(100 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(getNormalOptions())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 20, 20))
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    public String getImageCacheDir() {
        return getCacheRoot() + "/imagecache/";
    }

    private String getCacheRoot() {
        String mCacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (null != getExternalCacheDir()) {
                mCacheDir = getExternalCacheDir().getPath();
            } else {
                mCacheDir = getCacheDir().getPath();
            }
        } else {
            mCacheDir = getCacheDir().getPath();
        }
        return mCacheDir;
    }

    public DisplayImageOptions getNormalOptions() {
        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .delayBeforeLoading(150)
                .build();
        return options;
    }

    public DisplayImageOptions getCircleOptions() {
        if (null == circleOptions) {
            circleOptions = new DisplayImageOptions.Builder()
                    // 加载过程中显示的图片
                    .showStubImage(R.mipmap.ic_account_circle_black)
                    .showImageForEmptyUri(R.mipmap.ic_account_circle_black)
                    .showImageOnFail(R.mipmap.ic_account_circle_black)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .delayBeforeLoading(150)
                    .displayer(new CircleBitmapDisplayer())// 此处需要修改大小
                    .build();
        }
        return circleOptions;
    }

    private void setupDatabase(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"data",null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();
    }

}
