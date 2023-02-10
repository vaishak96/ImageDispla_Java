
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	BufferedImage img1;
	int width = 512;
	int height = 512;
	int centerX = 256;
	int centerY = 256;
	int radius = 256;
	int x_max = 512;
	int y_max = 512;
	int x_min = 0;
	int y_min = 0;

	// Draws a black line on the given buffered image from the pixel defined by (x1,
	// y1) to (x2, y2)
	public void drawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawLine(x1, y1, x2, y2);
		g.drawImage(image, 0, 0, null);
	}

	public void showIms(String[] args) {

		// Read a parameter from command line
		int n = Integer.parseInt(args[0]);
		System.out.println("The number of lines is: " + n);
		double s = Double.parseDouble(args[1]);
		System.out.println("The scaling factor is: " + s);
		int alias=Integer.parseInt(args[2]);

		double stepSize = 360 / n;
		// Initialize a plain white image
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				// byte a = (byte) 255;
				byte r = (byte) 255;
				byte g = (byte) 255;
				byte b = (byte) 255;

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x, y, pix);
			}
		}

		drawLine(img, 0, 0, width - 1, 0); // top edge
		drawLine(img, 0, 0, 0, height - 1); // left edge
		drawLine(img, 0, height - 1, width - 1, height - 1); // bottom edge
		drawLine(img, width - 1, height - 1, width - 1, 0); // right edge

		for (int i = 0; i < n; i++) {
			double theta = i * stepSize;
			double alpha = theta - 90 * Math.round(theta / 90);
			double[] point = intersectingPoint(centerX, centerY, Math.cos(Math.toRadians(theta)), Math.sin(Math.toRadians(theta)));
			drawLine(img, 256, 256, (int) point[0], (int) point[1]); // diagonal line
		}

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		convertImgToImg1(img, img1, s,alias);
		lbIm2 = new JLabel(new ImageIcon(img1));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);
	}

	private double[] intersectingPoint(int x0, int y0, double xd, double yd) {
		double[] res = new double[2];
		String xIntersect = "";
		String yIntersect = "";
		double tx = 0, ty = 0;
		if (xd > 0) {
			xIntersect = "RIGHT";
			tx = (x_max - x0) / xd;
		} else if (xd < 0) {
			xIntersect = "LEFT";
			tx = (x_min - x0) / xd;
		} else {
			xIntersect = "NONE";
		}

		if (yd > 0) {
			yIntersect = "TOP";
			ty = (y_max - y0) / yd;
		} else if (yd < 0) {
			yIntersect = "BOTTOM";
			ty = (y_min - y0) / yd;
		} else {
			yIntersect = "NONE";
		}

		if (xIntersect.equals("NONE")) {
			res[0] = x0 + ty * xd;
			res[1] = y0 + ty * yd;
			return res;
		} else if (yIntersect.equals("NONE")) {
			res[0] = x0 + tx * xd;
			res[1] = y0 + ty * yd;
		} else {
			if (tx < ty) {
				res[0] = x0 + tx * xd;
				res[1] = y0 + tx * yd;
			} else if (ty < tx) {
				res[0] = x0 + ty * xd;
				res[1] = y0 + ty * yd;
			} else {
				res[0] = x0 + tx * xd;
				res[1] = y0 + tx * yd;
			}
		}
		return res;
	}

	private void convertImgToImg1(BufferedImage img, BufferedImage img1, double s,int alias) {
		int scaledWidth = (int) (width * s);
		int scaledHeight = (int) (height * s);
		if(alias==0)
		{
			for (int x = 0; x < scaledWidth; x++) {
				for (int y = 0; y < scaledHeight; y++) {
					img1.setRGB(x, y, img.getRGB((int) (x / s), (int) (y / s)));
				}
			}
		}
		else if(alias==1)
		{  int x_old=0;
		   int y_old=0;
           double pixel_sum=0.0;
		   int avg_count=0;
		   int avg_pixel=0;
			for (int x = 0; x < scaledWidth; x++) {
			for (int y = 0; y < scaledHeight; y++)
			{   
				 x_old=(int)(x/s)-1;
				 y_old=(int)(y/s)-1;
				 for(int i=0;i<3;i++){
					for(int j=0;j<3;j++){
						if((x_old<512) && (y_old<512)&&(x_old>=0)&&(y_old>=0))
						{ 
							pixel_sum +=img.getRGB(x_old, y_old);
							avg_count++;


						}

					}
				 }
				 avg_pixel=(int)(pixel_sum/avg_count);
				 img1.setRGB(x, y,avg_pixel );
				  pixel_sum=0.0;
				  avg_count=0;
				  avg_pixel=0;

			}

		}
	}
}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}