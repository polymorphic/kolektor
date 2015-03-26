package com.microWorkflow.kolektor;

/**
 * Created by dam on 3/26/15.
 */
import org.collectd.api.DataSource;

import java.util.ArrayList;
import java.util.List;

public class Measurement {
    public final String type;
    public final List<Reading> readings;

    protected Measurement(String type, int size) {
        this.type = type;
        readings = new ArrayList<>(size);
    }

    public void add(Reading r) {
        readings.add(r);
    }

    public static Measurement build(String type, List<DataSource> sources, List<Number> values) {
        Measurement m = new Measurement(type, sources.size());
        for (int i=0; i<sources.size(); i++) {
            DataSource source = sources.get(i);
            String measurementType = source.getType() == 0 ? "counter" : "gauge";
            Reading r = new Reading( source.getName()
                    , measurementType
                    , values.get(i)
            );
            m.add(r);
        }
        return m;
    }
}

