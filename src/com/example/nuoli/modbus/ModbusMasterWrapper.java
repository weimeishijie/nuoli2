package com.example.nuoli.modbus;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;

/**
 * @Auth liwenya
 */
public class ModbusMasterWrapper {

    private String hostIp;
    private int hostPort;
    private int slaveId;

    private ModbusMaster modbusMaster;
    private boolean connected;   //连接成功 true， 否则 false

    public ModbusMasterWrapper(String hostIp, int port, int slaveId) {
        this.hostIp = hostIp;
        this.hostPort = port;
        this.slaveId = slaveId;
    }

    public synchronized boolean connect() {
        disconnect();
        try {
            IpParameters ipParameters = new IpParameters();
            ipParameters.setHost(this.getHostIp());
            ipParameters.setPort(this.getHostPort());

            modbusMaster = new ModbusFactory().createTcpMaster(ipParameters, true);
            modbusMaster.init();
            connected = true;
        } catch (ModbusInitException ex) {
            disconnect();
        }

        return isConnected();
    }

    public void disconnect() {
        if (modbusMaster != null) {
            modbusMaster.destroy();
        }
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public short[] readHoldingRegisterValues(int startOffset, int numberOfRegisters) throws ModbusTransportException {
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId,startOffset, numberOfRegisters);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse)modbusMaster.send(request);
        if (response != null && response.isException()) {
            return null;
        } else if (response != null) {
            return response.getShortData();
        }
        connected = false;
        return null;
    }

    public Short readHoldingRegisterValue(int startOffset) throws ModbusTransportException {
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId,
                startOffset, 1);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse)modbusMaster.send(request);

        if (response != null && response.isException()) {
            return null;
        } else if (response != null && response.getShortData().length > 0) {
            return response.getShortData()[0];
        }
        connected = false;
        return null;
    }


    public void writeRegisterValues(int startOffset, short[] values) throws ModbusTransportException {
        WriteRegistersRequest request = new WriteRegistersRequest(slaveId, startOffset, values);
        WriteRegistersResponse response = (WriteRegistersResponse) modbusMaster.send(request);
        if (response!= null && response.isException()) {
            throw new ModbusTransportException(response.getExceptionMessage());
        }
    }

    public void writeRegisterValue(int startOffset, short value)throws ModbusTransportException {
        WriteRegistersRequest request = new WriteRegistersRequest(slaveId, startOffset,new short[]{value});
        WriteRegistersResponse response = (WriteRegistersResponse) modbusMaster.send(request);
        if (response!= null && response.isException()) {
            throw new ModbusTransportException(response.getExceptionMessage());
        }
    }
}
