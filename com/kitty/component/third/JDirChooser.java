package com.kitty.component.third;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;

import com.kitty.radar.util.CommonUtils;

/**
 * <p>Title: OpenSwing</p>
 * <p>Description: JDirChooser 目录选择器</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
 *  && <a href="mailto:zt9788@126.com">zt9788</a>
 * @version 1.0
 */
public class JDirChooser extends JDialog implements TreeSelectionListener, Action, Serializable {
    
    private File selectedFile;

    JPanel pCenter = new JPanel(new BorderLayout());

    JFileTree fileTree = new JFileTree();

    JScrollPane spTree = new JScrollPane(fileTree);

    JPanel pSouth = new JPanel(new BorderLayout());

    JPanel pButtons = new JPanel(new BorderLayout(0, 0));

    JPanel pResult = new JPanel(new BorderLayout());

    JLabel lbFolder = new JLabel("文件夹: ");

    JTextField txtResult = new JTextField();

    JButton bttCreateNew = new JButton("新建文件夹(M)");

    JButton bttCancel = new JButton("取消");

    JButton bttOK = new JButton("确定");

    JLabel lbView = new JLabel();

    JPanel pAdjust = new JPanel() {
        public void paintChildren(Graphics g) {
            super.paintChildren(g);
            int w = getWidth();
            int h = getHeight();
            Color oldColor = g.getColor();
            // draw ///
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    g.setColor(Color.white);
                } else {
                    g.setColor(new Color(184, 181, 161));
                }
                g.fillRect(w - 3 - i, h - 3 - i, 2, 2);
                g.fillRect(w - 7 - i, h - 3 - i, 2, 2);
                g.fillRect(w - 11 - i, h - 3 - i, 2, 2);
                g.fillRect(w - 3 - i, h - 7 - i, 2, 2);
                g.fillRect(w - 7 - i, h - 7 - i, 2, 2);
                g.fillRect(w - 3 - i, h - 11 - i, 2, 2);
            }
            g.setColor(oldColor);
        }
    };

    MouseInputAdapter adjustWindowListener = new MouseInputAdapter() {
        Point oldP = null;

        public void mouseDragged(MouseEvent e) {
            if (oldP != null) {
                Point newP = e.getPoint();
                JDirChooser c = JDirChooser.this;
                c.setBounds(c.getX(), c.getY(), c.getWidth() + (newP.x - oldP.x), c.getHeight()
                        + (newP.y - oldP.y));
                c.validate();
                oldP = newP;
            }
        }

        public void mouseMoved(MouseEvent e) {
            Component c = e.getComponent();
            Rectangle r = new Rectangle(c.getWidth() - 12, 0, 12, c.getHeight());
            if (r.contains(e.getPoint())) {
                JDirChooser.this.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            } else {
                JDirChooser.this.setCursor(Cursor.getDefaultCursor());
            }
        }

        public void mousePressed(MouseEvent e) {
            Component c = e.getComponent();
            Rectangle r = new Rectangle(c.getWidth() - 12, 0, 12, c.getHeight());
            if (r.contains(e.getPoint())) {
                oldP = e.getPoint();
            } else {
                oldP = null;
            }
        }

        public void mouseExited(MouseEvent e) {
            JDirChooser.this.setCursor(Cursor.getDefaultCursor());
        }

        public void mouseReleased(MouseEvent e) {
            oldP = null;
        }
    };

    public static File showDialog(Component c, String title, boolean modal, final File initDir,
            String msg) {
        final JDirChooser dialog;
        Window owner = getRootWindow(c);
        if (owner instanceof Dialog) {
            dialog = new JDirChooser((Dialog) owner, title, modal);
        } else if (owner instanceof Frame) {
            dialog = new JDirChooser((Frame) owner, title, modal);
        } else {
            dialog = new JDirChooser();
            dialog.setTitle(title);
        }
        if (msg != null) {
            dialog.setMsg(msg);
        }
        if (initDir != null) {
            dialog.addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent e) {
                    dialog.setSelectFile(initDir);
                }
            });
        }
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((d.width - dialog.getSize().width) / 2,
                (d.height - dialog.getSize().height) / 2);
        dialog.setVisible(true);
        return dialog.getSelectFile();
    }

    /**
     * 取得根窗口
     * @param c Component
     * @return Window
     */
    static Window getRootWindow(Component c) {
        if (c == null)
            return null;
        Container parent = c.getParent();
        if (c instanceof Window)
            return (Window) c;
        while (!(parent instanceof Window))
            parent = parent.getParent();
        return (Window) parent;
    }

    public JDirChooser(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JDirChooser(Dialog frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JDirChooser() {
        this((Frame) null, "", false);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        JPanel contentPane = (JPanel) this.getContentPane();
        spTree.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        fileTree.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));
        fileTree.setUI(new BasicTreeUI() {
            protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {

            }

            protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {

            }
        });
        pCenter.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        lbView.setPreferredSize(new Dimension(190, 40));
        pResult.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        pAdjust.setPreferredSize(new Dimension(10, 12));
        contentPane.add(pCenter, BorderLayout.CENTER);
        pCenter.add(spTree, BorderLayout.CENTER);
        pCenter.add(pSouth, BorderLayout.SOUTH);
        pResult.add(txtResult, BorderLayout.CENTER);
        pResult.add(lbFolder, BorderLayout.WEST);
        pSouth.add(pButtons, BorderLayout.CENTER);
        pButtons.add(bttCreateNew, BorderLayout.WEST);
        JPanel pb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pButtons.add(pb, BorderLayout.CENTER);
        bttOK.setPreferredSize(new Dimension(75, 24));
        pb.add(bttOK);
        bttCancel.setPreferredSize(new Dimension(75, 20));
        pButtons.add(bttCancel, BorderLayout.EAST);
        bttCreateNew.setMnemonic(KeyEvent.VK_M);
        bttCreateNew.setPreferredSize(new Dimension(105, 24));
        pSouth.add(pResult, BorderLayout.NORTH);
        pCenter.add(lbView, BorderLayout.NORTH);
        contentPane.add(pAdjust, BorderLayout.SOUTH);
        this.getRootPane().setDefaultButton(bttOK);
        this.setSize(326, 322);
        this.setResizable(false);
        this.txtResult.setEditable(false);
        this.txtResult.setBackground(Color.white);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        fileTree.addTreeSelectionListener(this);
        CommonUtils.addEscAction(contentPane, this);
        bttOK.setEnabled(false);
        bttCreateNew.setEnabled(false);
        bttOK.addActionListener(this);
        bttCancel.addActionListener(this);
        bttCreateNew.addActionListener(this);

        pAdjust.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pAdjust.addMouseListener(adjustWindowListener);
        pAdjust.addMouseMotionListener(adjustWindowListener);
    }

    public void setMsg(String msg) {
        this.lbView.setText(msg);
    }

    public String getMsg() {
        return this.lbView.getText();
    }

    public void valueChanged(TreeSelectionEvent event) {
        File f = fileTree.getSelectFile();
        boolean enabled = (f != null);
        bttOK.setEnabled(enabled);
        enabled = enabled && JFileTree.fileSystemView.isFileSystem(f);
        if (f != null && JFileTree.fileSystemView.isDrive(f)) {
            enabled = enabled && f.canWrite();
        }
        bttCreateNew.setEnabled(enabled);
        if (f != null) {
            txtResult.setText(JFileTree.fileSystemView.getSystemDisplayName(f));
            //fileTree.setEditable(f.renameTo(f));
        }
    }

    public JFileTree getFileTree() {
        return this.fileTree;
    }

    public void setFileTree(JFileTree tree) {
        if (tree == null || tree == this.fileTree) {
            return;
        }
        this.spTree.getViewport().setView(tree);
        this.spTree.doLayout();
    }

    public File getSelectFile() {
        return selectedFile;
    }

    public void setSelectFile(File f) {
        try {
            fileTree.setSelectFile(f);
        } catch (Exception e) {
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Object obj = actionEvent.getSource();
        if (obj == bttCreateNew) {
            String dirName = JOptionPane.showInputDialog(this, null, "新建文件夹");
            if (dirName == null || dirName.trim().length() == 0) {
                return;
            }
            File f = fileTree.getSelectFile();
            f = new File(f.getAbsolutePath() + File.separator + dirName);
            if (f.mkdir()) {
                fileTree.getSelectFileNode().removeAllChildren();
                fileTree.getSelectFileNode().setExplored(false);
                this.setSelectFile(f);
            } else {
                JOptionPane.showMessageDialog(this, "新建文件夹错误，请确认名称是否正确", null,
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (obj == bttOK) {
                selectedFile = fileTree.getSelectFile();
            }
            this.dispose();
        }
    }

    public Object getValue(String key) {
        return null;
    }

    public void putValue(String key, Object value) {
        
    }

}
