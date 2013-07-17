import java.util.Scanner;
import java.io.*;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Main {

    static SerialPort serialPort;

    public static void main(String[] args) {
        serialPort = new SerialPort(args[0]); 
        try {
            serialPort.openPort();//Open port
            serialPort.setParams(9600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
            // serialPort.writeBytes("AAAA".getBytes());
            // serialPort.writeBytes("1".getBytes());
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      //  read the username from the command-line; need to use try/catch with the
      //  readLine() method
        while (true) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // System.out.println("TRYING TO READING...");
            try {
                while (!br.ready()) { }
                int read;
            // System.out.println("READING...");
                while (br.ready()) {
                    baos.write(br.read());
                }
                // System.out.println("READ " + baos.size());
                serialPort.writeBytes(baos.toByteArray());
            } catch (IOException ex) {
                System.err.println(ex);
                System.out.println("IO error");
                System.exit(1);
            } catch (SerialPortException ex) {
                System.err.println(ex);
                System.out.println("Serialport error");
                System.exit(1);
            }
        }
    }
    
    /*
     * In this class must implement the method serialEvent, through it we learn about 
     * events that happened to our port. But we will not report on all events but only 
     * those that we put in the mask. In this case the arrival of the data and change the 
     * status lines CTS and DSR
     */
    static class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR()){//If data is available
                if(event.getEventValue() > 0){//Check bytes count in the input buffer
                    //Read data, if 10 bytes available 
                    try {
                        byte buffer[] = serialPort.readBytes(event.getEventValue());
                        System.out.print(new String(buffer));
                        // if (new String(buffer).contains("0 bytes available.\n")) {
                        //     serialPort.writeBytes("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
                        // }
                    }
                    catch (SerialPortException ex) {
                        System.err.println(ex);
                    }
                }
            }
            else if(event.isCTS()){//If CTS line has changed state
                if(event.getEventValue() == 1){//If line is ON
                    System.err.println("CTS - ON");
                }
                else {
                    System.err.println("CTS - OFF");
                }
            }
            else if(event.isDSR()){///If DSR line has changed state
                if(event.getEventValue() == 1){//If line is ON
                    System.err.println("DSR - ON");
                }
                else {
                    System.err.println("DSR - OFF");
                }
            }
        }
    }
}