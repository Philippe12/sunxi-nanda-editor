package com.llt.awse;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class DrawerAdapter extends ArrayAdapter<DrawerEntry>
{
    int layoutResourceId;
    final Context context;
    final DrawerEntry[] entries;

    DrawerAdapter(Context context, int layoutResourceId, DrawerEntry[] ents)
    {
        super(context, layoutResourceId, ents);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.entries = ents;
    }

    private class EntryHolder
    {
        ImageView vIcon;
        TextView vSectName;
        TextView vSectCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        EntryHolder holder = null;

        if(v == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(layoutResourceId, parent, false);

            holder = new EntryHolder();
            holder.vIcon = (ImageView)v.findViewById(R.id.sect_icon);
            holder.vSectName = (TextView)v.findViewById(R.id.sect_name);
            holder.vSectCount = (TextView)v.findViewById(R.id.sect_count);
            v.setTag(holder);
        }
        else
        {
            holder = (EntryHolder)v.getTag();
        }

        DrawerEntry e = entries[position];
        holder.vSectName.setText(e.szEntry);
        holder.vSectCount.setText(""+e.nCount);
        holder.vIcon.setImageResource(e.nIcon);

        return v;
    }


}