
import java.awt.Point;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Vector;

class Util {

	public static Vector<LineSegment> interpolateLines(Vector<LineSegment> sourceLines,
			Vector<LineSegment> destinationLines, int numLines) {
		Vector<LineSegment> newLines = new Vector<>();
		int xStart, yStart, xEnd, yEnd;

		newLines.clear();

		for (int i = 0; i < numLines; i++) {

			xStart = (destinationLines.elementAt(i).x0 + sourceLines.elementAt(i).x0) / 2;
			yStart = (destinationLines.elementAt(i).y0 + sourceLines.elementAt(i).y0) / 2;
			xEnd = (destinationLines.elementAt(i).x1 + sourceLines.elementAt(i).x1) / 2;
			yEnd = (destinationLines.elementAt(i).y1 + sourceLines.elementAt(i).y1) / 2;

			newLines.addElement(new LineSegment(xStart, yStart, xEnd, yEnd));

		}

		return newLines;

	}
}
