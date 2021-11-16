package com.buzzware.nowapp.Models;

import com.buzzware.nowapp.R;

import java.util.ArrayList;
import java.util.List;

public class StickerModel {

    public int id;

    public StickerModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {

        String n =  name.replace("_"," ");

        return n.toLowerCase();

    }

    String capitalizeAllFirstLetter(String name)
    {
        char[] array = name.toCharArray();

        array[0] = Character.toUpperCase(array[0]);

        for (int i = 1; i < array.length; i++) {

            if (Character.isWhitespace(array[i - 1])) {

                array[i] = Character.toUpperCase(array[i]);
            }
        }

        return new String(array);
    }
    public void setName(String name) {
        this.name = name;
    }

    public static List<StickerModel> getStickerModels() {

        List<StickerModel> list = new ArrayList<>();

        list.add(new StickerModel(R.drawable.beaming_face_with_smiling_eyes,"beaming_face_with_smiling_eyes"));
        list.add(new StickerModel(R.drawable.drooling_face,"drooling_face"));
        list.add(new StickerModel(R.drawable.expressionless_face,"expressionless_face"));
        list.add(new StickerModel(R.drawable.face_blowing_a_kiss,"face_blowing_a_kiss"));
        list.add(new StickerModel(R.drawable.face_exhaling,"face_exhaling"));
        list.add(new StickerModel(R.drawable.face_in_clouds,"face_in_clouds"));
        list.add(new StickerModel(R.drawable.face_savoring_food,"face_savoring_food"));
        list.add(new StickerModel(R.drawable.face_with_hand_over_mouth,"face_with_hand_over_mouth"));
        list.add(new StickerModel(R.drawable.face_with_medical_mask,"face_with_medical_masks"));
        list.add(new StickerModel(R.drawable.face_with_raised_eyebrow,"face_with_raised_eyebrow"));
        list.add(new StickerModel(R.drawable.face_with_rolling_eyes,"face_with_rolling_eyes"));
        list.add(new StickerModel(R.drawable.face_with_tears_of_joy,"face_with_tears_of_joy"));
        list.add(new StickerModel(R.drawable.face_with_tongue,"face_with_tongue"));
        list.add(new StickerModel(R.drawable.face_without_mouth,"face_without_mouth"));
        list.add(new StickerModel(R.drawable.grimacing_face,"grimacing_face"));
        list.add(new StickerModel(R.drawable.grinning_face_with_big_eyes,"grinning_face_with_big_eyes"));
        list.add(new StickerModel(R.drawable.grinning_face_with_smiling_eyes,"grinning_face_with_smiling_eyes"));
        list.add(new StickerModel(R.drawable.grinning_face_with_sweat,"grinning_face_with_sweat"));
        list.add(new StickerModel(R.drawable.grinning_face,"grinning_face"));
        list.add(new StickerModel(R.drawable.grinning_squinting_face,"grinning_squinting_face"));
        list.add(new StickerModel(R.drawable.hugging_face,"hugging_face"));
        list.add(new StickerModel(R.drawable.face_with_tongue_close_eyes,"face_with_tongue_close_eyes"));
        list.add(new StickerModel(R.drawable.kissing_face_with_closed_eyes,"kissing_face_with_closed_eyes"));
        list.add(new StickerModel(R.drawable.kissing_face_with_smiling_eyes,"kissing_face_with_smiling_eyes"));
        list.add(new StickerModel(R.drawable.kissing_face,"kissing_face"));
        list.add(new StickerModel(R.drawable.lying_face,"lying_face"));
        list.add(new StickerModel(R.drawable.money_mouth_face,"money_mouth_face"));
        list.add(new StickerModel(R.drawable.neutral_face,"neutral_face"));
        list.add(new StickerModel(R.drawable.pensive_face,"pensive_face"));
        list.add(new StickerModel(R.drawable.relieved_face,"relieved_face"));
        list.add(new StickerModel(R.drawable.rolling_on_the_floor_laughing,"rolling_on_the_floor_laughing"));
        list.add(new StickerModel(R.drawable.shushing_face,"shushing_face"));
        list.add(new StickerModel(R.drawable.sleeping_face,"sleeping_face"));
        list.add(new StickerModel(R.drawable.sleepy_face,"sleepy_face"));
        list.add(new StickerModel(R.drawable.slightly_smiling_face,"slightly_smiling_face"));
        list.add(new StickerModel(R.drawable.smiling_face_with_halo,"smiling_face_with_halo"));
        list.add(new StickerModel(R.drawable.smiling_face_with_heart_eyes,"smiling_face_with_heart_eyes"));
        list.add(new StickerModel(R.drawable.smiling_face_with_hearts,"smiling_face_with_hearts"));
        list.add(new StickerModel(R.drawable.smiling_face_with_smiling_eyes,"smiling_face_with_smiling_eyes"));
        list.add(new StickerModel(R.drawable.smiling_face_with_tear,"smiling_face_with_tear"));
        list.add(new StickerModel(R.drawable.smiling_face,"smiling_face"));
        list.add(new StickerModel(R.drawable.smirking_fac,"smirking_fac"));
        list.add(new StickerModel(R.drawable.star_struck,"star_struck"));
        list.add(new StickerModel(R.drawable.thinking_face,"thinking_face"));
        list.add(new StickerModel(R.drawable.unamused_fac,"unamused_fac"));
        list.add(new StickerModel(R.drawable.upside_down_face,"upside_down_face"));
        list.add(new StickerModel(R.drawable.winking_face_with_tongue,"winking_face_with_tongue"));
        list.add(new StickerModel(R.drawable.winking_face,"winking_face"));
        list.add(new StickerModel(R.drawable.zany_face,"zany_face"));
        list.add(new StickerModel(R.drawable.zipper_mouth_face,"zipper_mouth_face"));

        return list;
    }

}
