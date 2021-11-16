package com.buzzware.nowapp.utils;

import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.buzzware.nowapp.Models.MentionUser;

public class MultiClickableSpans extends ClickableSpan {

    MentionUser mentionUser;
    int pos;

    public MultiClickableSpans(MentionUser mentionUser) {
        this.mentionUser = mentionUser;
    }

    @Override
    public void onClick(View widget) {
        Toast.makeText(widget.getContext(), mentionUser.fullName, Toast.LENGTH_SHORT).show();
    }

}