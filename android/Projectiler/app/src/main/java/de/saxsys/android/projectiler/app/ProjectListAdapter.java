package de.saxsys.android.projectiler.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by stefan.heinze on 26.05.2014.
 */
public class ProjectListAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;

    public ProjectListAdapter(Context context, List<String> itemList) {
        super(context, R.layout.project_list_adapter, R.id.tv_project_name, itemList);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.project_list_adapter, parent, false);
            lookup(view);
        }

        String item = getItem(position);

        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvProjectName.setText(item);

        return view;
    }

    private void lookup(final View view) {
        final TextView tvProjectName = (TextView) view.findViewById(R.id.tv_project_name);

        final ViewHolder holder = new ViewHolder(tvProjectName);
        view.setTag(holder);
    }


    private static class ViewHolder {
        TextView tvProjectName;

        private ViewHolder(final TextView tvProjectName) {
            this.tvProjectName = tvProjectName;
        }

    }
}
