package de.saxsys.android.projectiler.app.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.saxsys.android.projectiler.app.R;

/**
 * Created by stefan.heinze on 12.05.2014.
 */
public class NavigationDrawerAdapter extends BaseExpandableListAdapter {
    private final LayoutInflater inflater;
    private Context context;
    private final int activeElement;
    private List<String> groups;
    private List<String> projects;
    private List<String> navigation;

    public NavigationDrawerAdapter(Context context, List<String> objects, int activeElement) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activeElement = activeElement;
        this.projects = objects;
        this.context = context;
        groups = new ArrayList<String>();

        String[] stringArray = context.getResources().getStringArray(R.array.navigation_drawer_groups);

        for(int i = 0; i < stringArray.length; i++){
            groups.add(stringArray[i]);
        }
        navigation = new ArrayList<String>();
        navigation.add(context.getString(R.string.current_tracks));
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_group_item, null);
        }

        TextView tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);

        tvGroupName.setText(groups.get(groupPosition));

        ExpandableListView eLV = (ExpandableListView) viewGroup;
        eLV.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final String children = (String) getChild(groupPosition, childPosition);
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.adapter_navigation_drawer, viewGroup, false);
            lookup(view);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvProjectName.setText(children);


        if(groupPosition == 1){

            // kein Projekt gewaehlt
            if(activeElement == -1){
                holder.tvProjectName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                holder.tvProjectName.setTextColor(context.getResources().getColor(android.R.color.black));
            }else{
                if(childPosition == activeElement){
                    holder.tvProjectName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    holder.tvProjectName.setTextColor(context.getResources().getColor(android.R.color.black));
                }else{
                    holder.tvProjectName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    holder.tvProjectName.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                }
            }

        }else{
            holder.tvProjectName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.tvProjectName.setTextColor(context.getResources().getColor(android.R.color.black));
        }


        return view;
    }

    private void lookup(final View view) {
        final TextView tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);

        final ViewHolder holder = new ViewHolder(tvProjectName);
        view.setTag(holder);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(i == 0){
            return navigation.size();
        }else{
            return projects.size();
        }
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        if(i == 0){
            return navigation.get(i2);
        }else{
            return projects.get(i2);
        }
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    private static class ViewHolder {
        TextView tvProjectName;

        private ViewHolder(final TextView tvProjectName) {
            this.tvProjectName = tvProjectName;
        }

    }
}
