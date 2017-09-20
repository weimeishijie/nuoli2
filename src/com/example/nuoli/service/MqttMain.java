package com.example.nuoli.service;

import com.example.nuoli.collection.Vessel;
import com.example.nuoli.config.Conf;
import com.example.nuoli.modbus.ModbusMasterWrapper;
import com.example.nuoli.mqtt.MqttService;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auth liwenya
 * 逻辑处理类
 */
public class MqttMain implements MqttCallback {

    private static boolean enabled;
    private static boolean Exit = true;
    private static boolean fla = true;
    private static MqttMain mqttMain = new MqttMain();
    private static ModbusMasterWrapper modbusMasterWrapper = new ModbusMasterWrapper(Conf.hostIp,Conf.port,Conf.slaveId);
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public static void initialize(){
        if(!modbusMasterWrapper.isConnected()){
            modbusMasterWrapper.connect();
        }
        if(enabled){
            return;
        }
        cachedThreadPool.execute(mqttMain.new RunnableMonitor());//监听线程
        enabled = true;
    }

    /**
     * @param topic 主题
     * @param mqttMessage 订阅后接受到的消息
     * @throws Exception
     */
    private static String vehicleKey;//时时变更的小车号
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//        System.out.println("topic: "+topic+"  MqttMessage："+mqttMessage);
        String[] data = mqttMessage.toString().split(";");
        String vehicleNumber = topic.substring(topic.indexOf("/")+1,topic.lastIndexOf("/"));
        for(int i=1; i<data.length; i++){
            String [] str = data[i].split(",");
            if(str[0].equals("13")){
                Vessel.stationMap.put(vehicleNumber,str[1]);
                if(!Vessel.stackVehicle.contains(vehicleNumber))
                    Vessel.stackVehicle.push(vehicleNumber);
                vehicleKey = vehicleNumber;
            }
        }
    }

    //启动小车要抛的数据
    private void startPoint(String vehicleNumber, String point){
        if(!Vessel.map1100.containsKey(vehicleNumber)){
            String message = Conf.START_STRING1 + getStartSendMesage();
            MqttService.getInstance().publish("agv/"+vehicleNumber+"/ctl",message);
            System.out.println("MattMain.startPoint <-- go 起始点被调用");
            Vessel.map1100.put(vehicleNumber,point);
        }
    }

    //当数据抛出的为0点时调用（小车抛一次响应一次）
    private  synchronized void handleZero(String vehicleNumber){
            String message = Conf.START_STRING1+getStartSendMesage();
            MqttService.getInstance().publish("agv/"+vehicleNumber+"/ctl",message);
            System.out.println("MqttMain.handleZero <-- go 控制零点被调用");
    }

    //当数据抛出的为0点时调用（在没读到点就掉线，将不会再被调用）
//    private  synchronized void handleZero(String vehicleNumber){
//        if(!Vessel.map0.containsKey(vehicleNumber)){
//            String message = "00000000;0,0,3,0;"+getStartSendMesage();
//            MqttService.getInstance().publish("agv/"+vehicleNumber+"/ctl",message);
//            System.out.println("MqttMain.handleZero <-- go 控制零点被调用");
//            Vessel.map0.put(vehicleNumber,"  <<<-- upLineVehicle");
//        }
//    }

    //拼接要发送的数据
    private String getStartSendMesage(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Conf.START_STRING2);
        for(int i=0; i<29; i++){
            stringBuffer.append(Conf.START_STRING3);
        }
        stringBuffer.append(Conf.START_STRING4);
        return stringBuffer.toString();
    }

    private void handle1002(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1002) && !Vessel.stackMan01.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1002,Conf.M_WRITE);
                Conf.answer1002 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1002);
                System.out.println("answer1002 = "+Conf.answer1002+" 小车号："+ vehicleNumber);
                Vessel.stackMan01.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1002) && Vessel.stackMan01.contains(vehicleNumber)
                && !Vessel.stackMan02.contains(vehicleNumber)){
            Vessel.stackMan02.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1003(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1003) && !Vessel.stackMan03.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1003,Conf.M_WRITE);
                Conf.answer1003 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1003);
                System.out.println("answer1003 = "+Conf.answer1003+" 小车号："+ vehicleNumber);
                Vessel.stackMan03.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1003) && Vessel.stackMan03.contains(vehicleNumber)
                && !Vessel.stackMan04.contains(vehicleNumber)){
            Vessel.stackMan04.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1005(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1005) && !Vessel.stackMan05.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1005,Conf.M_WRITE);
                Conf.answer1005 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1005);
                System.out.println("answer1005 = "+Conf.answer1005+" 小车号："+ vehicleNumber);
                Vessel.stackMan05.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1005) && Vessel.stackMan05.contains(vehicleNumber)
                && !Vessel.stackMan06.contains(vehicleNumber)){
            Vessel.stackMan06.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1006(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1006) && !Vessel.stackMan07.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1006,Conf.M_WRITE);
                Conf.answer1006 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1006);
                System.out.println("answer1006 = "+Conf.answer1006+" 小车号："+ vehicleNumber);
                Vessel.stackMan07.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1006) && Vessel.stackMan07.contains(vehicleNumber)
                && !Vessel.stackMan08.contains(vehicleNumber)){
            Vessel.stackMan08.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1007(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1007) && !Vessel.stackMan09.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1007,Conf.M_WRITE);
                Conf.answer1007 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1007);
                System.out.println("answer1007 = "+Conf.answer1007+" 小车号："+ vehicleNumber);
                Vessel.stackMan09.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1007) && Vessel.stackMan09.contains(vehicleNumber)
                && !Vessel.stackMan10.contains(vehicleNumber)){
            Vessel.stackMan10.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1008(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1008) && !Vessel.stackMan11.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1008,Conf.M_WRITE);
                Conf.answer1008 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1008);
                System.out.println("answer1008 = "+Conf.answer1008+" 小车号："+ vehicleNumber);
                Vessel.stackMan11.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1008) && Vessel.stackMan11.contains(vehicleNumber)
                && !Vessel.stackMan12.contains(vehicleNumber)){
            Vessel.stackMan12.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1009(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1009) && !Vessel.stackMan13.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1009,Conf.M_WRITE);
                Conf.answer1009 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1009);
                System.out.println("answer1009 = "+Conf.answer1009+" 小车号："+ vehicleNumber);
                Vessel.stackMan13.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1009) && Vessel.stackMan13.contains(vehicleNumber)
                && !Vessel.stackMan14.contains(vehicleNumber)){
            Vessel.stackMan14.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1010(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1010) && !Vessel.stackMan15.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1010,Conf.M_WRITE);
                Conf.answer1010 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1010);
                System.out.println("answer1010 = "+Conf.answer1010+" 小车号："+ vehicleNumber);
                Vessel.stackMan15.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1010) && Vessel.stackMan15.contains(vehicleNumber)
                && !Vessel.stackMan16.contains(vehicleNumber)){
            Vessel.stackMan16.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1011(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer1011) && !Vessel.stackMan17.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1011,Conf.M_WRITE);
                Conf.answer1011 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1011);
                System.out.println("answer1011 = "+Conf.answer1011+" 小车号："+ vehicleNumber);
                Vessel.stackMan17.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer1011) && Vessel.stackMan17.contains(vehicleNumber)
                && !Vessel.stackMan18.contains(vehicleNumber)){
            Vessel.stackMan18.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3001(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3001) && !Vessel.stackMan41.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3001,Conf.M_WRITE);
                Conf.answer3001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3001);
                System.out.println("answer3001 = "+Conf.answer3001+" 小车号："+ vehicleNumber);
                Vessel.stackMan41.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3001) && Vessel.stackMan41.contains(vehicleNumber)
                && !Vessel.stackMan42.contains(vehicleNumber)){
            Vessel.stackMan42.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3002(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3002) && !Vessel.stackMan43.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3002,Conf.M_WRITE);
                Conf.answer3002 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3002);
                System.out.println("answer3002 = "+Conf.answer3002+" 小车号："+ vehicleNumber);
                Vessel.stackMan43.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3002) && Vessel.stackMan43.contains(vehicleNumber)
                && !Vessel.stackMan44.contains(vehicleNumber)){
            Vessel.stackMan44.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3003(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3003) && !Vessel.stackMan45.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3003,Conf.M_WRITE);
                Conf.answer3003 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3003);
                System.out.println("answer3003 = "+Conf.answer3003+" 小车号："+ vehicleNumber);
                Vessel.stackMan45.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3003) && Vessel.stackMan45.contains(vehicleNumber)
                && !Vessel.stackMan46.contains(vehicleNumber)){
            Vessel.stackMan46.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3004(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3004) && !Vessel.stackMan47.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3004,Conf.M_WRITE);
                Conf.answer3004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3004);
                System.out.println("answer3004 = "+Conf.answer3004+" 小车号："+ vehicleNumber);
                Vessel.stackMan47.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3004) && Vessel.stackMan47.contains(vehicleNumber)
                && !Vessel.stackMan48.contains(vehicleNumber)){
            Vessel.stackMan48.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3005(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3005) && !Vessel.stackMan49.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3005,Conf.M_WRITE);
                Conf.answer3005 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3005);
                System.out.println("answer3005 = "+Conf.answer3005+" 小车号："+ vehicleNumber);
                Vessel.stackMan49.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3005) && Vessel.stackMan49.contains(vehicleNumber)
                && !Vessel.stackMan50.contains(vehicleNumber)){
            Vessel.stackMan50.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3006(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3006) && !Vessel.stackMan51.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3006,Conf.M_WRITE);
                Conf.answer3006 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3006);
                System.out.println("answer3006 = "+Conf.answer3006+" 小车号："+ vehicleNumber);
                Vessel.stackMan51.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3006) && Vessel.stackMan51.contains(vehicleNumber)
                && !Vessel.stackMan52.contains(vehicleNumber)){
            Vessel.stackMan52.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3007(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3007) && !Vessel.stackMan53.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3007,Conf.M_WRITE);
                Conf.answer3007 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3007);
                System.out.println("answer3007 = "+Conf.answer3007+" 小车号："+ vehicleNumber);
                Vessel.stackMan53.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3007) && Vessel.stackMan53.contains(vehicleNumber)
                && !Vessel.stackMan54.contains(vehicleNumber)){
            Vessel.stackMan54.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3008(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3008) && !Vessel.stackMan55.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3008,Conf.M_WRITE);
                Conf.answer3008 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3008);
                System.out.println("answer3008 = "+Conf.answer3008+" 小车号："+ vehicleNumber);
                Vessel.stackMan55.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3008) && Vessel.stackMan55.contains(vehicleNumber)
                && !Vessel.stackMan56.contains(vehicleNumber)){
            Vessel.stackMan56.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3009(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3009) && !Vessel.stackMan57.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3009,Conf.M_WRITE);
                Conf.answer3009 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3009);
                System.out.println("answer3009 = "+Conf.answer3009+" 小车号："+ vehicleNumber);
                Vessel.stackMan57.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3009) && Vessel.stackMan57.contains(vehicleNumber)
                && !Vessel.stackMan58.contains(vehicleNumber)){
            Vessel.stackMan58.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle3010(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer3010) && !Vessel.stackMan59.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW3010,Conf.M_WRITE);
                Conf.answer3010 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3010);
                System.out.println("answer3010 = "+Conf.answer3010+" 小车号："+ vehicleNumber);
                Vessel.stackMan59.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer3010) && Vessel.stackMan59.contains(vehicleNumber)
                && !Vessel.stackMan60.contains(vehicleNumber)){
            Vessel.stackMan60.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2001(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2001) && !Vessel.stackMan19.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2001,Conf.M_WRITE);
                Conf.answer2001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2001);
                System.out.println("answer2001 = "+Conf.answer2001+" 小车号："+ vehicleNumber);
                Vessel.stackMan19.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2001) && Vessel.stackMan19.contains(vehicleNumber)
                && !Vessel.stackMan20.contains(vehicleNumber)){
            Vessel.stackMan20.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2002(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2002) && !Vessel.stackMan21.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2002,Conf.M_WRITE);
                Conf.answer2002 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2002);
                System.out.println("answer2002 = "+Conf.answer2002+" 小车号："+ vehicleNumber);
                Vessel.stackMan21.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2002) && Vessel.stackMan21.contains(vehicleNumber)
                && !Vessel.stackMan22.contains(vehicleNumber)){
            Vessel.stackMan22.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2003(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2003) && !Vessel.stackMan23.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2003,Conf.M_WRITE);
                Conf.answer2003 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2003);
                System.out.println("answer2003 = "+Conf.answer2003+" 小车号："+ vehicleNumber);
                Vessel.stackMan23.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2003) && Vessel.stackMan23.contains(vehicleNumber)
                && !Vessel.stackMan24.contains(vehicleNumber)){
            Vessel.stackMan24.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2004(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2004) && !Vessel.stackMan25.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2004,Conf.M_WRITE);
                Conf.answer2004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2004);
                System.out.println("answer2004 = "+Conf.answer2004+" 小车号："+ vehicleNumber);
                Vessel.stackMan25.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2004) && Vessel.stackMan25.contains(vehicleNumber)
                && !Vessel.stackMan26.contains(vehicleNumber)){
            Vessel.stackMan26.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2005(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2005) && !Vessel.stackMan27.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2005,Conf.M_WRITE);
                Conf.answer2005 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2005);
                System.out.println("answer2005 = "+Conf.answer2005+" 小车号："+ vehicleNumber);
                Vessel.stackMan27.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2005) && Vessel.stackMan27.contains(vehicleNumber)
                && !Vessel.stackMan28.contains(vehicleNumber)){
            Vessel.stackMan28.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2006(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2006) && !Vessel.stackMan29.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2006,Conf.M_WRITE);
                Conf.answer2006 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2006);
                System.out.println("answer2006 = "+Conf.answer2006+" 小车号："+ vehicleNumber);
                Vessel.stackMan29.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2006) && Vessel.stackMan29.contains(vehicleNumber)
                && !Vessel.stackMan30.contains(vehicleNumber)){
            Vessel.stackMan30.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2007(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2007) && !Vessel.stackMan31.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2007,Conf.M_WRITE);
                Conf.answer2007 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2007);
                System.out.println("answer2007 = "+Conf.answer2007+" 小车号："+ vehicleNumber);
                Vessel.stackMan31.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2007) && Vessel.stackMan31.contains(vehicleNumber)
                && !Vessel.stackMan32.contains(vehicleNumber)){
            Vessel.stackMan32.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2008(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2008) && !Vessel.stackMan33.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2008,(short)1);
                Conf.answer2008 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2008);
                System.out.println("answer2008 = "+Conf.answer2008+" 小车号："+ vehicleNumber);
                Vessel.stackMan33.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2008) && Vessel.stackMan33.contains(vehicleNumber)
                && !Vessel.stackMan34.contains(vehicleNumber)){
            Vessel.stackMan34.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2009(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2009) && !Vessel.stackMan35.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2009,Conf.M_WRITE);
                Conf.answer2009 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2009);
                System.out.println("answer2009 = "+Conf.answer2009+" 小车号："+ vehicleNumber);
                Vessel.stackMan35.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2009) && Vessel.stackMan35.contains(vehicleNumber)
                && !Vessel.stackMan36.contains(vehicleNumber)){
            Vessel.stackMan36.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2010(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2010) && !Vessel.stackMan37.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2010,Conf.M_WRITE);
                Conf.answer2010 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2010);
                System.out.println("answer2010 = "+Conf.answer2010+" 小车号："+ vehicleNumber);
                Vessel.stackMan37.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2010) && Vessel.stackMan37.contains(vehicleNumber)
                && !Vessel.stackMan38.contains(vehicleNumber)){
            Vessel.stackMan38.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle2011(String vehicleNumber){
        if(Short.valueOf("0").equals(Conf.answer2011) && !Vessel.stackMan39.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2011,Conf.M_WRITE);
                Conf.answer2011 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2011);
                System.out.println("answer2011 = "+Conf.answer2011+" 小车号："+ vehicleNumber);
                Vessel.stackMan39.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(Short.valueOf("0").equals(Conf.answer2011) && Vessel.stackMan39.contains(vehicleNumber)
                && !Vessel.stackMan40.contains(vehicleNumber)){
            Vessel.stackMan40.push(vehicleNumber);
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
        }
    }

    private void handle1001(String vehicleNumber){
        if(!Vessel.stack1.contains(vehicleNumber) && "0".equals(String.valueOf(Conf.answer1001))){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1001,Conf.M_WRITE);
                Conf.answer1001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1001);
                Vessel.stack1.push(vehicleNumber);
                System.out.println("answer1001 = "+Conf.answer1001+" 小车号："+ vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if("0".equals(String.valueOf(Conf.answer1001)) && !Vessel.stack2.contains(vehicleNumber)
                && Vessel.stack1.contains(vehicleNumber)){
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
            Vessel.stack2.push(vehicleNumber);
        }
    }

    private void handle1004(String vehicleNumber){
        if("0".equals(String.valueOf(Conf.answer1004)) && !Vessel.stack3.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1004,Conf.M_WRITE);
                Conf.answer1004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1004);
                System.out.println("answer1004 = "+Conf.answer1004+" 小车号："+ vehicleNumber);
                Vessel.stack3.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if("0".equals(String.valueOf(Conf.answer1004)) && !Vessel.stack4.contains(vehicleNumber)
                && Vessel.stack3.contains(vehicleNumber)){
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
            Vessel.stack4.push(vehicleNumber);
        }
    }

    private void handle1012(String vehicleNumber){
        if("0".equals(String.valueOf(Conf.answer1012)) && !Vessel.stack5.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW1012,Conf.M_WRITE);
                Conf.answer1012 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1012);
                System.out.println("answer1012= "+Conf.answer1012+" 小车号："+ vehicleNumber);
                Vessel.stack5.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if(vehicleNumber != null && "0".equals(String.valueOf(Conf.answer1012))
                && !Vessel.stack6.contains(vehicleNumber) && Vessel.stack5.contains(vehicleNumber)){
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
            Vessel.stack6.push(vehicleNumber);
        }
    }

    private void handle2012(String vehicleNumber){
        if("0".equals(String.valueOf(Conf.answer2012)) && !Vessel.stack7.contains(vehicleNumber)){
            try {
                modbusMasterWrapper.writeRegisterValue(Conf.MRW2012,Conf.M_WRITE);
                Conf.answer2012 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2012);
                System.out.println("answer2012 = "+Conf.answer2012+" 小车号："+ vehicleNumber);
                Vessel.stack7.push(vehicleNumber);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        }
        if("0".equals(String.valueOf(Conf.answer2012))
                && !Vessel.stack8.contains(vehicleNumber) && Vessel.stack7.contains(vehicleNumber)){
            MqttService.getInstance().publish("agv/"+ vehicleNumber +"/ctl",Conf.START_VEHICLE);
            Vessel.stack8.push(vehicleNumber);
        }
    }

    //手动|自动工位读取 PLC 线程
    class RunnableModbus implements Runnable{

        @Override
        public void run() {
            while (Exit){
                try {
                    if(!modbusMasterWrapper.isConnected())
                        modbusMasterWrapper.connect();
                    if(modbusMasterWrapper != null){
                        Conf.answer3001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3001);
                        Conf.answer3002 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3002);
                        Conf.answer3003 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3003);
                        Conf.answer3004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3004);
                        Conf.answer3005 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3005);
                        Conf.answer3006 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3006);
                        Conf.answer3007 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3007);
                        Conf.answer3008 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3008);
                        Conf.answer3009 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3009);
                        Conf.answer3010 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW3010);
                    } else {
                        modbusMasterWrapper.disconnect();
                        System.out.println("modbusMasterWrapper is connection failed");
                    }
                } catch (ModbusTransportException ex) {
                    ex.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("【系统异常】：");
                }

            }
        }
    }

    //手动|自动读取 PLC 线程
    class RunnableModbus1 implements Runnable{

        @Override
        public void run() {
            while (Exit){
                try {
                    if(!modbusMasterWrapper.isConnected())
                        modbusMasterWrapper.connect();
                    if(modbusMasterWrapper != null){
                        Conf.answer1001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1001);
                        Conf.answer1002 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1002);
                        Conf.answer1003 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1003);
                        Conf.answer1004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1004);
                        Conf.answer1005 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1005);
                        Conf.answer1006 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1006);
                        Conf.answer1007 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1007);
                        Conf.answer1008 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1008);
                        Conf.answer1009 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1009);
                        Conf.answer1010 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1010);
                        Conf.answer1011 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1011);
                        Conf.answer1012 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1012);
                    } else {
                        modbusMasterWrapper.disconnect();
                        System.out.println("modbusMasterWrapper is connection failed");
                    }
                } catch (ModbusTransportException ex) {
                    ex.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("【系统异常】：");
                }

            }

        }
    }

    //与小车抛进来的点匹配线程
    class RunnableModbus2 implements Runnable{

        @Override
        public void run() {

            while (Exit){
                try {
                    if(!modbusMasterWrapper.isConnected())
                        modbusMasterWrapper.connect();
                    if(Vessel.stackVehicle.empty()){
                        Thread.sleep(500);
                        continue;
                    }
                    String vehicleNumber = Vessel.stackVehicle.pop();
                    if(modbusMasterWrapper != null){
                        switch (Vessel.stationMap.get(vehicleNumber)){
                            case Conf.startPoint:
                                startPoint(vehicleNumber, Vessel.stationMap.get(vehicleNumber));
                                break;
                            case Conf.zeroPoint:
                                handleZero(vehicleNumber);
                                break;
                            case Conf.station1002:
                                handle1002(vehicleNumber);
                                break;
                            case Conf.station1003:
                                handle1003(vehicleNumber);
                                break;
                            case Conf.station1005:
                                handle1005(vehicleNumber);
                                break;
                            case Conf.station1006:
                                handle1006(vehicleNumber);
                                break;
                            case Conf.station1007:
                                handle1007(vehicleNumber);
                                break;
                            case Conf.station1008:
                                handle1008(vehicleNumber);
                                break;
                            case Conf.station1009:
                                handle1009(vehicleNumber);
                                break;
                            case Conf.station1010:
                                handle1010(vehicleNumber);
                                break;
                            case Conf.station1011:
                                handle1011(vehicleNumber);
                                break;
                            case Conf.station2001:
                                handle2001(vehicleNumber);
                                break;
                            case Conf.station2002:
                                handle2002(vehicleNumber);
                                break;
                            case Conf.station2003:
                                handle2003(vehicleNumber);
                                break;
                            case Conf.station2004:
                                handle2004(vehicleNumber);
                                break;
                            case Conf.station2005:
                                handle2005(vehicleNumber);
                                break;
                            case Conf.station2006:
                                handle2006(vehicleNumber);
                                break;
                            case Conf.station2007:
                                handle2007(vehicleNumber);
                                break;
                            case Conf.station2008:
                                handle2008(vehicleNumber);
                                break;
                            case Conf.station2009:
                                handle2009(vehicleNumber);
                                break;
                            case Conf.station2010:
                                handle2010(vehicleNumber);
                                break;
                            case Conf.station2011:
                                handle2011(vehicleNumber);
                                break;
                            case Conf.station3001:
                                handle3001(vehicleNumber);
                                break;
                            case Conf.station3002:
                                handle3002(vehicleNumber);
                                break;
                            case Conf.station3003:
                                handle3003(vehicleNumber);
                                break;
                            case Conf.station3004:
                                handle3004(vehicleNumber);
                                break;
                            case Conf.station3005:
                                handle3005(vehicleNumber);
                                break;
                            case Conf.station3006:
                                handle3006(vehicleNumber);
                                break;
                            case Conf.station3007:
                                handle3007(vehicleNumber);
                                break;
                            case Conf.station3008:
                                handle3008(vehicleNumber);
                                break;
                            case Conf.station3009:
                                handle3009(vehicleNumber);
                                break;
                            case Conf.station3010:
                                handle3010(vehicleNumber);
                                break;
                            case Conf.station1001:
                                handle1001(vehicleNumber);
                                break;
                            case Conf.station1004:
                                handle1004(vehicleNumber);
                                break;
                            case Conf.station1012:
                                handle1012(vehicleNumber);
                                break;
                            case Conf.station2012:
                                handle2012(vehicleNumber);
                                break;
                            default:
                        }
                    } else {
                        modbusMasterWrapper.disconnect();
                        System.out.println("modbusMasterWrapper is connection failed");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //代码优化（减少读取次数）
    private Boolean isContains(String vehicleNumber){
        return Arrays.asList(Conf.zeroPoint, Conf.startPoint, Conf.station1001, Conf.station1004, Conf.station1012, Conf.station2012).contains(vehicleNumber);
    }

    //自动工位处理线程
    class RunnableModbus3 implements Runnable{

        @Override
        public void run() {
            while (fla){
                try {
                    if(!modbusMasterWrapper.isConnected())
                        modbusMasterWrapper.connect();
                    if(modbusMasterWrapper != null){
                        if(Vessel.stackVehicle.empty()){
                            Thread.sleep(500);
                            continue;
                        }
                        String vehicleNumber = Vessel.stackVehicle.pop();
                        if(isContains(Vessel.stationMap.get(vehicleNumber))){
                            Conf.answer1001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1001);
                            Conf.answer1004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1004);
                            Conf.answer1012 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW1012);
                            Conf.answer2012 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2012);
                            switch (Vessel.stationMap.get(vehicleNumber)){
                                case Conf.startPoint:
                                    startPoint(vehicleNumber, Vessel.stationMap.get(vehicleNumber));
                                    break;
                                case Conf.zeroPoint:
                                    handleZero(vehicleNumber);
                                    break;
                                case Conf.station1001:
                                    handle1001(vehicleNumber);
                                    break;
                                case Conf.station1004:
                                    handle1004(vehicleNumber);
                                    break;
                                case Conf.station1012:
                                    handle1012(vehicleNumber);
                                    break;
                                case Conf.station2012:
                                    handle2012(vehicleNumber);
                                    break;
                                default:
                            }
                        }
                        }

                }catch (ModbusTransportException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //手动|自动工位读取 PLC 线程
    class RunnableModbus4 implements Runnable{

        @Override
        public void run() {
            while (Exit){
                try {
                    if(!modbusMasterWrapper.isConnected())
                        modbusMasterWrapper.connect();
                    if(modbusMasterWrapper != null){
                        Conf.answer2001 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2001);
                        Conf.answer2002 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2002);
                        Conf.answer2003 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2003);
                        Conf.answer2004 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2004);
                        Conf.answer2005 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2005);
                        Conf.answer2006 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2006);
                        Conf.answer2007 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2007);
                        Conf.answer2008 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2008);
                        Conf.answer2009 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2009);
                        Conf.answer2010 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2010);
                        Conf.answer2011 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2011);
                        Conf.answer2012 = modbusMasterWrapper.readHoldingRegisterValue(Conf.MRW2012);
                    } else {
                        modbusMasterWrapper.disconnect();
                        System.out.println("MqttMain().RunnableModbus1().run() <<-- go 链接失败");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("【未知异常】 MqttMain().RunnableModbus1().run() <-- go "+e.getMessage());
                }
            }
        }
    }

    //监听线程
    class RunnableMonitor implements Runnable{

        @Override
        public void run() {
            Timer timer = new Timer();
            timer.schedule(mqttMain.new MyTask(), 50, 1000);
        }
    }

    //清除集合中多余的小车
    private void cleanExcess(){

        //此功能没有开启
//        if(vehicleKey != null && Vessel.map0.containsKey(vehicleKey) && !Conf.zeroPoint.equals(Vessel.stationMap.get(vehicleKey))){
//            Vessel.map0.remove(vehicleKey);
//            System.out.println("集合map0清除数据零点 vehicleKey = "+vehicleKey);
//        }

        if(vehicleKey != null && Vessel.map1100.containsKey(vehicleKey) && !Conf.startPoint.equals(Vessel.stationMap.get(vehicleKey))){
            Vessel.map1100.remove(vehicleKey);
            System.out.println("集合map1100清除数据起始点 vehicleKey = "+vehicleKey);
        }

        if(!Vessel.stack1.empty() && Vessel.stack1.length() > Conf.POINT_SAME){
            Vessel.stack1.pop();
            Vessel.stack2.pop();
        }

        if(!Vessel.stack3.empty() && Vessel.stack3.length() > Conf.POINT_SAME){
            Vessel.stack3.pop();
            Vessel.stack4.pop();
        }

        if(!Vessel.stack5.empty() && Vessel.stack5.length() > Conf.POINT_SAME){
            Vessel.stack5.pop();
            Vessel.stack6.pop();
        }

        if(!Vessel.stack7.empty() && Vessel.stack7.length() > Conf.POINT_SAME){
            Vessel.stack7.pop();
            Vessel.stack8.pop();
        }

        if(!Vessel.stackMan01.empty() && Vessel.stackMan01.length() > Conf.POINT_SAME){
            Vessel.stackMan01.pop();
            Vessel.stackMan02.pop();
        }

        if(!Vessel.stackMan03.empty() && Vessel.stackMan03.length() > Conf.POINT_SAME){
            Vessel.stackMan03.pop();
            Vessel.stackMan04.pop();
        }

        if(!Vessel.stackMan05.empty() && Vessel.stackMan05.length() > Conf.POINT_SAME){
            Vessel.stackMan05.pop();
            Vessel.stackMan06.pop();
        }

        if(!Vessel.stackMan07.empty() && Vessel.stackMan07.length() > Conf.POINT_SAME){
            Vessel.stackMan07.pop();
            Vessel.stackMan08.pop();
        }

        if(!Vessel.stackMan09.empty() && Vessel.stackMan09.length() > Conf.POINT_SAME){
            Vessel.stackMan09.pop();
            Vessel.stackMan10.pop();
        }

        if(!Vessel.stackMan11.empty() && Vessel.stackMan11.length() > Conf.POINT_SAME){
            Vessel.stackMan11.pop();
            Vessel.stackMan12.pop();
        }

        if(!Vessel.stackMan13.empty() && Vessel.stackMan13.length() > Conf.POINT_SAME){
            Vessel.stackMan13.pop();
            Vessel.stackMan14.pop();
        }

        if(!Vessel.stackMan15.empty() && Vessel.stackMan15.length() > Conf.POINT_SAME){
            Vessel.stackMan15.pop();
            Vessel.stackMan16.pop();
        }

        if(!Vessel.stackMan17.empty() && Vessel.stackMan17.length() > Conf.POINT_SAME){
            Vessel.stackMan17.pop();
            Vessel.stackMan18.pop();
        }

        if(!Vessel.stackMan19.empty() && Vessel.stackMan19.length() > Conf.POINT_SAME){
            Vessel.stackMan19.pop();
            Vessel.stackMan20.pop();
        }

        if(!Vessel.stackMan21.empty() && Vessel.stackMan21.length() > Conf.POINT_SAME){
            Vessel.stackMan21.pop();
            Vessel.stackMan22.pop();
        }

        if(!Vessel.stackMan23.empty() && Vessel.stackMan23.length() > Conf.POINT_SAME){
            Vessel.stackMan23.pop();
            Vessel.stackMan24.pop();
        }

        if(!Vessel.stackMan25.empty() && Vessel.stackMan25.length() > Conf.POINT_SAME){
            Vessel.stackMan25.pop();
            Vessel.stackMan26.pop();
        }

        if(!Vessel.stackMan27.empty() && Vessel.stackMan27.length() > Conf.POINT_SAME){
            Vessel.stackMan27.pop();
            Vessel.stackMan28.pop();
        }

        if(!Vessel.stackMan29.empty() && Vessel.stackMan29.length() > Conf.POINT_SAME){
            Vessel.stackMan29.pop();
            Vessel.stackMan30.pop();
        }

        if(!Vessel.stackMan31.empty() && Vessel.stackMan31.length() > Conf.POINT_SAME){
            Vessel.stackMan31.pop();
            Vessel.stackMan32.pop();
        }

        if(!Vessel.stackMan33.empty() && Vessel.stackMan33.length() > Conf.POINT_SAME){
            Vessel.stackMan33.pop();
            Vessel.stackMan34.pop();
        }

        if(!Vessel.stackMan35.empty() && Vessel.stackMan35.length() > Conf.POINT_SAME){
            Vessel.stackMan35.pop();
            Vessel.stackMan36.pop();
        }

        if(!Vessel.stackMan37.empty() && Vessel.stackMan37.length() > Conf.POINT_SAME){
            Vessel.stackMan37.pop();
            Vessel.stackMan38.pop();
        }

        if(!Vessel.stackMan39.empty() && Vessel.stackMan39.length() > Conf.POINT_SAME){
            Vessel.stackMan39.pop();
            Vessel.stackMan40.pop();
        }

        if(!Vessel.stackMan41.empty() && Vessel.stackMan41.length() > Conf.POINT_SAME){
            Vessel.stackMan41.pop();
            Vessel.stackMan42.pop();
        }

        if(!Vessel.stackMan43.empty() && Vessel.stackMan43.length() > Conf.POINT_SAME){
            Vessel.stackMan43.pop();
            Vessel.stackMan44.pop();
        }

        if(!Vessel.stackMan45.empty() && Vessel.stackMan45.length() > Conf.POINT_SAME){
            Vessel.stackMan45.pop();
            Vessel.stackMan46.pop();
        }

        if(!Vessel.stackMan47.empty() && Vessel.stackMan47.length() > Conf.POINT_SAME){
            Vessel.stackMan47.pop();
            Vessel.stackMan48.pop();
        }

        if(!Vessel.stackMan49.empty() && Vessel.stackMan49.length() > Conf.POINT_SAME){
            Vessel.stackMan49.pop();
            Vessel.stackMan50.pop();
        }

        if(!Vessel.stackMan51.empty() && Vessel.stackMan51.length() > Conf.POINT_SAME){
            Vessel.stackMan51.pop();
            Vessel.stackMan52.pop();
        }

        if(!Vessel.stackMan53.empty() && Vessel.stackMan53.length() > Conf.POINT_SAME){
            Vessel.stackMan53.pop();
            Vessel.stackMan54.pop();
        }

        if(!Vessel.stackMan55.empty() && Vessel.stackMan55.length() > Conf.POINT_SAME){
            Vessel.stackMan55.pop();
            Vessel.stackMan56.pop();
        }

        if(!Vessel.stackMan57.empty() && Vessel.stackMan57.length() > Conf.POINT_SAME){
            Vessel.stackMan57.pop();
            Vessel.stackMan58.pop();
        }

        if(!Vessel.stackMan59.empty() && Vessel.stackMan59.length() > Conf.POINT_SAME){
            Vessel.stackMan59.pop();
            Vessel.stackMan60.pop();
        }

    }

    class MyTask extends TimerTask {
        short read;
        boolean readBoo = true;
        boolean readBool = true;
        @Override
        public void run() {
            try {
                if(!modbusMasterWrapper.isConnected())
                    modbusMasterWrapper.connect();
                read = modbusMasterWrapper.readHoldingRegisterValue(Conf.M_READ);
                if(Short.valueOf("0").equals(read) && readBoo){
                    Exit = false;
                    Thread.sleep(1500);//等待线程关闭
                    Vessel.cleanCollection();
                    fla = true;
                    cachedThreadPool.execute(mqttMain.new RunnableModbus3());//处理线程
                    readBoo = false;readBool = true;
                    System.out.println("读取 PLC ====================== "+read+" ====================== 启动完成");
                } else if(Short.valueOf("1").equals(read) && readBool){
                    fla = false;
                    Thread.sleep(1000);//等待线程关闭
                    Vessel.cleanCollection();
                    Exit = true;
                    cachedThreadPool.execute(mqttMain.new RunnableModbus());//处理线程读
                    cachedThreadPool.execute(mqttMain.new RunnableModbus1());//处理线程读
                    cachedThreadPool.execute(mqttMain.new RunnableModbus2());//处理线程写
                    cachedThreadPool.execute(mqttMain.new RunnableModbus4());//处理线程读
                    readBool = false;readBoo = true;
                    System.out.println("读取 PLC ====================== "+read+" ====================== 启动完成");
                }
                cleanExcess();
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

}








