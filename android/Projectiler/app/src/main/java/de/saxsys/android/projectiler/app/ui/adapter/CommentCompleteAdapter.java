package de.saxsys.android.projectiler.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.generatedmodel.Comment;

/**
 * Created by stefan.heinze on 17.06.2014.
 */
public class CommentCompleteAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;

    public CommentCompleteAdapter(Context context, List<String> objects) {
        super(context, R.layout.auto_completion_adapter, R.id.tvComment , objects);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.auto_completion_adapter, parent, false);
            lookup(view);
        }

        final ViewHolder holder = (ViewHolder) view.getTag();

        String item = getItem(position);

        holder.tvComment.setText(item);

        return view;
    }

    private void lookup(final View view) {
        final TextView tvComment = (TextView) view.findViewById(R.id.tvComment);

        final ViewHolder holder = new ViewHolder(tvComment);
        view.setTag(holder);
    }

    private static class ViewHolder {
        TextView tvComment;

        private ViewHolder(final TextView tvComment) {
            this.tvComment = tvComment;
        }

    }

}
