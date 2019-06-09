package com.example.maina.biker.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by maina on 2019/06/08.
 */

public class FindViewAssist {
    AppCompatActivity c;
    public FindViewAssist(AppCompatActivity c) {
        this.c = c;
    }
    public <T> T get(int component) {
        return (T)c.findViewById(component);
    }
}
