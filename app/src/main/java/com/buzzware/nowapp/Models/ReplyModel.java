package com.buzzware.nowapp.Models;

import java.util.List;

public class ReplyModel {

    public String id;
    public Long time;
    public String commentId;
    public String replierId;

    public NormalUserModel replier;

    public String text;
    public String imageUrl;
    public List<MentionUser> mentioned;
    public List<String> likes;
}
