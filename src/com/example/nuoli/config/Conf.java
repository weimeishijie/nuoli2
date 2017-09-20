package com.example.nuoli.config;

/**
 * @Auth liwenya
 * 配置类
 */
public class Conf {

    //链接mobus
    public static final String hostIp = "127.0.0.1";
    public static final int port = 502;
    public static final int slaveId = 1;

    //链接mqtt
    public static final String MQTT_HOST = "tcp://127.0.0.1:1883";
    public static final String MQTT_CLIENT = "Mes_Now";

    //订阅车辆
    public static final String[] str = new String[]{"agv/131/msg", "agv/132/msg",
            "agv/133/msg", "agv/134/msg"};
    public static final int[] strInt = new int[]{0,0,0,0};

    //切换线程plc地址
    public static final int M_READ = 1012;

    //向plc写的值
    public static final short M_WRITE = 1;

    //小车可以抛同一个点的数量（存储的数量会比数值少1）
    public static final int POINT_SAME = 3;

    //小车启动消息
    public static final String START_VEHICLE = "00000000;0,0,5,0;";

    public static final String START_STRING4 = "0,0,5,0;";

    //启动小车拼接的字符串
    public static final String  START_STRING1 = "00000000;0,0,3,0;";
    public static final String START_STRING2 = "0,0,0,555;";
    public static final String START_STRING3 = "0,0,0x100000,555;";



    //匹配点(两个特殊点，1.零点，2.启动点)
    public static final String zeroPoint = "0";
    public static final String startPoint = "1000";

    //匹配工位点（如果工位做修改，plc地址也做相应的变化，防止对不上号，引起错乱）
    public static final String station1001 = "1001";
    public static final String station1002 = "1002";
    public static final String station1003 = "1003";
    public static final String station1004 = "1004";
    public static final String station1005 = "1005";
    public static final String station1006 = "1006";
    public static final String station1007 = "1007";
    public static final String station1008 = "1008";
    public static final String station1009 = "1009";
    public static final String station1010 = "1010";
    public static final String station1011 = "1011";
    public static final String station1012 = "1012";

    public static final String station2001 = "2001";
    public static final String station2002 = "2002";
    public static final String station2003 = "2003";
    public static final String station2004 = "2004";
    public static final String station2005 = "2005";
    public static final String station2006 = "2006";
    public static final String station2007 = "2007";
    public static final String station2008 = "2008";
    public static final String station2009 = "2009";
    public static final String station2010 = "2010";
    public static final String station2011 = "2011";
    public static final String station2012 = "2012";

    public static final String station3001 = "3001";
    public static final String station3002 = "3002";
    public static final String station3003 = "3003";
    public static final String station3004 = "3004";
    public static final String station3005 = "3005";
    public static final String station3006 = "3006";
    public static final String station3007 = "3007";
    public static final String station3008 = "3008";
    public static final String station3009 = "3009";
    public static final String station3010 = "3010";




    //读写plc的地址
    public static final int MRW1001 = 1000;
    public static final int MRW1002 = 1001;
    public static final int MRW1003 = 1002;
    public static final int MRW1004 = 1003;
    public static final int MRW1005 = 1004;
    public static final int MRW1006 = 1005;
    public static final int MRW1007 = 1006;
    public static final int MRW1008 = 1007;
    public static final int MRW1009 = 1008;
    public static final int MRW1010 = 1009;
    public static final int MRW1011 = 1010;
    public static final int MRW1012 = 1011;

    public static final int MRW2001 = 2000;
    public static final int MRW2002 = 2001;
    public static final int MRW2003 = 2002;
    public static final int MRW2004 = 2003;
    public static final int MRW2005 = 2004;
    public static final int MRW2006 = 2005;
    public static final int MRW2007 = 2006;
    public static final int MRW2008 = 2007;
    public static final int MRW2009 = 2008;
    public static final int MRW2010 = 2009;
    public static final int MRW2011 = 2010;
    public static final int MRW2012 = 2011;

    public static final int MRW3001 = 3000;
    public static final int MRW3002 = 3001;
    public static final int MRW3003 = 3002;
    public static final int MRW3004 = 3003;
    public static final int MRW3005 = 3004;
    public static final int MRW3006 = 3005;
    public static final int MRW3007 = 3006;
    public static final int MRW3008 = 3007;
    public static final int MRW3009 = 3008;
    public static final int MRW3010 = 3009;



    //对应plc地址读取的数值
    public static short answer1002 = 0;
    public static short answer1003 = 0;
    public static short answer1005 = 0;
    public static short answer1006 = 0;
    public static short answer1007 = 0;
    public static short answer1008 = 0;
    public static short answer1009 = 0;
    public static short answer1010 = 0;
    public static short answer1011 = 0;

    public static short answer2001 = 0;
    public static short answer2002 = 0;
    public static short answer2003 = 0;
    public static short answer2004 = 0;
    public static short answer2005 = 0;
    public static short answer2006 = 0;
    public static short answer2007 = 0;
    public static short answer2008 = 0;
    public static short answer2009 = 0;
    public static short answer2010 = 0;
    public static short answer2011 = 0;

    public static short answer3001 = 0;
    public static short answer3002 = 0;
    public static short answer3003 = 0;
    public static short answer3004 = 0;
    public static short answer3005 = 0;
    public static short answer3006 = 0;
    public static short answer3007 = 0;
    public static short answer3008 = 0;
    public static short answer3009 = 0;
    public static short answer3010 = 0;

    public static short answer1001 = 0;
    public static short answer1004 = 0;
    public static short answer1012 = 0;
    public static short answer2012 = 0;


}
