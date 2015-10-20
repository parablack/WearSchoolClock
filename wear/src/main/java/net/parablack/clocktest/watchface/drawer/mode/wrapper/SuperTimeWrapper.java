package net.parablack.clocktest.watchface.drawer.mode.wrapper;


public class SuperTimeWrapper {

    private TimeWrapper begin, end;

    public SuperTimeWrapper(TimeWrapper begin, TimeWrapper end) {
        this.begin = begin;
        this.end = end;
    }

    /**
     *
     * @return The begin-timewrapper
     */
    public TimeWrapper getBegin() {
        return begin;
    }


    /**
     *
     * @return The end-timewrapper
     */
    public TimeWrapper getEnd() {
        return end;
    }

    /**
     *
     * @return The total duration of the event in minutes
     */
    public int getTotalMinutes(){
        return getBegin().getMinutes() + getEnd().getMinutes() + 1;           // CURRENT ONE!
    }

    /**
     *
     * @return The percentage done between the two times
     */
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
