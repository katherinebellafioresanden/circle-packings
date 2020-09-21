import java.awt.*;
import java.awt.geom.Ellipse2D;

public class LabeledCircle 
{
	
	// common for all circles - values will be changed by Packing.java
	public static double scaleFactor = 1;
	public static double xOuterCenter = 0;
	public static double yOuterCenter = 0;
	
	// unique to one circle
	private int curv;
	private Complex combo;
	private Color c;
	

	
	public LabeledCircle(Quadruple qIn, int indexIn) 
	{
		curv = qIn.quad()[indexIn];
		combo = qIn.combos()[indexIn];
		c = Color.BLACK;
		
//		if (Packing.isPrime(curv))
//		{
//			c = Color.red;
//		}

		
	}
	
	public int curv()
	{
		return curv;
	}
	
	public Complex combo()
	{
		return combo;
	}
	
	public void draw(Graphics g)
	{
		// calculate & draw circle
		double r = calculateRadius();
		double x = calculateX();
		double y = calculateY();
		
		g.setColor(c);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON);
		
		Shape theCircle = new Ellipse2D.Double(x - r, y - r, 2.0 * r, 2.0 * r);
	    g2.draw(theCircle);
	    
	    // draw numerical curvature inside of circle    
	    if (curv >= 0)
	    {
		    String text = "" + curv;
		    int fontSize = (int) (r);
		    
		    Font f = new Font("Arial", Font.BOLD, fontSize);
		    g.setFont(f); 
		    
		    int width = g.getFontMetrics().stringWidth(text);
		    int height = g.getFontMetrics().getHeight();
		    
		    g.drawString("" + curv, (int) (x - width / 2), (int) (y + height / 3));
	    }

//	    System.out.println("Just drew a circle at x = " + x + ", y = " + y + ", r = " + r);	
	}
	
	public double calculateRadius()
	{
		double temp = curv / scaleFactor;
		if (curv < 0) temp = -curv/scaleFactor;
		double r = 1.0 / temp;
		return r;
	}
	
	public double calculateX()
	{
		double centerX = combo.re().val() / curv;
		double x = xOuterCenter + centerX * scaleFactor;
		return x;
	}
	
	public double calculateY()
	{
		double centerY = combo.im().val() / curv;
		double y = yOuterCenter + centerY * scaleFactor;
		return y;
	}
	
	public String toString()
	{
		return curv + ",   " + combo.re() + ",   " + combo.im();
	}
	
	@Override
	public boolean equals(Object x)
	{
		if (x == null) return false;
		if (this.getClass() != x.getClass()) return false;
		
		LabeledCircle that = (LabeledCircle) x;
		return curv == that.curv() && combo.equals(that.combo());
	}
	
}
