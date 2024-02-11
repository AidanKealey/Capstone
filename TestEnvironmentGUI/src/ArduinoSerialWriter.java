import com.fazecast.jSerialComm.SerialPort;

public class ArduinoSerialWriter {
    private SerialPort serialPort = null;

    public void setupSerialComm(){
        // Find and open the serial port
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals("COM3")) { // Replace "COM3" with your Arduino's port
                this.serialPort = port;
                break;
            }
        }
        if (this.serialPort == null) {
            System.out.println("Arduino port not found.");
            return;
        }
        if (!this.serialPort.openPort()) {
            System.out.println("Failed to open Arduino port.");
            return;
        }
    }

    public void closeSerialComm(){
        this.serialPort.closePort();
    }

    public void turnOnCoils(String coilString){
        this.serialPort.writeBytes(coilString.getBytes(), coilString.length());
    }
}