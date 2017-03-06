package com.vincent.myfitnesslog;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by vincent on 3/5/17.
 */

public class UpdateWidgetService extends Service {

    private static final String LOG = "widgetService";

    // my changes - part 1 - beginning
    private static final String BUTTON_ONE_CLICKED = "com.yourpackage.BUTTON_ONE_CLICKED";
    private static final String BUTTON_TWO_CLICKED = "com.yourpackage.BUTTON_TWO_CLICKED";
    private static final String BUTTON_THREE_CLICKED = "com.yourpackage.BUTTON_THREE_CLICKED";
    private static final String BUTTON_FOUR_CLICKED = "com.yourpackage.BUTTON_FOUR_CLICKED";
// my changes - part 1 - end


    @Override
    public void onStart(Intent intent, int startId) {

        Log.i(LOG, "Called and started");

        // my changes - part 2 - beginning
        if(BUTTON_ONE_CLICKED.equals(intent.getAction())) {
            // do what should be done if button One has been clicked
        }
        if(BUTTON_TWO_CLICKED.equals(intent.getAction())) {
            // do what should be done if button Two has been clicked
        }
        if(BUTTON_THREE_CLICKED.equals(intent.getAction())) {
            // do what should be done if button Three has been clicked
        }
        if(BUTTON_FOUR_CLICKED.equals(intent.getAction())) {
            // do what should be done if button Four has been clicked
        }
        // my changes - part 2 - end

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        ComponentName thisWidget = new ComponentName(getApplicationContext(), com.vincent.myfitnesslog.SimpleWidgetProvider.class);
        int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
        Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
        Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));

        for (int widgetId : allWidgetIds) {
            // create some random data

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.simple_widget);
            Log.w("WidgetExample", String.valueOf(1));
            // Set the text
            remoteViews.setTextViewText(R.id.displayTextView, "Random: " + String.valueOf(1));

            // my changes - part 3 - beginning
            Context context = getApplicationContext();

            Intent buttonOneIntent = new Intent(context, UpdateWidgetService.class);
            Intent buttonTwoIntent = new Intent(context, UpdateWidgetService.class);
            Intent buttonThreeIntent = new Intent(context, UpdateWidgetService.class);
            Intent buttonFourIntent = new Intent(context, UpdateWidgetService.class);

            // set action
            buttonOneIntent.setAction(BUTTON_ONE_CLICKED);
            buttonTwoIntent.setAction(BUTTON_TWO_CLICKED);
            buttonThreeIntent.setAction(BUTTON_THREE_CLICKED);
            buttonFourIntent.setAction(BUTTON_FOUR_CLICKED);

            // put widgetId
            buttonOneIntent.putExtra("widgetId", widgetId);
            buttonTwoIntent.putExtra("widgetId", widgetId);
            buttonThreeIntent.putExtra("widgetId", widgetId);
            buttonFourIntent.putExtra("widgetId", widgetId);

            // make these intents unique to avoid collisions
//            buttonOneIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttonone/widgetid"), String.valueOf(widgetId)));
//            buttonTwoIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttontwo/widgetid"), String.valueOf(widgetId)));
//            buttonThreeIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttonthree/widgetid"), String.valueOf(widgetId)));
//            buttonFourIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttonfour/widgetid"), String.valueOf(widgetId)));

            // pending intents
            PendingIntent buttonOnePendingIntent = PendingIntent.getService(context, 0, buttonOneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonTwoPendingIntent = PendingIntent.getService(context, 0, buttonTwoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonThreePendingIntent = PendingIntent.getService(context, 0, buttonThreeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonFourPendingIntent = PendingIntent.getService(context, 0, buttonFourIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // register onClickListeners to your buttons
//            remoteViews.setOnClickPendingIntent(R.id.button_one, buttonOnePendingIntent);
//            remoteViews.setOnClickPendingIntent(R.id.button_two, buttonTwoPendingIntent);
//            remoteViews.setOnClickPendingIntent(R.id.button_three, buttonThreePendingIntent);
//            remoteViews.setOnClickPendingIntent(R.id.button_four, buttonFourPendingIntent);
            // my changes - part 3 - end

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
