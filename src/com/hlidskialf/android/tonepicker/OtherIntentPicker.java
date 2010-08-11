package com.hlidskialf.android.tonepicker;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.List;


public class OtherIntentPicker extends ListActivity
{
    private ArrayAdapter mAdapter;
    private ArrayList<OtherApp> mApps;
    private LayoutInflater mInflater;

    private static final int REQUEST_OTHER_APP=1;

    private class OtherApp {
        String name;
        Drawable icon;
        Intent intent;

        public String toString() {
          return this.name;
        }
    }

    private class ViewHolder {
      public TextView label;
      public ImageView icon;
    }

    private class OtherAppAdapter extends ArrayAdapter
    {
      public OtherAppAdapter()
      {
        super(OtherIntentPicker.this, R.layout.otherpicker_child, android.R.id.text1, mApps);
      }
      public View getView(int position, View convertView, ViewGroup parent)
      {
        ViewHolder holder;
        OtherApp app = mApps.get(position);

        if (convertView == null) {
          convertView = mInflater.inflate(R.layout.otherpicker_child, null);
          holder = new ViewHolder();
          holder.icon = (ImageView)convertView.findViewById(android.R.id.icon1);
          holder.label = (TextView)convertView.findViewById(android.R.id.text1);
          convertView.setTag(holder);
        } else {
          holder = (ViewHolder)convertView.getTag();
        }

        holder.label.setText(app.name);
        holder.icon.setImageDrawable(app.icon);

        return convertView;
      }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherpicker);

        mInflater = getLayoutInflater();

        mApps = _find_apps(new ComponentName(this, TonePicker.class));

        //mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mApps);
        mAdapter = new OtherAppAdapter();
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        OtherApp app = mApps.get(position);
        startActivityForResult(app.intent, REQUEST_OTHER_APP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      if (resultCode != RESULT_OK) return;

      if (requestCode == REQUEST_OTHER_APP) {
        setResult(RESULT_OK, data);
        finish();
      }
    }

    private ArrayList<OtherApp> _find_apps(ComponentName exclude) 
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT) 
                              .setType("audio/*") 
                              .addCategory(Intent.CATEGORY_OPENABLE);
        PackageManager package_manager = getPackageManager();
        List<ResolveInfo> list = package_manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list == null) return null;

        ArrayList<OtherApp> others = new ArrayList<OtherApp>();
        int N = list.size();
        for (int i=0; i<N; i++) {
            ResolveInfo ri = list.get(i);
            if (exclude != null && (ri.activityInfo.packageName.equals(exclude.getPackageName())
                    || ri.activityInfo.name.equals(exclude.getClassName()))) {
                list.remove(i);
                N--;
                continue;
            }

            OtherApp app = new OtherApp();
            app.icon = ri.loadIcon(package_manager);
            app.name = ri.loadLabel(package_manager).toString();

            intent.setComponent( new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name) );
            app.intent = (Intent)intent.clone();
            others.add(app);
        }

        return others;
    }


}
