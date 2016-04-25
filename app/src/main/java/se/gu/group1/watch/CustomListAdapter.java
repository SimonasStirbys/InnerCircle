package se.gu.group1.watch;

import android.app.LauncherActivity;
import android.graphics.Color;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import android.widget.ListView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter{

    LayoutInflater inflater;
    ArrayList<Holder> list;
    public CustomListAdapter(MainActivity mainActivity, ArrayList<Holder> list) {
        inflater = LayoutInflater.from(mainActivity);
        this.list =list;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Holder h = list.get(position);
        holder.tv.setText(h.getTitle());
        holder.tv.setOnClickListener(mClickListener);
        holder.tv.setBackgroundColor(h.getColor());
        holder.tv.setTag(position);
        return convertView;
    }

    private OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            int pos = (Integer) v.getTag();
            Holder h = (Holder) list.get(pos);

            if(h.getColor()==Color.WHITE){
                h.setColor(Color.CYAN);
                CustomListAdapter.this.notifyDataSetChanged();
            }else{
                h.setColor(Color.WHITE);
                CustomListAdapter.this.notifyDataSetChanged();
            }
        }
    };

    static class ViewHolder {
        TextView tv;
    }
}
