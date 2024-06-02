package com.kitty.radar;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.kitty.radar.business.area.Area;
import com.kitty.radar.business.area.AreaDialog;
import com.kitty.radar.business.surface.SurfaceDialog;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.gui.GUIManager;
import com.kitty.radar.util.CommonUtils;
import com.kitty.radar.util.PositionUtils;
import com.kitty.radar.util.RadarUtils;

import cma.music.client.DataQueryClient;
import wContour.Contour;
import wContour.Interpolate;
import wContour.Legend;
import wContour.Global.Border;
import wContour.Global.BorderLine;
import wContour.Global.LPolygon;
import wContour.Global.LegendPara;
import wContour.Global.PointD;
import wContour.Global.PolyLine;



public class RainOverlay {
	
	public RainOverlay(RadarBase radarBase) {
		this.radarBase = radarBase;
	}
	
	private RadarBase radarBase;

    public BufferedImage image;
    public double[][] _gridData = null;
    public double[][] _discreteData = null;
    public double[] _X = null;
    public double[] _Y = null;
    Color[] _colors = null;
    public double _undefData = -9999.0;
    Color _startColor = Color.blue;
    Color _endColor = Color.red;
    public boolean update = true;
    public String element;//指定显示地面气象要素

    // ===================== Rain Variables ========================

    public static boolean rain_on = true;
    public boolean _drawDiscreteData = false;
    public boolean _drawGridData = false;
    public boolean _drawBorderLine = false;
    public boolean _drawContourLine = false;
    public boolean _drawContourPolygon = false;
    public boolean _drawClipped = false;
    List<Border> _borders = new ArrayList<Border>();
    List<PolyLine> _contourLines = new ArrayList<PolyLine>();
    List<PolyLine> _clipContourLines = new ArrayList<PolyLine>();
    List<wContour.Global.Polygon> _contourPolygons = new ArrayList<wContour.Global.Polygon>();
    List<wContour.Global.Polygon> _clipContourPolygons = new ArrayList<wContour.Global.Polygon>();
    List<LPolygon> _legendPolygons = new ArrayList<LPolygon>();
    List<List<PointD>> _clipLines = new ArrayList<List<PointD>>();
    double[] values = null;
    double[] valuesrain = new double[]{2, 5, 10, 20, 25, 50, 100,150};
    double[] valuestemper = new double[]{10, 15, 20, 25, 30, 33, 36,39};
    double[] valuestd = new double[]{16, 18, 20, 22, 24, 26, 28, 30};
    double[] valueswindmax = new double[]{ 6, 10, 14, 18, 22, 26, 30, 34};
    String[] unites=new String[] {"mm","°C","m/s"};
    String unite=null;
    
    private static boolean mock = false;


    public class JsonParser //获取地面日资料jons格式数据结构
    {
        public String returnCode;
        public String returnMessage;
        public String rowCount;
        public String colCount;
        public String requestParams;
        public String requestTime;
        public String responseTime;
        public String takeTime;
        public String fieldNames ;
        public String fieldUnits;
    }
    public class JsonParserrain extends JsonParser //获取地面日资料jons格式数据结构
    {
        public List<Datammr> DS;
    }
    public class JsonParsertemper extends JsonParser //获取地面日资料jons格式数据结构
    {
        public List<Datatemper> DS;
    }
    public class JsonParsertd extends JsonParser //获取地面日资料jons格式数据结构
    {
        public List<Datatd> DS;
    }
    public class JsonParserwindmax extends JsonParser //获取地面日资料jons格式数据结构
    {
        public List<Datawindmax> DS;
    }
    
    public class Datammr //获取地面分钟值资料分钟累计雨量
    {
        public String Station_Id_C;
        public String Lat;
        public String Lon; 
        public String SUM_PRE; 
        public String COUNT_PRE;//COUNT_PRE_1H
    }
    public class Datatemper //获取地面小时温度
    {
        public String Station_Id_C;
        public String Lat;
        public String Lon; 
        public String TEM; 
    }
    
    public class Datatd //获取地面小时露点
    {
        public String Station_Id_C;
        public String Lat;
        public String Lon; 
        public String DPT; 
    }
    
    public class Datawindmax //获取地面小时极大风速
    {
        public String Station_Id_C;
        public String Lat;
        public String Lon; 
        public String WIN_S_Inst_Max; 
    }

    public BufferedImage drawRain() {
    	 if (image == null) {
             image = new BufferedImage(this.radarBase.getWidth(), this.radarBase.getHeight(), BufferedImage.TYPE_INT_ARGB);
             update = true;
         }
         if (update) {
             Graphics2D g = (Graphics2D) image.createGraphics();
             g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
             g.fillRect(0, 0, this.radarBase.getWidth(), this.radarBase.getHeight());             
             g.dispose();
             try {
                 if (rain_on) {
                	 if(element=="rain")
                	 {
                		 unite=unites[0];
                		 values=valuesrain;   
                		 _startColor = Color.blue;
                	 }
                	 if(element=="temper")
                	 {
                		 unite=unites[1];
                		 values=valuestemper;  
                		 _startColor = Color.yellow;
                	 }
                	 if(element=="td")
                	 {
                		 unite=unites[1];
                		 values=valuestd;   
                		 _startColor = Color.yellow;
                	 }
                	 if(element=="windmax")
                	 {
                		 unite=unites[2];
                		 values=valueswindmax;   
                		 _startColor = Color.blue;            		 
                	 }
                	 contourprepare();
                	  if (_drawContourPolygon && this._contourPolygons.size() > 0) {
                		  drawContourPolygons();
                		  drawLegend(unite);
                      }             
                                        
                	 if (_drawDiscreteData) {
                	 drawDiscreteData();
                	 }
                	
                	 if (_drawContourLine && this._contourLines.size() > 0) {
                	 drawBorder();
                	 drawContourLines();
                	 
                	 }
                	
                	
                 }              
                 update = false;
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         return image;
     
    }
    
    private double[][] mockDiscreteData() {
		double[][] S = new double[3][100];
		for(int i =0; i<100; i++) {
			S[0][i] = 104.5702-((double)i)/10;
			S[1][i] = 28.81781529;
			S[2][i] = 10+i;
		}
		this._discreteData = S;
		return S;
    }
    
    public void CreateDiscreteData(Date t1,String element) {
    	if(mock) {
    		this.mockDiscreteData();
    		return;
    	}
    	/* 1. 定义client对象 */
		DataQueryClient client = new DataQueryClient() ;		
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm00");
    	String date1 = sdf.format(t1);
    	String userId =RadarParams.userId;// cn ;
		String pwd = RadarParams.pw;//pw ;
		String threshold=SurfaceDialog.textrain.getText().toString();
		/* 2.2  接口ID */
		String interfaceId = "getSurfEleInRectByTime" ;
//		String minLon="105.0";
//		String maxLon="107.0";
//		String minLat="30.0";
//		String maxLat="32.0";

		String minLon=(radarBase.getLongitude()-1)+"";
		String maxLon=(radarBase.getLongitude()+1)+"";
		String minLat=(radarBase.getLatitude()-1)+"";
		String maxLat=(radarBase.getLatitude()+1)+"";
		String eledata="";
		if(element=="temper")
		{
			eledata="TEM";
		}
		if(element=="td")
		{
			eledata="DPT";
		}
		if(element=="windmax")
		{
			eledata="WIN_S_Inst_Max";
		}
		
		/* 2.3  接口参数，多个参数间无顺序 */
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("dataCode","SURF_CHN_MUL_HOR"); 
	    params.put("times", String.format("%s" ,date1.substring(0, 10))+"0000"); //时间段，前闭后开
	    params.put("elements", "Station_Id_C,Lat,Lon,"+eledata) ;//检索要素：站号、站名、小时降水、气压、相对湿度、能见度、2分钟平均风速、2分钟风向
		params.put("minLat",  minLat); //经纬度范围
		params.put("maxLat",  maxLat); //经纬度范围
		params.put("minLon",  minLon); //经纬度范围
		params.put("maxLon",  maxLon); //经纬度范围
		params.put("eleValueRanges", eledata+String.format(":[%s,99999))",threshold));
		String dataFormat = "json" ;
	    StringBuffer retStr = new StringBuffer() ;
        double[][] S = null;
		//可选参数
		/* 2.4 返回对象 */
		try {
		      //初始化接口服务连接资源
		      client.initResources() ;
		      //调用接口
		      int rst = client.callAPI_to_serializedStr(userId, pwd, interfaceId, params, dataFormat, retStr) ;	
		      
		      //输出结果
		      if(rst == 0) { //正常返回
		    	  if(element=="td")
            	  {
		          final  JsonParsertd Result = new Gson().fromJson(retStr.toString(), JsonParsertd.class);
		          if(Result.returnCode.equals("0"))
		           {
		        	 JOptionPane.showMessageDialog(null, "下载数据成功！");
		        	
		        	 final List<Datatd> modelList = Result.DS;		        	 
		              S = new double[3][modelList.size()];
		              for (int ii = 0; ii < modelList.size(); ii++) {
		            	 
		        	       Datatd datammr= modelList.get(ii);
		        		  S[0][ii] =Double.parseDouble(datammr.Lon);
		        		  S[1][ii] = Double.parseDouble(datammr.Lat);
		        		  S[2][ii] = Double.parseDouble(datammr.DPT);	            	  
	            	  }
		        }
		        else
		        {
			    	JOptionPane.showMessageDialog(null, retStr); 			    	
		        }	
            	  }
		    	  if(element=="temper")
            	  {
		          final  JsonParsertemper Result = new Gson().fromJson(retStr.toString(), JsonParsertemper.class);
		          if(Result.returnCode.equals("0"))
		           {
		        	 JOptionPane.showMessageDialog(null, "下载数据成功！");
		        	
		        	 final List<Datatemper> modelList = Result.DS;		        	 
		              S = new double[3][modelList.size()];
		              for (int ii = 0; ii < modelList.size(); ii++) {
		            	 
		        	       Datatemper datammr= modelList.get(ii);
		        		  S[0][ii] =Double.parseDouble(datammr.Lon);
		        		  S[1][ii] = Double.parseDouble(datammr.Lat);
		        		  S[2][ii] = Double.parseDouble(datammr.TEM);	            	  
	            	  }
		        }
		        else
		        {
			    	JOptionPane.showMessageDialog(null, retStr); 			    	
		        }	
            	  }
		    	  if(element=="windmax")
            	  {
		          final  JsonParserwindmax Result = new Gson().fromJson(retStr.toString(), JsonParserwindmax.class);
		          if(Result.returnCode.equals("0"))
		           {
		        	 JOptionPane.showMessageDialog(null, "下载数据成功！");
		        	
		        	 final List<Datawindmax> modelList = Result.DS;		        	 
		              S = new double[3][modelList.size()];
		              for (int ii = 0; ii < modelList.size(); ii++) {
		            	 
		        	       Datawindmax datammr= modelList.get(ii);
		        		  S[0][ii] =Double.parseDouble(datammr.Lon);
		        		  S[1][ii] = Double.parseDouble(datammr.Lat);
		        		  S[2][ii] = Double.parseDouble(datammr.WIN_S_Inst_Max);	            	  
	            	  }
		        }
		        else
		        {
			    	JOptionPane.showMessageDialog(null, retStr); 			    	
		        }	
            	  }
		      } else { //异常返回
		    	  JOptionPane.showMessageDialog(null, "下载数据失败！"); 
		      }
		    } catch (Exception e) {
		      e.printStackTrace() ;
		    } finally {
		      //释放接口服务连接资源
		      client.destroyResources() ;

		    }     
        _discreteData = S;

    }

    public void CreateDiscreteData(Date t1,Date t2) {
    	if(mock) {
    		mockDiscreteData();
    		return;
    	}
    	/* 1. 定义client对象 */
		DataQueryClient client = new DataQueryClient() ;		
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm00");
    	String date1 = sdf.format(t1);
        String date2 = sdf.format(t2);
    	String userId =RadarParams.userId;// cn ;
		String pwd = RadarParams.pw;//pw ;
		String threshold=SurfaceDialog.textrain.getText().toString();
		/* 2.2  接口ID */
		String interfaceId = "statSurfEleInRect" ;
//		String minLon="105.0";
//		String maxLon="107.0";
//		String minLat="30.0";
//		String maxLat="32.0";
		String minLon=(radarBase.getLongitude()-1)+"";
		String maxLon=(radarBase.getLongitude()+1)+"";
		String minLat=(radarBase.getLatitude()-1)+"";
		String maxLat=(radarBase.getLatitude()+1)+"";
		/* 2.3  接口参数，多个参数间无顺序 */
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("dataCode","SURF_CHN_PRE_MIN"); 
	    params.put("timeRange", String.format("(%s,%s]" ,date1, date2)); //时间段，前闭后开
	    params.put("elements", "Station_Id_C,Lat,Lon") ;//检索要素：站号、站名、小时降水、气压、相对湿度、能见度、2分钟平均风速、2分钟风向
		params.put("minLat",  minLat); //经纬度范围
		params.put("maxLat",  maxLat); //经纬度范围
		params.put("minLon",  minLon); //经纬度范围
		params.put("maxLon",  maxLon); //经纬度范围
		params.put("statEles","SUM_PRE,COUNT_PRE"); 
		params.put("statEleValueRanges", String.format("SUM_PRE:[%s,99999))",threshold));
//		
//		String interfaceId = "statSurfPreInRect" ;
//		String minLon="105.0";
//		String maxLon="107.0";
//		String minLat="30.0";
//		String maxLat="32.0";
//		/* 2.3  接口参数，多个参数间无顺序 */
//		HashMap<String, String> params = new HashMap<String, String>();
//	    params.put("timeRange", String.format("(%s,%s]" ,date1, date2)); //时间段，前闭后开
//	    params.put("elements", "Station_Id_C,Lat,Lon") ;//检索要素：站号、站名、小时降水、气压、相对湿度、能见度、2分钟平均风速、2分钟风向
//		params.put("minLat",  minLat); //经纬度范围
//		params.put("maxLat",  maxLat); //经纬度范围
//		params.put("minLon",  minLon); //经纬度范围
//		params.put("maxLon",  maxLon); //经纬度范围
//		params.put("statEleValueRanges", String.format("SUM_PRE_1h:[%s,99999))",threshold));
		String dataFormat = "json" ;
	    StringBuffer retStr = new StringBuffer() ;
        double[][] S = null;
		//可选参数
		/* 2.4 返回对象 */
		try {
		      //初始化接口服务连接资源
		      client.initResources() ;
		      //调用接口
		      int rst = client.callAPI_to_serializedStr(userId, pwd, interfaceId, params, dataFormat, retStr) ;	
		      
		      //输出结果
		      if(rst == 0) { //正常返回
		      final  JsonParserrain Result = new Gson().fromJson(retStr.toString(), JsonParserrain.class);
		        if(Result.returnCode.equals("0"))
		        {
		        	 JOptionPane.showMessageDialog(null, "下载数据成功！"); 
		        	 final List<Datammr> modelList = Result.DS;		        	 
		              S = new double[3][modelList.size()];
		              for (int ii = 0; ii < modelList.size(); ii++) {
		        	     Datammr datammr= modelList.get(ii);
		        		  S[0][ii] =Double.parseDouble(datammr.Lon);
		        		  S[1][ii] = Double.parseDouble(datammr.Lat);
		        		  S[2][ii] = Double.parseDouble(datammr.SUM_PRE);
		        	 }		         
		        }
		        else
		        {
			    	JOptionPane.showMessageDialog(null, retStr); 
//			    	Cursor oldCursor = Radar.radar.getCursor();
//			    	JOptionPane.showMessageDialog(null,oldCursor.getName()); 
			    	
		        }		        
		      } else { //异常返回
		    	  JOptionPane.showMessageDialog(null, "下载数据失败！"); 
		       // System.out.printf( "\treturn code: %d. \n", rst ) ;
		      }
		    } catch (Exception e) {
		      //异常输出

		      e.printStackTrace() ;
		    } finally {
		      //释放接口服务连接资源
		      client.destroyResources() ;

		    }     
		
		//demo:
//		S = new double[3][2000];
//		for(int i =0; i<500; i++) {
//			S[0][i] = 10457.02-((double)i)/10;
//			S[1][i] = 2881.781529;
//			S[2][i] = 10+i;
//		}
//		for(int i =0; i<500; i++) {
//			S[0][i+500] = 10457.02-((double)i)/10;
//			S[1][i+500] = 2882.781529;
//			S[2][i+500] = 10+i;
//		}
//		for(int i =0; i<500; i++) {
//			S[0][i+1000] = 10457.02-((double)i)/10;
//			S[1][i+1000] = 2883.781529;
//			S[2][i+1000] = 10+i;
//		}
//		for(int i =0; i<500; i++) {
//			S[0][i+1500] = 10457.02-((double)i)/10;
//			S[1][i+1500] = 2884.781529;
//			S[2][i+1500] = 10+i;
//		}
        _discreteData = S;

    }

    public void InterpolateData(int rows, int cols) {
        double[][] dataArray = null;
        //double XDelt = 0;
        //double YDelt = 0;
        //---- Generate Grid Coordinate           
        double Xlb = radarBase.getLongitude()-1;
        double Ylb = radarBase.getLatitude()-1;
        double Xrt = radarBase.getLongitude()+1;
        double Yrt = radarBase.getLatitude()+1;
        //XDelt = this.drawingPanel1.getWidth() / cols;
        //YDelt = this.drawingPanel1.getHeight() / rows;

        _X = new double[cols];
        _Y = new double[rows];
        
        Interpolate.CreateGridXY_Num(Xlb, Ylb, Xrt, Yrt, _X, _Y);// /** @param Xlb X left bottom   @param Ylb Y left bottom @param Xrt X right top @param Yrt Y right top

        dataArray = new double[rows][cols];
       // dataArray = Interpolate.Interpolation_IDW_Neighbor(_discreteData, _X, _Y, 1, _undefData);
       dataArray = Interpolate.Interpolation_IDW_Radius(_discreteData, _X, _Y, 4, 0.2, _undefData);

        _gridData = dataArray;
    }
    private void drawDiscreteData() {
    	   Graphics2D g = (Graphics2D) image.createGraphics();
    	   if(element=="temper"||element=="td")
      	 {
    		   g.setPaint(Color.gray); 
      	 }
    	   else
    	   {
           g.setPaint(new Color(214, 177, 159));  
    	   }
        if (_discreteData != null) {
    	     XYCoord raincoord=null;
            for (int i = 0; i < _discreteData[0].length; i++) {
            	 raincoord= PositionUtils.toXYCoord2(_discreteData[0][i], _discreteData[1][i], radarBase);
               // int[] sxy = ToScreen(_discreteData[0][i], _discreteData[1][i]);
               // g.setColor(Color.red);
               // g.fillOval(raincoord.x, raincoord.y, 4, 4);
                 if (_discreteData[2][i] >= 0.1)
                    g.drawString(String.format("%.1f",(_discreteData[2][i])).toString() ,raincoord.x, raincoord.y);
            }
        }
    
        g.dispose();
    }
    private void contourprepare() {
    	if(_discreteData !=null)
    	{
    	    int rows = 100;
            int cols = 100;           
            ClearObjects();    
            InterpolateData(rows, cols);                 
            TracingContourLines();
            SmoothLines();
            GetEcllipseClipping();
            ClipLines();
           TracingPolygons();
            ClipPolygons();           
          // SetCoordinate();
           CreateLegend();   		
    	}
    }
    private void drawContourLines() {
    	  Graphics2D g = (Graphics2D) image.createGraphics();   	             
        List<PolyLine> drawLines = _contourLines;
        if (_drawClipped) {
            drawLines = _clipContourLines;
        }
        XYCoord raincoord;
        PointD aPoint;
        for (int i = 0; i < drawLines.size(); i++) {
            PolyLine aLine = drawLines.get(i);
            int len = aLine.PointList.size();
            int[] xPoints = new int[len];
            int[] yPoints = new int[len];
            for (int j = 0; j < len; j++) {
                aPoint = aLine.PointList.get(j);
              //  int[] sxy = ToScreen(aPoint.X, aPoint.Y);
                raincoord= PositionUtils.toXYCoord2(aPoint.X, aPoint.Y, radarBase);
                xPoints[j] = raincoord.x;
                yPoints[j] = raincoord.y;
            }
            g.setColor(Color.WHITE);
            //g.drawString("hh", xPoints[len-2], xPoints[len-2]);

            g.drawPolyline(xPoints, yPoints, len);
        }
    }
    private void drawBorder() {
    	 Graphics2D g = (Graphics2D) image.createGraphics();   
    	 XYCoord raincoord;
        PointD aPoint;
        for (int i = 0; i < _borders.size(); i++) {
            Border aBorder = _borders.get(i);
            for (int j = 0; j < aBorder.getLineNum(); j++) {
                BorderLine bLine = aBorder.LineList.get(j);
                int len = bLine.pointList.size();
                int[] xPoints = new int[len];
                int[] yPoints = new int[len];

                for (int k = 0; k < len; k++) {
                    aPoint = bLine.pointList.get(k);
                   // int[] sxy = ToScreen(aPoint.X, aPoint.Y);
                    raincoord= PositionUtils.toXYCoord2(aPoint.X, aPoint.Y, radarBase);
                    xPoints[k] = raincoord.x;
                    yPoints[k] = raincoord.y;
                }
                g.setColor(Color.white);
                g.drawPolyline(xPoints, yPoints, len);

            }
        }
    }
    private void drawLegend(String unite1) {
    	  Graphics2D g = (Graphics2D) image.createGraphics(); 
        if (_legendPolygons.size() > 0) {
            LPolygon aLPolygon;
            int i, j;
            List<Double> values1 = new ArrayList<Double>();
            for (double v : values) {
                values1.add(v);
            }
            XYCoord raincoord;
            PointD aPoint;
            for (i = 0; i < _legendPolygons.size(); i++) {
                aLPolygon = _legendPolygons.get(i);
                double aValue = aLPolygon.value;
                int idx = values1.indexOf(aValue) + 1;
                Color aColor;
                if (aLPolygon.isFirst) {
                    aColor = _colors[0];
                } else {
                    aColor = _colors[idx];
                }
                List<PointD> newPList = aLPolygon.pointList;

                int len = newPList.size();
                GeneralPath drawPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, len);
                int dx = 0, dy = 0;
                for (j = 0; j < len; j++) {
                    aPoint = newPList.get(j);
                    int[] sxy = ToScreen(aPoint.X, aPoint.Y);
                   // raincoord= PositionUtils.toXYCoord2(aPoint.X, aPoint.Y);
                    if (j == 0) {
                        drawPolygon.moveTo(sxy[0], sxy[1]);
                    } else {
                        drawPolygon.lineTo(sxy[0], sxy[1]);
                        if (j == 2) {
                            dx = sxy[0];
                            dy = sxy[1];
                        }
                    }
                }
                drawPolygon.closePath();

                g.setColor(aColor);
                g.fill(drawPolygon);
                g.setColor(Color.WHITE);
                g.draw(drawPolygon);

                if (i < _legendPolygons.size() - 1) {
                	if(i==0)
                	{
                		g.drawString(unite1, dx + 15, dy-15);
                		g.drawString(String.valueOf(values[i]), dx + 15, dy+5);
                	}
                	else
                	{
                    g.drawString(String.valueOf(values[i]), dx + 5, dy+5);
                	}
                }
            }
        }
    }
    private void drawContourPolygons() {
    	 Graphics2D g = (Graphics2D) image.createGraphics();   	
        List<wContour.Global.Polygon> drawPolygons = _contourPolygons;
        if (_drawClipped) {
            drawPolygons = _clipContourPolygons;
        }

        List<String> values1 = new ArrayList<String>();
        for (double v : values) {
            values1.add(String.valueOf(v));
        }
        for (int i = 0; i < drawPolygons.size(); i++) {
            wContour.Global.Polygon aPolygon = drawPolygons.get(i);
            drawPolygon(g, aPolygon, values1, false);
        }

    }
    private void drawPolygon(Graphics2D g, wContour.Global.Polygon aPolygon, List<String> values, boolean isHighlight) {
        PointD aPoint;
        String aValue = String.valueOf(aPolygon.LowValue);
        int idx = values.indexOf(aValue) + 1;
        Color aColor = Color.black;
        Color bColor = Color.gray;
        if (isHighlight) {
            aColor = Color.green;
            bColor = Color.blue;
        } else {
            aColor = _colors[idx];
            if (!aPolygon.IsHighCenter) {
                for (int j = 1; j < _colors.length; j++) {
                    if (aColor.getRGB() == _colors[j].getRGB()) {
                        aColor = _colors[j - 1];
                    }
                }
            }
        }

        int len = aPolygon.OutLine.PointList.size();
        GeneralPath drawPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, len);
        XYCoord raincoord;
        for (int j = 0; j < len; j++) {
            aPoint = aPolygon.OutLine.PointList.get(j);
           
            raincoord= PositionUtils.toXYCoord2(aPoint.X, aPoint.Y, radarBase);
            if (j == 0) {
                drawPolygon.moveTo(raincoord.x, raincoord.y);
            } else {
                drawPolygon.lineTo(raincoord.x, raincoord.y);
            }
        }

        if (aPolygon.HasHoles()) {
            for (int h = 0; h < aPolygon.HoleLines.size(); h++) {
                List<PointD> newPList = aPolygon.HoleLines.get(h).PointList;
                for (int j = 0; j < newPList.size(); j++) {
                    aPoint = newPList.get(j);
                    raincoord= PositionUtils.toXYCoord2(aPoint.X, aPoint.Y, radarBase);
                    if (j == 0) {
                        drawPolygon.moveTo(raincoord.x, raincoord.y);
                    } else {
                        drawPolygon.lineTo(raincoord.x, raincoord.y);
                    }
                }
            }
        }
        drawPolygon.closePath();

        g.setColor(aColor);
        g.fill(drawPolygon);
        g.setColor(bColor);
        g.draw(drawPolygon);
    }
    
    public void SmoothLines() {
        _contourLines = Contour.smoothLines(_contourLines);
    }
    private int[] ToScreen(double pX, double pY) {
    	int sX= (int) pX ;
    	int sY = (int) pY;
        int[] sxy = {sX, sY};
        return sxy;
    }
    public void TracingContourLines() {
    	
        int nc = values.length;
        int[][] S1 = new int[_gridData.length][_gridData[0].length];
        _borders = Contour.tracingBorders(_gridData, _X, _Y, S1, _undefData);
        _contourLines = Contour.tracingContourLines(_gridData, _X, _Y, nc, values, _undefData, _borders, S1);
    }
   
    public void ClearObjects() {
        //_discreteData = null;
        _gridData = null;
        _borders = new ArrayList<Border>();
        _contourLines = new ArrayList<PolyLine>();
        _contourPolygons = new ArrayList<wContour.Global.Polygon>();
        _clipLines = new ArrayList<List<PointD>>();
        _clipContourLines = new ArrayList<PolyLine>();
        _clipContourPolygons = new ArrayList<wContour.Global.Polygon>();
       // _mapLines = new ArrayList<List<PointD>>();
        _legendPolygons = new ArrayList<LPolygon>();
       //_streamLines = new ArrayList<PolyLine>();
    }
    public void ClipLines() {
        _clipContourLines = new ArrayList<PolyLine>();
        for (List< PointD> cLine : _clipLines) {
            _clipContourLines.addAll(Contour.clipPolylines(_contourLines, cLine));
        }
    }

    public void ClipPolygons() {
        _clipContourPolygons = new ArrayList<wContour.Global.Polygon>();

        for (List<PointD> cLine : _clipLines) {
            _clipContourPolygons.addAll(Contour.clipPolygons(_contourPolygons, cLine));
        }
    }

    public void TracingPolygons() {
        int nc = values.length;
        //---- Colors
        CreateColors(_startColor, _endColor, nc + 1);

        _contourPolygons = Contour.tracingPolygons(_gridData, _contourLines, _borders, values);
    }
    
    public void createColors(){
        CreateColors(_startColor, _endColor, values.length + 1);
    }

    public void CreateColors(Color sColor, Color eColor, int cNum) {
        _colors = new Color[cNum];
        int sR = 0;
        int sG = 0;
        int sB = 0;
        int eR = 0;
        int eG = 0;
        int eB = 0;
        int rStep = 0;
        int gStep = 0;
        int bStep = 0;
        int i = 0;

        sR = sColor.getRed();
        sG = sColor.getGreen();
        sB = sColor.getBlue();
        eR = eColor.getRed();
        eG = eColor.getGreen();
        eB = eColor.getBlue();
        rStep = (int) ((eR - sR) / cNum);
        gStep = (int) ((eG - sG) / cNum);
        bStep = (int) ((eB - sB) / cNum);
        for (i = 0; i < _colors.length; i++) {
            int r = sR + i * rStep;
            int g = sG + i * gStep;
            int b = sB + i * bStep;
            _colors[i] = new Color(r, g, b);
        }
    }

    public void SetCoordinate() {
      //  SetCoordinate(-10, width, 0, height);
    }

    public void GetEcllipseClipping() {
        _clipLines = new ArrayList<List<PointD>>();

        //---- Generate border with ellipse
        double x0 = 0;
        double y0 = 0;
        double a = 0;
        double b = 0;
        double c = 0;
        boolean ifX = false;
        x0 = radarBase.getWidth() / 2;
        y0 = radarBase.getHeight() / 2;
        double dist = 0;
        dist = 100;
        a = x0 - dist;
        b = y0 - dist / 2;
        if (a > b) {
            ifX = true;
        } else {
            ifX = false;
            c = a;
            a = b;
            b = c;
        }

        int i = 0;
        int n = 0;
        n = 100;
        double nx = 0;
        double x1 = 0;
        double y1 = 0;
        double ytemp = 0;
        List<PointD> pList = new ArrayList<PointD>();
        List<PointD> pList1 = new ArrayList<PointD>();
        PointD aPoint;
        nx = (x0 * 2 - dist * 2) / n;
        for (i = 1; i <= n; i++) {
            x1 = dist + nx / 2 + (i - 1) * nx;
            if (ifX) {
                ytemp = Math.sqrt((1 - Math.pow((x1 - x0), 2) / Math.pow(a, 2)) * Math.pow(b, 2));
                y1 = y0 + ytemp;
                aPoint = new PointD();
                aPoint.X = x1;
                aPoint.Y = y1;
                pList.add(aPoint);
                aPoint = new PointD();
                aPoint.X = x1;
                y1 = y0 - ytemp;
                aPoint.Y = y1;
                pList1.add(aPoint);
            } else {
                ytemp = Math.sqrt((1 - Math.pow((x1 - x0), 2) / Math.pow(b, 2)) * Math.pow(a, 2));
                y1 = y0 + ytemp;
                aPoint = new PointD();
                aPoint.X = x1;
                aPoint.Y = y1;
                pList1.add(aPoint);
                aPoint = new PointD();
                aPoint.X = x1;
                y1 = y0 - ytemp;
                aPoint.Y = y1;
                pList1.add(aPoint);
            }
        }

        aPoint = new PointD();
        if (ifX) {
            aPoint.X = x0 - a;
        } else {
            aPoint.X = x0 - b;
        }
        aPoint.Y = y0;
        List<PointD> cLine = new ArrayList<PointD>();
        cLine.add(aPoint);
        for (i = 0; i < pList.size(); i++) {
            cLine.add(pList.get(i));
        }
        aPoint = new PointD();
        aPoint.Y = y0;
        if (ifX) {
            aPoint.X = x0 + a;
        } else {
            aPoint.X = x0 + b;
        }
        cLine.add(aPoint);
        for (i = pList1.size() - 1; i >= 0; i += -1) {
            cLine.add(pList1.get(i));
        }
        cLine.add(cLine.get(0));
        _clipLines.add(cLine);
    }
    public void CreateLegend() {
        PointD aPoint = new PointD();
        aPoint.X =this.radarBase.getCenter_X()/100;
        aPoint.Y =  this.radarBase.getCenter_Y()/2;
        LegendPara lPara = new LegendPara();
        lPara.startPoint = aPoint;
        lPara.isTriangle = true;
        lPara.isVertical = true;
        lPara.length = radarBase.getHeight() / 2;
        lPara.width = radarBase.getHeight()/ 100;
        lPara.contourValues = values;
        
        _legendPolygons = Legend.CreateLegend(lPara);
    }
  


}