package com.microWorkflow.kolektor;

/**
 * Created by dam on 3/26/15.
 */
public class Reading {
    public final String name;
    public final String type;
    public final Number value;

    public Reading(String name, String type, Number value) {
        this.name= name;
        this.type = type;
        this.value = value;
    }
}

