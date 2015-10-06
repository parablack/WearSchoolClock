package net.parablack.clocktest.watchface.drawer.mode.wrapper;


public class SuperTimeWrapper {

    private TimeWrapper begin, end;

    public SuperTimeWrapper(TimeWrapper begin, TimeWrapper end) {
        this.begin = begin;
        this.end = end;
    }

    public TimeWrapper getBegin() {
        return begin;
    }

    public TimeWrapper getEnd() {
        return end;
    }

    public static class TimeWrapper {

        private int hours, minutes, seconds;

        public TimeWrapper(int hours, int minutes, int seconds) {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }


        public int getHours() {
            return hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public int getSeconds() {
            return seconds;
        }

    }
}
