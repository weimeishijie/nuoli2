package com.example.nuoli;

import com.example.nuoli.service.MqttMain;
import com.example.nuoli.mqtt.MqttService;

/**
 * @Auth liwenya
 * 启动类
 */

public class Main {

    public static void main(String[] args) {
        MqttService.getInstance().connect();
        MqttMain.initialize();
    }
}
