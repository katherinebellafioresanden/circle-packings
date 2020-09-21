
public class Quadruple
{
	private int[] quad; // array of 4 integers that form a quadruple in an ACP
	private int offLimits; // the index of the quadruple which is off limits.  
						  // Meaning we don't want to apply that Sj and lose that curvature.
	
	private Complex[] centerCombos; // (center * curvature)'s
	
	// OG CONSTRUCTOR - when you know everything about the quad
	public Quadruple(int[] q, Complex[] c, int off)
	{
		quad = q.clone();
		centerCombos = c.clone();
		offLimits = off;
	}
	
	// CONSTRUCTOR: option B, when you're making a root quadruple and you don't know the centers
	public Quadruple(int[] q, int off)
	{
		offLimits = off;
		quad = q.clone();
		
		// figure out centers
		centerCombos = findCenterCombos(quad);
	}

	
	// CONSTRUCTOR: option C, when you don't really know anything about your quad
	public Quadruple()
	{
		quad = new int[4];
		centerCombos = new Complex[4];
		offLimits = -1;
	}
	
	public static Complex[] findCenterCombos(int[] rootquad)
	{
		Complex[] combos = new Complex[4];

		if (rootquad[0] == -1)
		{
			combos[0] = new Complex(0,1,0,1);
			combos[1] = new Complex(-1, 2, 0, 1).scale(rootquad[1], 1);
			combos[2] = new Complex(1, 2, 0, 1).scale(rootquad[2], 1);
			combos[3] = new Complex(0, 1, 2, 3).scale(rootquad[3], 1);
		}
		else if (rootquad[0] == -6)
		{
			combos[0] = new Complex(-3, 1, -4, 1);
			combos[1] = new Complex(6, 1, 8, 1);
			combos[2] = new Complex(7, 1, 8, 1);
			combos[3] = new Complex(6, 1, 10, 1);
		}
		else if (rootquad[0] == -11)
		{
			combos[0] = new Complex(-8, 1, -6, 1);
			combos[1] = new Complex(16, 1, 12, 1);
			combos[2] = new Complex(17, 1, 12, 1);
			combos[3] = new Complex(19, 1, 16, 1);
		}
		else // use CenterCalulator to compute these
		{
			combos[0] = new Complex(0, 1, 0, 1);
			combos[1] = new Complex(0, 1, 0, 1);
			combos[2] = new Complex(0, 1, 0, 1);
			combos[3] = new Complex(0, 1, 0, 1);
		}
		
		return combos;
	}
	

	
	// ACCESSOR METHODS
	public int[] quad()
	{
		return quad;
	}
	
	public Complex[] combos()
	{
		return centerCombos;
	}
	
	public Complex centerAt(int i)
	{
		if (i < 0 || i > 3)
		{
			System.out.println("invalid index of " + i);
			return null;
		}
		
		Complex combo = centerCombos[i];
		int curvature = quad[i];
		Complex center = combo.scale(1, curvature);
		
		return center;
	}


	public int offLimits()
	{
		return offLimits;
	}

	// SETTING METHODS
	public void setOffLimits(int newOffLimits)
	{
		offLimits = newOffLimits;
	}

	public void setQuadruple(int[] q)
	{
		quad = q.clone();
	}



	// OTHER METHODS

	public Quadruple clone()
	{
		int[] nums = new int[4];
		Complex[] cents = new Complex[4];
		for (int i = 0; i < nums.length; i++)
		{
			nums[i] = quad[i];
			cents[i] = centerCombos[i];
		}

		// make a new quadruple using this array and offLimits number
		Quadruple q = new Quadruple(nums, cents, offLimits);

		return q;
	}

	public int max()
	{
		int max = quad[0];
		
		for (int i = 1; i < quad.length; i++)
		{
			if (quad[i] > max)
			{
				max = quad[i];
			}
		}
		return max;
	}
	
	public int indexOfMax()
	{
		int indexOfMax = 0;
		
		for (int i = 1; i < quad.length; i++)
		{
			if (quad[i] > quad[indexOfMax])
			{
				indexOfMax = i;
			}
		}
		return indexOfMax;
	}
	
	public int min()
	{
		// if the first curvature is negative, treat it as a positive for this function
		int temporaryPositiveVersion = quad[0];
		if (quad[0] < 0)
		{
			temporaryPositiveVersion = -quad[0];
		}
		
		int min = temporaryPositiveVersion;
		
		for (int i = 1; i < quad.length; i++)
		{
			if (quad[i] < min)
			{
				min = quad[i];
			}
		}
		return min;
	}
	
	public boolean isNotLargest(int index)
	{	
		// compare quad[index] each other member of the quadruple
		for (int i = 0; i < quad.length; i++)
		{
			// don't compare quad[index] to itself... so, skip this stuff if i == index
			if (i != index) 
			{
				// if quad[index] is smaller than another entry, then quad[index] is not largest
				if (quad[index] < quad[i]) return true;
			}
		}

		// we went through and no one else in the quadruple was bigger than quad[index]
		return false;
	}
	

	/**
	 * Add two rational numbers: num1/denom1 + num2/denom2
	 * @param num1
	 * @param denom1
	 * @param num2
	 * @param denom2
	 * @return answer[0] = numerator, answer[1] = denominator
	 */
	public static long[] plus(long num1, long denom1, long num2, long denom2)
	{
		long comDenom = Rational.lcm(denom1, denom2);
		long ansNum = num1 * (comDenom / denom1) + num2 * (comDenom / denom2);
		
		long gcf = Rational.gcf(ansNum, comDenom);
		ansNum /= gcf;
		comDenom /= gcf;
		
		long[] answer = {ansNum, comDenom};
		return answer;

	}

	public String toString()
	{
		String x = "";
		x = x + "----------------";
		x = x + "\n";
		x = x + "Quadruple: \n";
		for (int i = 0; i < 4; i++)
		{
			x = x + quad[i] + "\n";
		}
		x = x + "offLimits = " + offLimits + "\n";
		x = x + "----------------";
		x = x + "\n";
		
		if (centerCombos != null)
		{
			for (int i = 0; i < 4; i++)
			{
				x = x + centerCombos[i] + "\n";
			}
		}
		x = x + "-------   *   *   *   ---------";
		x = x + "\n";

		return x;
	}

	public static void main(String[] args)
	{
		int[] rootquad = new int[4];
		rootquad[0] = -6;
		rootquad[1] = 11;
		rootquad[2] = 14;
		rootquad[3] = 15;

		Quadruple tester = new Quadruple(rootquad, -1);

		//tester.setOffLimits(3);
		
		System.out.println("Max: " + tester.max());
		System.out.println("Min: " + tester.min());

		System.out.println(tester.offLimits());
		System.out.println(tester);

		Quadruple tester2 = new Quadruple();
		System.out.println(tester2);

		Quadruple tester3 = tester.clone();
		System.out.println(tester3);

	}

}
