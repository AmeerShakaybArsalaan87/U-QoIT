package DTNRouting;


public class Location {
		public double x;
		public double y;
		public double z=0;

		public Location() {}
	   
		public Location(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = 0;
		}
		
		public Location getMidpoint(Location pt2) {
			Location mPt = new Location((x+pt2.x)/2.0, (y+pt2.y)/2.0, 0);
			return mPt;
		}
		
		public String toString() {
			String result = Double.toString(x) + ":" + Double.toString(y) + ":" + Double.toString(z);
			return result;
		}
}
