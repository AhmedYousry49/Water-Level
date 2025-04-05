package Waterlavel;

import com.fazecast.jSerialComm.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class SerialPortManager {
    private SerialPort serialPort;
    private Consumer<String> dataListener;

    /**
     * Opens the serial port with the given name and baud rate.
     *
     * @param portName e.g., "/dev/ttyS0" or "COM3"
     * @param baudRate e.g., 9600
     * @return true if port opened successfully, false otherwise
     */
    public boolean openPort(String portName, int baudRate) {
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
                    byte[] buffer = new byte[serialPort.bytesAvailable()];
                    int numRead = serialPort.readBytes(buffer, buffer.length);
                    if (numRead > 0 && dataListener != null) {
                        String received = new String(buffer, 0, numRead);
                        dataListener.accept(received); // Observer notification
                    }
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Closes the currently opened serial port.
     */
    public void closePort() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.removeDataListener();
            serialPort.closePort();
        }
    }

    /**
     * Sends a string of data over the serial port.
     *
     * @param data The string to send (add '\n' if required by your device)
     */
    public void sendData(String data) {
        try {
            OutputStream out = serialPort.getOutputStream();
            out.write(data.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the listener that will be called when data is received.
     *
     * @param listener A Consumer that accepts received strings
     */
    public void setDataListener(Consumer<String> listener) {
        this.dataListener = listener;
    }
}