package io.github.satxm;

import com.dosse.upnp.UPnP;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Window extends JFrame {
    JFrame Window = new JFrame("UPnP 管理器");
    JButton BOpenPort = new JButton("开启");
    JButton BClosePort = new JButton("关闭");
    JButton BExit = new JButton("退出");
    ButtonGroup BTCPUDP = new ButtonGroup();
    JRadioButton BTCP = new JRadioButton("TCP", true);
    JRadioButton BUDP = new JRadioButton("UDP");
    JLabel TStatus = new JLabel("UPnP 状态：");
    JLabel TAvail = new JLabel("已开启");
    JLabel TPort = new JLabel("端口：");
    JLabel TName = new JLabel("名称：");
    JTextField FName = new JTextField("UPnP Client", 32);
    JTextField FPort = new JTextField("", 7);
    JPanel JStatus = new JPanel();
    JPanel JName = new JPanel();
    JPanel JPort = new JPanel();

    public Window() {
        boolean CanUPnP = UPnP.isUPnPAvailable();
        Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Window.setLayout(null);
        Window.setMinimumSize(new Dimension(480, 270));
        Window.setIconImage(
                Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemClassLoader().getResource("icon.png")));

        int BWidth = 64;
        int BHeight = 24;
        int JHeight = 32;
        int YFooter = Window.getHeight() - 64;
        int XSurplus = (Window.getWidth() - BWidth * 4) / 4;
        int JWidth = Window.getWidth() - BWidth * 2;

        TAvail.setForeground(Color.BLUE);
        if (!CanUPnP) {
            TAvail = new JLabel("未开启");
            TAvail.setForeground(Color.RED);
            JOptionPane.showMessageDialog(null, "你的路由器未启用 UPnP 服务！", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        JStatus.setLayout(new FlowLayout(FlowLayout.LEFT));
        JStatus.add(TStatus);
        JStatus.add(TAvail);
        JStatus.setVisible(true);
        JStatus.setSize(JWidth, BHeight);
        JStatus.setLocation(XSurplus + BWidth / 2, 32);

        FPort.setDocument(new Number(5));
        FPort.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ChickPort();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ChickPort();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ChickPort();
            }
        });
        BTCPUDP.add(BTCP);
        BTCPUDP.add(BUDP);
        JPort.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPort.add(TPort);
        JPort.add(FPort);
        JPort.add(BTCP);
        JPort.add(BUDP);
        JPort.setVisible(true);
        JPort.setSize(JWidth, JHeight);
        JPort.setLocation(XSurplus + BWidth / 2, 64);

        JName.setLayout(new FlowLayout(FlowLayout.LEFT));
        JName.add(TName);
        JName.add(FName);
        JName.setVisible(true);
        JName.setSize(JWidth, JHeight);
        JName.setLocation(XSurplus + BWidth / 2, 96);

        BExit.setVisible(true);
        BExit.setSize(BWidth, BHeight);
        BExit.setLocation(XSurplus * 3 + BWidth * 2 + BWidth / 2, YFooter);
        BExit.addActionListener(e -> System.exit(0));

        BOpenPort.setVisible(true);
        BOpenPort.setSize(BWidth, BHeight);
        BOpenPort.setLocation(XSurplus + BWidth / 2, YFooter);
        BOpenPort.setEnabled(UPnP.isUPnPAvailable() && !FPort.getText().isEmpty());
        BOpenPort.addActionListener(e -> {
            int port = Integer.parseInt(FPort.getText());
            String name = FName.getText();
            if (BTCP.isSelected()) {
                if (UPnP.isMappedTCP(port)) {
                    JOptionPane.showMessageDialog(null, "端口" + port + "已被其他 UPnP 服务占用，请更换端口！", "端口占用",
                            JOptionPane.WARNING_MESSAGE);
                } else if (UPnP.openPortTCP(port, name)) {
                    JOptionPane.showMessageDialog(null, "成功映射端口" + port + "！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "无法映射端口" + port + "：未知错误！", "错误", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                if (UPnP.isMappedUDP(port)) {
                    JOptionPane.showMessageDialog(null, "端口" + port + "已被其他 UPnP 服务占用，请更换端口！", "端口占用",
                            JOptionPane.WARNING_MESSAGE);
                } else if (UPnP.openPortUDP(port, name)) {
                    JOptionPane.showMessageDialog(null, "成功映射端口" + port + "！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "无法映射端口" + port + "：未知错误！", "错误", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        BClosePort.setVisible(true);
        BClosePort.setSize(BWidth, BHeight);
        BClosePort.setLocation(XSurplus * 2 + BWidth + BWidth / 2, YFooter);
        BClosePort.setEnabled(UPnP.isUPnPAvailable() && !FPort.getText().isEmpty());
        BClosePort.addActionListener(e -> {
            int port = Integer.parseInt(FPort.getText());
            if (BTCP.isSelected()) {
                if (!UPnP.isMappedTCP(port)) {
                    JOptionPane.showMessageDialog(null, "端口" + port + "未映射！", "端口未映射", JOptionPane.WARNING_MESSAGE);
                } else if (UPnP.closePortTCP(port)) {
                    JOptionPane.showMessageDialog(null, "成功关闭端口" + port + "！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "无法关闭端口" + port + "：未知错误！", "错误", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                if (!UPnP.isMappedUDP(port)) {
                    JOptionPane.showMessageDialog(null, "端口" + port + "未映射！", "端口未映射", JOptionPane.WARNING_MESSAGE);
                } else if (UPnP.closePortUDP(port)) {
                    JOptionPane.showMessageDialog(null, "成功关闭端口" + port + "！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "无法关闭端口" + port + "：未知错误！", "错误", JOptionPane.WARNING_MESSAGE);
                }
            }

        });

        Container contentPane = Window.getContentPane();
        contentPane.add(JStatus);
        contentPane.add(JPort);
        contentPane.add(JName);
        contentPane.add(BExit);
        contentPane.add(BOpenPort);
        contentPane.add(BClosePort);

        Window.setSize(480, 270);
        Window.setLocationRelativeTo(null);
        Window.setVisible(true);

        Window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int YFooter = Window.getHeight() - 96;
                int XSurplus = (Window.getWidth() - BWidth * 4) / 4;
                BExit.setLocation(XSurplus * 3 + BWidth * 2 + BWidth / 2, YFooter);
                BOpenPort.setLocation(XSurplus + BWidth / 2, YFooter);
                BClosePort.setLocation(XSurplus * 2 + BWidth + BWidth / 2, YFooter);
                JStatus.setLocation(XSurplus + BWidth / 2, 32);
                JPort.setLocation(XSurplus + BWidth / 2, 64);
                JName.setLocation(XSurplus + BWidth / 2, 96);
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new Window();
    }

    private void ChickPort() {
        try {
            int port = Integer.parseInt(FPort.getText());
            if (port < 0) {
                BOpenPort.setEnabled(false);
                BClosePort.setEnabled(false);
            } else if (port > 65535) {
                BOpenPort.setEnabled(false);
                BClosePort.setEnabled(false);
            } else {
                BOpenPort.setEnabled(UPnP.isUPnPAvailable() && !FPort.getText().isEmpty());
                BClosePort.setEnabled(UPnP.isUPnPAvailable() && !FPort.getText().isEmpty());
            }
        } catch (NumberFormatException ex) {
            BOpenPort.setEnabled(false);
            BClosePort.setEnabled(false);
        }
    }
}
