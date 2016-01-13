package com.appman.appmanager.async;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.appman.appmanager.models.SmsInfo;
import com.appman.appmanager.activities.SmsActivity;
import com.appman.appmanager.adapter.SmsAdapter;

import java.util.ArrayList;

/**
 * Created by Rudraksh on 23-Dec-15.
 */
public class LoadSmsInBackground extends AsyncTask<Void, String, Void>{

    private Activity mActivity;

    public LoadSmsInBackground(Activity activity){
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SmsActivity.progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {


        SmsActivity.arrayList = new ArrayList<SmsInfo>();
        SmsActivity.arrayList =  getAllSms("inbox");

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        SmsActivity.progressWheel.setVisibility(View.GONE);
        if (SmsActivity.arrayList.size() < 0){
            Toast.makeText(mActivity, "No SMS found on your device", Toast.LENGTH_SHORT).show();
        }else{
            int count = SmsActivity.arrayList.size();
            SmsActivity.listViewSms.setVisibility(View.VISIBLE);
            SmsActivity.txtSmsCount.setText(String.valueOf(count));
            SmsActivity.listViewSms.setAdapter(new SmsAdapter(mActivity, SmsActivity.arrayList));
        }
    }

    /**
     * THIS METHOD WILL RETRIEVE ALL THE SMS FROM THE INBOX AND ADD IT TO ARRAYLIST
     * @param folderName
     * @return
     */

    public ArrayList<SmsInfo> getAllSms(String folderName) {
        ArrayList<SmsInfo> lstSms = new ArrayList<SmsInfo>();
        SmsInfo smsInfo = new SmsInfo();
        Uri message = Uri.parse("content://sms");
        ContentResolver cr = mActivity.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        mActivity.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                smsInfo = new SmsInfo();
                smsInfo.setId(Integer.parseInt(c.getString(c.getColumnIndexOrThrow("_id"))));
                smsInfo.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                smsInfo.setType(Integer.parseInt(c.getString(c.getColumnIndexOrThrow("type"))));
                System.out.println("TYPE-->" + smsInfo.getType());
                smsInfo.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                smsInfo.setRead(c.getString(c.getColumnIndex("read")));
                smsInfo.setDate(Long.parseLong(c.getString(c.getColumnIndexOrThrow("date"))));

                lstSms.add(smsInfo);
                c.moveToNext();
            }
        }

        return lstSms;
    }
}