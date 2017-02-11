package com.vincent.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.BaseBundle;

import java.util.Random;

public class SimpleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        WorkRoutine rountie = new WorkRoutine("Test Rountie");

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            int number = new Random().nextInt((rountie.Excerises.length - 1) + 1);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.simple_widget);


            String text = rountie.Excerises[number].Name;
            remoteViews.setTextViewText(R.id.textView, text);

            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);


            /*AppWidgetManager appWidgetManager;
            int widgetId;
            Bundle myOptions = appWidgetManager.getAppWidgetOptions (widgetId);

            // Get the value of OPTION_APPWIDGET_HOST_CATEGORY
            int category = myOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);

            // If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
            boolean isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

            int baseLayout = isKeyguard ? R.layout.keyguard_widget_layout : R.layout.widget_layout;*/
        }
    }
}