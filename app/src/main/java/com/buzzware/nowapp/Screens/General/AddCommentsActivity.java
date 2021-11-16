////package com.buzzware.nowapp.Screens.General;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.DisplayMetrics;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.CheckBox;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.buzzware.nowapp.Addapters.CommentsAdapter;
//import com.buzzware.nowapp.Constants.Constant;
//import com.buzzware.nowapp.FilterTextEditor.StickerBSFragment;
//import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
//import com.buzzware.nowapp.Models.CommentModel;
//import com.buzzware.nowapp.Models.NormalUserModel;
//import com.buzzware.nowapp.Models.PostsModel;
//import com.buzzware.nowapp.Models.MentionUser;
//import com.buzzware.nowapp.Models.ReplyModel;
//import com.buzzware.nowapp.R;
//import com.buzzware.nowapp.Screens.UserScreens.UploadPostScreen;
//import com.buzzware.nowapp.Screens.UserScreens.post.Post;
//import com.buzzware.nowapp.Sessions.UserSessions;
//import com.buzzware.nowapp.UIUpdates.UIUpdate;
//import com.buzzware.nowapp.databinding.ActivityAddCommentsBinding;
//import com.buzzware.nowapp.databinding.ItemCommentBinding;
//import com.buzzware.nowapp.spyglass.DisableScrollMsg;
//import com.buzzware.nowapp.spyglass.EnableScrollMsg;
//import com.buzzware.nowapp.spyglass.Person;
//import com.buzzware.nowapp.spyglass.mentions.Mentionable;
//import com.buzzware.nowapp.spyglass.suggestions.SuggestionsResult;
//import com.buzzware.nowapp.spyglass.suggestions.impl.BasicSuggestionsListBuilder;
//import com.buzzware.nowapp.spyglass.suggestions.interfaces.Suggestible;
//import com.buzzware.nowapp.spyglass.suggestions.interfaces.SuggestionsResultListener;
//import com.buzzware.nowapp.spyglass.tokenization.QueryToken;
//import com.buzzware.nowapp.spyglass.tokenization.interfaces.QueryTokenReceiver;
//import com.buzzware.nowapp.spyglass.ui.MentionsEditText;
//import com.buzzware.nowapp.spyglass.ui.RichEditorView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//public class AddCommentsActivity extends AppCompatActivity implements QueryTokenReceiver, StickerBSFragment.StickerListener {
//
//    ActivityAddCommentsBinding binding;
//
//    List<CommentModel> comments;
//    PostsModel post;
//
//    private StickerBSFragment mStickerBSFragment;
//
//    private static final String PERSON_BUCKET = "people-database";
//    private static final String CITY_BUCKET = "city-network";
//    private static final int PERSON_DELAY = 10;
//    private static final int CITY_DELAY = 2000;
//
//    List<Person> persons;
//
//    private SuggestionsResult lastPersonSuggestions;
//
//    List<Person> mentionsList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityAddCommentsBinding.inflate(getLayoutInflater());
//
//        setContentView(binding.getRoot());
//
////        binding.lockableNSV.setScrollingEnabled(false);
//
//        getExtrasFromIntent();
//
//        setStickersBottomSheet();
//
//        setTitle();
//
//        setListeners();
//    }
//
//
//    private void setStickersBottomSheet() {
//
//        mStickerBSFragment = new StickerBSFragment();
//
//        mStickerBSFragment.setStickerListener(this);
//
//    }
//
//    private void setTitle() {
//
//        binding.includeAppBar.backAppBarTitle.setText("Comments");
//
//    }
//
//    void setRecyclerViewHeight() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.commentsListRV.getLayoutParams();
//        lp.height = (int) (height * 0.84);
//        binding.commentsListRV.setLayoutParams(lp);
//    }
//
//    void openedComment() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.commentsListRV.getLayoutParams();
//        lp.height = (int) (height * 0.3);
//        binding.commentsListRV.setLayoutParams(lp);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(DisableScrollMsg event) {
//
//        binding.emojiIV.setOnClickListener(null);
//
//        binding.lockableNSV.setScrollingEnabled(false);
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(EnableScrollMsg event) {
//
//        binding.emojiIV.setOnClickListener(view -> showStickerView());
//
//        binding.lockableNSV.setScrollingEnabled(true);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        EventBus.getDefault().register(this);
//
//        getUsersList();
//
//        setRecyclerViewHeight();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        EventBus.getDefault().unregister(this);
//
//    }
//
//    private void getUsersList() {
//        mentionsList = new ArrayList<>();
//        UIUpdate.GetUIUpdate(this).destroy();
//        UIUpdate.GetUIUpdate(this).setProgressDialog("", "Loading", this);
//
//        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
//                .addSnapshotListener(this, (value, error) -> {
//
//
//                    if (isNotNull(error) && isNotNull(error.getLocalizedMessage())) {
//
//                        UIUpdate.GetUIUpdate(AddCommentsActivity.this).DismissProgressDialog();
//
//                        UIUpdate.GetUIUpdate(AddCommentsActivity.this).AlertDialog("Alert", error.getLocalizedMessage());
//
//                        return;
//                    }
//
//                    persons = new ArrayList<>();
//
//                    for (DocumentSnapshot document : value.getDocuments()) {
//                        String fName = "";
//                        String lName = "";
//                        String imageUrl = "";
//                        String userId = "";
//                        Map<String, Object> map = document.getData();
//
//                        if (isNotNull(map.get("userFirstName"))) {
//                            fName = map.get("userFirstName").toString();
//                        }
//
//                        if (isNotNull(map.get("userLastName"))) {
//                            lName = map.get("userLastName").toString();
//                        }
//
//                        if (isNotNull(map.get("userImageUrl"))) {
//                            imageUrl = map.get("userImageUrl").toString();
//                        }
//
//                        if (isNotNull(document.getId())) {
//                            userId = document.getId();
//                        }
//
//                        Person person = new Person(fName, lName, imageUrl, userId);
//
//                        persons.add(person);
//                    }
//
//                    getComments();
//
//                });
//
//    }
//
//    public List<Person> getSuggestions(QueryToken queryToken) {
//        String[] namePrefixes = queryToken.getKeywords().toLowerCase().split(" ");
//        List<Person> suggestions = new ArrayList<>();
////        if (mData != null) {
//        for (Person suggestion : persons) {
//            String firstName = suggestion.getFirstName().toLowerCase();
//            String lastName = suggestion.getLastName().toLowerCase();
//            if (namePrefixes.length == 2) {
//                if (firstName.startsWith(namePrefixes[0]) && lastName.startsWith(namePrefixes[1])) {
//                    suggestions.add(suggestion);
//                }
//            } else {
//                if (firstName.startsWith(namePrefixes[0]) || lastName.startsWith(namePrefixes[0])) {
//                    suggestions.add(suggestion);
//                }
//            }
//        }
////        }
//        return suggestions;
//    }
//
//
//    private void setMentionTextField() {
//
//        binding.addCommentField.setQueryTokenReceiver(this);
//        binding.addCommentField.setSuggestionsListBuilder(new CustomSuggestionsListBuilder());
//
//        binding.addCommentField.addMentionWatcher(new MentionsEditText.MentionWatcher() {
//            @Override
//            public void onMentionAdded(Mentionable mention, String text, int start, int end) {
//
//                for (int i = 0; i < persons.size(); i++) {
//
//                    Person p = persons.get(i);
//
//                    if (p.getSuggestibleId() == mention.getSuggestibleId()) {
//
//                        p.startingIndex = start;
//                        p.endingIndex = end;
//                        mentionsList.add(p);
//                    }
//                }
//
////                Toast.makeText(AddCommentsActivity.this, mentionsList.size() + text, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onMentionDeleted(Mentionable mention, String text, int start, int end) {
//
//                for (int i = 0; i < persons.size(); i++) {
//
//                    Person p = persons.get(i);
//
//                    if (p.getSuggestibleId() == mention.getSuggestibleId()) {
//                        mentionsList.remove(p);
//                    }
//                }
//
////                Toast.makeText(AddCommentsActivity.this, mentionsList.size() + text, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onMentionPartiallyDeleted(Mentionable mention, String text, int start, int end) {
////                Toast.makeText(MultiSourceMentions.this, text,Toast.LENGTH_LONG).show();
//            }
//        });
//
//        updateSuggestions();
//    }
//
//    void getComments() {
//
//        FirebaseFirestore.getInstance().collection("Comments").whereEqualTo("postId", post.getPostId())
//                .addSnapshotListener(this, (value, error) -> {
//
//                    comments = new ArrayList<>();
//
//                    UIUpdate.GetUIUpdate(AddCommentsActivity.this).DismissProgressDialog();
//
//                    if (isNotNull(error) && isNotNull(error.getLocalizedMessage())) {
//
//                        UIUpdate.GetUIUpdate(AddCommentsActivity.this).AlertDialog("Alert", error.getLocalizedMessage());
//
//                        return;
//                    }
//
//                    if (!value.isEmpty())
//
//                        for (DocumentSnapshot document : value.getDocuments()) {
//
//                            CommentModel commentModel = document.toObject(CommentModel.class);
//
//                            commentModel.id = document.getId();
//
//                            commentModel.repliesCount = 0;
//
//                            comments.add(commentModel);
//                        }
//
//                    getReplies();
//
//                    setMentionTextField();
//
//                });
//    }
//
//    private void setCommentsList() {
//
//        if (comments != null && comments.size() > 0)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                comments.sort((commentModel, t1) -> commentModel.time.compareTo(t1.time));
//
//            }
//
//
//        binding.commentsListRV.setLayoutManager(new LinearLayoutManager(this));
//        binding.commentsListRV.setAdapter(new CommentsAdapter(this, comments));
//    }
//
//    void getReplies() {
//
//        FirebaseFirestore.getInstance().collection("Replies")
//                .addSnapshotListener(this, (value, error) -> {
//
//                    if (error != null && error.getLocalizedMessage() != null) {
//
//                       setCommentsList();
//
//                        return;
//                    }
//
//                    List<ReplyModel> replies = new ArrayList<>();
//
//                    for (DocumentSnapshot document : value.getDocuments()) {
//
//                        ReplyModel replyModel = document.toObject(ReplyModel.class);
//
//                        replyModel.id = document.getId();
//
//                        replies.add(replyModel);
//                    }
//
//                    for (CommentModel comment : comments) {
//
//                        for (ReplyModel reply : replies) {
//
//                            if (reply.commentId.equalsIgnoreCase(comment.id)) {
//                                comment.repliesCount++;
//                            }
//                        }
//                    }
//
//                    setCommentsList();
//                });
//    }
//
//    private void updateSuggestions() {
////        final boolean hasPeople = peopleCheckBox.isChecked();
//
//        // Handle person mentions
////        if (lastPersonSuggestions != null) {
////            editor.onReceiveSuggestionsResult(lastPersonSuggestions, PERSON_BUCKET);
////        } else
//        if (lastPersonSuggestions != null) {
//            SuggestionsResult emptySuggestions = new SuggestionsResult(lastPersonSuggestions.getQueryToken(), new ArrayList<Person>());
//            binding.addCommentField.onReceiveSuggestionsResult(emptySuggestions, PERSON_BUCKET);
//        }
//
//    }
//
//    private void setListeners() {
//
//        binding.sendIV.setOnClickListener(view -> validateComment());
//
//        binding.includeAppBar.backIcon.setOnClickListener(view -> onBackPressed());
//
//        binding.emojiIV.setOnClickListener(view -> showStickerView());
//    }
//
//    private void showStickerView() {
//
//        binding.lockableNSV.setScrollingEnabled(true);
//
//        mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
//
//    }
//
//    private void validateComment() {
//
//        if (binding.addCommentField.getText().toString().isEmpty())
//
//            return;
//
//        List<MentionUser> mentions = new ArrayList<>();
//
//
//        DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Comments").document();
//
//        CommentModel commentModel = new CommentModel();
//        commentModel.imageUrl = null;
//        commentModel.commenterId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        commentModel.text = binding.addCommentField.getText().toString();
//        commentModel.postId = post.getPostId();
//        commentModel.time = new Date().getTime();
//        if (mentionsList == null)
//            mentionsList = new ArrayList<>();
//
//        for (Person person : mentionsList) {
//
//            MentionUser mention = new MentionUser();
//            mention.end = person.endingIndex;
//            mention.start = person.startingIndex;
//            mention.fullName = person.getFullName();
//            mention.firstName = person.getFirstName();
//            mention.lastName = person.getLastName();
//            mention.userId = person.getUserId();
//
//            mentions.add(mention);
//        }
//
//        commentModel.mentioned = mentions;
//
//        documentReferenceUser.set(commentModel);
//
//        UIUpdate.GetUIUpdate(this).hideKeyboard(this);
//
//        binding.addCommentField.setText("");
//
//        getComments();
//
//        mentionsList.clear();
//    }
//
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//        if (actionId == EditorInfo.IME_ACTION_DONE) {
//
//            String comment = binding.addCommentField.getText().toString();
//
//            if (isNotNull(comment) && comment.toCharArray().length > 0)
//
//                checkAndPostComment(comment);
//
//            return true;
//
//        }
//
//        return false;
//    }
//
//    private void checkAndPostComment(String comment) {
//
//        if (isNotNull(post) && isNotNull(post.getPostId())) {
//
//            DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Comments").document(post.getPostId());
//
//
////            documentReferenceUser.set(userData);
//        }
//    }
//
//    public static void startActivity(PostsModel postsModel, Context c) {
//
//        Intent i = new Intent(c, AddCommentsActivity.class);
//
//        i.putExtra("post", postsModel);
//
//        c.startActivity(i);
//    }
//
//    Boolean isNotNull(Object o) {
//
//        return (o != null);
//
//    }
//
//    private void getExtrasFromIntent() {
//
//        Bundle b = getIntent().getExtras();
//
//        if (b.getParcelable("post") != null) {
//
//            post = b.getParcelable("post");
//
//        } else {
//
//            finish();
//
//        }
//    }
//
//
//    @Override
//    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
////        final boolean hasPeople = peopleCheckBox.isChecked();
////        final boolean hasCities = citiesCheckBox.isChecked();
//
//        final List<String> buckets = new ArrayList<>();
//        final SuggestionsResultListener listener = binding.addCommentField;
//        final Handler handler = new Handler(Looper.getMainLooper());
//
//        // Fetch people if necessary
////        if (hasPeople) {
//        buckets.add(PERSON_BUCKET);
//        handler.postDelayed(() -> {
////                TODO GET SUGGESTIONS
//            List<Person> suggestions = getSuggestions(queryToken);
//            lastPersonSuggestions = new SuggestionsResult(queryToken, suggestions);
//            listener.onReceiveSuggestionsResult(lastPersonSuggestions, PERSON_BUCKET);
//        }, PERSON_DELAY);
///*
//                }
//         Fetch cities if necessary
//                if (hasCities) {
//                    buckets.add(CITY_BUCKET);
//                    handler.postDelayed(() -> {
//                        List<City> suggestions = cities.getSuggestions(queryToken);
//                        lastCitySuggestions = new SuggestionsResult(queryToken, suggestions);
//                        listener.onReceiveSuggestionsResult(lastCitySuggestions, CITY_BUCKET);
//                    }, CITY_DELAY);
//                }
//         Return buckets, one for each source (serves as promise to editor that we will call
//         onReceiveSuggestionsResult at a later time)
//        */
//
//        return buckets;
//    }
//
//    @Override
//    public void onStickerClick(Bitmap bitmap) {
//
//        uploadThumbnail(bitmap);
//    }
//
//    private class CustomSuggestionsListBuilder extends BasicSuggestionsListBuilder {
//
//        @NonNull
//        @Override
//        public View getView(@NonNull Suggestible suggestion,
//                            @Nullable View convertView,
//                            ViewGroup parent,
//                            @NonNull Context context,
//                            @NonNull LayoutInflater inflater,
//                            @NonNull Resources resources) {
//
//            View v = super.getView(suggestion, convertView, parent, context, inflater, resources);
//            if (!(v instanceof TextView)) {
//                return v;
//            }
//
//            // Color text depending on the type of mention
//            TextView tv = (TextView) v;
//            if (suggestion instanceof NormalUserModel) {
//                tv.setTextColor(getResources().getColor(R.color.yellow_color_picker));
//            }
///*
//            else if (suggestion instanceof City) {
//                tv.setTextColor(getResources().getColor(R.color.city_mention_text));
//*/
//
//
//            return tv;
//        }
//    }
//
//    private void uploadThumbnail(Bitmap sticker) {
//
//        UIUpdate.destroy();
//
//        UIUpdate.GetUIUpdate(AddCommentsActivity.this).setProgressDialog("", "Uploading", this);
//
//        FirebaseRequests.GetFirebaseRequests(this).firebaseUploadBitmap((message, errorMessage) -> {
//
//            if (message != null) {
//
//                sendStickerMsg(message);
//
//            } else {
//                UIUpdate.GetUIUpdate(this).DismissProgressDialog();
//
//                UIUpdate.GetUIUpdate(AddCommentsActivity.this).AlertDialog("Alert", errorMessage);
//            }
//
//        }, sticker);
//
//    }
//
//    private void sendStickerMsg(String url) {
//
//        DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Comments").document();
//
//        CommentModel commentModel = new CommentModel();
//        commentModel.imageUrl = url;
//        commentModel.commenterId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        commentModel.text = null;
//        commentModel.postId = post.getPostId();
//        commentModel.time = new Date().getTime();
//        if (mentionsList == null)
//            mentionsList = new ArrayList<>();
//
//        documentReferenceUser.set(commentModel);
//
//        UIUpdate.GetUIUpdate(this).DismissProgressDialog();
//        UIUpdate.GetUIUpdate(this).hideKeyboard(this);
//
//        binding.addCommentField.setText("");
//
//        getComments();
//
//    }
//}
