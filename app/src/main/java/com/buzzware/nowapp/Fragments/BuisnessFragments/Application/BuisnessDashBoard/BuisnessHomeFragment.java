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

import com.buzzware.nowapp.Addapters.HomeListAddapters;
import com.buzzware.nowapp.Addapters.Latest24HourAdapter;
import com.buzzware.nowapp.Addapters.LatestPostAddapters;
import com.buzzware.nowapp.Addapters.TagUserAdapters;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirestoreHelper;
import com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments.UserFollowActivity;
import com.buzzware.nowapp.Fragments.UserFragments.BaseFragment;
import com.buzzware.nowapp.Fragments.UserFragments.PostDetailFragment;
import com.buzzware.nowapp.Libraries.libactivities.VideoPreviewActivity;
import com.buzzware.nowapp.Models.BusinessHomeGraphData;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.Models.ReplyModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.Models.TagUserModel;
import com.buzzware.nowapp.NetworkRequests.Interfaces.NetWorkRequestsCallBack;
import com.buzzware.nowapp.NetworkRequests.NetworkRequests;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessProfile;
import com.buzzware.nowapp.Screens.General.Video.VideoCommentsLikesActivity;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentBuisnessHomeBinding;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
        hour = hour - 1;

        if (hour <= 24) {

            switch (hour) {
                case 19:
                    currentHour = hour - 18;
                    finalHours = hour + 18;
                case 20:
                    currentHour = hour - 19;
                    finalHours = hour + 19;
                case 21:
                    currentHour = hour - 20;
                    finalHours = hour + 20;
                case 22:
                    currentHour = hour - 21;
                    finalHours = hour + 21;
                case 23:
                    currentHour = hour - 22;
                    finalHours = hour + 22;
                case 24:
                    currentHour = hour - 23;
                    finalHours = hour + 23;
                default:
                    finalHours = hour + 6;
            }
        }
//
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

        } catch (Exception e) {

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

                for (int i = 0; i < 24; i++) {

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

                setChart(graphDataList);

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

    private void setChart(List<BusinessHomeGraphData> graphDataList) {
        ArrayList NoOfEmp = new ArrayList();
        ArrayList year = new ArrayList();
        int colors[] = {
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
                Color.rgb(255, 255, 255),
        };

        int i = 0;
        for (BusinessHomeGraphData data : graphDataList) {

            float intensity = Float.parseFloat(data.getIntensity());

            if (intensity < 0) {

                intensity = intensity * -1;

            }

            NoOfEmp.add(new BarEntry(intensity, i));
            year.add(data.getHour() + ":00");

            i++;
        }


        mBinding.businessGraph.barChart.setDescription("Busy Times");
        mBinding.businessGraph.barChart.setDescriptionColor(Color.rgb(255, 255, 255));
        mBinding.businessGraph.barChart.setBorderColor(Color.rgb(255, 255, 255));
        mBinding.businessGraph.barChart.getXAxis().setTextColor(Color.rgb(255, 255, 255));
        mBinding.businessGraph.barChart.getAxisLeft().setTextColor(Color.rgb(255, 255, 255));
        mBinding.businessGraph.barChart.getAxisRight().setTextColor(Color.rgb(255, 255, 255));
        BarDataSet bardataset = new BarDataSet(NoOfEmp, "");
        mBinding.businessGraph.barChart.animateY(5000);
        BarData data = new BarData(year, bardataset);
        bardataset.setColors(colors);
        mBinding.businessGraph.barChart.setData(data);

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


        mBinding.tagLocationCountTV.setText(String.valueOf(taggedUsers.size() + ""));

        FirestoreHelper.validatePosts(myPosts, posts -> {

            mBinding.nuberOfPostTV.setText(posts.size() + "");

            LatestPostAddapters achivementListAddapters = new LatestPostAddapters(getActivity(), posts, latestItemListener);

            mBinding.postRV.setAdapter(achivementListAddapters);

        });


        FirestoreHelper.validatePosts(pinnedPosts, posts -> {

            Latest24HourAdapter latest24HourAdapter = new Latest24HourAdapter(getActivity(), posts, hour24PostItemListener);

            mBinding.taggedPostsRV.setAdapter(latest24HourAdapter);

        });

        validateTaggedUser();
    }

    private void validateTaggedUser() {

        getUsersList();
    }

    private void getUsersList() {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .addSnapshotListener(getActivity(), (value, error) -> {

                    for (DocumentSnapshot document : value.getDocuments()) {

                        NormalUserModel user = document.toObject(NormalUserModel.class);

                        if (user != null) {
                            user.id = document.getId();

                            for (int i = 0; i < taggedUsers.size(); i++) {

                                try {
                                    if (taggedUsers.get(i).getTagUserID().equalsIgnoreCase(user.id)) {

                                        taggedUsers.get(i).user = user;
                                    }
                                } catch (Exception e) {


                                }


                            }
                        }

                    }

                    List<PostsModel> tempList = new ArrayList<>();

                    for (int i = 0; i < taggedUsers.size(); i++) {

                        if (taggedUsers.get(i).user != null) {

                            tempList.add(taggedUsers.get(i));

                        }

                    }

                    taggedUsers = new ArrayList<>();

                    taggedUsers.addAll(tempList);

                    mBinding.tagLocationCountTV.setText(taggedUsers.size() + "");

                    TagUserAdapters adapters = new TagUserAdapters(getActivity(), taggedUsers, onUserTappedCallback);

                    mBinding.tagUserRV.setAdapter(adapters);

                });

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

        if (businessId != null) {

            FirestoreHelper.checkUserDeleted(post.getUserID(), isDeleted -> {

                if (!isDeleted) {

                    startActivity(new Intent(getActivity(), UserFollowActivity.class)
                            .putExtra("userId", post.getUserID()));

                    return;

                } else {

                    if (getActivity() != null)

                        FirestoreHelper.showUserDeletedAlert(getActivity());

                }


            });
        } else
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