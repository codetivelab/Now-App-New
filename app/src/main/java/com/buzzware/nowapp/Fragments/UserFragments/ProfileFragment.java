package com.buzzware.nowapp.Fragments.UserFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Addapters.Latest24HourAdapter;
import com.buzzware.nowapp.Addapters.LatestPostAddapters;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments.UserFollowActivity;
import com.buzzware.nowapp.Libraries.libactivities.VideoPreviewActivity;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.General.Video.VideoCommentsLikesActivity;
import com.buzzware.nowapp.Screens.UserScreens.UserProfileScreen;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment {

    FragmentProfileBinding mBinding;

    FirebaseFirestore db;

    List<PostsModel> myPosts;
    List<PostsModel> pinnedPosts;

    BusinessModel businessModel;

    FirebaseAuth mAuth;

    int levels = 0;

    public ProfileFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        initFirebase();
        setUpViews();
        setListeners();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        getUserData();
        getMyPosts();
    }

    private void getUserData() {

        if (isOnline()) {
            db.collection("Users").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            NormalUserModel normalUserModel = task.getResult().toObject(NormalUserModel.class);

                            if (normalUserModel == null)
                                return;

                            mBinding.nameTV.setText(normalUserModel.getUserFirstName() + " " + normalUserModel.getUserLastName());
                            mBinding.followersCountTV.setText(normalUserModel.getUserFollowers());
                            mBinding.followingCountTV.setText(normalUserModel.getUserFollowings());
                            if (normalUserModel.getUserImageUrl() != null && !normalUserModel.getUserImageUrl().isEmpty())
                                Picasso.with(getContext()).load(normalUserModel.getUserImageUrl()).placeholder(R.drawable.dummy_post_image).into(mBinding.userImage);


                        }
                    });
        }
    }


    private void setListeners() {

        mBinding.editProfileLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), UserProfileScreen.class));
            }
        });
        mBinding.backRL.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }

    private void initFirebase() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    private void setUpViews() {

        LinearLayoutManager lm = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);

        mBinding.rvLatestPosts.setLayoutManager(lm);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        mBinding.rvLatest24Hour.setLayoutManager(gridLayoutManager);

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
        levels = 0;
        if (task.isSuccessful()) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                Log.d("TAG14123", document.getId() + " => " + document.getData());

                PostsModel postsModel = document.toObject(PostsModel.class);

                postsModel.postId = document.getId();

                validatePost(postsModel);
            }

            setAdapters();

        } else {

            UIUpdate.GetUIUpdate(getActivity()).AlertDialog("Alert", task.getException().getLocalizedMessage());
        }
    }

    private void validatePost(PostsModel postsModel) {

        if (postsModel.belongsToCurrentUser(getContext(), null)) {

            levels ++;

            if (postsModel.createdToday()) {
                myPosts.add(postsModel);
            }

            if (postsModel.getPinned()) {
                pinnedPosts.add(postsModel);
            }
        }
    }

    private void setAdapters() {

        LatestPostAddapters achivementListAddapters = new LatestPostAddapters(getContext(), myPosts, latestItemListener);

        mBinding.rvLatestPosts.setAdapter(achivementListAddapters);

        Latest24HourAdapter latest24HourAdapter = new Latest24HourAdapter(getContext(), pinnedPosts, hour24PostItemListener);

        mBinding.rvLatest24Hour.setAdapter(latest24HourAdapter);

        mBinding.pointsTV.setText(levels * 10 + " Points");


        if (levels < 10) {
            mBinding.levelTV.setText("BRONZE");
            mBinding.progressBar1.setProgress(levels*10);
        } else if (levels < 20) {
            mBinding.levelTV.setText("SILVER");

            int progress = (levels - 10) * 10;
            mBinding.progressBar1.setProgress(progress);
        } else if (levels < 30) {
            mBinding.levelTV.setText("GOLD");

            int progress = (levels - 20) * 10;
            mBinding.progressBar1.setProgress(progress);
        } else if (levels < 40) {
            mBinding.levelTV.setText("PLATINUM");
            int progress = (levels - 30) * 10;
            mBinding.progressBar1.setProgress(progress);
        } else {
            mBinding.progressBar1.setProgress(100);
        }
    }

    LatestPostAddapters.ItemClickListener latestItemListener = achivementModel -> ShowDialog(achivementModel);
    Latest24HourAdapter.ItemClickListener hour24PostItemListener = postsModel -> ShowDialog(postsModel);

    private void ShowDialog(PostsModel post) {

        VideoCommentsLikesActivity.startCommentsLikesActivity(post, getActivity());

        return;

//        Dialog myDialog = new Dialog(getContext());
//
//        myDialog.setContentView(R.layout.post_detail_dialog_lay);
//
//        setDialogUI(myDialog, post);
//
//        myDialog.setCancelable(true);
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        myDialog.show();
    }

    private void setDialogUI(Dialog myDialog, PostsModel post) {

        RoundedImageView thumbnailIV = myDialog.findViewById(R.id.thumbnailIV);

        Picasso.with(getContext()).load(post.getUserPostThumbnail()).placeholder(R.drawable.dummy_post_image)
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
        unPinIV.setOnClickListener(v -> pinOrUnpinPost(post,pinIV,unPinIV));
        pinIV.setOnClickListener(v -> pinOrUnpinPost(post, pinIV, unPinIV));
    }

    private void pinOrUnpinPost(PostsModel post, ImageView pinIV, ImageView unPinIV) {

        post.setPinned(!post.getPinned());

        FirebaseRequests.GetFirebaseRequests(getActivity()).updatePost(post);

        if (!post.getPinned() == true) {
            pinIV.setVisibility(View.GONE);
            unPinIV.setVisibility(View.VISIBLE);
        } else {
            pinIV.setVisibility(View.VISIBLE);
            unPinIV.setVisibility(View.GONE);
        }

        getMyPosts();
    }

    private void openVideoPlayer(PostsModel post) {

        VideoPreviewActivity.getStartIntent(getActivity(), post.getUserVideoUrl());
    }
}