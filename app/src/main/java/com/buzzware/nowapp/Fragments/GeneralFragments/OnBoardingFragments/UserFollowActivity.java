package com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzware.nowapp.Addapters.Latest24HourAdapter;
import com.buzzware.nowapp.Addapters.LatestPostAddapters;
import com.buzzware.nowapp.Addapters.TagUserAdapters;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.Libraries.libactivities.VideoPreviewActivity;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.FollowModel;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.Models.TagUserModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBuisnessProfilePhoto;
import com.buzzware.nowapp.Screens.General.BaseActivity;
import com.buzzware.nowapp.Screens.UserScreens.UserProfileScreen;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityUserFollowBinding;
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

public class UserFollowActivity extends BaseActivity {

    ActivityUserFollowBinding binding;

    FirebaseFirestore db;

    List<PostsModel> myPosts;
    List<PostsModel> pinnedPosts;

    FirebaseAuth mAuth;

    String selectedUserID = "";
    int levels = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromIntent();


    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        selectedUserID = intent.getStringExtra("userId");

        initFirebase();

        getUserData();

        getFollowingData();

        setUpViews();
        getMyPosts();
        setListeners();
    }


    private void initFirebase() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    private void setListeners() {

        binding.backIV.setOnClickListener(v -> finish());
        binding.followTV.setOnClickListener(v -> {
            if (binding.followTV.getText().equals("Follow")) {

                addFollow();

            } else if (binding.followTV.getText().equals("Following")) {

                removeFollow();
            }
        });
    }

    private void getFollowingData() {

        if (isOnline()) {

            db.collection("UserFollowings").document(mAuth.getCurrentUser().getUid()).collection(selectedUserID)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    // binding.followingCountTV.setText(String.valueOf(task.getResult().size()));
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        // Log.d("TAG14123", document.getId() + " => " + document.getData());
                        FollowModel followModel = document.toObject(FollowModel.class);
                        if (followModel.getUserID().equals(selectedUserID)) {
                            binding.followTV.setText("Following");
                            binding.rvLatestPosts.setVisibility(View.VISIBLE);
                            binding.rvLatest24Hour.setVisibility(View.VISIBLE);

                        } else {
                            binding.followTV.setText("Follow");
                            binding.rvLatestPosts.setVisibility(View.GONE);
                            binding.rvLatest24Hour.setVisibility(View.GONE);
                        }

                        return;
                    }
                }
            });
        }

    }


    private void removeFollow() {

        db.collection("UserFollowings").document(mAuth.getCurrentUser().getUid()).collection(selectedUserID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // binding.followingCountTV.setText(String.valueOf(task.getResult().size()));
                for (QueryDocumentSnapshot document : task.getResult()) {

                    // Log.d("TAG14123", document.getId() + " => " + document.getData());
                    FollowModel followModel = document.toObject(FollowModel.class);
                    db.collection("UserFollowings").document(mAuth.getCurrentUser().getUid()).collection(selectedUserID).document(document.getId()).delete();

                }
            }
        });
        db.collection("UserFollowers").document(selectedUserID).collection(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // binding.followingCountTV.setText(String.valueOf(task.getResult().size()));
                for (QueryDocumentSnapshot document : task.getResult()) {

                    // Log.d("TAG14123", document.getId() + " => " + document.getData());
                    FollowModel followModel = document.toObject(FollowModel.class);
                    db.collection("UserFollowers").document(selectedUserID).collection(mAuth.getCurrentUser().getUid()).document(document.getId()).delete();

                }
            }
        });

        updatedUsersDataRemove();


    }

    private void addFollow() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FollowModel followModel = new FollowModel();
        followModel.setUserID(mAuth.getCurrentUser().getUid());
        followModel.setUserImage(UserSessions.UserIMAGE);
        followModel.setUserName(UserSessions.UserFirstNAME);
        db.collection("UserFollowers").document(selectedUserID).collection(mAuth.getCurrentUser().getUid()).document().set(followModel);

        followModel.setUserID(selectedUserID);
        followModel.setUserImage("");
        followModel.setUserName(binding.nameTV.getText().toString());
        db.collection("UserFollowings").document(mAuth.getCurrentUser().getUid()).collection(selectedUserID).document().set(followModel);


        updatedUsersDataAdd();
        getFollowingData();


    }

    private void updatedUsersDataAdd() {
        if (isOnline()) {
            db.collection("Users").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            NormalUserModel normalUserModel = task.getResult().toObject(NormalUserModel.class);

                            binding.nameTV.setText(normalUserModel.getUserFirstName() + " " + normalUserModel.getUserLastName());
                            int count = Integer.parseInt(normalUserModel.getUserFollowers());
                            count = count + 1;

                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).update("userFollowings", String.valueOf(count));


                        }
                    });
        }
        int count = Integer.parseInt(binding.followersCountTV.getText().toString());
        count = count + 1;

        db.collection("Users").document(selectedUserID).update("userFollowers", String.valueOf(count));

        binding.followersCountTV.setText(String.valueOf(count));


    }

    private void updatedUsersDataRemove() {


        int count = Integer.parseInt(binding.followersCountTV.getText().toString());
        if (count > 0) {
            count = count - 1;
        }
        db.collection("Users").document(selectedUserID).update("userFollowers", String.valueOf(count));

        binding.followingCountTV.setText(String.valueOf(count));

        if (isOnline()) {
            db.collection("Users").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            NormalUserModel normalUserModel = task.getResult().toObject(NormalUserModel.class);

                            binding.nameTV.setText(normalUserModel.getUserFirstName() + " " + normalUserModel.getUserLastName());
                            int count = Integer.parseInt(normalUserModel.getUserFollowings());
                            if (count > 0) {
                                count = count - 1;
                            }

                            db.collection("Users").document(mAuth.getCurrentUser().getUid()).update("userFollowings", String.valueOf(count));


                        }
                    });
        }
        finish();


    }

    private void getUserData() {

        if (isOnline()) {
            db.collection("Users").document(selectedUserID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            NormalUserModel normalUserModel = task.getResult().toObject(NormalUserModel.class);


                            if (normalUserModel == null)
                                return;
                            binding.nameTV.setText(normalUserModel.getUserFirstName() + " " + normalUserModel.getUserLastName());
                            binding.followersCountTV.setText(normalUserModel.getUserFollowers());
                            binding.followingCountTV.setText(normalUserModel.getUserFollowings());
                            if (normalUserModel.getUserImageUrl() != null && !normalUserModel.getUserImageUrl().isEmpty())
                                Picasso.with(UserFollowActivity.this).load(normalUserModel.getUserImageUrl()).placeholder(R.drawable.dummy_post_image).into(binding.userImage);


                        }
                    });

        }

    }

    private void setUpViews() {

        LinearLayoutManager lm = new LinearLayoutManager(UserFollowActivity.this, RecyclerView.HORIZONTAL, false);

        binding.rvLatestPosts.setLayoutManager(lm);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(UserFollowActivity.this, 2);

        binding.rvLatest24Hour.setLayoutManager(gridLayoutManager);

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


            binding.pointsTV.setText(levels * 10 + " Points");

            if (levels < 10) {
                binding.levelTV.setText("BRONZE");
                binding.progressBar1.setProgress(levels*10);
            } else if (levels < 20) {
                binding.levelTV.setText("SILVER");

                int progress = (levels - 10) * 10;
                binding.progressBar1.setProgress(progress);
            } else if (levels < 30) {
                binding.levelTV.setText("GOLD");

                int progress = (levels - 20) * 10;
                binding.progressBar1.setProgress(progress);
            } else if (levels < 40) {
                binding.levelTV.setText("PLATINUM");
                int progress = (levels - 30) * 10;
                binding.progressBar1.setProgress(progress);
            } else {
                binding.progressBar1.setProgress(100);
            }
            setAdapters();

        } else {

            UIUpdate.GetUIUpdate(UserFollowActivity.this).AlertDialog("Alert", task.getException().getLocalizedMessage());
        }
    }

    private void validatePost(PostsModel postsModel) {

        if (postsModel.getUserID().equals(selectedUserID)) {

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

        LatestPostAddapters achivementListAddapters = new LatestPostAddapters(UserFollowActivity.this, myPosts, latestItemListener);

        binding.rvLatestPosts.setAdapter(achivementListAddapters);

        Latest24HourAdapter latest24HourAdapter = new Latest24HourAdapter(UserFollowActivity.this, pinnedPosts, hour24PostItemListener);

        binding.rvLatest24Hour.setAdapter(latest24HourAdapter);
    }

    LatestPostAddapters.ItemClickListener latestItemListener = achivementModel -> ShowDialog(achivementModel);
    Latest24HourAdapter.ItemClickListener hour24PostItemListener = postsModel -> ShowDialog(postsModel);

    private void ShowDialog(PostsModel post) {

        Dialog myDialog = new Dialog(UserFollowActivity.this);

        myDialog.setContentView(R.layout.post_detail_dialog_lay);

        setDialogUI(myDialog, post);

        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myDialog.show();
    }

    private void setDialogUI(Dialog myDialog, PostsModel post) {

        RoundedImageView thumbnailIV = myDialog.findViewById(R.id.thumbnailIV);

        Picasso.with(UserFollowActivity.this).load(post.getUserPostThumbnail()).placeholder(R.drawable.dummy_post_image)
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
        pinIV.setOnClickListener(v -> pinOrUnpinPost(post,pinIV,unPinIV));
    }

    private void pinOrUnpinPost(PostsModel post, ImageView pinIV, ImageView unPinIV) {

        post.setPinned(!post.getPinned());

        FirebaseRequests.GetFirebaseRequests(UserFollowActivity.this).updatePost(post);

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

        VideoPreviewActivity.getStartIntent(this, post.getUserVideoUrl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}