package com.hlidskialf.android.tonepicker;

import android.app.ListActivity;
import android.os.Bundle;

class OtherIntentPicker extends ListActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    /*
        Intent intent;
        intent = new Intent(Intent.ACTION_GET_CONTENT) .setType("audio/*") .addCategory(Intent.CATEGORY_OPENABLE);
        mContentIntents = _cache_intents(intent);
        intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        mPickerIntents = _cache_intents(intent);
        */
    }

/*
  private Object[] _cache_intents(Intent intent) {
      PackageManager package_manager = mContext.getPackageManager();
      List<ResolveInfo> list = package_manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
      if (list == null) return null;

      int N = list.size();
      ArrayList<AppCache> cache = new ArrayList<AppCache>(N);
      for (int i=0; i<N; i++) {
          ResolveInfo ri = list.get(i);
          if (mExcludeApp != null && (ri.activityInfo.packageName.equals(mExcludeApp.getPackageName())
                  || ri.activityInfo.name.equals(mExcludeApp.getClassName()))) {
              list.remove(i);
              N--;
              continue;
          }

          AppCache app = new AppCache();
          app.icon = ri.loadIcon(package_manager);
          app.name = ri.loadLabel(package_manager).toString();

          intent.setComponent( new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name) );
          app.intent = (Intent)intent.clone();
          cache.add(app);
      }

      return cache.toArray();
  }
  */
}
