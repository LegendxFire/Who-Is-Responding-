package com.fireapps.whoisresponding;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Austin on 3/31/2015.
 */
public class RespondingListAdapter extends ArrayAdapter<MemberObject> {
    TextView name, title, resTo;
    private Context mContext;
    private List<MemberObject> mList;

    public RespondingListAdapter(Context context, List<MemberObject> objects) {
        super(context, R.layout.responding_list_item, objects);
        this.mContext = context;
        this.mList = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.responding_list_item, null);
        }

        ParseUser memberObject = mList.get(position);

        name = (TextView) convertView.findViewById(R.id.member_list_name);
        title = (TextView) convertView.findViewById(R.id.member_list_title);
        resTo = (TextView) convertView.findViewById(R.id.res_list_to);

        name.setText(memberObject.get("name").toString());
        title.setText(memberObject.get("position").toString());
        resTo.setText(memberObject.get("respondingTo").toString());

        return convertView;
    }
}

