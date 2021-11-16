package com.buzzware.nowapp.Fragments.video;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buzzware.nowapp.Models.StickerModel;
import com.buzzware.nowapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    List<StickerModel> stickers;

    List<StickerModel> unfilteredList;

    public CommentsBottomSheet() {

        stickers = StickerModel.getStickerModels();

        unfilteredList = StickerModel.getStickerModels();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                dismiss();

            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.activity_add_comments, null);
        dialog.setContentView(contentView);

        EditText addCommentField = contentView.findViewById(R.id.addCommentField);

        ImageView mentionIV = contentView.findViewById(R.id.sendIV);

        ImageView emojiIV = contentView.findViewById(R.id.emojiIV);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}