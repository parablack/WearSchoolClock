package net.parablack.clocktest.json.subjects;

import net.parablack.clocktest.json.subjects.WearEventSubject;

/**
 * Created by Simon on 16.09.2015.
 */
public class SimpleSubject implements WearEventSubject{
    private String text;

    public SimpleSubject(String text) {
        this.text = text;
    }

    @Override
    public String getId() {
        return "$simple";
    }

    @Override
    public String getDisplayName() {
        return text;
    }
}
