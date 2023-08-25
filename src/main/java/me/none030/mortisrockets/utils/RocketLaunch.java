package me.none030.mortisrockets.utils;

public class RocketLaunch {

    private boolean canLaunch;
    private CancelReason reason;

    public RocketLaunch(boolean canLaunch, CancelReason reason) {
        this.canLaunch = canLaunch;
        this.reason = reason;
    }

    public boolean isCanLaunch() {
        return canLaunch;
    }

    public CancelReason getReason() {
        return reason;
    }
}
