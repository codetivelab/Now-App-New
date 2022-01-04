package com.buzzware.nowapp;

import android.app.AlertDialog;
import android.content.Context;

import com.buzzware.nowapp.Addapters.HomeListAddapters;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.Models.CommentModel;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.Models.ReplyModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirestoreHelper {

    public static void checkUserDeleted(String id, UserDeletedCallback callback) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .document(id)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        NormalUserModel user = task.getResult().toObject(NormalUserModel.class);

                        if(user != null) {

                            task.getResult().getId();

                            user.id = task.getResult().getId();

                            if (user.id != null && !user.getUserFirstName().isEmpty()) {

                                callback.onReceived(false);

                                return;
                            }
                        }

                    }

                    callback.onReceived(true);

                });


    }

    public static void checkUserPending(String id, UserPendingCallback callback) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .document(id)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        NormalUserModel user = task.getResult().toObject(NormalUserModel.class);

                        if(user != null) {

                            task.getResult().getId();

                            user.id = task.getResult().getId();

                            if (user.userStatus != null) {

                                if(user.userStatus.equalsIgnoreCase("pending")) {

                                    callback.onReceived(true);

                                    return;

                                }

                            }
                        }

                    }

                    callback.onReceived(false);

                });


    }

    public static void validatePosts(List<PostsModel> list, PostFilterCallback callback) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {

                            NormalUserModel user = document.toObject(NormalUserModel.class);

                            if (user != null) {

                                user.id = document.getId();

                                for (int i = 0; i < list.size(); i++) {

                                    if (list.get(i).getUserID().equalsIgnoreCase(user.id)) {

                                        list.get(i).user = user;
                                    }

                                }
                            }

                        }

                        List<PostsModel> tempList = new ArrayList<>();

                        for (int i = 0; i < list.size(); i++) {

                            if (list.get(i).user != null) {

                                tempList.add(list.get(i));

                            }

                        }
                        list.clear();
                        list.addAll(tempList);
                    }

                    callback.onReceived(list);
                });



    }


    public static void validateComments(List<CommentModel> list, CommentsFilterCallback callback) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {

                            NormalUserModel user = document.toObject(NormalUserModel.class);

                            if (user != null) {

                                user.id = document.getId();

                                for (int i = 0; i < list.size(); i++) {

                                    if (list.get(i).commenterId.equalsIgnoreCase(user.id)) {

                                        list.get(i).commenter = user;
                                    }

                                }
                            }

                        }

                        List<CommentModel> tempList = new ArrayList<>();

                        for (int i = 0; i < list.size(); i++) {

                            if (list.get(i).commenter != null) {

                                tempList.add(list.get(i));

                            }

                        }
                        list.clear();
                        list.addAll(tempList);
                    }

                    callback.onReceived(list);
                });



    }


    public static void validateReplies(List<ReplyModel> list, RepliesFilterCallback callback) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {

                            NormalUserModel user = document.toObject(NormalUserModel.class);

                            if (user != null) {

                                user.id = document.getId();

                                for (int i = 0; i < list.size(); i++) {

                                    if (list.get(i).replierId.equalsIgnoreCase(user.id)) {

                                        list.get(i).replier = user;
                                    }

                                }
                            }

                        }

                        List<ReplyModel> tempList = new ArrayList<>();

                        for (int i = 0; i < list.size(); i++) {

                            if (list.get(i).replier != null) {

                                tempList.add(list.get(i));

                            }

                        }
                        list.clear();
                        list.addAll(tempList);
                    }

                    callback.onReceived(list);
                });



    }


    public static void showUserDeletedAlert(Context c) {

        new AlertDialog.Builder(c)
                .setTitle("Alert")
                .setMessage("This user is deleted by Admin.")
                .setPositiveButton("Ok", (dialogInterface, i) -> {

                    dialogInterface.dismiss();


                })
                .create()
                .show();

    }

    public interface UserDeletedCallback {

        public void onReceived(Boolean isDeleted);

    }


    public interface UserPendingCallback {

        public void onReceived(Boolean isPending);

    }

    public interface PostFilterCallback {

        public void onReceived(List<PostsModel> posts);

    }


    public interface CommentsFilterCallback {

        public void onReceived(List<CommentModel> posts);

    }

    public interface RepliesFilterCallback {

        public void onReceived(List<ReplyModel> posts);

    }

}
