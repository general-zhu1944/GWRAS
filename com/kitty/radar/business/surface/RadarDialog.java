package com.kitty.radar.business.surface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.eltima.components.ui.DatePicker;
import com.kitty.component.gui.BasicDialog;
import com.kitty.radar.Radar;
import com.kitty.radar.RadarBase;
import com.kitty.radar.RadarParams;
import com.kitty.radar.RainOverlay;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.gui.ImportAreaDialog;
import com.kitty.radar.gui.MainPanel;
import com.kitty.radar.listener.MouseHandler;
import com.kitty.radar.util.CommonProps;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.ConfigInfo;

import cma.music.RetFilesInfo;
import cma.music.client.DataQueryClient;

/**
 * 下载雷达基数据对话框。
 */

public class RadarDialog extends JDialog {
	    public static com.eltima.components.ui.DatePicker datePicker1;
	    public static com.eltima.components.ui.DatePicker datePicker2;	    
	    public static JCheckBox jCheckBox_SurfaceData;
	    public static JButton download;
	    public static JButton querytemper;
	    public static JButton querytd;
	    public static JButton querywindmax;
	    public static JComboBox<String> jcb;
	    public static JTextField rada_id;
	    JLabel label1 = new JLabel("起始：");
        JLabel label2 = new JLabel("结束：");
        JLabel label3 = new JLabel("雷达站号：");
        JLabel label4 = new JLabel("雷达基数据编码：");




    public RadarDialog(ActionListener processor) {      
        super(Radar.radar, "雷达基数据资料下载");
        Dimension d = new Dimension(380, 150);
        this.setSize(d);
        this.setLocation(CommonUtils.getCenterLocation(this.getSize()));
        JPanel elePanel = new JPanel();
        elePanel.setLayout(new FlowLayout(0,5,5));
        datePicker1 = getDatePicker();
        datePicker2 = getDatePicker();
        elePanel.add(label1);
        elePanel.add(datePicker1);
        elePanel.add(label3);
        elePanel.add(label2);
        elePanel.add(datePicker2);
        JPanel elePanel1 = new JPanel();
        elePanel1.setLayout(new FlowLayout(1,5,5));
        elePanel1.add(label3);     
        rada_id = new JTextField(RadarParams.downloadradarstationid, 4);
        elePanel1.add(rada_id);
        elePanel1.add(label4);
        String[] listData = new String[]{"RADA_L2_FMT", "RADA_L2_X_FMT"};
        
        //用count记录ItemListener运行的次数
        int count=0;
        jcb = new JComboBox<String>(listData);

        elePanel1.add(jcb);
        download = new JButton("下载雷达基数据");
        download.setActionCommand(CommonProps.AC_QUERY_RDAR);
        download.addActionListener(processor);       
        elePanel1.add(download);
        JPanel selectrainPanel = new JPanel();
        selectrainPanel.add(download,BorderLayout.SOUTH);
         this.add(selectrainPanel,BorderLayout.SOUTH);
        this.add(elePanel, BorderLayout.NORTH);
        this.add(elePanel1, BorderLayout.CENTER);
        this.setVisible(true);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	RadarParams.downloadradarstationid= rada_id.getText();
            }
       });
    }
    public static DatePicker getDatePicker() {
        // 显示格式
        String DefaultFormat = "yyyy-MM-dd HH:mm";
        // 当前时间
        Date date = new Date();
        // 设置字体
        Font font = new Font("Times New Roman", Font.BOLD, 14);
        Dimension dimension = new Dimension(130, 24);
        // 高亮显示的日期
        int[] hilightDays = { 1, 3, 5, 7 };
        // 灰色显示的日期
        //int[] disabledDays = { 4, 6, 5, 9 };        
        //构造方法（初始时间，时间显示格式，字体，控件大小）
        DatePicker datepick = new DatePicker(date, DefaultFormat, font, dimension);
        //设置起始位置
        datepick.setLocation(137, 83);
        // 设置一个月份中需要高亮显示的日子
        datepick.setHightlightdays(hilightDays, Color.red);
        // 设置一个月份中不需要的日子，呈灰色显示
       // datepick.setDisableddays(disabledDays);
        // 设置国家
        datepick.setLocale(Locale.CANADA);
        // 设置时钟面板可见
        datepick.setTimePanleVisible(true);       
        return datepick;
    }
    
	public static void downradar(String startTime,String endTime,String radardatatype,String id,String cn,String pw,String savedir) {
		RadarParams.downloadradarstationid= rada_id.getText();	//存储雷达站号到配置文件
		/* 1. 定义client对象 */
		DataQueryClient client = new DataQueryClient() ;
		
		/* 2. 调用方法的参数定义，并赋值 */
		/* 2.1 用户名&密码 */
		String userId = cn ;
		String pwd = pw ;
		/* 2.2  接口ID */
		String interfaceId = "getRadaFileByTimeRangeAndStaId" ;
		/* 2.3  接口参数，多个参数间无顺序 */
		HashMap<String, String> params = new HashMap<String, String>();
		//必选参数
	    params.put("dataCode", String.format("%s" , radardatatype)); //资料：质控前原始格式多普勒雷达基数据RADA_L2_X_FMT  RADA_L2_FMT
	    params.put("timeRange", String.format("(%s,%s]" ,startTime, endTime)); //时间段，前闭后开
		params.put("staIds", String.format("%s" , id)); //雷达站
		//可选参数
		/* 2.4 返回对象 */
		String saveDir = savedir ;
		RetFilesInfo retFilesInfo = new RetFilesInfo() ;


		/* 3. 调用接口 */
		try {
			//初始化接口服务连接资源
			client.initResources() ;
			//调用接口
			StringBuffer retStr=null;
			 File file = new File(saveDir);
			if(!file.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "请设置存储路径！" );	
				return;
			}
			
			int rst = client.callAPI_to_downFile(userId, pwd, interfaceId, params, saveDir, retFilesInfo);
			//输出结果
			JOptionPane.showMessageDialog(null, " 返回码："+ rst );	
			if(rst == 0) { //正常返回	
				JOptionPane.showMessageDialog(null, retFilesInfo.fileInfos[0].fileName +".....,下载成功！");	
				//System.out.printf( retFilesInfo.fileInfos[0].fileName ) ;
			
			} else { //异常返回
				JOptionPane.showMessageDialog(null, "下载失败！");				

			}
		} catch (Exception e) {
			//异常输出
			e.printStackTrace() ;
		} finally {
			//释放接口服务连接资源
			client.destroyResources() ;
		}
	}
	


    public void actionPerformed(ActionEvent e) {
        
                CommonUtils.alert("请选择要删除的区域", null);
      
    }

}
