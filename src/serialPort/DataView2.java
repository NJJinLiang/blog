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
 * 监测数据显示类
 *
 * @author Zhong
 */
public class DataView2 extends Frame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Client client = null;

    private List<String> commList = null;    //保存可用端口号
    private SerialPort serialPort = null;    //保存串口对象

    private Font font = new Font("微软雅黑", Font.BOLD, 25);


    private Choice commChoice = new Choice();    //串口选择（下拉框）
    private Choice bpsChoice = new Choice();    //波特率选择

    private Button openSerialButton = new Button("打开串口");

    private TextArea textArea = new TextArea("");

    Image offScreen = null;    //重画时的画布

    //设置window的icon
    Toolkit toolKit = getToolkit();
    Image icon = toolKit.getImage(DataView2.class.getResource("computer.png"));

    private List<Byte> byteList = new ArrayList<>();

    int postion = 0;

    /**
     * 类的构造方法
     *
     * @param client
     */
    public DataView2(Client client) {
        this.client = client;
        commList = SerialTool.findPort();    //程序初始化时就扫描一次有效串口
    }

    /**
     * 主菜单窗口显示；
     * 添加Label、按钮、下拉条及相关事件监听；
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
                    //程序退出时关闭串口释放资源
                    SerialTool.closePort(serialPort);
                }
                System.exit(0);
            }

        });


        //添加串口选择选项
        commChoice.setBounds(160, 50, 100, 200);
        //检查是否有可用串口，有则加入选项中
        if (commList == null || commList.size() < 1) {
            JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String s : commList) {
                commChoice.add(s);
            }
        }
        add(commChoice);

        //添加波特率选项
        bpsChoice.setBounds(526, 50, 200, 200);
        bpsChoice.add("1200");
        bpsChoice.add("2400");
        bpsChoice.add("4800");
        bpsChoice.add("9600");
        bpsChoice.add("14400");
        bpsChoice.add("19200");
        bpsChoice.add("115200");
        add(bpsChoice);

        //添加打开串口按钮
        openSerialButton.setBounds(250, 100, 300, 50);
        openSerialButton.setBackground(Color.lightGray);
        openSerialButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
        openSerialButton.setForeground(Color.darkGray);
        add(openSerialButton);

        textArea.setBounds(50, 170, 700, 400);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        add(textArea);
        //添加打开串口按钮的事件监听
        openSerialButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    //获取串口名称
                    String commName = commChoice.getSelectedItem();
                    //获取波特率
                    String bpsStr = bpsChoice.getSelectedItem();

                    //检查串口名称是否获取正确
                    if (commName == null || commName.equals("")) {
                        JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        //检查波特率是否获取正确
                        if (bpsStr == null || bpsStr.equals("")) {
                            JOptionPane.showMessageDialog(null, "波特率获取错误！", "错误", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            //串口名、波特率均获取正确时
                            int bps = Integer.parseInt(bpsStr);
                            try {

                                //获取指定端口名及波特率的串口对象
                                serialPort = SerialTool.openPort(commName, bps);
                                //在该串口对象上添加监听器
                                SerialTool.addListener(serialPort, new SerialListener());
                                //监听成功进行提示
                                JOptionPane.showMessageDialog(null, "打开串口成功", "提示", JOptionPane.INFORMATION_MESSAGE);

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
                                //发生错误时使用一个Dialog提示具体的错误信息
                                JOptionPane.showMessageDialog(null, e1, "错误", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
            }
        });


        this.setResizable(false);

        new Thread(new RepaintThread()).start();    //启动重画线程

    }

    /**
     * 画出主界面组件元素
     */
    public void paint(Graphics g) {
        g.setColor(Color.gray);
        g.setFont(new Font("微软雅黑", Font.BOLD, 20));
        g.drawString(" 串口选择： ", 45, 62);

        g.setColor(Color.gray);
        g.setFont(new Font("微软雅黑", Font.BOLD, 20));
        g.drawString(" 波特率： ", 425, 62);

    }

    /**
     * 双缓冲方式重画界面各元素组件
     */
    public void update(Graphics g) {
        if (offScreen == null) offScreen = this.createImage(Client.WIDTH, Client.HEIGHT);
        Graphics gOffScreen = offScreen.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.white);
        gOffScreen.fillRect(0, 0, Client.WIDTH, Client.HEIGHT);    //重画背景画布
        this.paint(gOffScreen);    //重画界面元素
        gOffScreen.setColor(c);
        g.drawImage(offScreen, 0, 0, null);    //将新画好的画布“贴”在原画布上
    }

    /*
     * 重画线程（每隔30毫秒重画一次）
     */
    private class RepaintThread implements Runnable {
        public void run() {
            while (true) {
                //调用重画方法
                repaint();


                //扫描可用串口
                commList = SerialTool.findPort();
                if (commList != null && commList.size() > 0) {

                    //添加新扫描到的可用串口
                    for (String s : commList) {

                        //该串口名是否已存在，初始默认为不存在（在commList里存在但在commChoice里不存在，则新添加）
                        boolean commExist = false;

                        for (int i = 0; i < commChoice.getItemCount(); i++) {
                            if (s.equals(commChoice.getItem(i))) {
                                //当前扫描到的串口名已经在初始扫描时存在
                                commExist = true;
                                break;
                            }
                        }

                        if (commExist) {
                            //当前扫描到的串口名已经在初始扫描时存在，直接进入下一次循环
                            continue;
                        } else {
                            //若不存在则添加新串口名至可用串口下拉列表
                            commChoice.add(s);
                        }
                    }

                    //移除已经不可用的串口
                    for (int i = 0; i < commChoice.getItemCount(); i++) {

                        //该串口是否已失效，初始默认为已经失效（在commChoice里存在但在commList里不存在，则已经失效）
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
                    //如果扫描到的commList为空，则移除所有已有串口
                    commChoice.removeAll();
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    String err = ExceptionWriter.getErrorInfoFromException(e);
                    JOptionPane.showMessageDialog(null, err, "错误", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }

    }

    /**
     * 以内部类形式创建一个串口监听类
     *
     * @author zhong
     */
    private class SerialListener implements SerialPortEventListener {

        /**
         * 处理监控到的串口事件
         */
        public void serialEvent(SerialPortEvent serialPortEvent) {

            switch (serialPortEvent.getEventType()) {

                case SerialPortEvent.BI: // 10 通讯中断
                    JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case SerialPortEvent.OE: // 7 溢位（溢出）错误

                case SerialPortEvent.FE: // 9 帧错误

                case SerialPortEvent.PE: // 8 奇偶校验错误

                case SerialPortEvent.CD: // 6 载波检测

                case SerialPortEvent.CTS: // 3 清除待发送数据

                case SerialPortEvent.DSR: // 4 待发送数据准备好了

                case SerialPortEvent.RI: // 5 振铃指示

                case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                    break;

                case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据

                    //System.out.println("found data");
                    byte[] data = null;

                    try {
                        if (serialPort == null) {
                            JOptionPane.showMessageDialog(null, "串口对象为空！监听失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            data = SerialTool.readFromPort(serialPort);    //读取数据，存入字节数组
                            for (byte b : data) {
                                byteList.add(b);
                            }

                        }

                    } catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
                        JOptionPane.showMessageDialog(null, e, "错误", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);    //发生读取错误时显示错误信息后退出系统
                    }

                    break;

            }

        }

    }


}
