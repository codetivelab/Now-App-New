package com.buzzware.nowapp.Libraries.libactivities;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Libraries.adapter.VideoGridAdapter;
import com.buzzware.nowapp.Libraries.base.BaseActivity;
import com.buzzware.nowapp.Libraries.model.LocalVideoModel;
import com.buzzware.nowapp.Libraries.utils.VideoUtil;
import com.buzzware.nowapp.Libraries.view.DividerGridItemDecoration;
import com.buzzware.nowapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor
 * @Date 2018/8/21 15:16
 * @description 视频相册界面
 */
public class VideoAlbumActivity extends BaseActivity implements VideoGridAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<LocalVideoModel> mLocalVideoModels = new ArrayList<>();
    private VideoGridAdapter mAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoAlbumActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_album;
    }

//    @Override
//    protected void initToolbar(ToolbarHelper toolbarHelper) {
//        toolbarHelper.setTitle("相册");
//    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mAdapter = new VideoGridAdapter(this, mLocalVideoModels);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        VideoUtil.getLocalVideoFiles(this)
            .subscribe(new Observer<ArrayList<LocalVideoModel>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    subscribe(d);
                }

                @Override
                public void onNext(ArrayList<LocalVideoModel> localVideoModels) {
                    mLocalVideoModels = localVideoModels;
                    mAdapter.setData(mLocalVideoModels);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            });
    }

    @Override
    public void onItemClick(int position, LocalVideoModel model) {
        Intent intent = new Intent(this, TrimVideoActivity.class);
        intent.putExtra("videoPath", model.getVideoPath());
        startActivityForResult(intent, 100);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalVideoModels = null;
    }
}
