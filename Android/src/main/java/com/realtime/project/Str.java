package com.realtime.project;

import java.util.UUID;

public class Str {

    public final static UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");
    public static final String ENABLE_BT = "s1";
    public static final String DISABLE_BT = "s2";
    public static final String CONNECT_TO_SERVER = "s3";
    public static final String DISCONNECT_FROM_SERVER = "s4";
    public static final String SEND_TO_SERVER = "s5";
    public static final String UPDATE_PLOT_DATA = "s6";
    public static final String SERVER_STATE_s = "s7";
    public static final String TOAST = "s8";
    public static final String BT_STATE_s = "s9";
    public static final String READ_DATA_s = "s10";
    public static final String SCAN_DEVICES_s = "s11";
    public static final String MAKE_DISCOVERABLE_s = "s12";
    public static final String CHECK_BT_STATE_s = "s13";
    
    public static final String UPDATE_PI_K = "s14";
    public static final String UPDATE_PI_H = "s15";
    public static final String UPDATE_PI_TI = "s16";
    public static final String UPDATE_PI_TR = "s17";
    public static final String UPDATE_PI_BETA = "s18";
    
    public static final String UPDATE_PID_K = "s19";
    public static final String UPDATE_PID_H = "s20";
    public static final String UPDATE_PID_BETA = "s21";
    public static final String UPDATE_PID_TI = "s22";
    public static final String UPDATE_PID_TD = "s23";
    public static final String UPDATE_PID_TR = "s24";
    public static final String UPDATE_PID_N = "s25";
    
    public static final String UPDATE_PI_PARAMS = "s26";
    public static final String UPDATE_PID_PARAMS = "27";
    
    public final static int ENABLE_BT_int = 6;
    public final static int MAKE_DISCOVERABLE = 8;

    public final static int SERVER_STATE_DISCONNECTED = 13;
    public final static int SERVER_STATE_CONNECTING = 14;
    public final static int SERVER_STATE_CONNECTED = 15;
    public final static int BT_STATE_ENABLED = 16;
    public final static int BT_STATE_DISABLED = 17;


}
