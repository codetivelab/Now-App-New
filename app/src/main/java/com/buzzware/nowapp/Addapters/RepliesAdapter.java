package com.buzzware.nowapp.Addapters;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FirestoreHelper;
import com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments.UserFollowActivity;
import com.buzzware.nowapp.Models.CommentModel;
import com.buzzware.nowapp.Models.MentionUser;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.ReplyModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.ItemCommentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.CommentsHolder> {

    List<ReplyModel> comments;
    Activity c;

    public RepliesAdapter(Activity c, List<ReplyModel> comments) {
        this.c = c;
        this.comments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RepliesAdapter.CommentsHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentsHolder holder, int position) {

        ReplyModel comment = comments.get(position);

        if (comment.likes != null) {

            if (comment.likes.size() == 1)

                holder.binding.likeTV.setText(comment.likes.size() + " Like");

            else if (comment.likes.size() > 1)

                holder.binding.likeTV.setText(comment.likes.size() + " Likes");

            else

                holder.binding.likeTV.setText("");

            if (comment.likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                holder.binding.likeIV.setColorFilter(ContextCompat.getColor(c, R.color.red));

            } else {

                holder.binding.likeIV.setColorFilter(ContextCompat.getColor(c, R.color.white));

            }

        }

        String time = covertTimeToText(new Date(comment.time));

        holder.binding.timeTV.setText(time);

        if (comment.text != null) {

            holder.binding.commentTV.setMovementMethod(LinkMovementMethod.getInstance());

            SpannableString spannableString = new SpannableString(comment.text);


            for (MentionUser user : comment.mentioned)
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull @NotNull View view) {

                        FirestoreHelper.checkUserDeleted(user.userId, isDeleted -> {

                            if (!isDeleted) {


                                c.startActivity(new Intent(c, UserFollowActivity.class)
                                        .putExtra("userId", user.userId));

                                return;

                            } else {

                                if (c != null)

                                    FirestoreHelper.showUserDeletedAlert(c);

                            }


                        });

                    }
                }, user.start, user.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.binding.commentTV.setHighlightColor(c.getResources().getColor(android.R.color.white));
            holder.binding.commentTV.setText(spannableString, TextView.BufferType.SPANNABLE);

        } else {

            holder.binding.commentTV.setVisibility(View.GONE);

            holder.binding.stickerIV.setVisibility(View.VISIBLE);

            Glide.with(c).load(comment.imageUrl).into(holder.binding.stickerIV);
        }

        holder.binding.likeIV.setOnClickListener(view -> likeAComment(comment));

        getUserData(holder.binding, comment);
    }

    private void getUserData(ItemCommentBinding binding, ReplyModel comment) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .document(comment.replierId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        NormalUserModel normalUserModel = task.getResult().toObject(NormalUserModel.class);

                        binding.userNameTV.setText(normalUserModel.getUserFirstName() + " " + normalUserModel.getUserLastName());

                        Glide.with(c).load(normalUserModel.getUserImageUrl()).into(binding.picCIV);
                    }

                });
    }


    public String covertTimeToText(Date date) {

        String convTime = null;

        String prefix = "";
        String suffix = "Ago";

        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - date.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes "+suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {

                String days = "Days";

                if(day == 1)
                    days = "Day";

                convTime = day+ " "+ days +" "+suffix;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }

    public void likeAComment(ReplyModel commentModel) {

        DocumentReference documentReferenceUser = FirebaseFirestore.getInstance().collection("Replies").document(commentModel.id);

        if (commentModel.likes == null)
            commentModel.likes = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (commentModel.likes.contains(userId))
            commentModel.likes.remove(userId);
        else
            commentModel.likes.add(userId);

        documentReferenceUser.set(commentModel);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentsHolder extends RecyclerView.ViewHolder {
        View view;
        ItemCommentBinding binding;

        public CommentsHolder(@NonNull ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.replyTV.setVisibility(View.GONE);
        }
    }


}
