package net.parablack.schedulelib;


import android.support.annotation.NonNull;

import net.parablack.schedulelib.utils.TimeUtils;


public interface WearEvent extends Comparable<WearEvent>{

    /**
     *
     * @return The time til the end of the event
     */
    long getTimeTilEnd();

    /**
     *
     * @return The name of the event
     */
    String getName();

    class NothingUpEvent implements WearEvent{

        private long timeEnd = -1;
        private int weekDay = -1;
        private String label = null;

        public NothingUpEvent(long timeEnd) {
            this.timeEnd = timeEnd;
        }

        public NothingUpEvent(long timeEnd, int weekDay) {
            this.timeEnd = timeEnd;
            this.weekDay = weekDay;
        }

        public NothingUpEvent() {
        }


        public NothingUpEvent setLabel(String label){
            this.label = label;
            return this;
        }

        @Override
        public long getTimeTilEnd() {
            if(timeEnd == -1) return (24 * 60 * 60 * 1000) - (TimeUtils.dayMillis() + 1000);

            long dayMillis = TimeUtils.dayMillis();
            long tti = timeEnd - TimeUtils.dayMillis();
        //    System.out.println("tti = " + tti);
            if (tti < -3666000) { // Ends on next day (more than one hour backwards)
                long millisToday = 24 * 60 * 60 * 1000 - dayMillis;
                //        System.out.println("In if statement returned " + restTime );

                return millisToday + timeEnd;
            }


            return tti;
        }

        @Override
        public String getName() {
            if(label != null) return label;
            return "Kein Event!";
        }


        @Override
        public int compareTo(@NonNull WearEvent another) {
            return 1;
        }
    }

}
