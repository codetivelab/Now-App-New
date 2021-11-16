package com.buzzware.nowapp.FilterTextEditor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.StickerModel;
import com.buzzware.nowapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class StickerBSFragment extends BottomSheetDialogFragment {

    List<StickerModel> stickers;

    List<StickerModel> unfilteredList;

    public StickerBSFragment() {

        stickers = StickerModel.getStickerModels();

        unfilteredList = StickerModel.getStickerModels();
    }

    private StickerListener mStickerListener;

    public void setStickerListener(StickerListener stickerListener) {

        mStickerListener = stickerListener;
    }

    public interface StickerListener {

        void onStickerClick(Bitmap bitmap);

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
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog_video_editor, null);
        dialog.setContentView(contentView);
        EditText searchField = contentView.findViewById(R.id.searchField);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {

                    List<StickerModel> filteredList = new ArrayList<>();

                    for (int index = 0; index < unfilteredList.size(); index++) {

                        StickerModel stickerModel = unfilteredList.get(index);

                        if (stickerModel.getName().startsWith(charSequence.toString().toLowerCase())) {

                            filteredList.add(unfilteredList.get(index));

                        }

                    }

//                    stickers.clear();
//                    stickers.addAll(filteredList);

                    StickerAdapter stickerAdapter = new StickerAdapter(filteredList);
                    rvEmoji.setAdapter(stickerAdapter);

                } else {

                    StickerAdapter stickerAdapter = new StickerAdapter(unfilteredList);
                    rvEmoji.setAdapter(stickerAdapter);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        rvEmoji.setLayoutManager(gridLayoutManager);
        StickerAdapter stickerAdapter = new StickerAdapter(StickerModel.getStickerModels(), StickerModel.getStickerModels());
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        public StickerAdapter(List<StickerModel> list) {
            stickers = list;
        }

        public StickerAdapter(List<StickerModel> stickerModels, List<StickerModel> stickerModels1) {
            unfilteredList = StickerModel.getStickerModels();
            stickers = StickerModel.getStickerModels();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker_video_editor, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            StickerModel stickerModel = stickers.get(position);

            holder.imgSticker.setImageResource(stickerModel.id);

            holder.imgSticker.setOnClickListener(view -> setSticker(stickerModel));
        }

        private void setSticker(StickerModel stickerModel) {

            if (mStickerListener != null) {

                mStickerListener.onStickerClick(
                        BitmapFactory.decodeResource(getResources(), stickerModel.id)
                );
            }

            dismiss();
        }

        @Override
        public int getItemCount() {
            return stickers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
            }
        }
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}