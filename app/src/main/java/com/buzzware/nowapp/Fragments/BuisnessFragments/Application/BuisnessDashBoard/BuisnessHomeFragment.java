package com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Addapters.Latest24HourAdapter;
import com.buzzware.nowapp.Addapters.LatestPostAddapters;
import com.buzzware.nowapp.Addapters.TagUserAdapters;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments.UserFollowActivity;
import com.buzzware.nowapp.Fragments.UserFragments.BaseFragment;
import com.buzzware.nowapp.Fragments.UserFragments.PostDetailFragment;
import com.buzzware.nowapp.Libraries.libactivities.VideoPreviewActivity;
import com.buzzware.nowapp.Models.BusinessHomeGraphData;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.Models.TagUserModel;
import com.buzzware.nowapp.NetworkRequests.Interfaces.NetWorkRequestsCallBack;
import com.buzzware.nowapp.NetworkRequests.NetworkRequests;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessProfile;
import com.buzzware.nowapp.Screens.General.Video.VideoCommentsLikesActivity;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentBuisnessHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BuisnessHomeFragment extends BaseFragment implements View.OnClickListener {

    FragmentBuisnessHomeBinding mBinding;
    boolean isSpinnerOpen = false;
    int finalHours = 0, currentHour = 0;

    FirebaseFirestore db;
    int tagLocationCount = 0;
    FirebaseAuth mAuth;

    List<TagUserModel> tagUserList = new ArrayList<>();

    BusinessModel model;

    List<PostsModel> myPosts;
    List<PostsModel> taggedUsers;
    List<PostsModel> pinnedPosts;

    String businessId;

    public BuisnessHomeFragment() {

        // Required empty public constructor
    }

    public BuisnessHomeFragment(String id) {

        businessId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buisness_home, container, false);

        if (businessId == null) {

            model = UserSessions.GetUserSession().getBusinessModel(getActivity());

            setUpViews();
            SetData();
            GetCurrentHours();

            db = FirebaseFirestore.getInstance();

            mBinding.btnSpinner.setOnClickListener(this::onClick);
            mBinding.postTab.setOnClickListener(this);
            mBinding.tagLocationTab.setOnClickListener(this);
            mBinding.btViewProfile.setOnClickListener(this);

//            getMyPosts();
        } else {
//            getBusinessData();
        }
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (businessId == null)
            getMyPosts();
        else
            getBusinessData();
    }

    private void getBusinessData() {

        FirebaseRequests.GetFirebaseRequests(getActivity()).getBusinessData((FirebaseRequests.SnapshotCallback) (snapshot, error) -> {

            if (snapshot != null && snapshot.getData() != null)
                parseBusinessSnapshot(snapshot);

        }, businessId);
    }

    private void parseBusinessSnapshot(DocumentSnapshot snapshot) {

        model = snapshot.toObject(BusinessModel.class);

        if (model != null) {

            setUpViews();
            SetData();
            GetCurrentHours();

            db = FirebaseFirestore.getInstance();

            mBinding.btnSpinner.setOnClickListener(this::onClick);
            mBinding.postTab.setOnClickListener(this);
            mBinding.tagLocationTab.setOnClickListener(this);
            mBinding.btViewProfile.setOnClickListener(this);

            getMyPosts();
        }
    }

    private void SetData() {
        mBinding.bNameTV.setText(model.getBusinessName());

        if (model.businessBackgroundImage != null) {

            Picasso.with(getActivity()).load(model.businessBackgroundImage).fit().into(mBinding.bBackgroundIV, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    mBinding.bBackgroundIV.setImageResource(R.drawable.no_image_placeholder);
                }
            });
        }

        if (model.businessLogo != null) {
            Picasso.with(getActivity()).load(model.businessLogo).fit().into(mBinding.bLogoIV, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    mBinding.bBackgroundIV.setImageResource(R.drawable.no_image_placeholder);
                }
            });
        }
    }

    private void GetCurrentHours() {
        Calendar calendar = Calendar.getInstance();
        currentHour = calendar.getTime().getHours();
        CreateFinalHourCount(currentHour);
    }

    private void CreateFinalHourCount(int hour) {
        if (hour <= 24) {
            switch (hour) {
                case 19:
                    hour = hour - 1;
                    finalHours = hour + 6;
                case 20:
                    hour = hour - 2;
                    finalHours = hour + 6;
                case 21:
                    hour = hour - 3;
                    finalHours = hour + 6;
                case 22:
                    hour = hour - 4;
                    finalHours = hour + 6;
                case 23:
                    hour = hour - 5;
                    finalHours = hour + 6;
                case 24:
                    hour = hour - 6;
                    finalHours = hour + 6;
                default:
                    finalHours = hour + 6;
            }
        }

        GetData();
    }

    private int GetCurrentDay() {
        int currentDayNumber;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        Log.e("xcz", dayOfTheWeek);
        if (dayOfTheWeek.equals("Monday")) {
            currentDayNumber = 0;
        } else if (dayOfTheWeek.equals("Tuesday")) {
            currentDayNumber = 1;
        } else if (dayOfTheWeek.equals("Wednesday")) {
            currentDayNumber = 2;
        } else if (dayOfTheWeek.equals("Thursday")) {
            currentDayNumber = 3;
        } else if (dayOfTheWeek.equals("Friday")) {
            currentDayNumber = 4;
        } else if (dayOfTheWeek.equals("Saturday")) {
            currentDayNumber = 5;
        } else {
            currentDayNumber = 6;
        }

        return currentDayNumber;
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btnSpinner) {
            if (isSpinnerOpen) {
                isSpinnerOpen = false;
                mBinding.SpinnerLay.setVisibility(View.GONE);
            } else {
                isSpinnerOpen = true;
                mBinding.SpinnerLay.setVisibility(View.VISIBLE);
            }
        } else if (v == mBinding.postTab) {

            mBinding.postsContainerLL.setVisibility(View.VISIBLE);
            mBinding.tagUserRV.setVisibility(View.GONE);

            mBinding.postLine.setBackgroundColor(getContext().getResources().getColor(R.color.color_pink_primary));
            mBinding.tagLocationLine.setBackgroundColor(getContext().getResources().getColor(R.color.text_gray));
            mBinding.tagLocationLay.setVisibility(View.GONE);
            mBinding.postRV.setVisibility(View.VISIBLE);
        } else if (v == mBinding.tagLocationTab) {

            mBinding.postsContainerLL.setVisibility(View.GONE);
            mBinding.tagUserRV.setVisibility(View.VISIBLE);

            mBinding.postLine.setBackgroundColor(getContext().getResources().getColor(R.color.text_gray));
            mBinding.tagLocationLine.setBackgroundColor(getContext().getResources().getColor(R.color.color_pink_primary));
            mBinding.postRV.setVisibility(View.GONE);
            mBinding.tagLocationLay.setVisibility(View.VISIBLE);
        } else if (v == mBinding.btViewProfile) {
            if (businessId == null)
                startActivity(new Intent(getContext(), BuisnessProfile.class));
            else
                startActivity(new Intent(getContext(), BuisnessProfile.class)
                        .putExtra("businessId", businessId));
        }
    }


    private void GetData() {

        try {

            NetworkRequests.GetNetworkRequests(getContext()).GetVenueDetail(callBack, model.getBusinessName(), model.getBusinessAddress(), getContext());

        } catch ( Exception e) {

        }
    }

    NetWorkRequestsCallBack callBack = new NetWorkRequestsCallBack() {
        @Override
        public void ResponseListener(String response, boolean isError) {
            if (!isError) {
                HandelResponse(response);
            } else {
                UIUpdate.GetUIUpdate(getContext()).DismissProgressDialog();
                mBinding.graphInfo.setVisibility(View.VISIBLE);
            }
        }
    };

    private void HandelResponse(String response) {

        UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();

        List<String> busyHoursList = new ArrayList<>();
        List<BusinessHomeGraphData> graphDataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (status.equals("OK")) {
                JSONArray jsonArray = jsonObject.getJSONArray("analysis");
                JSONObject currentDayData = jsonArray.getJSONObject(GetCurrentDay());
                JSONArray hoursAnalysis = currentDayData.getJSONArray("hour_analysis");

                for (int i = currentHour; i < finalHours; i++) {

                    for (int j = 0; j < hoursAnalysis.length(); j++) {

                        JSONObject jsonObjectTempHour = hoursAnalysis.getJSONObject(j);

                        String hour = jsonObjectTempHour.getString("hour");

                        String intensity = jsonObjectTempHour.getString("intensity_nr");

                        if (hour.equals(String.valueOf(i))) {

                            graphDataList.add(new BusinessHomeGraphData(hour, intensity));
                        }
                    }
                }

                Log.e("zxcxzc", new Gson().toJson(graphDataList));

                mBinding.graphInfo.setVisibility(View.GONE);

                SetData(graphDataList);

            } else {

                mBinding.graphInfo.setVisibility(View.VISIBLE);

                Log.e("xczxcz", status);
            }
        } catch (JSONException e) {

            e.printStackTrace();

            mBinding.graphInfo.setVisibility(View.VISIBLE);

            Log.e("xczxcz", e.getMessage());
        }
    }

    private void SetData(List<BusinessHomeGraphData> graphDataList) {

        for (int i = 0; i < graphDataList.size(); i++) {

            BusinessHomeGraphData businessHomeGraphData = graphDataList.get(i);

            if (i == 0) {

                if (!businessHomeGraphData.getHour().equals("0")) {

                    mBinding.businessGraph.bottomLable1.setText(businessHomeGraphData.getHour() + "H");

                    int progress = GetProgressFromIntensity(businessHomeGraphData.getIntensity());

                    mBinding.businessGraph.verticalProgressbar1.setProgress(progress);

                    SetProgressColor(progress, mBinding.businessGraph.verticalProgressbar1);

                    Log.e("cxxdd", businessHomeGraphData.getHour() + " : " + GetProgressFromIntensity(businessHomeGraphData.getIntensity()));

                } else {

                    mBinding.businessGraph.bottomLable1.setText("0H");

                    mBinding.businessGraph.verticalProgressbar1.setProgress(0);
                }
            } else if (i == 1) {

                if (!businessHomeGraphData.getHour().equals("0")) {

                    mBinding.businessGraph.bottomLable2.setText(businessHomeGraphData.getHour() + "H");

                    int progress = GetProgressFromIntensity(businessHomeGraphData.getIntensity());

                    mBinding.businessGraph.verticalProgressbar2.setProgress(progress);

                    SetProgressColor(progress, mBinding.businessGraph.verticalProgressbar2);

                    Log.e("cxxdd", businessHomeGraphData.getHour() + " : " + GetProgressFromIntensity(businessHomeGraphData.getIntensity()));

                } else {

                    mBinding.businessGraph.bottomLable2.setText("0H");

                    mBinding.businessGraph.verticalProgressbar2.setProgress(0);
                }
            } else if (i == 2) {

                if (!businessHomeGraphData.getHour().equals("0")) {

                    mBinding.businessGraph.bottomLable3.setText(businessHomeGraphData.getHour() + "H");

                    int progress = GetProgressFromIntensity(businessHomeGraphData.getIntensity());

                    mBinding.businessGraph.verticalProgressbar3.setProgress(progress);

                    SetProgressColor(progress, mBinding.businessGraph.verticalProgressbar3);

                    Log.e("cxxdd", businessHomeGraphData.getHour() + " : " + GetProgressFromIntensity(businessHomeGraphData.getIntensity()));

                } else {

                    mBinding.businessGraph.bottomLable3.setText("0H");

                    mBinding.businessGraph.verticalProgressbar3.setProgress(0);

                }
            } else if (i == 3) {

                if (!businessHomeGraphData.getHour().equals("0")) {

                    mBinding.businessGraph.bottomLable4.setText(businessHomeGraphData.getHour() + "H");

                    int progress = GetProgressFromIntensity(businessHomeGraphData.getIntensity());

                    mBinding.businessGraph.verticalProgressbar4.setProgress(progress);

                    SetProgressColor(progress, mBinding.businessGraph.verticalProgressbar4);

                    Log.e("cxxdd", businessHomeGraphData.getHour() + " : " + GetProgressFromIntensity(businessHomeGraphData.getIntensity()));

                } else {

                    mBinding.businessGraph.bottomLable4.setText("0H");

                    mBinding.businessGraph.verticalProgressbar4.setProgress(0);

                }
            } else if (i == 4) {

                if (!businessHomeGraphData.getHour().equals("0")) {

                    mBinding.businessGraph.bottomLable5.setText(businessHomeGraphData.getHour() + "H");

                    int progress = GetProgressFromIntensity(businessHomeGraphData.getIntensity());

                    mBinding.businessGraph.verticalProgressbar5.setProgress(progress);

                    SetProgressColor(progress, mBinding.businessGraph.verticalProgressbar5);

                    Log.e("cxxdd", businessHomeGraphData.getHour() + " : " + GetProgressFromIntensity(businessHomeGraphData.getIntensity()));

                } else {

                    mBinding.businessGraph.bottomLable5.setText("0H");

                    mBinding.businessGraph.verticalProgressbar5.setProgress(0);

                }
            } else if (i == 5) {

                if (!businessHomeGraphData.getHour().equals("0")) {

                    mBinding.businessGraph.bottomLable6.setText(businessHomeGraphData.getHour() + "H");

                    int progress = GetProgressFromIntensity(businessHomeGraphData.getIntensity());

                    mBinding.businessGraph.verticalProgressbar6.setProgress(progress);

                    SetProgressColor(progress, mBinding.businessGraph.verticalProgressbar6);

                    Log.e("cxxdd", businessHomeGraphData.getHour() + " : " + GetProgressFromIntensity(businessHomeGraphData.getIntensity()));
                } else {

                    mBinding.businessGraph.bottomLable6.setText("0H");

                    mBinding.businessGraph.verticalProgressbar6.setProgress(0);

                }
            }
        }
    }


    private void SetProgressColor(int progress, ProgressBar verticalProgressbar) {

        if (progress <= 20) {

            verticalProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar_20));

        } else if (progress > 20 && progress <= 40) {

            verticalProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar_20));

        } else if (progress > 40 && progress <= 60) {

            verticalProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar_60));

        } else if (progress > 60 && progress <= 80) {

            verticalProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar_80));

        } else if (progress > 80 && progress <= 10) {

            verticalProgressbar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar_100));

        }
    }

    public int GetProgressFromIntensity(String intensity) {

        if (Integer.parseInt(intensity) == -2) {

            Log.e("czx", "20");
            ///20
            return 20;

        } else if (Integer.parseInt(intensity) == -1) {
            //40
            Log.e("czx", "40");

            return 40;

        } else if (Integer.parseInt(intensity) == 0) {
            //60
            Log.e("czx", "60");

            return 60;

        } else if (Integer.parseInt(intensity) == 1) {
            //80
            Log.e("czx", "80");

            return 80;

        } else if (Integer.parseInt(intensity) == 2) {
            //100
            Log.e("czx", "100");

            return 100;

        } else {

            return 0;

        }
    }

    private void getMyPosts() {

//        if (isOnline()) {

        db.collection("UserPosts")
                .get()
                .addOnCompleteListener(task -> {

                    UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();

                    parseMyPostsSnapshot(task);
                });
//        }
    }

    private void parseMyPostsSnapshot(Task<QuerySnapshot> task) {

        myPosts = new ArrayList<>();

        taggedUsers = new ArrayList<>();

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

            UIUpdate.GetUIUpdate(getActivity()).AlertDialog("Alert", task.getException().getLocalizedMessage());
        }
    }

    private void setUpViews() {

        mBinding.bNameTV.setText(model.getBusinessName());

        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);

        mBinding.postRV.setLayoutManager(lm);

        LinearLayoutManager ll = new LinearLayoutManager(getActivity());

        mBinding.tagUserRV.setLayoutManager(ll);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        mBinding.taggedPostsRV.setLayoutManager(gridLayoutManager);

    }

    private void validatePost(PostsModel postsModel) {

        if (businessId != null) {

            if (postsModel.taggedToCurrentBusiness(getActivity(), businessId)) {

                if (postsModel.createdToday()) {

                    myPosts.add(postsModel);

                }

                if (postsModel.getPinned()) {
                    pinnedPosts.add(postsModel);
                }

                taggedUsers.add(postsModel);

            } else if (postsModel.getUserID().equalsIgnoreCase(businessId)) {


                if (postsModel.createdToday()) {

                    myPosts.add(postsModel);

                }

                if (postsModel.getPinned()) {
                    pinnedPosts.add(postsModel);
                }
            }

        } else if (postsModel.belongsToCurrentUser(getActivity(), null)) {

            if (postsModel.createdToday()) {

                myPosts.add(postsModel);

            }

            if (postsModel.getPinned()) {

                pinnedPosts.add(postsModel);

            }

        } else if (postsModel.taggedToCurrentBusiness(getActivity(), UserSessions.GetUserSession().getFirebaseUserID(getActivity()))) {

            if (postsModel.createdToday()) {

                myPosts.add(postsModel);

            }

            if (postsModel.getPinned()) {
                pinnedPosts.add(postsModel);
            }

            taggedUsers.add(postsModel);

        }

    }

    private void setAdapters() {

        mBinding.nuberOfPostTV.setText(myPosts.size() + "");

        mBinding.tagLocationCountTV.setText(String.valueOf(taggedUsers.size() + ""));

        LatestPostAddapters achivementListAddapters = new LatestPostAddapters(getActivity(), myPosts, latestItemListener);

        mBinding.postRV.setAdapter(achivementListAddapters);

        Latest24HourAdapter latest24HourAdapter = new Latest24HourAdapter(getActivity(), pinnedPosts, hour24PostItemListener);

        mBinding.taggedPostsRV.setAdapter(latest24HourAdapter);

        TagUserAdapters adapters = new TagUserAdapters(getActivity(), taggedUsers, onUserTappedCallback);

        mBinding.tagUserRV.setAdapter(adapters);
    }

    TagUserAdapters.OnPostTappedCallback onUserTappedCallback = postsModel -> ShowDialog(postsModel, true);

    LatestPostAddapters.ItemClickListener latestItemListener = achivementModel -> ShowDialog(achivementModel, false);

    Latest24HourAdapter.ItemClickListener hour24PostItemListener = postsModel -> ShowDialog(postsModel, false);

    private void ShowDialog(PostsModel post, Boolean userTapped) {

        if (userTapped) {

            onUserPostTapped(post);

            return;
        }

        VideoCommentsLikesActivity.startCommentsLikesActivity(post, getActivity());

        return;

//        Dialog myDialog = new Dialog(getActivity());
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

    private void onUserPostTapped(PostsModel post) {

        if (businessId != null)
            startActivity(new Intent(getActivity(), UserFollowActivity.class)
                    .putExtra("userId", post.getUserID()));
        else
            VideoCommentsLikesActivity.startCommentsLikesActivity(post, getActivity());
    }

    private void setDialogUI(Dialog myDialog, PostsModel post) {

        RoundedImageView thumbnailIV = myDialog.findViewById(R.id.thumbnailIV);

        if (!post.getUserPostThumbnail().isEmpty())
            Picasso.with(getActivity()).load(post.getUserPostThumbnail()).placeholder(R.drawable.dummy_post_image)
                    .into(thumbnailIV);
        else
            Picasso.with(getActivity()).load(R.drawable.dummy_post_image)
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

        pinIV.setOnClickListener(v -> pinOrUnpinPost(post, pinIV, unPinIV));
        unPinIV.setOnClickListener(v -> pinOrUnpinPost(post, pinIV, unPinIV));
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
//        Toast.makeText(getActivity(), "Opening Video Player", Toast.LENGTH_SHORT).show();
    }
}