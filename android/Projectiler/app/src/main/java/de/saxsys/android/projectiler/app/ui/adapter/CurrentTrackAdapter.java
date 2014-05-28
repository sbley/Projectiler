package de.saxsys.android.projectiler.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.projectiler.crawler.Booking;

/**
 * Created by stefan.heinze on 23.05.2014.
 */
public class CurrentTrackAdapter extends ArrayAdapter<Booking> {
    private final LayoutInflater inflater;

    public CurrentTrackAdapter(Context context, List<Booking> objects) {
        super(context, R.layout.current_track_adapter, R.id.tv_project_name, objects);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.current_track_adapter, parent, false);
            lookup(view);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();

        Booking item = getItem(position);

        holder.tvProjectName.setText(item.getProjectName());
        holder.tvStartDate.setText(item.getStartTime());
        holder.tvEndDate.setText(item.getEndTime());

        return view;
    }

    private void lookup(final View view) {
        final TextView tvProjectName = (TextView) view.findViewById(R.id.tvProjectName);
        final TextView tvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
        final TextView tvEndDate = (TextView) view.findViewById(R.id.tvEndDate);

        final ViewHolder holder = new ViewHolder(tvProjectName, tvStartDate, tvEndDate);
        view.setTag(holder);
    }

    private static class ViewHolder {
        TextView tvProjectName;
        TextView tvStartDate;
        TextView tvEndDate;

        private ViewHolder(final TextView tvProjectName, final TextView tvStartDate, final TextView tvEndDate) {
            this.tvProjectName = tvProjectName;
            this.tvStartDate = tvStartDate;
            this.tvEndDate = tvEndDate;
        }

    }
}
