package com.davidbarron.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;

public class WeatherAlarmReceiver extends BroadcastReceiver {
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        DatabaseDriver databaseDriver = new DatabaseDriver(context);
        Cursor cursor = databaseDriver.getHomeLocation();
        cursor.moveToFirst();
        String entry = cursor.getString(cursor.getColumnIndex("city")) + ", " + cursor.getString(cursor.getColumnIndex("state"));
        cursor.close();
        databaseDriver.close();
        new FetchWeatherTask().execute(entry);

    }
    private void sendNotice(WeatherItem item) {
        Notification notification = new NotificationCompat.Builder(mContext)
            .setSmallIcon(mContext.getResources().getIdentifier("weather_vclouds_" + item.getWeather_number(), "mipmap", mContext.getPackageName()))
            .setContentTitle("Weather for " + item.getCity())
            .setContentText(item.getWeather_value() + " " + item.getTemp_value())
            .build();
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001,notification);
    }
    public class FetchWeatherTask extends AsyncTask<String,Void,WeatherItem> {

        @Override
        protected WeatherItem doInBackground(String...params) {
            if(params!=null && params.length>0) {
                String loc = params[0];
                return new WeatherFetcher().fetchWeather(loc);
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeatherItem item) {
            if(item != null) {
                sendNotice(item);
            }
        }
    }
}
