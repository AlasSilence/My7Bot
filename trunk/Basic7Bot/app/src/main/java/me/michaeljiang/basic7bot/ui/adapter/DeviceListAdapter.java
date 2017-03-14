package me.michaeljiang.basic7bot.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.michaeljiang.basic7bot.R;

public class DeviceListAdapter extends BaseAdapter {
    private ArrayList<DeviceListItem> list;
    private LayoutInflater mInflater;

    public DeviceListAdapter(Context context, ArrayList<DeviceListItem> l) {
    	list = l;
		mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder = null;
        DeviceListItem item=list.get(position);
        if(convertView == null){
        	convertView = mInflater.inflate(R.layout.list_item, null);
        	viewHolder=new ViewHolder(
        			(View) convertView.findViewById(R.id.list_child),
        			(TextView) convertView.findViewById(R.id.chat_msg)
        	       );
        	convertView.setTag(viewHolder);
        }
        else{
        	viewHolder = (ViewHolder)convertView.getTag();
        }       
        
        if(item.isSiri())
        {
        	viewHolder.child.setBackgroundResource(R.drawable.msgbox_rec);
        }
        else 
        {
        	viewHolder.child.setBackgroundResource(R.drawable.msgbox_send);
        }
        viewHolder.msg.setText(item.getMessage());
        
        return convertView;
    }
    
    class ViewHolder {
    	  protected View child;
          protected TextView msg;
  
          public ViewHolder(View child, TextView msg){
              this.child = child;
              this.msg = msg;
              
          }
    }
}
