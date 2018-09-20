package gdal;

public class CoordinateTranslate {

	private static double a = 6378137.0;
	private static double b = 6356752.3142451;
	private static double lon0 = 121 * Math.PI / 180;
	private static double k0 = 0.9999;
	private static int dx = 250000;
	private static int dy = 0;
	private static double e = 1 - Math.pow(b, 2) / Math.pow(a, 2);
	private static double e2 = (1 - Math.pow(b, 2) / Math.pow(a, 2)) / (Math.pow(b, 2) / Math.pow(a, 2));

	public static double[] Wgs84ToTwd97(double lon, double lat) {
		lon = (lon - (long) ((lon + 180) / 360) * 360) * Math.PI / 180;
		lat = lat * Math.PI / 180;

		double V = a / Math.sqrt(1 - e * Math.pow(Math.sin(lat), 2));
		double T = Math.pow(Math.tan(lat), 2);
		double C = e2 * Math.pow(Math.cos(lat), 2);
		double A = Math.cos(lat) * (lon - lon0);
		double M = a * ((1.0 - e / 4.0 - 3.0 * Math.pow(e, 2) / 64.0 - 5.0 * Math.pow(e, 3) / 256.0) * lat
				- (3.0 * e / 8.0 + 3.0 * Math.pow(e, 2) / 32.0 + 45.0 * Math.pow(e, 3) / 1024.0) * Math.sin(2.0 * lat)
				+ (15.0 * Math.pow(e, 2) / 256.0 + 45.0 * Math.pow(e, 3) / 1024.0) * Math.sin(4.0 * lat)
				- (35.0 * Math.pow(e, 3) / 3072.0) * Math.sin(6.0 * lat));
		// x
		double x = dx + k0 * V * (A + (1 - T + C) * Math.pow(A, 3) / 6
				+ (5 - 18 * T + Math.pow(T, 2) + 72 * C - 58 * e2) * Math.pow(A, 5) / 120);
		// y
		double y = dy + k0 * (M
				+ V * Math.tan(lat) * (Math.pow(A, 2) / 2 + (5 - T + 9 * C + 4 * Math.pow(C, 2)) * Math.pow(A, 4) / 24
						+ (61 - 58 * T + Math.pow(T, 2) + 600 * C - 330 * e2) * Math.pow(A, 6) / 720));

		return new double[] { x, y };
	}

	public static double[] Twd97ToWgs84(double x, double y) {
		x -= dx;
		y -= dy;

		// Calculate the Meridional Arc
		double M = y / k0;

		// Calculate Footprint Latitude
		double mu = M / (a * (1.0 - e / 4.0 - 3 * Math.pow(e, 2) / 64.0 - 5 * Math.pow(e, 3) / 256.0));
		double e1 = (1.0 - Math.sqrt(1.0 - e)) / (1.0 + Math.sqrt(1.0 - e));

		double J1 = (3 * e1 / 2 - 27 * Math.pow(e1, 3) / 32.0);
		double J2 = (21 * Math.pow(e1, 2) / 16 - 55 * Math.pow(e1, 4) / 32.0);
		double J3 = (151 * Math.pow(e1, 3) / 96.0);
		double J4 = (1097 * Math.pow(e1, 4) / 512.0);

		double fp = mu + J1 * Math.sin(2 * mu) + J2 * Math.sin(4 * mu) + J3 * Math.sin(6 * mu) + J4 * Math.sin(8 * mu);

		// Calculate Latitude and Longitude
		double C1 = e2 * Math.pow(Math.cos(fp), 2);
		double T1 = Math.pow(Math.tan(fp), 2);
		double R1 = a * (1 - e) / Math.pow((1 - e * Math.pow(Math.sin(fp), 2)), (3.0 / 2.0));
		double N1 = a / Math.pow((1 - e * Math.pow(Math.sin(fp), 2)), 0.5);

		double D = x / (N1 * k0);

		// 計算緯度
		double Q1 = N1 * Math.tan(fp) / R1;
		double Q2 = (Math.pow(D, 2) / 2.0);
		double Q3 = (5 + 3 * T1 + 10 * C1 - 4 * Math.pow(C1, 2) - 9 * e2) * Math.pow(D, 4) / 24.0;
		double Q4 = (61 + 90 * T1 + 298 * C1 + 45 * Math.pow(T1, 2) - 3 * Math.pow(C1, 2) - 252 * e2) * Math.pow(D, 6)
				/ 720.0;
		double lat = fp - Q1 * (Q2 - Q3 + Q4);

		// 計算經度
		double Q5 = D;
		double Q6 = (1 + 2 * T1 + C1) * Math.pow(D, 3) / 6;
		double Q7 = (5 - 2 * C1 + 28 * T1 - 3 * Math.pow(C1, 2) + 8 * e2 + 24 * Math.pow(T1, 2)) * Math.pow(D, 5)
				/ 120.0;
		double lon = lon0 + (Q5 - Q6 + Q7) / Math.cos(fp);

		lat = (lat * 180) / Math.PI; // 緯度
		lon = (lon * 180) / Math.PI; // 經度

		return new double[] { lon, lat };
	}
}
