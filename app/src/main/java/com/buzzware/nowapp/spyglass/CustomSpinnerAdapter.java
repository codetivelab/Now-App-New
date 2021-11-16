package com.buzzware.nowapp.spyglass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.buzzware.nowapp.R;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;

    ArrayList<Person> persons;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, ArrayList<Person> persons) {
        this.context = applicationContext;
       persons = persons;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.suggestions_spinner_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);

        Glide.with(context).load(persons.get(i).getPictureURL()).into(icon);
        names.setText(persons.get(i).getFullName());

        return view;
    }
}