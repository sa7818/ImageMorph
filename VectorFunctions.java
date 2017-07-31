
import java.awt.Point;

public class VectorFunctions {

	public static Point vectorSubtraction(Point vector1, Point vector2){
		Point value = null;
		value.x = vector1.x - vector2.x;
		value.y = vector1.y - vector2.y;
		return value;
	}
	
	public Point vectorAddition(Point vector1, Point vector2){
		Point value = null;
		value.x = vector1.x + vector2.x;
		value.y = vector1.y + vector2.y;
		return value;
	}
	
	public LineSegment scalarAddition(LineSegment vector1, int scalar){
		LineSegment value = null;
		value.x0 = vector1.x0 + scalar;
		value.x1 = vector1.x1 + scalar;
		value.y0 = vector1.y0 + scalar;
		value.y1 = vector1.y1 + scalar;
		return value;
	}
	
	
	public static int dotProduct(Point point1, Point point2){
		int value = (point1.x * point2.x + point1.y * point2.y);
		return value;
	}
	
	public static double magnitude(LineSegment vector){
		double LenthOfVector = 0 ;
		double X = vector.x1 - vector.x0;
		double Y = vector.y1 - vector.y0;
		LenthOfVector = Math.sqrt((X*X) + (Y*Y));
		return LenthOfVector;
	}
	
	public static LineSegment perpendicular(LineSegment vector){
		LineSegment prep = new LineSegment(vector.x0,vector.y0);
		
		prep.resetEndPos(-vector.y1, vector.x1);
		
		return prep;
		
	}
	
	public LineSegment scalarMult(LineSegment vector, int scalar){
		
		LineSegment prep = new LineSegment(vector.x0,vector.y0);
		int absScalar = Math.abs(scalar);
		
		prep.resetEndPos(vector.x1 * absScalar, vector.y1  * absScalar);
		
		return prep;
	}
	
	public LineSegment scalarDivision(LineSegment vector, int scalar){
		
		LineSegment prep = new LineSegment(vector.x0,vector.y0);
		int absScalar = Math.abs(scalar);
		
		prep.resetEndPos(vector.x1 / absScalar, vector.y1  / absScalar);
		
		return prep;
	}
	

	
}
