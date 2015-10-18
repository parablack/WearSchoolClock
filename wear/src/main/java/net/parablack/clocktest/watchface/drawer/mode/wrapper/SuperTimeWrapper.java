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

    public int getTotalMinutes(){
        return getBegin().getMinutes() + getEnd().getMinutes() + 1;           // CURRENT ONE!
    }

    public double getPercentageDone(){
        double totalSec = getTotalMinutes() * 60;
        double done = totalSec - end.toSeconds();
      //  System.out.println("total: " +totalSec + " done " + done);
        return (done / totalSec);
    }


    // Simple TW

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

        public long toSeconds(){
            return (hours * 60 * 60) + minutes * 60 + seconds;
        }

        @Override
        public String toString() {
            return "TimeWrapper[h:" + getHours() + ";m=" + getMinutes() + ";s=" + getSeconds();
        }
    }

}
