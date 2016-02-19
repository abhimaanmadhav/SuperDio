package barqsoft.footballscores.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;

/**
 * Implementation of App Widget functionality.
 */
public class FootballAppWidget extends AppWidgetProvider
{
    public final static String ACTION_DATA_UPDATED = "com.udacity.data_updated";
    public final static String ACTION_DAY_CHANGE = "com.udacity.day_change";
    PendingIntent pendingIntent;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId)
        {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout
                    .football_app_widget);
            Intent intent = new Intent(context, widgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.wid_listview, intent);
            views.setEmptyView(R.id.wid_listview, R.id.widget_empty);
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
        {
            // There may be multiple widgets active, so update all of them
            Intent intent = new Intent(context, widgetService.class);
            context.startService(intent);
            for (int appWidgetId : appWidgetIds)
                {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            super.onUpdate(context, appWidgetManager, appWidgetIds);
        }

    @Override
    public void onEnabled(Context context)
        {
            // Enter relevant functionality for when the first widget is created
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 30);
            pendingIntent = PendingIntent.getBroadcast(context, 99, new Intent
                    (FootballAppWidget.ACTION_DAY_CHANGE), PendingIntent.FLAG_UPDATE_CURRENT);
            // 8 hour window as i used need a trigger when the phone wakes up
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), c
                    .getTimeInMillis() + (8 * 60 * 60 * 100), pendingIntent);

        }

    @Override
    public void onDisabled(Context context)
        {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }

    @Override
    public void onReceive(Context context, Intent intent)
        {
            Log.d("onReceive", "action " + intent.getAction());
            if (ACTION_DATA_UPDATED.equalsIgnoreCase(intent.getAction()))
                {
                    Log.e("onReceive", "action " + intent.getAction());

                    updateWidget(context);
                } else if (ACTION_DAY_CHANGE.equalsIgnoreCase(intent.getAction()))
                {
                    //Alarm for day to change data a 0 hours
                    Log.e("onReceive", "action " + intent.getAction());
                    context.startService(new Intent(context, myFetchService.class));
                    updateWidget(context);
                }
            super.onReceive(context, intent);

        }

    void updateWidget(Context context)
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                    (context);
            ComponentName thisAppWidget = new ComponentName(context
                    .getPackageName(), FootballAppWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
}

