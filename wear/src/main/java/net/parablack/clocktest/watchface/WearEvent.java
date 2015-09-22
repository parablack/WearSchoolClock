package net.parablack.clocktest.watchface;

/**
 * Created by Simon on 16.09.2015.
 */
public interface WearEvent {

    long getTimeTilEnd();

    String getName();

    class NothingUpEvent implements  WearEvent{

        private long timeEnd = -1;

        public NothingUpEvent(long timeEnd) {
            this.timeEnd = timeEnd;
        }

        public NothingUpEvent() {
        }

        @Override
        public long getTimeTilEnd() {
            if(timeEnd == -1) return (24 * 60 * 60 * 1000) - (TimeUtils.dayMillis() + 1000);

            long dayMillis = TimeUtils.dayMillis();
            long tti = timeEnd - TimeUtils.dayMillis();
        //    System.out.println("tti = " + tti);
            if (tti < -3666000) { // Ends on next day (more than one hour backwards)
                long millisToday = 24 * 60 * 60 * 1000 - dayMillis;
                long restTime = millisToday + timeEnd;
        //        System.out.println("In if statement returned " + restTime );

                return restTime;
            }


            return tti;
        }

        @Override
        public String getName() {
            return "Heute noch frei!";
        }
    }

}
