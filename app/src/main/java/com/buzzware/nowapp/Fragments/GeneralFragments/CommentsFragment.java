package com.buzzware.nowapp.Fragments.GeneralFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Addapters.CommentsAdapter;
import com.buzzware.nowapp.Addapters.NearbyPlacesAdapter;
import com.buzzware.nowapp.Addapters.RestaurantsPicturesAdapter;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FilterTextEditor.StickerBSFragment;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.Models.CommentModel;
import com.buzzware.nowapp.Models.MentionUser;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.Models.ReplyModel;
import com.buzzware.nowapp.R;
//import com.buzzware.nowapp.Screens.General.AddCommentsActivity;
import com.buzzware.nowapp.Screens.UserScreens.post.Post;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityAddCommentsBinding;
import com.buzzware.nowapp.databinding.FragmentBottomsheetParentBinding;
import com.buzzware.nowapp.response.NearbyPlacesResponse;
import com.buzzware.nowapp.retrofit.Controller;
import com.buzzware.nowapp.spyglass.DisableScrollMsg;
import com.buzzware.nowapp.spyglass.EnableScrollMsg;
import com.buzzware.nowapp.spyglass.Person;
import com.buzzware.nowapp.spyglass.mentions.Mentionable;
import com.buzzware.nowapp.spyglass.suggestions.SuggestionsResult;
import com.buzzware.nowapp.spyglass.suggestions.impl.BasicSuggestionsListBuilder;
import com.buzzware.nowapp.spyglass.suggestions.interfaces.Suggestible;
import com.buzzware.nowapp.spyglass.suggestions.interfaces.SuggestionsResultListener;
import com.buzzware.nowapp.spyglass.tokenization.QueryToken;
import com.buzzware.nowapp.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.buzzware.nowapp.spyglass.ui.MentionsEditText;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends BottomSheetDialogFragment implements StickerBSFragment.StickerListener, QueryTokenReceiver {

    public static String TAG = com.buzzware.nowapp.BottomSheets.PlacesDialogListFragment.class.getSimpleName();

    private BottomSheetBehavior sheetBehavior;

    List<CommentModel> comments;
    PostsModel post;

    private StickerBSFragment mStickerBSFragment;

    private static final String PERSON_BUCKET = "people-database";
    private static final String CITY_BUCKET = "city-network";
    private static final int PERSON_DELAY = 10;
    private static final int CITY_DELAY = 2000;

    List<Person> persons;

    private SuggestionsResult lastPersonSuggestions;

    List<Person> mentionsList;

    ActivityAddCommentsBinding binding;

    public CommentsFragment(PostsModel post) {
        this.post = post;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = ActivityAddCommentsBinding.inflate(inflater, container, false);

        setSheetBehaviour();

        binding.lockableNSV.setScrollingEnabled(false);

        setStickersBottomSheet();

        setTitle();

        setListeners();
        return binding.getRoot();
    }


    private void setStickersBottomSheet() {

        mStickerBSFragment = new StickerBSFragment();

        mStickerBSFragment.setStickerListener(CommentsFragment.this);

    }

    private void setTitle() {

        binding.includeAppBar.backAppBarTitle.setText("Comments");

    }

    void setRecyclerViewHeight() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.commentsListRV.getLayoutParams();
//        lp.height = (int) (height * 0.84);
//        binding.commentsListRV.setLayoutParams(lp);
    }

    void openedComment() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.commentsListRV.getLayoutParams();
        lp.height = (int) (height * 0.3);
        binding.commentsListRV.setLayoutParams(lp);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DisableScrollMsg event) {

        binding.emojiIV.setOnClickListener(null);

        binding.lockableNSV.setScrollingEnabled(false);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EnableScrollMsg event) {

        binding.emojiIV.setOnClickListener(view -> showStickerView());

        binding.lockableNSV.setScrollingEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        getUsersList();

        setRecyclerViewHeight();

    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

    }

    private void getUsersList() {
        mentionsList = new ArrayList<>();
        UIUpdate.GetUIUpdate(getActivity()).destroy();
        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog("", "Loading", getActivity());

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .addSnapshotListener(getActivity(), (value, error) -> {


                    if (isNotNull(error) && isNotNull(error.getLocalizedMessage())) {

                        UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();

                        UIUpdate.GetUIUpdate(getActivity()).AlertDialog("Alert", error.getLocalizedMessage());

                        return;
                    }

                    persons = new ArrayList<>();

                    for (DocumentSnapshot document : value.getDocuments()) {
                        String fName = "";
                        String lName = "";
                        String imageUrl = "";
                        String userId = "";
                        Map<String, Object> map = document.getData();

                        if (isNotNull(map.get("userFirstName"))) {
                            fName = map.get("userFirstName").toString();
                        }

                        if (isNotNull(map.get("userLastName"))) {
                            lName = map.get("userLastName").toString();
                        }

                        if (isNotNull(map.get("userImageUrl"))) {
                            imageUrl = map.get("userImageUrl").toString();
                        }

                        if (isNotNull(document.getId())) {
                            userId = document.getId();
                        }

                        Person person = new Person(fName, lName, imageUrl, userId);

                        persons.add(person);
                    }

                    getComments();

                });

    }

    public List<Person> getSuggestions(QueryToken queryToken) {
        String[] namePrefixes = queryToken.getKeywords().toLowerCase().split(" ");
        List<Person> suggestions = new ArrayList<>();

        for (Person suggestion : persons) {
            String firstName = suggestion.getFirstName().toLowerCase();
            String lastName = suggestion.getLastName().toLowerCase();
            if (namePrefixes.length == 2) {
                if (firstName.startsWith(namePrefixes[0]) && lastName.startsWith(namePrefixes[1])) {
                    suggestions.add(suggestion);
                }
            } else {
                if (firstName.startsWith(namePrefixes[0]) || lastName.startsWith(namePrefixes[0])) {
                    suggestions.add(suggestion);
                }
            }
        }

        return suggestions;
    }


    private void setMentionTextField() {

        binding.addCommentField.setQueryTokenReceiver(CommentsFragment.this);
        binding.addCommentField.setSuggestionsListBuilder(new CustomSuggestionsListBuilder());

        binding.addCommentField.addMentionWatcher(new MentionsEditText.MentionWatcher() {
            @Override
            public void onMentionAdded(Mentionable mention, String text, int start, int end) {

                for (int i = 0; i < persons.size(); i++) {

                    Person p = persons.get(i);

                    if (p.getSuggestibleId() == mention.getSuggestibleId()) {

                        p.startingIndex = start;
                        p.endingIndex = end;
                        mentionsList.add(p);
                    }
                }
            }

            @Override
            public void onMentionDeleted(Mentionable mention, String text, int start, int end) {

                for (int i = 0; i < persons.size(); i++) {

                    Person p = persons.get(i);

                    if (p.getSuggestibleId() == mention.getSuggestibleId()) {
                        mentionsList.remove(p);
                    }
                }
            }

            @Override
            public void onMentionPartiallyDeleted(Mentionable mention, String text, int start, int end) {
//                Toast.makeText(MultiSourceMentions.this, text,Toast.LENGTH_LONG).show();
            }
        });

        updateSuggestions();
    }

    void getComments() {

        FirebaseFirestore.getInstance().collection("Comments").whereEqualTo("postId", post.getPostId())
                .addSnapshotListener(getActivity(), (value, error) -> {

                    comments = new ArrayList<>();

                    UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();

                    if (isNotNull(getActivity()) && isNotNull(error)) {

                        UIUpdate.GetUIUpdate(getActivity()).AlertDialog("Alert", error.getLocalizedMessage());

                        return;
                    }

                    if (!value.isEmpty())

                        for (DocumentSnapshot document : value.getDocuments()) {

                            CommentModel commentModel = document.toObject(CommentModel.class);

                            commentModel.id = document.getId();

                            commentModel.repliesCount = 0;

                            comments.add(commentModel);
                        }

                    getReplies();

                    setMentionTextField();

                });
    }

    private void setCommentsList() {

        if (comments != null && comments.size() > 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                comments.sort((commentModel, t1) -> commentModel.time.compareTo(t1.time));

            }


        binding.commentsListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.commentsListRV.setAdapter(new CommentsAdapter(getActivity(), comments));
    }

    void getReplies() {

        FirebaseFirestore.getInstance().collection("Replies")
                .addSnapshotListener(getActivity(), (value, error) -> {

                    if (error != null && error.getLocalizedMessage() != null) {

                        setCommentsList();

                        return;
                    }

                    List<ReplyModel> replies = new ArrayList<>();

                    for (DocumentSnapshot document : value.getDocuments()) {

                        ReplyModel replyModel = document.toObject(ReplyModel.class);

                        replyModel.id = document.getId();

                        replies.add(replyModel);
                    }

                    for (CommentModel comment : comments) {

                        for (ReplyModel reply : replies) {

                            if (reply.commentId.equalsIgnoreCase(comment.id)) {
                                comment.repliesCount++;
                            }
                        }
                    }

                    setCommentsList();
                });
    }

    private void updateSuggestions() {
//        final boolean hasPeople = peopleCheckBox.isChecked();

        // Handle person mentions
//        if (lastPersonSuggestions != null) {
//            editor.onReceiveSuggestionsResult(lastPersonSuggestions, PERSON_BUCKET);
//        } else
        if (lastPersonSuggestions != null) {
            SuggestionsResult emptySuggestions = new SuggestionsResult(lastPersonSuggestions.getQueryToken(), new ArrayList<Person>());
            binding.addCommentField.onReceiveSuggestionsResult(emptySuggestions, PERSON_BUCKET);
        }

    }

    private void setListeners() {

        binding.sendIV.setOnClickListener(view -> validateComment());

        binding.includeAppBar.backIcon.setOnClickListener(view -> getActivity().finish());

        binding.emojiIV.setOnClickListener(view -> showStickerView());
    }

    private void showStickerView() {

        binding.lockableNSV.setScrollingEnabled(true);

        mStickerBSFragment.show(getActivity().getSupportFragmentManager(), mStickerBSFragment.getTag());

    }

    private void validateComment() {

        if (binding.addCommentField.getText().toString().isEmpty())

            return;

        List<MentionUser> mentions = new ArrayList<>();


        DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Comments").document();

        CommentModel commentModel = new CommentModel();
        commentModel.imageUrl = null;
        commentModel.commenterId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        commentModel.text = binding.addCommentField.getText().toString();
        commentModel.postId = post.getPostId();
        commentModel.time = new Date().getTime();
        if (mentionsList == null)
            mentionsList = new ArrayList<>();

        for (Person person : mentionsList) {

            MentionUser mention = new MentionUser();
            mention.end = person.endingIndex;
            mention.start = person.startingIndex;
            mention.fullName = person.getFullName();
            mention.firstName = person.getFirstName();
            mention.lastName = person.getLastName();
            mention.userId = person.getUserId();

            mentions.add(mention);
        }

        commentModel.mentioned = mentions;

        documentReferenceUser.set(commentModel);

        UIUpdate.GetUIUpdate(getActivity()).hideKeyboard(getActivity());

        binding.addCommentField.setText("");

        getComments();

        mentionsList.clear();
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {

            String comment = binding.addCommentField.getText().toString();

            if (isNotNull(comment) && comment.toCharArray().length > 0)

                checkAndPostComment(comment);

            return true;

        }

        return false;
    }

    private void checkAndPostComment(String comment) {

        if (isNotNull(post) && isNotNull(post.getPostId())) {

            DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Comments").document(post.getPostId());


//            documentReferenceUser.set(userData);
        }
    }

//    public static void startActivity(PostsModel postsModel, Context c) {
//
//        Intent i = new Intent(c, AddCommentsActivity.class);
//
//        i.putExtra("post", postsModel);
//
//        c.startActivity(i);
//    }

    Boolean isNotNull(Object o) {

        return (o != null);

    }

    @Override
    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
//        final boolean hasPeople = peopleCheckBox.isChecked();
//        final boolean hasCities = citiesCheckBox.isChecked();

        final List<String> buckets = new ArrayList<>();
        final SuggestionsResultListener listener = binding.addCommentField;
        final Handler handler = new Handler(Looper.getMainLooper());

        // Fetch people if necessary
//        if (hasPeople) {
        buckets.add(PERSON_BUCKET);
        handler.postDelayed(() -> {
//                TODO GET SUGGESTIONS
            List<Person> suggestions = getSuggestions(queryToken);
            lastPersonSuggestions = new SuggestionsResult(queryToken, suggestions);
            listener.onReceiveSuggestionsResult(lastPersonSuggestions, PERSON_BUCKET);
        }, PERSON_DELAY);
/*
                }
         Fetch cities if necessary
                if (hasCities) {
                    buckets.add(CITY_BUCKET);
                    handler.postDelayed(() -> {
                        List<City> suggestions = cities.getSuggestions(queryToken);
                        lastCitySuggestions = new SuggestionsResult(queryToken, suggestions);
                        listener.onReceiveSuggestionsResult(lastCitySuggestions, CITY_BUCKET);
                    }, CITY_DELAY);
                }
         Return buckets, one for each source (serves as promise to editor that we will call
         onReceiveSuggestionsResult at a later time)
        */

        return buckets;
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {

        uploadThumbnail(bitmap);
    }

    private class CustomSuggestionsListBuilder extends BasicSuggestionsListBuilder {

        @NonNull
        @Override
        public View getView(@NonNull Suggestible suggestion,
                            @Nullable View convertView,
                            ViewGroup parent,
                            @NonNull Context context,
                            @NonNull LayoutInflater inflater,
                            @NonNull Resources resources) {

            View v = super.getView(suggestion, convertView, parent, context, inflater, resources);
            if (!(v instanceof TextView)) {
                return v;
            }

            // Color text depending on the type of mention
            TextView tv = (TextView) v;
            if (suggestion instanceof NormalUserModel) {
                tv.setTextColor(getResources().getColor(R.color.yellow_color_picker));
            }
/*
            else if (suggestion instanceof City) {
                tv.setTextColor(getResources().getColor(R.color.city_mention_text));
*/


            return tv;
        }
    }

    private void uploadThumbnail(Bitmap sticker) {

        UIUpdate.destroy();

        UIUpdate.GetUIUpdate(getActivity()).setProgressDialog("", "Uploading", getActivity());

        FirebaseRequests.GetFirebaseRequests(getActivity()).firebaseUploadBitmap((message, errorMessage) -> {

            if (message != null) {

                sendStickerMsg(message);

            } else {
                UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();

                UIUpdate.GetUIUpdate(getActivity()).AlertDialog("Alert", errorMessage);
            }

        }, sticker);

    }

    private void sendStickerMsg(String url) {

        DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Comments").document();

        CommentModel commentModel = new CommentModel();
        commentModel.imageUrl = url;
        commentModel.commenterId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        commentModel.text = null;
        commentModel.postId = post.getPostId();
        commentModel.time = new Date().getTime();
        if (mentionsList == null)
            mentionsList = new ArrayList<>();

        documentReferenceUser.set(commentModel);

        UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();
        UIUpdate.GetUIUpdate(getActivity()).hideKeyboard(getActivity());

        binding.addCommentField.setText("");

        getComments();

    }

    private void setSheetBehaviour() {

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout);

        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
