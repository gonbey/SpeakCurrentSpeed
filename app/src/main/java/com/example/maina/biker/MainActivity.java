package com.example.maina.biker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.TimeZone;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maina.biker.services.GPSConfiguration;
import com.example.maina.biker.utils.FindViewAssist;
import com.example.maina.biker.utils.LocationUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    GPSConfiguration gpsConf;
    FindViewAssist f = new FindViewAssist(this);
    Handler h = new Handler();
    Date startTime = new Date();
    Timer timer;
    TextToSpeech tts;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Text-To-Speech設定
        tts = new TextToSpeech(this, (status)-> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.JAPAN);
            } else {
                throw new RuntimeException("TTS INITALIZE ERROR?");
            }
        });


        // GPS設定
        LocationManager lMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        lMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000/**ms*/, 0/**m*/, getLocationListener());

        // タイマー処理
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                h.post(() -> {
                    /** 合計時間 */
                    Date currentDate = new Date();
                    long dTime = currentDate.getTime() - startTime.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                    Long min = TimeUnit.MILLISECONDS.toMinutes(dTime);
                    Long sec = TimeUnit.MILLISECONDS.toSeconds(dTime) % 60L;
                    String minsec = String.format("%02d:%02d", min, sec);
                    TextView tlTimeValue = f.get(R.id.ammountTimeValue);
                    tlTimeValue.setText(minsec);

                    /** 現在時刻 */
                    TextView tvCurrentTimeValue = f.get(R.id.currentTimeValue);
                    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss", Locale.JAPAN);
                    tvCurrentTimeValue.setText(sdfTime.format(currentDate));
                });
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    /** GPS更新時の処理 */
    private Location beforeLocation;
    private double ammountDist = 0;
    private LocationListener getLocationListener() {
        return new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                /** 速度 */
                if (beforeLocation == null) {
                    beforeLocation = location;
                    return;
                }
                float dLengthM = (float)LocationUtils.distance(beforeLocation.getLatitude(), location.getLatitude(), beforeLocation.getLongitude(), location.getLongitude(), 0f,0f);
                Log.d("Length(cm)", Float.toString(dLengthM * 100f));
                float dLengthKM = dLengthM / 1000f;
                ammountDist += dLengthKM;
                long currentTime = location.getTime();
                /** debug */
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd E HH:mm:ss.SSS");
                Log.d("CurrentDateTime", sdf.format(new Date(currentTime)));

                long beforeTime = beforeLocation.getTime();
                Log.d("beforeDateTime", sdf.format(new Date(beforeTime)));
                long dTimeMs =  currentTime - beforeTime;
                float dTimeS = dTimeMs / 1000f;
                float dTimeM = dTimeS / 60f;
                float dTimeH = dTimeM / 60f;
                float speedPerKM = dLengthKM / dTimeH;
                h.post(() -> {
                    TextView tvSpeedValue = f.get(R.id.textViewSpeedValue);
                    tvSpeedValue.setText(new BigDecimal(speedPerKM).setScale(2, BigDecimal.ROUND_DOWN).toString());
                    TextView tvAmmoutValue = f.get(R.id.ammountDistValue);
                    tvAmmoutValue.setText(new BigDecimal(ammountDist).setScale(2, BigDecimal.ROUND_DOWN).toString());
                });
                beforeLocation = location;

                /** TTS */
                tts.stop();
                tts.speak(new BigDecimal(speedPerKM).setScale(0, BigDecimal.ROUND_DOWN).toString(), TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
}
