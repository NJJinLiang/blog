package serialPort;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import serialException.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * ���������ʾ��
 *
 * @author Zhong
 */
public class DataView2 extends Frame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Client client = null;

    private List<String> commList = null;    //������ö˿ں�
    private SerialPort serialPort = null;    //���洮�ڶ���

    private Font font = new Font("΢���ź�", Font.BOLD, 25);


    private Choice commChoice = new Choice();    //����ѡ��������
    private Choice bpsChoice = new Choice();    //������ѡ��

    private Button openSerialButton = new Button("�򿪴���");

    private TextArea textArea = new TextArea("");

    Image offScreen = null;    //�ػ�ʱ�Ļ���

    //����window��icon
    Toolkit toolKit = getToolkit();
    Image icon = toolKit.getImage(DataView2.class.getResource("computer.png"));

    private List<Byte> byteList = new ArrayList<>();

    int postion = 0;

    /**
     * ��Ĺ��췽��
     *
     * @param client
     */
    public DataView2(Client client) {
        this.client = client;
        commList = SerialTool.findPort();    //�����ʼ��ʱ��ɨ��һ����Ч����
    }

    /**
     * ���˵�������ʾ��
     * ���Label����ť��������������¼�������
     */
    public void dataFrame() {
        this.setBounds(client.LOC_X, client.LOC_Y, client.WIDTH, client.HEIGHT);
        this.setTitle("ThreeTun");
        this.setIconImage(icon);
        this.setBackground(Color.white);
        this.setLayout(null);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                if (serialPort != null) {
                    //�����˳�ʱ�رմ����ͷ���Դ
                    SerialTool.closePort(serialPort);
                }
                System.exit(0);
            }

        });


        //��Ӵ���ѡ��ѡ��
        commChoice.setBounds(160, 50, 100, 200);
        //����Ƿ��п��ô��ڣ��������ѡ����
        if (commList == null || commList.size() < 1) {
            JOptionPane.showMessageDialog(null, "û����������Ч���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String s : commList) {
                commChoice.add(s);
            }
        }
        add(commChoice);

        //��Ӳ�����ѡ��
        bpsChoice.setBounds(526, 50, 200, 200);
        bpsChoice.add("1200");
        bpsChoice.add("2400");
        bpsChoice.add("4800");
        bpsChoice.add("9600");
        bpsChoice.add("14400");
        bpsChoice.add("19200");
        bpsChoice.add("115200");
        add(bpsChoice);

        //��Ӵ򿪴��ڰ�ť
        openSerialButton.setBounds(250, 100, 300, 50);
        openSerialButton.setBackground(Color.lightGray);
        openSerialButton.setFont(new Font("΢���ź�", Font.BOLD, 20));
        openSerialButton.setForeground(Color.darkGray);
        add(openSerialButton);

        textArea.setBounds(50, 170, 700, 400);
        textArea.setFont(new Font("΢���ź�", Font.PLAIN, 20));
        add(textArea);
        //��Ӵ򿪴��ڰ�ť���¼�����
        openSerialButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    //��ȡ��������
                    String commName = commChoice.getSelectedItem();
                    //��ȡ������
                    String bpsStr = bpsChoice.getSelectedItem();

                    //��鴮�������Ƿ��ȡ��ȷ
                    if (commName == null || commName.equals("")) {
                        JOptionPane.showMessageDialog(null, "û����������Ч���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        //��鲨�����Ƿ��ȡ��ȷ
                        if (bpsStr == null || bpsStr.equals("")) {
                            JOptionPane.showMessageDialog(null, "�����ʻ�ȡ����", "����", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            //�������������ʾ���ȡ��ȷʱ
                            int bps = Integer.parseInt(bpsStr);
                            try {

                                //��ȡָ���˿����������ʵĴ��ڶ���
                                serialPort = SerialTool.openPort(commName, bps);
                                //�ڸô��ڶ�������Ӽ�����
                                SerialTool.addListener(serialPort, new SerialListener());
                                //�����ɹ�������ʾ
                                JOptionPane.showMessageDialog(null, "�򿪴��ڳɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);

                                new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        postion = 0;
                                        for(int i = 0 ; i < 70 ;i++ ){
                                            try{
                                                if(byteList.size() > 0 && byteList.get(0) == (byte) 0xAA)
                                                textArea.append(postion + EncodingFormatUtils.toHexStringAddSpace(byteList) + "\n");

                                                postion = i;
                                                byteList.clear();
                                                SerialTool.sendToPort(serialPort , new byte[]{(byte) 0xAA, (byte) 0x06 , (byte) 0x07 , (byte) i , (byte) 0x02 , (byte) 0x00 , (byte) 0x00 , (byte) 0x00});
                                                Thread.sleep(1000);
                                            }catch (Exception e){

                                            }
                                        }
                                    }
                                }.start();
                            } catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
                                //��������ʱʹ��һ��Dialog��ʾ����Ĵ�����Ϣ
                                JOptionPane.showMessageDialog(null, e1, "����", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
            }
        });


        this.setResizable(false);

        new Thread(new RepaintThread()).start();    //�����ػ��߳�

    }

    /**
     * �������������Ԫ��
     */
    public void paint(Graphics g) {
        g.setColor(Color.gray);
        g.setFont(new Font("΢���ź�", Font.BOLD, 20));
        g.drawString(" ����ѡ�� ", 45, 62);

        g.setColor(Color.gray);
        g.setFont(new Font("΢���ź�", Font.BOLD, 20));
        g.drawString(" �����ʣ� ", 425, 62);

    }

    /**
     * ˫���巽ʽ�ػ������Ԫ�����
     */
    public void update(Graphics g) {
        if (offScreen == null) offScreen = this.createImage(Client.WIDTH, Client.HEIGHT);
        Graphics gOffScreen = offScreen.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.white);
        gOffScreen.fillRect(0, 0, Client.WIDTH, Client.HEIGHT);    //�ػ���������
        this.paint(gOffScreen);    //�ػ�����Ԫ��
        gOffScreen.setColor(c);
        g.drawImage(offScreen, 0, 0, null);    //���»��õĻ�����������ԭ������
    }

    /*
     * �ػ��̣߳�ÿ��30�����ػ�һ�Σ�
     */
    private class RepaintThread implements Runnable {
        public void run() {
            while (true) {
                //�����ػ�����
                repaint();


                //ɨ����ô���
                commList = SerialTool.findPort();
                if (commList != null && commList.size() > 0) {

                    //�����ɨ�赽�Ŀ��ô���
                    for (String s : commList) {

                        //�ô������Ƿ��Ѵ��ڣ���ʼĬ��Ϊ�����ڣ���commList����ڵ���commChoice�ﲻ���ڣ�������ӣ�
                        boolean commExist = false;

                        for (int i = 0; i < commChoice.getItemCount(); i++) {
                            if (s.equals(commChoice.getItem(i))) {
                                //��ǰɨ�赽�Ĵ������Ѿ��ڳ�ʼɨ��ʱ����
                                commExist = true;
                                break;
                            }
                        }

                        if (commExist) {
                            //��ǰɨ�赽�Ĵ������Ѿ��ڳ�ʼɨ��ʱ���ڣ�ֱ�ӽ�����һ��ѭ��
                            continue;
                        } else {
                            //��������������´����������ô��������б�
                            commChoice.add(s);
                        }
                    }

                    //�Ƴ��Ѿ������õĴ���
                    for (int i = 0; i < commChoice.getItemCount(); i++) {

                        //�ô����Ƿ���ʧЧ����ʼĬ��Ϊ�Ѿ�ʧЧ����commChoice����ڵ���commList�ﲻ���ڣ����Ѿ�ʧЧ��
                        boolean commNotExist = true;

                        for (String s : commList) {
                            if (s.equals(commChoice.getItem(i))) {
                                commNotExist = false;
                                break;
                            }
                        }

                        if (commNotExist) {
                            //System.out.println("remove" + commChoice.getItem(i));
                            commChoice.remove(i);
                        } else {
                            continue;
                        }
                    }

                } else {
                    //���ɨ�赽��commListΪ�գ����Ƴ��������д���
                    commChoice.removeAll();
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    String err = ExceptionWriter.getErrorInfoFromException(e);
                    JOptionPane.showMessageDialog(null, err, "����", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }

    }

    /**
     * ���ڲ�����ʽ����һ�����ڼ�����
     *
     * @author zhong
     */
    private class SerialListener implements SerialPortEventListener {

        /**
         * �����ص��Ĵ����¼�
         */
        public void serialEvent(SerialPortEvent serialPortEvent) {

            switch (serialPortEvent.getEventType()) {

                case SerialPortEvent.BI: // 10 ͨѶ�ж�
                    JOptionPane.showMessageDialog(null, "�봮���豸ͨѶ�ж�", "����", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case SerialPortEvent.OE: // 7 ��λ�����������

                case SerialPortEvent.FE: // 9 ֡����

                case SerialPortEvent.PE: // 8 ��żУ�����

                case SerialPortEvent.CD: // 6 �ز����

                case SerialPortEvent.CTS: // 3 �������������

                case SerialPortEvent.DSR: // 4 ����������׼������

                case SerialPortEvent.RI: // 5 ����ָʾ

                case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 ��������������
                    break;

                case SerialPortEvent.DATA_AVAILABLE: // 1 ���ڴ��ڿ�������

                    //System.out.println("found data");
                    byte[] data = null;

                    try {
                        if (serialPort == null) {
                            JOptionPane.showMessageDialog(null, "���ڶ���Ϊ�գ�����ʧ�ܣ�", "����", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            data = SerialTool.readFromPort(serialPort);    //��ȡ���ݣ������ֽ�����
                            for (byte b : data) {
                                byteList.add(b);
                            }

                        }

                    } catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
                        JOptionPane.showMessageDialog(null, e, "����", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);    //������ȡ����ʱ��ʾ������Ϣ���˳�ϵͳ
                    }

                    break;

            }

        }

    }


}
