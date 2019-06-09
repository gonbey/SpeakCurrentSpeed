package com.example.maina.biker.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

public class GPSConfiguration {
    AppCompatActivity c;
    LocationManager lMan;

    @SuppressLint("MissingPermission")
    public GPSConfiguration(AppCompatActivity c, LocationListener listener) throws RuntimeException {
            this.c = c;

    }
}
