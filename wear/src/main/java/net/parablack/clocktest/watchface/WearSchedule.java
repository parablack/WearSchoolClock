package net.parablack.clocktest.watchface;

/**
 * Created by Simon on 16.09.2015.
 */
public interface WearSchedule {

    void reload();

    WearEvent getCurrent();

    WearEvent getNext();

}
