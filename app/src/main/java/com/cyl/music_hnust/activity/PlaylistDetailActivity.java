package com.cyl.music_hnust.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cyl.music_hnust.R;
import com.cyl.music_hnust.adapter.LocalMusicAdapter;
import com.cyl.music_hnust.model.Music;
import com.cyl.music_hnust.utils.Constants;
import com.cyl.music_hnust.utils.MusicUtils;
import com.cyl.music_hnust.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yonglong on 2016/8/14 16:15
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
public class PlaylistDetailActivity extends BaseActivity implements LocalMusicAdapter.OnItemClickListener {

    RecyclerView mRecyclerView;
    TextView tv_empty;
    Toolbar mToolbar;

    private static LocalMusicAdapter mAdapter;
    private static List<Music> musicInfos = new ArrayList<>();
    private String playlist_id;

    /**
     * 新建一个线程更新UI
     */

    final Handler myHandler = new Handler() {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mAdapter.setMusicInfos(musicInfos);
                mAdapter.notifyDataSetChanged();
                init();
            }
        }
    };

    /**
     * 初始化列表,当无数据时显示提示
     */
    private void init() {
        if (musicInfos.size() == 0) {
            tv_empty.setText("本地暂无音乐!");
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.GONE);
        }
    }

    /**
     * 耗时操作
     */
    Runnable GMRunable = new Runnable() {
        @Override
        public void run() {
            //查询所有音乐
            MusicUtils.getMusicForPlaylist(PlaylistDetailActivity.this, playlist_id, musicInfos);
            myHandler.sendEmptyMessage(0);
        }
    };

    /**
     * 设置监听事件
     */
    @Override
    protected void listener() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_detail);
        SystemUtils.setSystemBarTransparent(this);
        initView();
        initData();
    }

    private void initData() {
        playlist_id = getIntent().getStringExtra(Constants.PLAYLIST_ID);
        if (playlist_id != null) {
            Log.e("歌单id++++++", playlist_id + "");
            MusicUtils.getMusicForPlaylist(this, playlist_id, musicInfos);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LocalMusicAdapter(this, musicInfos);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        if (musicInfos.size() == 0) {
            tv_empty.setText("请稍后，本地音乐加载中...");
            tv_empty.setVisibility(View.VISIBLE);
        }
        mHandler.post(GMRunable);
    }


    private void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("歌单列表");


        tv_empty = (TextView) findViewById(R.id.tv_empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        MainActivity.mPlayService.setMyMusicList(musicInfos);
        MainActivity.mPlayService.playMusic(position);
    }
}