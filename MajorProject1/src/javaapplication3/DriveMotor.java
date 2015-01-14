package javaapplication3;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DriveMotor
{
    public DriveMotor()
    {
        super();
    }
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
       
        if ( portIdentifier.isCurrentlyOwned())
        {
            System.out.println("Hello");
            System.out.println("Error: Port is currently in use");
        }
        else
        {
              CommPort commPort = portIdentifier.open("a",2000);
                     //open(this.getClass().getName(),2000);
            
            //if ( commPort instanceof SerialPort )
            if  (commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
            //    InputStream in = serialPort.getInputStream();
                  OutputStream out = serialPort.getOutputStream();
                
                //(new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out,serialPort))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
     
    
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        SerialPort serialPort;
        
        String  messageString  = "a\n";
        public SerialWriter ( OutputStream out ,SerialPort serialPort)
        {
            this.out = out;
            this.serialPort = serialPort;
        }
        
        public void run ()
        {
            try
            {                
                /*int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                } */
                out.write(messageString.getBytes());
                serialPort.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
    public static void main ( String[] args )
    {
        try
        {
            //System.out.println("exception occured");
            (new DriveMotor()).connect("COM2");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            
            e.printStackTrace();
        }
    }
}