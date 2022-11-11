package me.none030.mortisrockets.utils;

public enum CancelReason {

    NONE,
    NO_SPACE,
    LAND_NEAR_TOWN,
    LAUNCH_NEAR_TOWN,
    OUTSIDE_RANGE,
    NOT_SAFE,
    COULD_NOT_FIND;

    private CancelReason() {

    }
}
