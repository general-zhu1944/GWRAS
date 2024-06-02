package com.kitty.radar.map.shp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kitty.radar.MapOverlay.MapHead;
import com.kitty.radar.MapOverlay.MapMsgHead;
import com.kitty.radar.MapOverlay.MapTownName;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.map.shp.type.Point;
import com.kitty.radar.map.shp.type.PolyLine;
import com.kitty.radar.map.shp.type.Polygon;
import com.kitty.radar.util.PositionUtils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ShpMap2Map {
	

	public static void main(String[] args) throws IOException {
		try {
			
			List<String> provinceFiles = new ArrayList<String>();
			List<String> cityFiles = new ArrayList<String>();
			List<String> townFiles = new ArrayList<String>();
			
			provinceFiles.add("D:\\workspace\\ncrad\\maptest\\sichuan_full.shp");
			//cityFiles.add("D:\\workspace\\ncrad\\maptest\\liangshan.shp");
     		//townFiles.add("D:\\workspace\\ncrad\\maptest\\liangshan.shp");
			//townFiles.add("D:\\workspace\\ncrad\\maptest\\雅安乡镇_PolygonToLine\\雅安乡镇_PolygonToLine.shp");
			
			File stationf=new File("D:\\workspace\\ncrad\\maptest\\station1.xls"); 

			File mapFile = new File("D:\\xxx.map");
			RandomAccessFile randomAccessFile = new RandomAccessFile(mapFile, "rw");
			int pos = 0;
			pos = initMapHead(randomAccessFile);
			pos = writeProviceMap(randomAccessFile, provinceFiles, pos);		
			pos = writeCityMap(randomAccessFile, cityFiles, pos);		
			pos = writeTownMap(randomAccessFile, townFiles, pos);	
			pos = writeCityName(randomAccessFile, stationf, pos);
			randomAccessFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	private static int writeTownMap(RandomAccessFile randomAccessFile, List<String> townFiles, int startPos) throws IOException {
		List<ShpMap> shpMapList = new ArrayList<ShpMap>();
		for (String shpFilePath : townFiles) {
			File shpFile = new File(shpFilePath);
			ShpMap shpMap = null;
			try {
				shpMap = ShpMap.load(shpFile);
				shpMapList.add(shpMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
    	MapHead mapHead = new MapHead();
        //mapId:2字节,156省界,198城市,152town,144河流,196城市名称
    	mapHead.mapID = 152;
        //flag1:2字节
        mapHead.flag1 = 0;
        //flag2:2字节
        mapHead.flag2 = 0;
        //长度:4字节
        mapHead.length = 0;
        //startpos:2字节,不含MsgHead的
        mapHead.startpos = 0; //startpos*256才是真正的偏移量
        //无用202字节
        mapHead.unused = new byte[202];
        
        byte[] mapMsg = getMapMsg(shpMapList);
        mapHead.length = mapMsg.length;
        //startpos:256算一个块,前5个块给MapHead使用
        mapHead.startpos = startPos;
        randomAccessFile.seek(startPos*256);
        randomAccessFile.write(mapMsg);
        //补满256块数据
        int posLen = mapHead.length/256;
        if(mapHead.length%256 !=0 ) {
        	posLen = posLen+1;
        	int padCnt = 256-mapHead.length%256;
        	randomAccessFile.seek(startPos*256+mapHead.length);
        	randomAccessFile.write(new byte[padCnt]);
        }
        
        randomAccessFile.seek(4+214*2);
        randomAccessFile.write(mapHead.toByteArray());
        int nextPos = startPos+posLen;
        return nextPos;
        
	}
	private static int writeCityName(RandomAccessFile randomAccessFile, File nameFile, int startPos) throws IOException {
		
		
    	MapHead mapHead = new MapHead();
        //mapId:2字节,156省界,198城市,152town,144河流,196城市名称
    	mapHead.mapID = 196;
        //flag1:2字节
        mapHead.flag1 = 0;
        //flag2:2字节
        mapHead.flag2 = 0;
        //长度:4字节
        mapHead.length = 0;
        //startpos:2字节,不含MsgHead的
        mapHead.startpos = 0; //startpos*256才是真正的偏移量
        //无用202字节
        mapHead.unused = new byte[202];
        
        byte[] mapName = getMapName(nameFile);
        mapHead.length = mapName.length;
        //startpos:256算一个块,前5个块给MapHead使用
        mapHead.startpos = startPos;
        randomAccessFile.seek(startPos*256);
        randomAccessFile.write(mapName);
        //补满256块数据
        int posLen = mapHead.length/256;
        if(mapHead.length%256 !=0 ) {
        	posLen = posLen+1;
        	int padCnt = 256-mapHead.length%256;
        	randomAccessFile.seek(startPos*256+mapHead.length);
        	randomAccessFile.write(new byte[padCnt]);
        }
        
        randomAccessFile.seek(4+214*2);
        randomAccessFile.write(mapHead.toByteArray());
        int nextPos = startPos+posLen;
        return nextPos;
        
	}


	private static int writeCityMap(RandomAccessFile randomAccessFile, List<String> cityFiles, int startPos) throws IOException {
		List<ShpMap> shpMapList = new ArrayList<ShpMap>();
		for (String shpFilePath : cityFiles) {
			File shpFile = new File(shpFilePath);
			ShpMap shpMap = null;
			try {
				shpMap = ShpMap.load(shpFile);
				shpMapList.add(shpMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
    	MapHead mapHead = new MapHead();
        //mapId:2字节,156省界,198城市,152town,144河流,196城市名称
    	mapHead.mapID = 198;
        //flag1:2字节
        mapHead.flag1 = 0;
        //flag2:2字节
        mapHead.flag2 = 0;
        //长度:4字节
        mapHead.length = 0;
        //startpos:2字节,不含MsgHead的
        mapHead.startpos = 0; //startpos*256才是真正的偏移量
        //无用202字节
        mapHead.unused = new byte[202];
        
        byte[] mapMsg = getMapMsg(shpMapList);
        mapHead.length = mapMsg.length;
        //startpos:256算一个块,前5个块给MapHead使用
        mapHead.startpos = startPos;
        randomAccessFile.seek(startPos*256);
        randomAccessFile.write(mapMsg);
        //补满256块数据
        int posLen = mapHead.length/256;
        if(mapHead.length%256 !=0 ) {
        	posLen = posLen+1;
        	int padCnt = 256-mapHead.length%256;
        	randomAccessFile.seek(startPos*256+mapHead.length);
        	randomAccessFile.write(new byte[padCnt]);
        }
        
        randomAccessFile.seek(4+214*1);
        randomAccessFile.write(mapHead.toByteArray());
        int nextPos = startPos+posLen;
        return nextPos;
        
	}

	private static int initMapHead(RandomAccessFile randomAccessFile) throws IOException {
		//2+2+2+8+2+202=218字节
		randomAccessFile.seek(0);
		randomAccessFile.write(new byte[]{0x0, 0x0, 0x0, 0x0});	//4字节头
		randomAccessFile.seek(4);
		
    	MapHead mapHead = new MapHead();
        //mapId:2字节,156省界,198城市,152town,144河流,196城市名称
    	mapHead.mapID = 156;
        //flag1:2字节
        mapHead.flag1 = 0;
        //flag2:2字节
        mapHead.flag2 = 0;
        //长度:4字节
        mapHead.length = 0;
        //startpos:2字节,不含MsgHead的
        mapHead.startpos = 0; //startpos*256才是真正的偏移量
        //无用202字节
        mapHead.unused = new byte[202];
        randomAccessFile.seek(4);
        byte[] mapHeadBytes = mapHead.toByteArray();
        int mapHeadLen = mapHeadBytes.length;
    	mapHead.mapID = 156;
        randomAccessFile.write(mapHead.toByteArray());
        randomAccessFile.seek(mapHeadLen*1+4);

    	mapHead.mapID = 198;
        randomAccessFile.write(mapHead.toByteArray());
        randomAccessFile.seek(mapHeadLen*2+4);

    	mapHead.mapID = 152;
        randomAccessFile.write(mapHead.toByteArray());
        randomAccessFile.seek(mapHeadLen*3+4);

    	mapHead.mapID = 144;
        randomAccessFile.write(mapHead.toByteArray());
        randomAccessFile.seek(mapHeadLen*4+4);

    	mapHead.mapID = 196;
        randomAccessFile.write(mapHead.toByteArray());
        randomAccessFile.seek(mapHeadLen*5+4);
        randomAccessFile.write(new byte[186]);	//凑整偏移量startpos 256*5=218*5+186+4
        return 5;
	}
	
	private static int writeProviceMap(RandomAccessFile randomAccessFile, List<String> shpFiles, int startPos) throws IOException {

		List<ShpMap> shpMapList = new ArrayList<ShpMap>();
		for (String shpFilePath : shpFiles) {
			File shpFile = new File(shpFilePath);
			ShpMap shpMap = null;
			try {
				shpMap = ShpMap.load(shpFile);
				shpMapList.add(shpMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
    	MapHead mapHead = new MapHead();
        //mapId:2字节,156省界,198城市,152town,144河流,196城市名称
    	mapHead.mapID = 156;
        //flag1:2字节
        mapHead.flag1 = 0;
        //flag2:2字节
        mapHead.flag2 = 0;
        //长度:4字节
        mapHead.length = 0;
        //startpos:2字节,不含MsgHead的
        mapHead.startpos = 0; //startpos*256才是真正的偏移量
        //无用202字节
        mapHead.unused = new byte[202];
        
        byte[] mapMsg = getMapMsg(shpMapList);
        mapHead.length = mapMsg.length;
        //startpos:256算一个块,前5个块给MapHead使用
        mapHead.startpos = startPos;
        randomAccessFile.seek(startPos*256);
        randomAccessFile.write(mapMsg);
        //补满256块数据
        int posLen = mapHead.length/256;
        if(mapHead.length%256 !=0 ) {
        	posLen = posLen+1;
        	int padCnt = 256-mapHead.length%256;
        	randomAccessFile.seek(startPos*256+mapHead.length);
        	randomAccessFile.write(new byte[padCnt]);
        }
        
        randomAccessFile.seek(4);
        randomAccessFile.write(mapHead.toByteArray());
        int nextPos = startPos+posLen;
        return nextPos;
        
	}
	private static byte[] getMapName(File namefile) throws IOException {
        ByteArrayOutputStream mapNameMsg = new ByteArrayOutputStream();
        String[][] stationInfo=new String[204][3] ;
        File f=namefile; 
		 try { 
		 Workbook book=Workbook.getWorkbook(f);// 
		 Sheet sheet=book.getSheet(0); //获得第一个工作表对象 
		 for(int i=0;i<sheet.getRows();i++){ 
		  for(int j=0;j<3;j++){ 
		  Cell cell=sheet.getCell(j, i); //获得单元格 
		  stationInfo[i][j]=cell.getContents()+"";
		  
		  //System.out.print(cell.getContents()+" "); 
		  } 
		  System.out.print("\n"); 
		 } 
		 } catch (BiffException e) { 
		 // TODO Auto-generated catch block 
		 e.printStackTrace(); 
		 } catch (IOException e) { 
		 // TODO Auto-generated catch block 
		 e.printStackTrace(); 
		 } 
		 for(int i=0;i<stationInfo.length;i++){ 
			 double numlon = Double.parseDouble(stationInfo[i][1]);
			 double numlat = Double.parseDouble(stationInfo[i][2]);
			 XYCoord startXY = getMapXY(numlon, numlat, longitudeStart, latitudeStart);
			 MapTownName mapTownName=new MapTownName();
			 mapTownName.flag1=0x0; //2字节
			 mapTownName.flag2=0x0; //2字节
			 mapTownName.flag3=0x0; //2字节
			 mapTownName.x1=(short) startXY.x;	//2字节
			 mapTownName.y1=(short) startXY.y;	//2字节
			 mapTownName.bytes=6;
			 byte[] byteArray = stationInfo[i][0].getBytes();
				 if(byteArray.length<6)
				 {
					 mapTownName.name=Arrays.copyOf(byteArray, 6);
					 	 
				 }
				 if(byteArray.length>=6)
				 {
					 System.arraycopy(byteArray, 0, mapTownName.name, 0, 6);
				 }
				byte[] msg = mapTownName.toByteArray();
				mapNameMsg.write(msg);
				 
			 }
		  byte[] all = mapNameMsg.toByteArray();
	      return all;		
	}
	
	
	private static byte[] getMapMsg(List<ShpMap> shpMapList) throws IOException {
        ByteArrayOutputStream mapMsg = new ByteArrayOutputStream();
        for (ShpMap shpMap : shpMapList) {
    		for(ShpRecord shpRecord: shpMap.getShpRecords()) {
    			if(shpRecord.getRecordContent() instanceof Polygon) {
                	Polygon polygon = (Polygon) shpRecord.getRecordContent();
                	int numParts = polygon.getNumParts();
                	for(int partId = 0; partId<numParts; partId++) {

                    	Point[] points = polygon.getPart(partId);
                    	XYCoord startXY = getMapXY(points[0].getX(), points[0].getY(), longitudeStart, latitudeStart);
                    	//
                    	MapMsgHead mapMsgHead = new MapMsgHead();
                    	mapMsgHead.flag1 = 3619;	//2字节
                    	mapMsgHead.flag2 = 0x0; //2字节
                    	mapMsgHead.x1 = (short) startXY.x;	//2字节
                    	mapMsgHead.y1 = (short) startXY.y;	//2字节
                    	//循环的x,y坐标:bytes/4 对
                    	int len = points.length;
                    	mapMsgHead.bytes = (len-1)*4;
                    	mapMsgHead.xy = new short[(len-1)*2];
                    	for(int pos = 1; pos<len; pos++) {
                    		XYCoord xy = getMapXY(points[pos].getX(), points[pos].getY(), longitudeStart, latitudeStart);
                    		mapMsgHead.xy[pos*2-2] = (short) xy.x;
                    		mapMsgHead.xy[pos*2-1] = (short) xy.y;
                    	}
        				byte[] msg = mapMsgHead.toByteArray();
        				mapMsg.write(msg);
                	}
    			}
    			if(shpRecord.getRecordContent() instanceof PolyLine) {
    				PolyLine polyLine = (PolyLine) shpRecord.getRecordContent();
                	int numParts = polyLine.getNumParts();
                	for(int partId = 0; partId<numParts; partId++) {

                    	Point[] points = polyLine.getPart(partId);
                    	XYCoord startXY = getMapXY(points[0].getX(), points[0].getY(), longitudeStart, latitudeStart);
                    	//
                    	MapMsgHead mapMsgHead = new MapMsgHead();
                    	mapMsgHead.flag1 = 3619;	//2字节
                    	mapMsgHead.flag2 = 0x0; //2字节
                    	mapMsgHead.x1 = (short) startXY.x;	//2字节
                    	mapMsgHead.y1 = (short) startXY.y;	//2字节
                    	//循环的x,y坐标:bytes/4 对
                    	int len = points.length;
                    	mapMsgHead.bytes = (len-1)*4;
                    	mapMsgHead.xy = new short[(len-1)*2];
                    	for(int pos = 1; pos<len; pos++) {
                    		XYCoord xy = getMapXY(points[pos].getX(), points[pos].getY(), longitudeStart, latitudeStart);
                    		mapMsgHead.xy[pos*2-2] = (short) xy.x;
                    		mapMsgHead.xy[pos*2-1] = (short) xy.y;
                    	}
        				byte[] msg = mapMsgHead.toByteArray();
        				mapMsg.write(msg);
                	}
    			}
            }
		}
        byte[] all = mapMsg.toByteArray();
        return all;
	}
     
//	得到Map格式的xy坐标
//	x1 = (ar.r * Math.cos((ar.azimuth - 90.0) * Math.PI / 180.0)+460)/mapTimes;
//	y1 = (ar.r * Math.sin((ar.azimuth + 90.0) * Math.PI / 180.0)+460)/mapTimes;
//	其中ar = PositionUtils.toARCoord(longitude, latitude, longitudeStart, latitudeStart);
//	其中longitudeStart,latitudeStart为地图制作的中心点,应当和雷达位置一致

//	南充
	private static final double longitudeStart = 106.078000;
	private static final double latitudeStart =  30.822000;
// 	凉山、雅安
//	private static final double longitudeStart =102.410599;// 103.040253;
//	private static final double latitudeStart =27.879700;//  29.945055;
	
//	private static final double longitudeStart = 103.040253;
//	private static final double latitudeStart = 29.945055;
	//成都  104.233887  30.413055
	//private static final double longitudeStart = 104.233887;
	//private static final double latitudeStart = 30.413055;
	//巴中  104.233887  30.413055
//	private static final double longitudeStart = 106.809448;
//	private static final double latitudeStart = 31.833889;
	
	
	private static final double mapTimes = 1.0 / 8.0; // 地图数据放大了8倍,
	private static XYCoord getMapXY(double longitude,double latitude,double longitudeStart,double latitudeStart) {
		ARCoord ar = PositionUtils.toARCoord(longitude, latitude, longitudeStart, latitudeStart);
		int x1 = (int) Math.round((ar.r * Math.cos((ar.azimuth - 90.0) * Math.PI / 180.0)+460.0)/mapTimes);
		int y1 = (int) Math.round((-ar.r * Math.sin((ar.azimuth + 90.0) * Math.PI / 180.0)+460.0)/mapTimes);
		return new XYCoord(x1, y1);
	}
}
