// Skeletal program for the "Image Morpher" assignment
// Written by:  Minglun Gong


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javafx.animation.*;

import javax.swing.JOptionPane;

import java.io.*;

// Main class
public class ImageMorph extends Frame implements ActionListener {
	ControlCanvas source, target, result;
	BufferedImage srcImage, destImage;
	int width, height;

	// Control Lines drawn on Source and Destination images
	Vector<LineSegment> sourceLines = new Vector<>();
	Vector<LineSegment> destinationLines = new Vector<>();
	//Vector<LineSegment> interpolatedLines = new Vector<>();
	
	
	// Constants
	float a = (float) 0.0001;
	float b = 1;
	float c = 2;

	// Constructor
	public ImageMorph(String srcName, String destName) {
		super("Image Morpher");
		// load images
		srcImage = loadImage(srcName);
		destImage = loadImage(destName);
		// prepare the panel for source and target images.
		Panel main = new Panel();
		source = new ControlCanvas(srcImage);
		target = new ControlCanvas(destImage);
		result = new ControlCanvas();
		main.setLayout(new GridLayout(1, 3, 10, 10));
		main.add(source);
		main.add(result);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Clear Controls");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Interpolate Lines");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Warp Source");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Warp Target");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Morph Images");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Load Controls");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Save Controls");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
	}

	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public BufferedImage loadImage(String name) {
		Image image = Toolkit.getDefaultToolkit().getImage(name);
		MediaTracker mt = new MediaTracker(this);
		try {
			mt.addImage(image, 0);
			mt.waitForID(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// convert to buffered image
		width = image.getWidth(null);
		height = image.getHeight(null);
		BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		buff.createGraphics().drawImage(image, 0, 0, null);
		return buff;
	}

	// Action listener for buttons
	public void actionPerformed(ActionEvent e) {
		// clear control lines and restore the target panel
		if (((Button) e.getSource()).getLabel().equals("Clear Controls")) {
			source.lines.clear();
			source.repaint();
			target.lines.clear();
			target.repaint();
			result.resetImage(null);
		}
		if (((Button) e.getSource()).getLabel().equals("Interpolate Lines")) {
			int numLines = 43; // default number of lines

			Vector<LineSegment> interpolatedLines = new Vector<>();
			if (source.lines.size() == target.lines.size()) {
				numLines = source.lines.size();
			}

			// interpolatedLines =
			// Util.interpolateLines(sourceLines,destinationLines,numLines);

			interpolatedLines = Util.interpolateLines(source.lines, target.lines, numLines);

			result.lines.clear();

			for (LineSegment lineSegment : interpolatedLines) {
				result.lines.addElement(lineSegment);
			}
			result.repaint();
		}
		int[][] dest = new int[height][width]; // the destination image integer array
		if (((Button) e.getSource()).getLabel().equals("Warp Source")) {
			int pixels[] = new int[width * height];
			if (!source.lines.isEmpty()) {
				dest = WarpImage(dest, srcImage, result.lines); // call for WarpImage
														// Function
				for (int x = 0, i = 0; x < height; x++) {
					for (int y = 0; y < width; y++, i++) {
						pixels[i] = dest[x][y];
					}
				}
				result.resetImage(createImage(new MemoryImageSource(width, height, pixels, 0, width)));
			}
			result.lines.clear();
			result.repaint();
		}
		if (((Button) e.getSource()).getLabel().equals("Warp Target")) {
			int pixels[] = new int[width * height];
			if (!target.lines.isEmpty()) {
				dest = WarpImage(dest, destImage, result.lines); // call for WarpImage
														// Function
				for (int x = 0, i = 0; x < height; x++) {
					for (int y = 0; y < width; y++, i++) {
						pixels[i] = dest[x][y];
					}
				}
				result.resetImage(createImage(new MemoryImageSource(width, height, pixels, 0, width)));
			}
			result.lines.clear();
			result.repaint();
		}
		if (((Button) e.getSource()).getLabel().equals("Morph Images")) {
			int pixels[] = new int[width * height];
			// Demo -- generate the cross-dissolving result for the two images
			for (int y = 0, i = 0; y < height; y++)
				for (int x = 0; x < width; x++, i++) {
					Color s = new Color(srcImage.getRGB(x, y));
					Color d = new Color(destImage.getRGB(x, y));
					pixels[i] = new Color((s.getRed() + d.getRed()) / 2, // Get
																			// half
																			// Red
																			// pixels
																			// of
																			// each
																			// image
							(s.getGreen() + d.getGreen()) / 2, // Get half Green
																// pixels of
																// each image
							(s.getBlue() + d.getBlue()) / 2).getRGB(); // Get
																		// half
																		// Blue
																		// pixels
																		// of
																		// each
																		// image
				}
			// End of demo -- replace this section with image morphing
			// algorithm.
			result.resetImage(createImage(new MemoryImageSource(width, height, pixels, 0, width)));
		}
		if (((Button) e.getSource()).getLabel().equals("Save Controls"))
			saveControlLines();
		if (((Button) e.getSource()).getLabel().equals("Load Controls")) {
			loadControlLines();
			source.repaint();
			target.repaint();
		}
	}

	public static void main(String[] args) {
		ImageMorph window;
		if (args.length > 1)
			window = new ImageMorph(args[0], args[1]);
		else
			window = new ImageMorph("./img/kitty.png", "./img/puppy.png");
		window.setSize(850, 350);
		window.setVisible(true);
	}

	// load control lines from a file
	private void loadControlLines() {
		source.lines.clear();
		target.lines.clear();
		try {
			BufferedReader bw = new BufferedReader(
					new FileReader((new File("./ctl/kitty-puppy.ctl")).getAbsoluteFile()));
			int size = Integer.parseInt(bw.readLine());
			bw.readLine();
			for (int i = 0; i < size; i++) {
				String[] numbers = bw.readLine().split("\\s");
				LineSegment l = new LineSegment(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
				l.resetEndPos(Integer.parseInt(numbers[2]), Integer.parseInt(numbers[3]));
				source.lines.add(l);
				// test
				sourceLines.addElement(l);

			}
			bw.readLine();
			for (int i = 0; i < size; i++) {
				String[] numbers = bw.readLine().split("\\s");
				LineSegment l = new LineSegment(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
				l.resetEndPos(Integer.parseInt(numbers[2]), Integer.parseInt(numbers[3]));
				target.lines.add(l);
				// test
				destinationLines.addElement(l);
			}
			bw.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	// save control lines to a file
	private void saveControlLines() {
		try {
			File f = new File("./ctl/controls.ctl");
			// if file doesnt exists, then create it
			if (!f.exists())
				f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()));
			bw.write(source.lines.size() + "\n\n");
			// Gets the coordinates of each lines' start and end points (X,Y)
			// from SOURCE image1
			for (LineSegment line : source.lines)
				bw.write(line.x0 + " " + line.y0 + "\t" + line.x1 + " " + line.y1 + "\n");
			bw.write("\n");
			// Gets the coordinates of each lines' start and end points (X,Y)
			// from TARGET image
			for (LineSegment line : target.lines)
				bw.write(line.x0 + " " + line.y0 + "\t" + line.x1 + " " + line.y1 + "\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int[][] WarpImage(int[][] dest, BufferedImage bufferedImage, Vector<LineSegment> lines2) {

		double uv[] = new double[2];
		vector pixel;
		vector P_src = new vector();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double[] sum_p = { 0, 0 };
				double sum_w = 0;
				double w = 0;
				pixel = new vector(j, i);// pixel = new Pixel(i,j);

				Iterator<LineSegment> oline = result.lines.iterator(); // creat
																		// ArrayList
																		// for
																		// input
																		// and
																		// output
																		// lines
				Iterator<LineSegment> iline = source.lines.iterator();
				while (oline.hasNext() && iline.hasNext()) // iterate through
															// each lines
				{

					LineSegment line = oline.next();
					LineSegment inputLine = iline.next();

					uv = TransferToLocalCoord(line, pixel); // call Transfer to
															// LocalCoord to
															// retreive U and V
					w = CalculateWeight(line, uv, pixel); // Calculate the
															// weight
					P_src = globalCoordInSrc(uv, inputLine); // obtain the
																// respected
																// source image
																// pixels

					// check for boundries
					if (P_src.x > 0 && P_src.x < width && P_src.y > 0 && P_src.y < height) {
						sum_p[0] += P_src.x * w;
						sum_p[1] += P_src.y * w;

					} else if (P_src.x > 0 && P_src.x < width && P_src.y < 0 || P_src.y > height) {
						sum_p[0] += P_src.x * w;
						sum_p[1] += 0;
					} else if (P_src.x < 0 || P_src.x > width && P_src.y > 0 && P_src.y < height) {
						sum_p[0] += 0;
						sum_p[1] += P_src.y * w;
					}
					sum_w += w;
				}
				P_src.x = sum_p[0] / sum_w;
				P_src.y = sum_p[1] / sum_w;
				P_src = new vector(P_src.x, P_src.y);
				// check for right values and boundaries
				if (P_src.x > 0 && P_src.y > 0)
					dest[i][j] = SampleSource(bufferedImage, P_src);

				else
					dest[i][j] = 0; // if out of boundary put zero value
			}
		}
		return dest;
	}

	// obtain U and V values
	// input values are the current Control line and the pixel which we watn to
	// find the uv value
	public double[] TransferToLocalCoord(LineSegment line, vector pixel) {
		double uv[] = new double[2];
		vector A, B;
		vector temp = new vector();

		A = new vector(line.x0, line.y0);
		B = new vector(line.x1, line.y1);//

		vector first = new vector(pixel.x - A.x, pixel.y - A.y); // P-A
		vector second = new vector(B.x - A.x, B.y - A.y);// B-A
		uv[0] = temp.dot(first, second) / temp.norm2(second);// u
		vector D0 = new vector(B.y - A.y, -(B.x - A.x)); // Perpendicular B-a

		double p = temp.magnitude(D0); // |D|
		vector a = temp.devision(D0, p);// d0/|d}
		uv[1] = temp.dot(first, a); // (P-A) . D0/|d|
		return uv;
	}

	// Backward warping, find the the respected source image values according to
	// destination's image uv
	public vector globalCoordInSrc(double[] uv, LineSegment line) {

		vector temp = new vector();
		vector A_prime, B_prime;
		int i = 0;
		double w = 0;
		A_prime = new vector(line.x0, line.y0);
		B_prime = new vector(line.x1, line.y1);
		vector second = new vector(B_prime.x - A_prime.x, B_prime.y - A_prime.y); // b-a
		vector D0 = new vector(B_prime.y - A_prime.y, -(B_prime.x - A_prime.x));// Pixel
																				// D0
		double bottom = temp.magnitude(D0);// |D|
		vector t = temp.devision(D0, bottom); // D/|D|
		vector top = temp.mult(uv[1], t);// V.D
		vector mid = temp.mult(uv[0], second);
		vector P = new vector(A_prime.x + mid.x + top.x, A_prime.y + mid.y + top.y);

		return P;
	}

	public double CalculateWeight(LineSegment line, double[] uv, vector pixel) {

		double w = 0;
		// double length = Math.sqrt(Math.pow(Math.abs(line.y1-line.y0), 2)+
		// Math.pow(Math.abs(line.x1-line.x0), 2));
		double distance = 0;
		double a = 0.0001;
		int b = 1;
		int c = 2;
		// calculate distance to control line
		vector temp = new vector();
		// Vect AB = new Vect(line.x0,line.y0,line.x1,line.y1);
		vector B = new vector(line.x1, line.y1);
		vector A = new vector(line.x0, line.y0);
		vector AB = new vector(B.x - A.x, B.y - A.y); // b-a
		double length = temp.magnitude(AB);
		vector first = new vector(pixel.x - A.x, pixel.y - A.y); // P-A
		vector second = new vector(pixel.x - B.x, pixel.y - B.y);// P-B
		if (uv[0] < 0)
			distance = temp.magnitude(first);// |p-a|
		else if (uv[0] > 1)
			distance = temp.magnitude(second);//// |p-a|
		else
			distance = Math.abs(uv[1]);// v
		// calculate weight
		w = Math.pow(length, b) / (a + distance);
		w = Math.pow(w, c);
		return w;
	}

	// bilinear sampling the source image
	public int SampleSource(BufferedImage bufferImage, vector p_src) {
		// red
		int resultColor1 = 0, resultColor2 = 0, resultColor3 = 0;
		Color resultColor;
		double temp1, temp2, temp11, tempf1, tempf2;
		int x = (int) Math.floor(p_src.x);
		int y = (int) Math.floor(p_src.y);
		int rgb, rgb1, rgb2, rgb3;

		// red
		if (y + 1 < height && x + 1 < width) {
			rgb = bufferImage.getRGB(x, y);
			rgb1 = bufferImage.getRGB(x + 1, y);
			rgb2 = bufferImage.getRGB(x, y + 1);
			rgb3 = bufferImage.getRGB(x + 1, y + 1);
			temp1 = ((rgb & 0X00FF0000) >> 16) * (x + 1 - p_src.x);
			temp11 = ((rgb1 & 0X00FF0000) >> 16) * (p_src.x - x);
			tempf1 = temp1 + temp11;
			temp1 = ((rgb2 & 0X00FF0000) >> 16) * (x + 1 - p_src.x);
			temp11 = ((rgb3 & 0X00FF0000) >> 16) * (p_src.x - x);
			tempf2 = temp1 + temp11;

			resultColor1 = (int) (tempf1 * (y + 1 - p_src.y) + tempf2 * (p_src.y - y));

		}
		// green
		if (y + 1 < height && x + 1 < width) {
			rgb = bufferImage.getRGB(x, y);
			rgb1 = bufferImage.getRGB(x + 1, y);
			rgb2 = bufferImage.getRGB(x, y + 1);
			rgb3 = bufferImage.getRGB(x + 1, y + 1);
			temp1 = ((rgb & 0X0000FF00) >> 8) * (x + 1 - p_src.x);
			temp11 = ((rgb1 & 0X0000FF00) >> 8) * (p_src.x - x);
			tempf1 = temp1 + temp11;
			temp1 = ((rgb2 & 0X0000FF00) >> 8) * (x + 1 - p_src.x);
			temp11 = ((rgb3 & 0X0000FF00) >> 8) * (p_src.x - x);
			tempf2 = temp1 + temp11;
			resultColor2 = (int) (tempf1 * (y + 1 - p_src.y) + tempf2 * (p_src.y - y));
		}
		// blue
		if (y + 1 < height && x + 1 < width) {
			rgb = bufferImage.getRGB(x, y);
			rgb1 = bufferImage.getRGB(x + 1, y);
			rgb2 = bufferImage.getRGB(x, y + 1);
			rgb3 = bufferImage.getRGB(x + 1, y + 1);
			temp1 = ((rgb & 0X000000FF)) * (x + 1 - p_src.x);
			temp11 = ((rgb1 & 0X000000FF)) * (p_src.x - x);
			tempf1 = temp1 + temp11;
			temp1 = ((rgb2 & 0X000000FF)) * (x + 1 - p_src.x);
			temp11 = ((rgb3 & 0X000000FF)) * (p_src.x - x);
			tempf2 = temp1 + temp11;
			resultColor3 = (int) (tempf1 * (y + 1 - p_src.y) + tempf2 * (p_src.y - y));
		}
		// combine three color channels
		resultColor = new Color(resultColor1, resultColor2, resultColor3);
		return resultColor.getRGB();

	}

}

// Canvas for image display and control source
class ControlCanvas extends Canvas {
	int xoffset, yoffset;
	Image image;
	Vector<LineSegment> lines = new Vector<LineSegment>();
	LineSegment newline;

	// initialize the image and mouse control
	public ControlCanvas(Image source) {
		image = source;
		DragListener drag = new DragListener();
		addMouseListener(drag);
		addMouseMotionListener(drag);
	}

	public ControlCanvas() {
	}

	// change the image and redraw the canvas
	public void resetImage(Image source) {
		image = source;
		repaint();
	}

	// maintain control line segment list
	public void createLineSegment(int x, int y) {
		newline = new LineSegment(x - xoffset, y - yoffset);
	}

	public void updateLineSegment(int x, int y) {
		Graphics g = getGraphics();
		((Graphics2D) g).setStroke(new BasicStroke(2));
		g.setXORMode(Color.RED);
		newline.draw(g, xoffset, yoffset);
		newline.resetEndPos(x - xoffset, y - yoffset);
		newline.draw(g, xoffset, yoffset);
	}

	public void insertLineSegment(int x, int y) {
		lines.add(newline);
		Graphics g = getGraphics();
		((Graphics2D) g).setStroke(new BasicStroke(2));
		g.setColor(Color.RED);
		newline.draw(g, xoffset, yoffset);
	}

	// redraw the canvas
	public void paint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		if (image != null) {
			xoffset = (getWidth() - image.getWidth(null)) / 2;
			yoffset = (getHeight() - image.getHeight(null)) / 2;
			g.drawImage(image, xoffset, yoffset, this);
		}
		((Graphics2D) g).setStroke(new BasicStroke(2));
		g.setColor(Color.RED);
		for (int i = 0; i < lines.size(); i++)
			lines.elementAt(i).draw(g, xoffset, yoffset);
	}

	// Action listener for mouse
	class DragListener extends MouseAdapter implements MouseMotionListener {
		public void mousePressed(MouseEvent e) {
			createLineSegment(e.getX(), e.getY());

		}

		public void mouseReleased(MouseEvent e) {
			insertLineSegment(e.getX(), e.getY());
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			updateLineSegment(e.getX(), e.getY());
		}
	}
}

//vector class for related operations over vectors AB and A'B'
class vector {
	public double x, y, x0 = 0, y0 = 0;

	public vector() {
		x = 0;
		y = 0;
	}

	public vector(double x, double y) {

		this.x = x;
		this.y = y;
	}

	public vector(double x0, double y0, double x, double y) {

		this.x0 = x0;
		this.y0 = y0;
		this.x = x;
		this.y = y;
	}

	public vector devision(vector v, double d) {
		v.x = v.x / d;
		v.y = v.y / d;
		return v;
	}

	public vector mult(double d, vector v)// scalar product
	{
		v.x = d * v.x;
		v.y = d * v.y;
		return v;
	}

	public double norm2(vector v) {

		return v.x * v.x + v.y * v.y;
	}

	// normalize vector
	public vector norm(vector v) 				
	{
		double d = Math.sqrt(norm2(v));
		v.x /= d;
		v.y /= d;
		return v;
	}

	public double magnitude(vector v) {

		return Math.sqrt(v.x * v.x + v.y * v.y);
	}

	public void add(vector v) {
		x += v.x;
		y += v.y;
	}

	public void sub(vector v) {
		x -= v.x;
		y -= v.y;
	}

	public vector subtraction(vector v1, vector v2) {
		return new vector(v1.x - v2.x, v1.y - v2.y);
	}

	public double dot(vector v1, vector v2) {
		return v1.x0 * v2.x0 + v1.x * v2.x + v1.y0 * v2.y0 + v1.y * v2.y;

	}
}



// LineSegment class defines the control line
class LineSegment {
	int x0, y0, x1, y1;

	// Constructor
	public LineSegment(int x, int y) {
		x0 = x1 = x;
		y0 = y1 = y;
	}

	public LineSegment(int xStart, int yStart, int xEnd, int yEnd) {
		x0 = xStart;
		y0 = yStart;
		x1 = xEnd;
		y1 = yEnd;
	}

	public void resetStartPos(int x, int y) {
		x0 = x;
		y0 = y;
	}

	public void resetEndPos(int x, int y) {
		x1 = x;
		y1 = y;
	}

	public void draw(Graphics g, int xoffset, int yoffset) {
		g.drawLine(x0 + xoffset, y0 + yoffset, x1 + xoffset, y1 + yoffset);
	}
}
