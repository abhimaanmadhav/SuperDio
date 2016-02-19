package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;

public class widgetService extends RemoteViewsService
{
    public widgetService()
        {
        }
int mAppWidgetId;
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
        {
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            return new widgetRemoteViewFactory(this);
        }

    class widgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory
    {
        Context context;
        Cursor mCursor;

        widgetRemoteViewFactory(Context context)
            {
                this.context = context;
            }

        @Override
        public void onCreate()
            {

            }

        @Override
        public void onDataSetChanged()
            {
                if (mCursor != null)
                    {
                        mCursor.close();
                    }
                Date fragmentdate = new Date(System.currentTimeMillis());
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                mCursor = getContentResolver().query(DatabaseContract.scores_table
                        .buildScoreWithDate(), null, null, new String[]{mformat.format
                        (fragmentdate)}, null);
                Log.e("this","ondata set changed "+mCursor.getCount());
            }

        @Override
        public void onDestroy()
            {
                if (mCursor != null) {
                    mCursor.close();
                }
            }

        @Override
        public int getCount()
            {
                return mCursor.getCount();
            }

        @Override
        public RemoteViews getViewAt(int position)
            {
                mCursor.moveToPosition(position);
                RemoteViews view = new RemoteViews(context.getPackageName(), R.layout
                        .widget_list_item);
                view.setTextViewText(R.id.home_name, mCursor.getString(scoresAdapter.COL_HOME));
                view.setTextViewText(R.id.away_name, mCursor.getString(scoresAdapter.COL_AWAY));
                Log.e("away name","name "+ mCursor.getString(scoresAdapter.COL_AWAY));
                view.setTextViewText(R.id.data_textview, mCursor.getString(scoresAdapter
                        .COL_MATCHTIME));
                view.setTextViewText(R.id.score_textview, Utilies.getScores(mCursor.getInt
                        (scoresAdapter.COL_AWAY_GOALS), (mCursor.getInt
                        (scoresAdapter.COL_HOME_GOALS))));
                view.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                        mCursor.getString(scoresAdapter.COL_HOME)));
                view.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                        mCursor.getString(scoresAdapter.COL_AWAY)));
                Log.e("remote view adapter", "getViewAt :"+mCursor.getString(scoresAdapter.COL_HOME));
                return view;
            }

        @Override
        public RemoteViews getLoadingView()
            {
                Log.e("remote view adapter", "getLoadingView");
                return null;
            }

        @Override
        public int getViewTypeCount()
            {
                return 1;
            }

        @Override
        public long getItemId(int position)
            {
                return 0;
            }

        @Override
        public boolean hasStableIds()
            {
                return false;
            }

    }
}