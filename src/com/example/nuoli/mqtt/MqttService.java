package com.example.nuoli.mqtt;

import com.example.nuoli.collection.Vessel;
import com.example.nuoli.config.Conf;
import com.example.nuoli.service.MqttMain;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @Auth liwenya
 * 链接mqtt
 */
public class MqttService {

    public static MqttService mqttService = null;
    private MqttClient client = null;
    private MqttConnectOptions options = null;

    /**
     * 单例模式构造
     * @return mqttService
     */
    public static MqttService getInstance(){
        if(mqttService == null){
            mqttService = new MqttService();
        }
        return mqttService;
    }

    private MqttService(){init();}

    private void init(){
        try {
            client = new MqttClient(Conf.MQTT_HOST,Conf.MQTT_CLIENT,new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(50);
            options.setKeepAliveInterval(30);
            client.setCallback(new MqttMain());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立连接
     */
    public void connect(){
        try {
            client.connect(options);
            client.subscribe(Conf.str, Conf.strInt);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布消息
     * @param topic 主题
     * @param msg 消息
     */
    public void publish(String topic,String msg){
        System.out.println("Start Publish ------------------- topic = "+topic+"  Message = " +msg);
        try {
            MqttTopic mqttTopic = client.getTopic(topic);
            MqttDeliveryToken messageToken = mqttTopic.publish(msg.getBytes(),2,true);
            //如果总是出现第一次发布出去的命令为空则将此功能打开
//            while(messageToken == null){
//                messageToken = mqttTopic.publish(msg.getBytes(),2,true);
//                System.out.println(" MqttService.publish <-- go >> "+" topic = "+topic+"  Message = "+messageToken.getMessage());
//            }
            System.out.println("MqttService.publish <-- go >> "+" topic = "+topic+"  Message = "+messageToken.getMessage());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}



















