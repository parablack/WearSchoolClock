package net.parablack.clocktest.watchface;


public interface WearEvent {

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
                //        System.out.println("In if statement returned " + restTime );

                return millisToday + timeEnd;
            }


            return tti;
        }

        @Override
        public String getName() {
            return "Heute noch frei!";
        }
    }

}
