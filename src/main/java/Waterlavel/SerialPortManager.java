package Waterlavel;

import com.fazecast.jSerialComm.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class SerialPortManager {

    private SerialPort serialPort;
    private Consumer<String> dataListener;
    StringBuilder buffer ;


    public boolean openPort(String portName, int baudRate) {
        buffer = new StringBuilder();

        serialPort = SerialPort.getCommPort(portName);

        serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        if (serialPort.openPort()) {

            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {

                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

                    byte[] data = new byte[ serialPort.bytesAvailable() ];

                    serialPort.readBytes(data, data.length);
                
                    String received = new String(data);

                    buffer.append(received);
                
                    int index;
                    while ((index = buffer.indexOf("\n")) != -1) {
                        String completeLine = buffer.substring(0, index).trim(); 
                        
                        buffer.delete(0, index + 1);

                        if (dataListener != null) dataListener.accept(completeLine);
                    }
                }
            });
            return true;
        }
        return false;
    }

    
    public void closePort() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.removeDataListener();
            serialPort.closePort();
        }
    }

  
    public void sendData(String data) {
        try {
            OutputStream out = serialPort.getOutputStream();
            out.write(data.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    public void setDataListener(Consumer<String> listener) {
        this.dataListener = listener;
    }
}