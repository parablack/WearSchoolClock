package net.parablack.schedulelib;

import org.json.JSONException;

public interface WearSchedule {
    /**
     * Reload the event
     */
    void reload() throws JSONException;

    /**
     *
     * @return The current event of the day
     */
    WearEvent getCurrent();


    /**
     *
     * @return The next event of the day, or an event on another day if theres no more today
     */
    WearEvent getNext();

}
