package com.nousanimation.nousreview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkAdapter extends ArrayAdapter<Work> {

    private final LayoutInflater inflater;
    private final int layoutResource;
    private final ArrayList<Work> works;

    public WorkAdapter(Context context,
                       int resource,
                       ArrayList<Work> objects) {
        super(context, resource, objects);

        inflater = LayoutInflater.from(context);
        layoutResource = resource;
        works = objects;
    }

    @Override
    public int getCount() {
        return works.size();
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable final View convertView,
                        @NonNull ViewGroup parent) {

        final View view = inflater.inflate(layoutResource,
                parent,
                false);

        TextView workName = view.findViewById(R.id.work_name);
        TextView workProduction = view.findViewById(R.id.production_name);
        TextView workDate = view.findViewById(R.id.creation_date);

        Work work = works.get(position);
        workName.setText(work.getName());
        workProduction.setText(work.getProduction());
        workDate.setText(work.getUpload_date());

        return view;
    }
}

