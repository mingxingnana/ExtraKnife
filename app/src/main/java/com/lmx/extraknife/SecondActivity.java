package com.lmx.extraknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.annotations.Extra;
import com.example.api.Lmx;

public class SecondActivity extends AppCompatActivity {

    @Extra
    String orderid;

    @Extra("id")
    int oId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Lmx.bind(this);

        TextView textView = findViewById(R.id.text);

        textView.setText(orderid + "\n" + oId);
    }
}
