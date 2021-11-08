package com.buzzware.nowapp.Screens.BuisnessScreens;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzware.nowapp.Addapters.Latest24HourAdapter;
import com.buzzware.nowapp.Addapters.LatestPostAddapters;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.Libraries.libactivities.VideoPreviewActivity;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.General.BaseActivity;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityBuisnessProfileBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BuisnessProfile extends BaseActivity {

    ActivityBuisnessProfileBinding mBinding;
    FirebaseFirestore db;

    BusinessModel model;
    String businessId;

    List<PostsModel> myPosts;
    List<PostsModel> pinnedPosts;

    int levels = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_buisness_profile);

        getExtrasFromIntent();
        if (businessId == null) {

            model = UserSessions.GetUserSession().getBusinessModel(this);

            setUpViews();
            initFirebase();
            setListener();
            getMyPosts();
        } else {
            getBusinessData();
        }
    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() != null) {

            Bundle b = getIntent().getExtras();

            if (b.getString("businessId") != null) {
                businessId = b.getString("businessId");
            }
        }
    }

    private void getBusinessData() {

        FirebaseRequests.GetFirebaseRequests(this).getBusinessData((FirebaseRequests.SnapshotCallback) (snapshot, error) -> {

            if (snapshot != null && snapshot.getData() != null)
                parseBusinessSnapshot(snapshot);

        }, businessId);
    }

    private void parseBusinessSnapshot(DocumentSnapshot snapshot) {

        model = snapshot.toObject(BusinessModel.class);

        if (model != null) {

            setUpViews();
            initFirebase();
            setListener();
            getMyPosts();

            getMyPosts();
        }
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setListener() {
        mBinding.backIV.setOnClickListener(v -> finish());
    }

    private void setUpViews() {


        mBinding.bNameTV.setText(model.getBusinessName());

        LinearLayoutManager lm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        mBinding.rvlatestPostsProfile.setLayoutManager(lm);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mBinding.rvlatest24HourProfile.setLayoutManager(gridLayoutManager);

    }

    private void getMyPosts() {

        if (isOnline()) {

            db.collection("UserPosts")
                    .get()
                    .addOnCompleteListener(task -> {

                        parseMyPostsSnapshot(task);
                    });
        }
    }

    private void parseMyPostsSnapshot(Task<QuerySnapshot> task) {

        myPosts = new ArrayList<>();
        pinnedPosts = new ArrayList<>();

        if (task.isSuccessful()) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                Log.d("TAG14123", document.getId() + " => " + document.getData());

                PostsModel postsModel = document.toObject(PostsModel.class);

                postsModel.postId = document.getId();

                validatePost(postsModel);
            }

            setAdapters();

        } else {

            UIUpdate.GetUIUpdate(this).AlertDialog("Alert", task.getException().getLocalizedMessage());
        }
    }


    private void validatePost(PostsModel postsModel) {

        if (businessId != null) {

            if (postsModel.taggedToCurrentBusiness(this, businessId)) {

                if (postsModel.createdToday()) {

                    myPosts.add(postsModel);

                }

                if (postsModel.getPinned()) {
                    pinnedPosts.add(postsModel);
                }

//                taggedUsers.add(postsModel);

            } else if (postsModel.getUserID().equalsIgnoreCase(businessId)) {


                if (postsModel.createdToday()) {

                    myPosts.add(postsModel);

                }

                if (postsModel.getPinned()) {
                    pinnedPosts.add(postsModel);
                }
            }

        } else if (postsModel.belongsToCurrentUser(this, null)) {

            if (postsModel.createdToday()) {

                myPosts.add(postsModel);

            }

            if (postsModel.getPinned()) {

                pinnedPosts.add(postsModel);

            }

        } else if (postsModel.taggedToCurrentBusiness(this, UserSessions.GetUserSession().getFirebaseUserID(this))) {

            if (postsModel.createdToday()) {

                myPosts.add(postsModel);

            }

            if (postsModel.getPinned()) {
                pinnedPosts.add(postsModel);
            }

//            taggedUsers.add(postsModel);

        }

    }

    private void setAdapters() {

        LatestPostAddapters achivementListAddapters = new LatestPostAddapters(this, myPosts, latestItemListener);

        mBinding.rvlatestPostsProfile.setAdapter(achivementListAddapters);

        Latest24HourAdapter latest24HourAdapter = new Latest24HourAdapter(this, pinnedPosts, hour24PostItemListener);

        mBinding.rvlatest24HourProfile.setAdapter(latest24HourAdapter);
    }

    LatestPostAddapters.ItemClickListener latestItemListener = achivementModel -> ShowDialog(achivementModel);
    Latest24HourAdapter.ItemClickListener hour24PostItemListener = postsModel -> ShowDialog(postsModel);


    private void ShowDialog(PostsModel post) {

        Dialog myDialog = new Dialog(this);

        myDialog.setContentView(R.layout.post_detail_dialog_lay);

        setDialogUI(myDialog, post);

        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myDialog.show();
    }

    private void setDialogUI(Dialog myDialog, PostsModel post) {

        RoundedImageView thumbnailIV = myDialog.findViewById(R.id.thumbnailIV);

        if (!post.getUserPostThumbnail().isEmpty())
            Picasso.with(this).load(post.getUserPostThumbnail()).placeholder(R.drawable.dummy_post_image)
                    .into(thumbnailIV);
        else
            Picasso.with(this).load(R.drawable.dummy_post_image)
                    .into(thumbnailIV);


        TextView descriptionTV = myDialog.findViewById(R.id.descriptionTV);

        descriptionTV.setText(post.getUserPostComment());

        thumbnailIV.setOnClickListener(v -> openVideoPlayer(post));

        descriptionTV.setOnClickListener(v -> openVideoPlayer(post));

        ImageView pinIV = myDialog.findViewById(R.id.pinIV);
        ImageView unPinIV = myDialog.findViewById(R.id.unPinIV);

        if (!post.getPinned() == true) {
            pinIV.setVisibility(View.GONE);
            unPinIV.setVisibility(View.VISIBLE);
        } else {
            pinIV.setVisibility(View.VISIBLE);
            unPinIV.setVisibility(View.GONE);
        }

        if (businessId != null) {
            pinIV.setVisibility(View.GONE);
            unPinIV.setVisibility(View.GONE);
        }

        unPinIV.setOnClickListener(v -> pinOrUnpinPost(post,pinIV,unPinIV));
        pinIV.setOnClickListener(v -> pinOrUnpinPost(post,pinIV,unPinIV));
    }

    private void pinOrUnpinPost(PostsModel post, ImageView pinIV, ImageView unPinIV) {

        post.setPinned(!post.getPinned());

        FirebaseRequests.GetFirebaseRequests(this).updatePost(post);

        if (!post.getPinned() == true) {
            pinIV.setVisibility(View.GONE);
            unPinIV.setVisibility(View.VISIBLE);
        } else {
            pinIV.setVisibility(View.VISIBLE);
            unPinIV.setVisibility(View.GONE);
        }

        getMyPosts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }


    private void openVideoPlayer(PostsModel post) {

        VideoPreviewActivity.getStartIntent(this, post.getUserVideoUrl());
    }

}