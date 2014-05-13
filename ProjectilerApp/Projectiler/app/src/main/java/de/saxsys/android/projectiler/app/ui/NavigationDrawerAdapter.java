package de.saxsys.android.projectiler.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.saxsys.android.projectiler.app.R;

/**
 * Created by stefan.heinze on 12.05.2014.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;
    private final int activeElement;

    public NavigationDrawerAdapter(Context context, List<String> objects, int activeElement) {
        super(context, R.layout.adapter_navigation_drawer, objects);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activeElement = activeElement;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.adapter_navigation_drawer, parent, false);
            lookup(view);
        }

        String projectName = getItem(position);

        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvProjectName.setText(projectName);

        if(position == activeElement){
            holder.ivActive.setImageResource(R.drawable.online_icon);
        }else{
            holder.ivActive.setImageBitmap(null);
        }


        return view;
    }


    private void lookup(final View view) {
        final TextView tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);
        final ImageView ivActive = (ImageView) view.findViewById(R.id.iv_active);

        final ViewHolder holder = new ViewHolder(tvProjectName, ivActive);
        view.setTag(holder);
    }

    private static class ViewHolder {
        TextView tvProjectName;
        ImageView ivActive;

        private ViewHolder(final TextView tvProjectName, final ImageView ivActive) {
            this.tvProjectName = tvProjectName;
            this.ivActive = ivActive;
        }

    }
}
