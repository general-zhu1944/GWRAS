package com.kitty.radar.data;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.*;

import com.kitty.component.third.RandomAccessFile;
import com.kitty.radar.RadarBase;

import javax.swing.*;

public class PARD extends RadarData {

    public static final byte DATA_TYPE_NUMBER = 9;

    public static final byte[] MOMENT_INDEX = new byte[128];

    private static final Map<Integer, Integer> dataTypeToSlotIndex = new HashMap<>();

    static {
        Arrays.fill(MOMENT_INDEX, (byte) -1);
        
        MOMENT_INDEX[RadarData.DBT]  = 0;   
        MOMENT_INDEX[RadarData.DBZ]  = 1;   
        MOMENT_INDEX[RadarData.ZDR]  = 2;   
        MOMENT_INDEX[RadarData.KDP]  = 3;   
        MOMENT_INDEX[RadarData.CC]   = 4;   
        MOMENT_INDEX[RadarData.DP]   = 5;   
        MOMENT_INDEX[RadarData.SNRH] = 6;   
        MOMENT_INDEX[RadarData.V]    = 7;   
        MOMENT_INDEX[RadarData.W]    = 8;   
        
        dataTypeToSlotIndex.put(1,  0);   
        dataTypeToSlotIndex.put(2,  1);   
        dataTypeToSlotIndex.put(7,  2);   
        dataTypeToSlotIndex.put(11, 3);   
        dataTypeToSlotIndex.put(9,  4);   
        dataTypeToSlotIndex.put(10, 5);   
        dataTypeToSlotIndex.put(16, 6);   
        dataTypeToSlotIndex.put(3,  7);   
        dataTypeToSlotIndex.put(4,  8);   
    }

    static class Moment {
        Map<String, Object> header = new LinkedHashMap<>();
        short binNumber;
        long filePointer;
    }

    static class Radial {
        Map<String, Object> header = new LinkedHashMap<>();
        
        Moment[] moments = new Moment[DATA_TYPE_NUMBER];
    }

    
    private Map<String, Object> commonMap = new LinkedHashMap<>();
    
    private Map<String, Object> taskConfigMap = new LinkedHashMap<>();
    
    private List<Map<String, Object>> beamConfigs = new ArrayList<>();
    
    private List<Map<String, Object>> cutConfigs = new ArrayList<>();
    
    private Radial[] radials = new Radial[MAX_FILE_RECORDS];
    private int maxRadialNum = 0;
    private short maxBinNum = 0;
    private int recordNum;                     
    
    private float[][][] values;

    public PARD(RadarBase radarBase) {
        super(radarBase);   
    }


    @Override
    public Date getFileTime() {
        long sec = (long) taskConfigMap.get("ScanStartTime");
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1, 0, 0, 0);
        cal.add(Calendar.SECOND, (int) sec);
        return cal.getTime();
    }

    @Override
    public double getElevation(int cutNum) {
        if (cutNum < 0 || cutNum >= cutConfigs.size()) {
            return 0.0;
        }
        return (float) cutConfigs.get(cutNum).get("Elevation");
    }

    @Override
    public boolean readHeader(int recordNum) {
        super.readHeader(recordNum);
        this.recordNum = recordNum;
        readParams(this.getCutNum(recordNum));
        return true;
    }

    @Override
    public boolean readRcecordnum(int recordNum) {
        super.readRcecordnum(recordNum);
        this.recordNum = recordNum;
        return true;
    }

    @Override
    public boolean readRecord(int recordNum) {
        super.readRecord(recordNum);
        this.recordNum = recordNum;
        readParams(this.getCutNum(recordNum));
        try {
            Radial radial = radials[recordNum];
            for (int i = 0; i < radial.moments.length; i++) {
                Moment moment = radial.moments[i];
                if (moment != null) {
                    int scale   = (int) moment.header.get("Scale");
                    int offset  = (int) moment.header.get("Offset");
                    int binLen  = (int) moment.header.get("BinLength");
                    raf.seek(moment.filePointer);
                    for (int j = 0; j < moment.binNumber; j++) {
                        int value;
                        if (binLen == 2) {
                            value = raf.readUnsignedShort();
                        } else {
                            value = raf.readUnsignedByte();
                        }
                        
                        if (value < 5) {
                            values[0][i][j] = RadarData.NO_DATA;
                        } else {
                            values[0][i][j] = (value - offset) / (float) scale;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int readCut(int recordNum) {
        super.readCut(recordNum);
        try {
            this.recordNum = recordNum;
            readParams(this.getCutNum(recordNum));
            int cutn = (int) radials[recordNum].header.get("ElevationNumber");
            int k = recordNum;
            for (; k < maxRadialNum; k++) {
                int cn = (int) radials[k].header.get("ElevationNumber");
                if (cn != cutn) {
                    break;
                }
                Radial radial = radials[k];
                for (int i = 0; i < radial.moments.length; i++) {
                    Moment moment = radial.moments[i];
                    if (moment != null) {
                        int scale   = (int) moment.header.get("Scale");
                        int offset  = (int) moment.header.get("Offset");
                        int binLen  = (int) moment.header.get("BinLength");
                        raf.seek(moment.filePointer);
                        for (int j = 0; j < moment.binNumber; j++) {
                            int value;
                            if (binLen == 2) {
                                value = raf.readUnsignedShort();
                            } else {
                                value = raf.readUnsignedByte();
                            }
                            if (value < 5) {
                                values[k - recordNum][i][j] = RadarData.NO_DATA;
                            } else {
                                values[k - recordNum][i][j] = (value - offset) / (float) scale;
                            }
                        }
                    }
                }
            }
            return k - recordNum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getAzimuth() {
        return this.getAzimuth(recordNum);
    }

    @Override
    public double getElevation() {
        return this.getElevation(this.getCutNum(recordNum));
    }

    @Override
    public int getBinaryValue(int moment, int radial, int bin) {
        float fv = this.getMomentValue(moment, radial, bin);
        if (fv == RadarData.NO_DATA) {
            return 0;
        }
        return SA_SB.momentToBinary(fv, moment, this.resolution);
    }

    @Override
    public float getMomentValue(int moment, int radial, int bin) {
        int slot = MOMENT_INDEX[moment];
        if (slot < 0) {
            return RadarData.NO_DATA;
        }
        return values[radial][slot][bin];
    }

    @Override
    public boolean open(File file) {
        try {
            raf = new RandomAccessFile(file.getPath(), "r");
            raf.order(RandomAccessFile.LITTLE_ENDIAN);
            
            if (!readGenericHeader()) return false;
            
            if (!readSiteConfig()) return false;
            
            if (!readTaskConfig()) return false;
            
            int beamNum = (int) taskConfigMap.get("ScanBeamNumber");
            readBeamConfigs(beamNum);
            
            int cutNum = (int) taskConfigMap.get("CutNumber");
            readCutConfigs(cutNum);
            
            readRadialHeaders();
            
            readParams(0);
            
            values = new float[MAX_CUT_RECORDS][DATA_TYPE_NUMBER][maxBinNum];
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean readGenericHeader() throws IOException {
        int magic = raf.readInt();
        if (magic != 0x4D545352) {

            return false;
        }
        commonMap.put("MagicNumber", magic);
        commonMap.put("MajorVersion", raf.readUnsignedShort());
        commonMap.put("MinorVersion", raf.readUnsignedShort());
        int generic_type = raf.readInt();

        if (generic_type != 1) {
            return false;
        }
        commonMap.put("GenericType", generic_type);      
        commonMap.put("ProductType", raf.readInt());
        raf.skipBytes(16);
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean readSiteConfig() throws IOException {
        commonMap.put("SiteCode", raf.readString(8).trim());
        commonMap.put("SiteName", raf.readString(32).trim());
        commonMap.put("Latitude", raf.readFloat());
        commonMap.put("Longitude", raf.readFloat());
        commonMap.put("AntennaHeight", raf.readInt());
        commonMap.put("GroundHeight", raf.readInt());
        commonMap.put("Frequency", raf.readFloat());
        commonMap.put("AntennaType", raf.readInt());
        commonMap.put("TRNumber", raf.readInt());
        commonMap.put("RDAVersion", raf.readInt());
        this.radarType = raf.readShort();
        commonMap.put("RadarType", radarType);

        if (radarType == 7 || radarType ==8||radarType == 44 || radarType ==43||radarType == 69 || radarType ==70 || radarType ==27) {
            raf.skipBytes(54);  
            
            radarBase.setAntennaHeight(((int) commonMap.get("AntennaHeight")) / 1000.0f);
            radarBase.setLatitude((float) commonMap.get("Latitude"));
            radarBase.setLongitude((float) commonMap.get("Longitude"));
            radarBase.radarName = (String) commonMap.get("SiteName");
            radarBase.siteCode = (String) commonMap.get("SiteCode");
            return true;
        } else
        {
            return false;
        }

    }

    @SuppressWarnings("unchecked")
    private boolean readTaskConfig() throws IOException {
        taskConfigMap.put("TaskName", raf.readString(32).trim());
        taskConfigMap.put("TaskDescription", raf.readString(128).trim());
        taskConfigMap.put("PolarizationType", raf.readInt());
        taskConfigMap.put("ScanType", raf.readInt());
        taskConfigMap.put("ScanBeamNumber", raf.readInt());
        int cutNumber = raf.readInt();
        taskConfigMap.put("CutNumber", cutNumber);
        this.cutNumber = (byte) cutNumber;               
        taskConfigMap.put("RayOrder", raf.readInt());
        taskConfigMap.put("ScanStartTime", raf.readLong());
        raf.skipBytes(68);  
       
        this.vcp = ((String) taskConfigMap.get("TaskName")).toUpperCase().replaceFirst("^VCP", "");
        return true;
    }

    @SuppressWarnings("unchecked")
    private void readBeamConfigs(int beamNum) throws IOException {
        for (int b = 0; b < beamNum; b++) {
            Map<String, Object> beam = new LinkedHashMap<>();
            beam.put("BeamIndex", raf.readInt());
            beam.put("BeamType", raf.readInt());
            beam.put("SubPulseNumber", raf.readInt());
            beam.put("TxBeamDirection", raf.readFloat());
            beam.put("TxBeamWidthH", raf.readFloat());
            beam.put("TxBeamWidthV", raf.readFloat());
            beam.put("TxBeamGain", raf.readFloat());
            raf.skipBytes(100); 
            
            for (int s = 0; s < 4; s++) {
                Map<String, Object> subPulse = new LinkedHashMap<>();
                subPulse.put("SubPulseStrategy", raf.readInt());
                subPulse.put("SubPulseModulation", raf.readInt());
                subPulse.put("SubPulseFrequency", raf.readFloat());
                subPulse.put("SubPulseBandWidth", raf.readFloat());
                subPulse.put("SubPulseWidth", raf.readInt());
                subPulse.put("HorizontalNoise", raf.readFloat());
                subPulse.put("VerticalNoise", raf.readFloat());
                subPulse.put("HorizontalCalibration", raf.readFloat());
                subPulse.put("VerticalCalibration", raf.readFloat());
                subPulse.put("HorizontalNoiseTemperature", raf.readFloat());
                subPulse.put("VerticalNoiseTemperature", raf.readFloat());
                subPulse.put("ZDRCalibration", raf.readFloat());
                subPulse.put("PHIDPCalibration", raf.readFloat());
                subPulse.put("LDRCalibration", raf.readFloat());
                subPulse.put("PulsePoints", raf.readShort());
                raf.skipBytes(70);
                beam.put("SubPulse" + (s + 1), subPulse);
            }
            beamConfigs.add(beam);
        }
    }

    @SuppressWarnings("unchecked")
    private void readCutConfigs(int cutNum) throws IOException {
        for (int i = 0; i < cutNum; i++) {
            Map<String, Object> cut = new LinkedHashMap<>();
            cut.put("CutIndex", raf.readShort());
            cut.put("TxBeamIndex", raf.readShort());
            cut.put("Elevation", raf.readFloat());       
            cut.put("TxBeamGain", raf.readFloat());
            cut.put("RxBeamWidthH", raf.readFloat());
            cut.put("RxBeamWidthV", raf.readFloat());
            cut.put("RxBeamGain", raf.readFloat());
            cut.put("ProcessMode", raf.readInt());
            cut.put("WaveForm", raf.readInt());
            cut.put("PRF#1", raf.readFloat());
            cut.put("PRF#2", raf.readFloat());
            cut.put("N2PRF#1", raf.readFloat());
            cut.put("N2PRF#2", raf.readFloat());
            cut.put("DealiasingMode", raf.readInt());
            cut.put("Azimuth", raf.readFloat());
            cut.put("StartAngle", raf.readFloat());
            cut.put("EndAngle", raf.readFloat());
            cut.put("AngularResolution", raf.readFloat());
            cut.put("ScanSpeed", raf.readFloat());
            cut.put("LogResolution", raf.readFloat());
            cut.put("DopplerResolution", raf.readFloat());
            cut.put("MaximumRange#1", raf.readInt());
            cut.put("MaximumRange#2", raf.readInt());
            cut.put("StartRange", raf.readInt());
            cut.put("Sample#1", raf.readInt());
            cut.put("Sample#2", raf.readInt());
            cut.put("PhaseMode", raf.readInt());
            cut.put("AtmosphericLoss", raf.readFloat());
            cut.put("NyquistSpeed", raf.readFloat());
            cut.put("MomentsMask", raf.readLong());
            cut.put("MomentsSizeMask", raf.readLong());
            cut.put("MiscFilterMask", raf.readInt());
            cut.put("SQIThreshold", raf.readFloat());
            cut.put("SIGThreshold", raf.readFloat());
            cut.put("CSRThreshold", raf.readFloat());
            cut.put("LOGThreshold", raf.readFloat());
            cut.put("CPAThreshold", raf.readFloat());
            cut.put("PMIThreshold", raf.readFloat());
            cut.put("DPLOGThreshold", raf.readFloat());
            raf.skipBytes(4);   
            cut.put("dBTMask", raf.readInt());
            cut.put("dBZMask", raf.readInt());
            cut.put("VelocityMask", raf.readInt());
            cut.put("SpectrumWidthMask", raf.readInt());
            cut.put("DPMask", raf.readInt());
            raf.skipBytes(12);  
            raf.skipBytes(4);   
            cut.put("Direction", raf.readInt());
            cut.put("GroundClutterClassifierType", raf.readShort());
            cut.put("GroundClutterFilterType", raf.readShort());
            cut.put("GroundClutterFilterNotchWidth", raf.readShort());
            cut.put("GroundClutterFilterWindow", raf.readShort());
            raf.skipBytes(44);  
            cutConfigs.add(cut);
        }
    }

    @SuppressWarnings("unchecked")
    private void readRadialHeaders() throws IOException {
        maxRadialNum = 0;
        maxBinNum = 0;
        try {
            while (true) {
                radials[maxRadialNum] = new Radial();
                
                int radialState = raf.readInt();
                radials[maxRadialNum].header.put("RadialState", radialState);
                radials[maxRadialNum].header.put("SpotBlank", raf.readInt());
                radials[maxRadialNum].header.put("SequenceNumber", raf.readInt());
                radials[maxRadialNum].header.put("RadialNumber", raf.readInt());
                int elevationNumber = raf.readInt();
                radials[maxRadialNum].header.put("ElevationNumber", elevationNumber);
                radials[maxRadialNum].header.put("Azimuth", raf.readFloat());
                radials[maxRadialNum].header.put("Elevation", raf.readFloat());
                radials[maxRadialNum].header.put("Seconds", raf.readLong());      
                radials[maxRadialNum].header.put("Microseconds", raf.readInt());
                int lengthOfData = raf.readInt();
                radials[maxRadialNum].header.put("Lengthofdata", lengthOfData);
                int momentNumber = raf.readInt();
                radials[maxRadialNum].header.put("MomentNumber", momentNumber);
                raf.skipBytes(2);   
                radials[maxRadialNum].header.put("HorizontalEstimatedNoise", raf.readShort());
                radials[maxRadialNum].header.put("VerticalEstimatedNoise", raf.readShort());
                radials[maxRadialNum].header.put("PRFFlag", raf.readInt());
                raf.skipBytes(70);  
                
                int readLength = 0;
                int readMoment = 0;
                for (int i = 0; i < momentNumber; i++) {
                    int dataType = raf.readInt();
                    int scale = raf.readInt();
                    int offset = raf.readInt();
                    int binLength = raf.readShort();
                    short flags = raf.readShort();
                    int length = raf.readInt();
                    raf.skipBytes(12);  

                    dataTypeSet.add(dataType);   
                    int index = MOMENT_INDEX[dataType];
                    if (index != -1) {
                        radials[maxRadialNum].moments[index] = new Moment();
                        radials[maxRadialNum].moments[index].header.put("DataType", dataType);
                        radials[maxRadialNum].moments[index].header.put("Scale", scale);
                        radials[maxRadialNum].moments[index].header.put("Offset", offset);
                        radials[maxRadialNum].moments[index].header.put("BinLength", binLength);
                        radials[maxRadialNum].moments[index].header.put("Flags", flags);
                        radials[maxRadialNum].moments[index].header.put("Length", length);
                        short binNum = (short) (length / binLength);
                        if (binNum > maxBinNum) {
                            maxBinNum = binNum;
                        }
                        radials[maxRadialNum].moments[index].binNumber = binNum;
                        radials[maxRadialNum].moments[index].filePointer = raf.getFilePointer();
                        readMoment++;
                    }
                    raf.skipBytes(length);
                    readLength += (32 + length);  
                    
                    if (readMoment == radials[maxRadialNum].moments.length) {
                        raf.skipBytes(lengthOfData - readLength);
                        break;
                    }
                }
                
                if (radialState == 0 || radialState == 3) {
                    cutStarts[elevationNumber - 1] = (short) maxRadialNum;
                }
                azimuths[maxRadialNum] = (float) radials[maxRadialNum].header.get("Azimuth");
                maxRadialNum++;

            }
        } catch (Exception e) {
            

        }
        if(maxRadialNum>415*11) {

        }
    }

    private void readParams(int cutNum) {
        if (cutNum < 0 || cutNum >= cutConfigs.size()) {
            return;
        }
        Map<String, Object> cutMap = cutConfigs.get(cutNum);
        
        this.surveillanceRange = (short) (int) cutMap.get("StartRange");
        this.dopplerRange = this.surveillanceRange;
        
        this.surveillanceInterval = (short) (float) cutMap.get("LogResolution");
        this.dopplerInterval = (short) (float) cutMap.get("DopplerResolution");

        int rnum = this.getCutStart(cutNum);
        if (rnum < 0 || rnum >= maxRadialNum) {
            return;
        }
        Moment moment = radials[rnum].moments[MOMENT_INDEX[DBZ]];
        if (moment != null) {
            this.surveillanceBins = moment.binNumber;
        } else {
            this.surveillanceBins = 0;
        }
        moment = radials[rnum].moments[MOMENT_INDEX[V]];
        if (moment != null) {
            this.dopplerBins = moment.binNumber;
        } else {
            this.dopplerBins = 0;
        }
    }

    @Override
    public short getBinCount(int moment) {
        int slot = MOMENT_INDEX[moment];
        if (slot < 0 || recordNum < 0 || recordNum >= maxRadialNum) return 0;
        Moment m = radials[recordNum].moments[slot];
        return m != null ? m.binNumber : 0;
    }
}