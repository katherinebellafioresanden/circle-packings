import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*; 
import java.util.Scanner; 

/******************************************************************************************
 * 
 * Program:	CirclePackingGenerator
 * Created by:	Katherine Bellafiore Sanden
 * Created on: August 2020
 * 
 * Description:
 * Generates data and/or an image of a circle packing according to your specifications.  
 *
 *****************************************************************************************/

public class CirclePackingGenerator extends JFrame 
{
	public static final int TITLE_BAR = 22;
	public static final int SCREEN_WIDTH = 900;
	public static final int SCREEN_HEIGHT = SCREEN_WIDTH;
	public static final int DRAWING_THRESHOLD = 5000; // max curvature that's worth drawing

	private static Packing packing;
		
	public static void main(String args[])
	{
		
		// ----------------------- get info from user -----------------------
		Scanner in = new Scanner(System.in);
		System.out.println("\n");
		System.out.println("        ------------------------------------------ ");
		System.out.println("         Welcome to the Circle Packing Generator.");
		System.out.println("        ------------------------------------------ ");
		System.out.println("\n");
		
		System.out.println("Enter your root quadruple, separated by spaces (e.g. -6 11 14 15):");
		int[] rootQuadruple = new int[4];
		rootQuadruple[0] = in.nextInt();
		rootQuadruple[1] = in.nextInt();
		rootQuadruple[2] = in.nextInt();
		rootQuadruple[3] = in.nextInt();
		in.nextLine();
		
		System.out.println("\nPrime component (p)?  Or the whole packing (w)?");
		String ans = in.nextLine();
		while (!ans.equals("p") && !ans.equals("w"))
		{
			System.out.println("\nInvalid.  Please type 'p' or 'w'.  Try again: ");
			ans = in.nextLine();
		}
		
		boolean pc = false;
		if (ans.equals("p"))  pc = true;

		boolean thickened = false;
		int pcrIndex = -1;
		
		if (pc)
		{
			System.out.println("\nPrime Component.  Thickened (y) or not (n)? ");
			ans = in.nextLine();
			while (!ans.equals("y") && !ans.equals("n"))
			{
				System.out.println("\nInvalid.  Please type 'y' or 'n'.  Try again: ");
				ans = in.nextLine();
			}
			if (ans.equals("y")) thickened = true;
			
			System.out.println("\nEnter the quadruple containing your PCR (Prime Component Root), "
					+ "separated by spaces (e.g. -6 11 14 23): ");
			int[] pcrQuad = new int[4];
			pcrQuad[0] = in.nextInt();
			pcrQuad[1] = in.nextInt();
			pcrQuad[2] = in.nextInt();
			pcrQuad[3] = in.nextInt();
			
			System.out.println("\nIn the quadruple you just entered, which curvature "
					+ "is the PCR? (e.g. 23): ");
			int pcr = in.nextInt();
			pcrIndex = calculateIndex(pcrQuad, pcr);
			while (pcrIndex == -1)
			{
				System.out.println("\nInvalid.  The PCR you entered was not in your PCR quad.  "
						+ "Enter a different PCR: ");
				pcr = in.nextInt();
				pcrIndex = calculateIndex(pcrQuad, pcr);
			}	
		}
		
			
		
		System.out.println("\nEnter the min and max curvatures to be recorded, "
				+ "separated by a space (e.g. 0 1000): ");
		int min = in.nextInt();
		int max = in.nextInt();
		in.nextLine();
		
		boolean image = false;
		boolean data = false;
		String filename = "";
		
		if (max <= DRAWING_THRESHOLD)
		{
			System.out.println("Image (i)?  Data (d)?  Or both (b)? ");
			ans = in.nextLine();
			while (!ans.equals("i") && !ans.equals("d") && !ans.equals("b"))
			{
				System.out.println("Invalid.  Please type 'i', 'd', or 'b'.  Try again: ");
				ans = in.nextLine();
			}
			if (ans.equals("b"))
			{
				image = true;
				data = true;
			}
			else if (ans.equals("i"))
			{
				image = true;
			}
			else // ans = "d"
			{
				data = true;
			}
		}
		else // we can only do data.  No image.
		{
			data = true;
		}
		
		if (data)
		{
			System.out.println("Your data will be saved to a .txt file.  "
					+ "Please provide a filename (omit the extension): ");
			filename = in.nextLine();
			
		}
		
		// ----------------------- done getting info from user -----------------------
		 
		long start = System.currentTimeMillis();
		
		Quadruple root = new Quadruple(rootQuadruple, pcrIndex); 
		
		if (data)
		{
			filename += ".txt";
			File file = new File(filename);

			try 
			{
				PrintWriter output = new PrintWriter(filename);
				packing = new Packing(root, pc, min, max, thickened, output);
				output.close();
			}
			catch (IOException ex)
			{
				System.out.printf("ERROR: %s\n", ex);
			}
			
			System.out.println("\n \n \nJust printed data to file: " + filename);
		}

		if (image)
		{
			packing = new Packing(root, pc, min, max, thickened, null);
			
			CirclePackingGenerator window = new CirclePackingGenerator(); 
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
			window.setTitle("Circle Packing Sketcher");			
			window.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);	
			window.setVisible(true);
		}
		
		
		
		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		System.out.println("Elapsed time: " + elapsedTime + " ms.");
	
	}
	
	public static int calculateIndex(int[] q, int curv)
	{
		int index = -1;
		for (int i = 0; i < q.length; i++)
		{
			if (q[i] == curv)
			{
				index = i;
			}
		}
		return index; // return -1 if curv is not in the array
	}


	// Graphics methods ------------------------------------------
	public void paint(Graphics g) 		
	{
		clearScreen(g, Color.WHITE);
		packing.draw(g);

	}

	public void clearScreen(Graphics g, Color c)
	{
		g.setColor(c);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);


	}

}
