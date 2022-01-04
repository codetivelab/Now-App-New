package com.buzzware.nowapp.Screens.General.Video;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.BottomSheets.PlacesDialogListFragment;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FilterTextEditor.PreviewVideoActivity;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirestoreHelper;
import com.buzzware.nowapp.Fragments.GeneralFragments.CommentsFragment;
import com.buzzware.nowapp.Libraries.libactivities.VideoPreviewActivity;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.CommentModel;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
//import com.buzzware.nowapp.Screens.General.AddCommentsActivity;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityLikesCommentsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VideoCommentsLikesActivity extends AppCompatActivity {

    ActivityLikesCommentsBinding binding;

    PostsModel post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityLikesCommentsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        setData();

        getUserProfile();

        setListeners();
    }

    private void setPinOrUnpin() {

        if (!post.getUserID().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

            binding.pinIV.setVisibility(View.GONE);

            binding.unPinIV.setVisibility(View.GONE);

            return;
        }

        if (!post.getPinned() == true) {

            binding.pinIV.setVisibility(View.GONE);

            binding.unPinIV.setVisibility(View.VISIBLE);

        } else {

            binding.pinIV.setVisibility(View.VISIBLE);

            binding.unPinIV.setVisibility(View.GONE);
        }

        binding.unPinIV.setOnClickListener(v -> pinOrUnpinPost(post));
        binding.pinIV.setOnClickListener(v -> pinOrUnpinPost(post));

    }

    private void pinOrUnpinPost(PostsModel post) {

        post.setPinned(!post.getPinned());

        FirebaseRequests.GetFirebaseRequests(this).updatePost(post);

        if (!post.getPinned() == true) {

            binding.pinIV.setVisibility(View.GONE);

            binding.unPinIV.setVisibility(View.VISIBLE);

        } else {

            binding.pinIV.setVisibility(View.VISIBLE);

            binding.unPinIV.setVisibility(View.GONE);

        }

        getMyPosts();
    }


    private void getMyPosts() {

        FirebaseFirestore.getInstance().collection("UserPosts")
                .document(post.postId)
                .get()

                .addOnCompleteListener(task -> {

                    parseMyPostsSnapshot(task);
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getMyPosts();
    }

    private void parseMyPostsSnapshot(Task<DocumentSnapshot> task) {

        if (task.isSuccessful()) {

            if (task.getResult() != null) {

                post = task.getResult().toObject(PostsModel.class);

                post.postId = task.getResult().getId();

                setLikesCommentsAndRating(post);

                setPinOrUnpin();

                setRating();

                checkAndGetBusinessData();
            }

        } else {

            UIUpdate.GetUIUpdate(this).AlertDialog("Alert", task.getException().getLocalizedMessage());
        }
    }

    private void checkAndGetBusinessData() {

        if (post.getTagBusinessId() == null || post.getTagBusinessId().equalsIgnoreCase( "N/A")){

            return;
        }

        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("BusinessData").document(post.getTagBusinessId());

                ref.addSnapshotListener((documentSnapshot, error) -> {

                    if (documentSnapshot != null && documentSnapshot.getData() != null)

                        parseBusinessSnapshot(documentSnapshot);

                } );
    }


    private void parseBusinessSnapshot(DocumentSnapshot snapshot) {

        BusinessModel model = snapshot.toObject(BusinessModel.class);

        if (model != null && model.getBusinessAddress() != null) {

            binding.tagLocationTV.setText(model.getBusinessAddress());
        }
    }
    private void setRating() {

        float rating = 0.0f;

        try {

            rating = Float.parseFloat(post.getUserRating());

        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.ratingBar.setRating(rating);

    }

    private void setLikesCommentsAndRating(PostsModel post) {

        if (post.likes == null)
            binding.likeTV.setText("0");
        else
            binding.likeTV.setText(post.likes.size() + "");

        if (post.likes != null)

            if (post.likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                binding.likeIV.setColorFilter(ContextCompat.getColor(this, R.color.red));

            } else {

                binding.likeIV.setColorFilter(ContextCompat.getColor(this, R.color.white));

            }

        
        getComments();

    }

    void getComments() {

        FirebaseFirestore.getInstance().collection("Comments").whereEqualTo("postId", post.getPostId())
                .addSnapshotListener(this, (value, error) -> {


                    int commentCount = 0;

                    binding.commentTV.setText(commentCount + "");

                    if (!value.isEmpty()) {

                        List<CommentModel> commentModelList = new ArrayList<>();

                        for (DocumentSnapshot document : value.getDocuments()) {

                            CommentModel commentModel = document.toObject(CommentModel.class);

                            if(commentModel != null) {

                                commentModel.id = document.getId();

                                commentModelList.add(commentModel);

                            }
                        }

                        FirestoreHelper.validateComments(commentModelList, comments -> {

                            binding.commentTV.setText(commentModelList.size() + "");

                        });
                    }



                });
    }


    private void setListeners() {

        binding.commentIV.setOnClickListener(view -> openCommentFragment());

        binding.shareIV.setOnClickListener(view -> shareVideo());

        binding.shareTV.setOnClickListener(view -> shareVideo());

        binding.commentIV.setOnClickListener(v -> openCommentFragment());

        binding.likeIV.setOnClickListener(v -> likePost());

        binding.thumbnailIV.setOnClickListener(view -> startPlayVideoActivity());
    }

    private void shareVideo() {

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "POST");
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, post.getUserVideoUrl());
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void startPlayVideoActivity() {

        VideoPreviewActivity.getStartIntent(this, post.getUserVideoUrl());

    }

    private void likePost() {

        DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("UserPosts").document(post.postId);

        if (post.likes == null)
            post.likes = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (post.likes.contains(userId))
            post.likes.remove(userId);
        else
            post.likes.add(userId);

        documentReferenceUser.set(post);

        getMyPosts();
    }

    private void openCommentFragment() {
        if (post != null) {

            CommentsFragment placesDialogListFragment = new CommentsFragment(post);

            placesDialogListFragment.show(getSupportFragmentManager(), "VideoCommentsLikes");
        }

    }

    private void getUserProfile() {

        if (!isNotNull(post))
            return;

        String id = "";

        if (isNotNull(post.getUserID()))

            id = post.getUserID();

        UIUpdate.GetUIUpdate(this).destroy();
        UIUpdate.GetUIUpdate(this).setProgressDialog("", "Loading", this);

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection()).document(id)
                .addSnapshotListener(this, (value, error) -> {

                    UIUpdate.GetUIUpdate(VideoCommentsLikesActivity.this).DismissProgressDialog();

                    if (isNotNull(error) && isNotNull(error.getLocalizedMessage())) {

                        UIUpdate.GetUIUpdate(VideoCommentsLikesActivity.this).AlertDialog("Alert", error.getLocalizedMessage());

                        return;
                    }

                    NormalUserModel userModel = value.toObject(NormalUserModel.class);

                    setUserData(userModel);

                });

    }

    private void setUserData(NormalUserModel userModel) {

        if (isNotNull(userModel.getUserFirstName()) && isNotNull(userModel.getUserLastName())) {

            binding.userNameTV.setText(userModel.getUserFirstName() + " " + userModel.getUserLastName());

        }

        if (isNotNull(userModel.getUserImageUrl())) {

            Glide.with(this).load(userModel.getUserImageUrl()).apply(new RequestOptions().placeholder(R.drawable.no_image_placeholder)).into(binding.picCIV);

        }

    }

    public static void startCommentsLikesActivity(PostsModel postsModel, Context c) {

        Intent i = new Intent(c, VideoCommentsLikesActivity.class);

        i.putExtra("post", postsModel);

        c.startActivity(i);
    }

    private void setData() {

        if (!isNotNull(post))

            return;

        if (isNotNull(post.getUserPostThumbnail())) {

            Glide.with(this).load(post.getUserPostThumbnail()).apply(new RequestOptions().centerCrop())
                    .into(binding.thumbnailIV);

        }

        if (isNotNull(post.getUserPostComment())) {

            binding.descriptionTV.setText(post.getUserPostComment());

            binding.descriptionTV.setMovementMethod(new ScrollingMovementMethod());
        }

        if (isNotNull(post.getUserName())) {

            binding.userNameTV.setText(post.getUserName());

        }

        if (isNotNull(post.getTagBusinessName())) {

            binding.tagLocationTV.setText(post.getTagBusinessName());

        } else {

            binding.tagLocationTV.setVisibility(View.GONE);
        }
    }

    Boolean isNotNull(Object o) {

        return (o != null);

    }

    private void getExtrasFromIntent() {

        Bundle b = getIntent().getExtras();

        if (b.getParcelable("post") != null) {

            post = b.getParcelable("post");

        } else {

            finish();

        }
    }
}
