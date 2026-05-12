package com.kitty.radar.util;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import org.meteoinfo.ndarray.Array;
import org.meteoinfo.ndarray.DataType;
import com.kitty.radar.RadarBase;
import com.kitty.radar.domain.ARCoord;
import com.kitty.radar.domain.LLCoord;
import com.kitty.radar.domain.StringCoord;
import com.kitty.radar.domain.XYCoord;
import com.kitty.radar.domain.XYDCoord;

/**
 * 坐标、距离计算相关工具类。 注意：极径(r)与斜距(range)的区别
 */
public class PositionUtils {

	/**
	 * 距离转换，km to 像素
	 * 
	 * @param distance
	 *            单位：km
	 * 
	 * @param scaleX
	 *            缩放
	 * @return
	 */
	public static int toLength(double distance, double scaleX) {
		return (int) Math.round(distance * scaleX);
	}

	/**
	 * 距离转换，像素 to km
	 * 
	 * @param length
	 *            单位：像素
	 * @return
	 */
	public static double toDistance(int length, double scaleX) {
		return length / scaleX;
	}

	/**
	 * 极径转换为斜距
	 * 
	 * @param r
	 *            单位：km
	 * @param elevation
	 *            仰角，单位：度
	 * @return
	 */
	public static double toRange(double r, double elevation) {
		return toRange2(r, Math.cos(elevation * Math.PI / 180.0));
	}

	/**
	 * 极径转换为斜距
	 * 
	 * @param r
	 *            单位：km
	 * @param cos
	 *            仰角cos值
	 * @return
	 */
	public static double toRange2(double r, double cos) {
		return r / cos;
	}

	/**
	 * 斜距转换为极径
	 * 
	 * @param range 单位：km
	 * @param cos 仰角cos值
	 * @return
	 */
	public static double toR2(double range, double cos) {
		return range * cos;
	}
	
	/**
	 * 斜距转换为极径
	 * 
	 * @param range 单位：km
	 * @param elevation 仰角，单位：度
	 * @return
	 */
	public static double toR(double range, double elevation) {
		return toR2(range, Math.cos(elevation * Math.PI / 180.0));
	}
	
	/**
	 * 极坐标转换为经纬度坐标
	 * 
	 * @param azimuth
	 *            方位角，单位：度
	 * @param r
	 *            极径，单位：km
	 * @return
	 */
	public static LLCoord toLLCoord(double azimuth, double r,double longitude, double latitude) {
		double dx = r * 1000 * Math.sin(azimuth * Math.PI / 180.0);
		double dy = r * 1000 * Math.cos(azimuth * Math.PI / 180.0);
		double m_RadLo = longitude * Math.PI / 180.0;
		double m_RadLa = latitude * Math.PI / 180.0;
		double Ec = CommonProps.RJ + (CommonProps.RC - CommonProps.RJ)
				* (90 - latitude) / 90.0;
		double Ed = Ec * Math.cos(m_RadLa);
		double lng = (dx / Ed + m_RadLo) * 180 / Math.PI;
		double lat = (dy / Ec + m_RadLa) * 180 / Math.PI;
		return new LLCoord(lng, lat);
	}

	/**
	 * 经纬度坐标转换为极坐标
	 * 
	 * @param longitude
	 *            单位：度
	 * @param latitude
	 *            单位：度
	 * @param longitudeStart
	 *            起点经度
	 * @param latitudeStart
	 *            起点维度
	 * @return
	 */
	public static ARCoord toARCoord(double longitude, double latitude, double longitudeStart, double latitudeStart) {
		double m_RadLo = longitude * Math.PI / 180.0;
		double m_RadLa = latitude * Math.PI / 180.0;
		double am_RadLo = longitudeStart * Math.PI / 180.0;
		double am_RadLa = latitudeStart * Math.PI / 180.0;
		double aEc = CommonProps.RJ + (CommonProps.RC - CommonProps.RJ)
				* (90 - latitudeStart) / 90.0;
		double aEd = aEc * Math.cos(am_RadLa);

		double dx = (m_RadLo - am_RadLo) * aEd;
		double dy = (m_RadLa - am_RadLa) * aEc;
		double out = Math.sqrt(dx * dx + dy * dy);
		double azimuth = Math.atan(Math.abs(dx / dy)) * 180 / Math.PI;

		// 判断象限
		double dLo = longitude - longitudeStart;
		double dLa = latitude - latitudeStart;

		if (dLo > 0 && dLa <= 0) {
			azimuth = (90 - azimuth) + 90;
		} else if (dLo <= 0 && dLa < 0) {
			azimuth = azimuth + 180;
		} else if (dLo < 0 && dLa >= 0) {
			azimuth = (90 - azimuth) + 270;
		}
		if (Double.isNaN(azimuth)) {
			azimuth = 0;
		}
		return new ARCoord(azimuth, out / 1000.0);
	}

	/**
	 * 以左上角为原点的X、Y坐标转换为极坐标
	 * 
	 * @param x
	 *            单位：像素
	 * @param y
	 *            单位：像素
	 * @return
	 */
	public static ARCoord toARCoord(int x, int y, RadarBase RadarBase) {
		double dx = (x + RadarBase.getXoffset() - RadarBase.getCenter_X())
				/ RadarBase.getScale_X();
		double dy = (y + RadarBase.getYoffset() - RadarBase.getCenter_Y())
				/ RadarBase.getScale_Y();
		return toARCoord2(dx, dy);
	}

	/**
	 * 以雷达中心为原点的X、Y坐标转换为极坐标
	 * 
	 * @param x
	 *            单位：km
	 * @param y
	 *            单位：km
	 * @return
	 */
	public static ARCoord toARCoord2(double x, double y) {
		double r = Math.sqrt(x * x + y * y);
		double radial;
		if (Math.abs(y) > 0.001) {
			radial = Math.atan(Math.abs(x / y));
		} else {
			radial = Math.PI / 2;
		}
		if (x >= 0) {
			if (y < 0) {
				radial = Math.PI - radial;
			}
		} else {
			if (y >= 0) {
				radial = 2 * Math.PI - radial;
			} else {
				radial = Math.PI + radial;
			}
		}
		double azimuth = radial * 180.0 / Math.PI;
		if (azimuth >= 360) {
			azimuth -= 360;
		} else if (azimuth < 0) {
			azimuth += 360;
		}
		return new ARCoord(azimuth, r);
	}

	/**
	 * 极坐标转换为以左上角为原点的X、Y坐标
	 * 
	 * @param azimuth
	 *            单位：度
	 * @param r
	 *            单位：km
	 * @return
	 */
	public static XYCoord toXYCoord(double azimuth, double r, RadarBase RadarBase) {
		int x = (int) Math.round(r
				* Math.cos((azimuth - 90.0) * Math.PI / 180.0)
				* RadarBase.getScale_X() - RadarBase.getXoffset() + RadarBase.getCenter_X());
		int y = (int) Math.round(r
				* Math.sin((azimuth + 90.0) * Math.PI / 180.0)
				* RadarBase.getScale_Y() - RadarBase.getYoffset() + RadarBase.getCenter_Y());
		return new XYCoord(x, y);
	}

	/**
	 * 经纬度坐标转换为以左上角为原点的X、Y坐标
	 * 
	 * @param longitude
	 *            单位：度
	 * @param latitude
	 *            单位：度
	 * @return
	 */
	public static XYCoord toXYCoord2(double longitude, double latitude, RadarBase RadarBase) {
		ARCoord c = PositionUtils.toARCoord(longitude, latitude, RadarBase.getLongitude(), RadarBase.getLatitude());
		return toXYCoord(c.azimuth, c.r, RadarBase);
	}
	public static XYCoord toXYCoord2(double longitude, double latitude, RadarBase RadarBase, double longitudeStart, double latitudeStart) {
		ARCoord c = PositionUtils.toARCoord(longitude, latitude, longitudeStart, latitudeStart);
		return toXYCoord(c.azimuth, c.r, RadarBase);
	}

	/**
	 * 以雷达中心为原点的X、Y坐标转换为以左上角为原点的X、Y坐标
	 * 
	 * @param x
	 *            单位：km
	 * @param y
	 *            单位：km
	 * @return
	 */
	public static XYCoord toXYCoord3(double x, double y, RadarBase RadarBase) {
		int x1 = (int) Math.round(x * RadarBase.getScale_X() - RadarBase.getXoffset()
				+ RadarBase.getCenter_X());
		int y1 = (int) Math.round(y * RadarBase.getScale_Y() - RadarBase.getYoffset()
				+ RadarBase.getCenter_Y());
		return new XYCoord(x1, y1);
	}

	/**
	 * 以左上角为原点的X、Y坐标转换为以雷达中心为原点的X、Y坐标，单位：km
	 * 
	 * @param x
	 *            单位：像素
	 * @param y
	 *            单位：像素
	 * @return
	 */
	public static XYDCoord toXYDCoord(int x, int y, RadarBase RadarBase) {
		double dx = (x + RadarBase.getXoffset() - RadarBase.getCenter_X())
				/ RadarBase.getScale_X();
		double dy = (y + RadarBase.getYoffset() - RadarBase.getCenter_Y())
				/ RadarBase.getScale_Y();
		return new XYDCoord(dx, dy);
	}

	/**
	 * 计算高度（方法1，目前应用于CAPPI、RHI），单位：km
	 * 
	 * @param range
	 *            单位：km
	 * @param sin
	 *            仰角sin值
	 * @param cos
	 *            仰角cos值
	 * @return
	 */
	public static double getHeight(double range, double sin, double cos, RadarBase radarBase) {
		return range * sin + CommonProps.D * range * range * cos * cos
				+ radarBase.getAntennaHeight();
	}

	/**
	 * 计算高度（方法1，目前应用于CAPPI、RHI），单位：km
	 * 
	 * @param range
	 *            单位：km
	 * @param elevation
	 *            仰角，单位：度
	 * @return
	 */
	public static double getHeight(double range, double elevation, RadarBase radarBase) {
		double ang = elevation * Math.PI / 180.0;
		return getHeight(range, Math.sin(ang), Math.cos(ang), radarBase);
	}

	/**
	 * 计算高度（方法2，目前应用于ET、HP），单位：km
	 * 
	 * @param range
	 *            单位：km
	 * @param sin
	 *            仰角sin值
	 * @return
	 */
	public static double getHeightNew2(double range, double sin) {
		return range * sin + range * range / (8.0 / 3.0 * CommonProps.RE);
	}
	/**
	 * Get antenna azimuth from cartesian x, y coordinate
	 * @param x X value
	 * @param y Y value
	 * @return Azimuth value
	 */
	public static double xyToAzimuth(float x, float y) {
		double az = Math.PI / 2 - Math.atan2(y, x);
		if (az < 0) {
			az = 2 * Math.PI + az;
		}
		return Math.toDegrees(az);
	}

	/**
	 * Get antenna azimuth from cartesian x, y coordinate
	 * @param xa X array value
	 * @param ya Y array value
	 * @return Azimuth array value
	 */
	public static Array xyToAzimuth(Array xa, Array ya) {
		xa = xa.copyIfView();
		ya = ya.copyIfView();

		Array aa = Array.factory(DataType.FLOAT, xa.getShape());
		for (int i = 0; i < aa.getSize(); i++) {
			aa.setFloat(i, (float) xyToAzimuth(xa.getFloat(i), ya.getFloat(i)));
		}

		return aa;
	}
	/**
	 * 计算高度（方法2，目前应用于ET、HP），单位：km
	 * 
	 * @param range
	 *            单位：km
	 * @param elevation
	 *            仰角，单位：度
	 * @return
	 */
	public static double getHeightNew(double range, double elevation) {
		return getHeightNew2(range, Math.sin(elevation * Math.PI / 180.0));
	}

	public static int getTextPosition(int base, String text) {
		int length = text.length();
		for (int i = 1; i < length; i++) {
			base -= 6;
		}
		return base;
	}

	public static void computePosition(List list, List texts) {
		if (texts.size() == 0) {
			return;
		}
		int h = 14;
		Iterator it = texts.iterator();
		while (it.hasNext()) {
			StringCoord pos = (StringCoord) it.next();
			int w = pos.text.getBytes().length * 6 + 1;
			Rectangle rect = new Rectangle(pos.x + 5, pos.y - 10, w, h);
			if (intersects(rect, list)) {
				rect = new Rectangle(pos.x - w - 2, pos.y - 10, w, h);
				if (intersects(rect, list)) {
					rect = new Rectangle(pos.x - w / 2 + 1, pos.y + 2, w, h);
					if (intersects(rect, list)) {
						rect = new Rectangle(pos.x - w / 2 + 1, pos.y - 20, w,
								h);
						if (intersects(rect, list)) {
							rect = new Rectangle(pos.x - 4, pos.y - 27, w, h);
							if (intersects(rect, list)) {

							} else {
								list.add(rect);
								pos.x = rect.x;
								pos.y = rect.y + h;
							}
						} else {
							list.add(rect);
							pos.x = rect.x;
							pos.y = rect.y + h;
						}
					} else {
						list.add(rect);
						pos.x = rect.x;
						pos.y = rect.y + h;
					}
				} else {
					list.add(rect);
					pos.x = rect.x;
					pos.y = rect.y + h;
				}
			} else {
				list.add(rect);
				pos.x = rect.x;
				pos.y = rect.y + h;
			}
		}
	}

	private static boolean intersects(Rectangle rect, List list) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Rectangle r = (Rectangle) it.next();
			if (r.intersects(rect)) {
				return true;
			}
		}
		return false;
	}

}
