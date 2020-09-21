
import java.awt.Graphics;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

// for curvatures, a' = 2(b + c + d) - a;
// for centers a'*z_1' = 2(b*z_2 + c*z_3 + d*z_4) - a*z_1;

public class Packing {
	
	// make this true if you want a verbose narrative of the process
	private static final boolean DEBUG_MODE = false;
	
	// for visuals - how big do you want the packing to be?
	public static final double IDEAL_OUTER_RADIUS = 0.45 * CirclePackingGenerator.SCREEN_HEIGHT;

	// do we care about just primes this time?  or all curvatures in the prime component?
	private static final boolean PRIMES_ONLY = false;
	
	// --------------------------------------------------------------------------------------------
	// These variables are only relevant if we're doing a prime component (not whole packing)

	// are we doing the full prime component? Or just 2 generations?  (Added 6-30-19)
	private static final boolean TWO_GEN = false;
	
	// are we doing the THICKENED prime component? (usually yes)
	// 		THICKENED prime component = all circles tangent to our PCR,
	//		as well as all circles tangent to any primes that are tangent to the PCR
	
	// extra stack for storing quadruples with an extra prime... to be investigated later
	private Stack<Quadruple> extraStack = new Stack<Quadruple>();
	
	// --------------------------------------------------------------------------------------------
	
	// stack for storing and investigating quadruples we generate
	private Stack<Quadruple> quadStack = new Stack<Quadruple>();

	// other instance variables
	private double scaleFactor;
	private Quadruple root;
	private boolean pc;
	private Complex centerOfOuterCircle;
	private int min;
	private double max;
	private boolean thickened;
	private PrintWriter output;
	
	private ArrayList<LabeledCircle> circles = new ArrayList<LabeledCircle>();
	private HashSet<LabeledCircle> seen = new HashSet<LabeledCircle>();
	

	public Packing(Quadruple rootIn, boolean pcIn, int minIn, double maxIn, boolean thickOrNot, PrintWriter outputIn)
	{
		root = rootIn;
		pc = pcIn;
		centerOfOuterCircle = root.centerAt(0);
		scaleFactor = calculateScaleFactor(rootIn);
		min = minIn;
		max = maxIn;
		thickened = thickOrNot;
		output = outputIn;

		if (maxIn <= CirclePackingGenerator.DRAWING_THRESHOLD)
		{
			establishCircleInfo();
		}

		
		if (!pc) // WHOLE PACKING
		{
			if (DEBUG_MODE) System.out.println("root: \n" + root);
			generateAllCurvatures(root);
		}
		else // PRIME COMPONENT
		{
			if (DEBUG_MODE) System.out.println("pcr: \n" + root);
			generateAllCurvaturesPC(root);
		}

	}

	public double calculateScaleFactor(Quadruple rootIn)
	{
		int outerCurv = -root.quad()[0];
		double r = 1.0 / outerCurv;
		double scale = IDEAL_OUTER_RADIUS / r;
		return scale;
	}

	public void establishCircleInfo()
	{
		LabeledCircle.scaleFactor = scaleFactor;
		
		double shiftX = centerOfOuterCircle.re().val() * scaleFactor;
		double shiftY = centerOfOuterCircle.im().val() * scaleFactor;

		LabeledCircle.xOuterCenter = CirclePackingGenerator.SCREEN_WIDTH / 2 - shiftX;
		LabeledCircle.yOuterCenter = CirclePackingGenerator.SCREEN_HEIGHT / 2 - shiftY;
		
	}
	
	public void displayPacking(PrintWriter op)
	{

		displayRoot(op);
		for (LabeledCircle c : circles)
		{
			op.println(c);
		}
		
		
	}
	
	public void displayRoot(PrintWriter output)
	{
		for (int i = 0; i < root.quad().length; i++)
		{
			int curv = root.quad()[i];
			if (curv < 0)
			{
				curv = -curv;
			}
			
			if (curv >= min && curv <= max)
			{
				LabeledCircle circle = new LabeledCircle(root, i);
				if (PRIMES_ONLY)
				{
					if (isPrime(curv))
					{
						output.println(circle);
					}
				}
				else
				{
					output.println(circle);
				}
				
			}
		}
	}
		
		
	public void draw(Graphics g)
	{
		drawRoot(g);	
		for (LabeledCircle c : circles)
		{
			c.draw(g);
		}
	}
	
	
	public void drawRoot(Graphics g)
	{

		for (int i = 0; i < root.quad().length; i++)
		{
			
			int curv = root.quad()[i];
			if (curv < 0)
			{
				curv = -curv;
			}
			
			if (curv >= min && curv <= max)
			{
				LabeledCircle circle = new LabeledCircle(root, i);
				if (PRIMES_ONLY)
				{
					if (isPrime(curv))
					{
						circle.draw(g);
					}
				}
				else
				{
					circle.draw(g);
				}
				
			}
			
			
		}
	}

	/*
	 *  Generate all curvatures for the WHOLE packing - 
	 *  		call this method if you're NOT doing a Prime Component
	 */
	public void generateAllCurvatures(Quadruple q) 
	{	
		// curretQuad is just a copy of the quad that was passed to the method
		Quadruple currentQuad = q.clone();

		if (DEBUG_MODE)
		{
			System.out.println("We're starting with this quadruple, which should be the root quadruple: ");
			System.out.println(currentQuad);
		}

		//  apply 3 Sj matrices to the pcr Quadruple 
		// the offLimits attribute of currentQuad will tell you which one to skip
		generateNewQuad(currentQuad, 0); // APPLY S1 (if possible)
		generateNewQuad(currentQuad, 1); // APPLY S2 (if possible)
		generateNewQuad(currentQuad, 2); // APPLY S3 (if possible)
		generateNewQuad(currentQuad, 3); // APPLY S4 (if possible)

		// while the stack of quadruples is not empty...
		while (!quadStack.isEmpty()) 
		{
			// pop the quadruple on top (most recently added)
			currentQuad = quadStack.pop();

			if (DEBUG_MODE) 
			{
				System.out.println("Just popped this quadruple: ");
				System.out.println(currentQuad);
				System.out.println("We are in the while loop. The stack has " + quadStack.size() + " elements.");
			}

			// generate child quadruples from the quadruple we just popped...

			// if the jth element is not the largest member of the current quadruple, apply Sj
			//***************************************
			for (int i = 0; i < 4; i++)
			{
				if (currentQuad.isNotLargest(i))
				{
					generateNewQuad(currentQuad, i);
				}
			} 
		}
		if (DEBUG_MODE)
		{
			System.out.println("We finished the while loop.");
		}
	}

	/**
	 * Generates a new quadruple (newQuad) from q.
	 * Stores the newest curvature (newCurvature) in the list of circles.
	 * If newQuad is in range, pushes newQuad to either quadStack or extraStack
	 * 
	 * @param q - the quadruple you're about to apply an Sj to
	 * @param j - tells you which of the Sj's to apply.  j = 0 --> S1,  j = 1 --> S2, etc
	 */
	public void generateNewQuad(Quadruple q, int j)
	{
		// if j is 0, we'll print S1, because that's the first of the Sj's 
		int jForPrintStatements = j + 1;

		// declare & construct the Quadruple that will hold the new quadruple
		Quadruple newQuad = new Quadruple();

		// ------------ now we know the Sj we're supposed to apply is NOT off limits ------------

		// generate new quadruple, using the Sj specified by the input parameter
		if (j == 0) newQuad = applyS1(q);
		else if (j == 1) newQuad = applyS2(q);
		else if (j == 2) newQuad = applyS3(q);
		else if (j == 3) newQuad = applyS4(q);
		else System.out.println("ERROR!!! You can only use S1, S2, S3, or S4.");

		if (DEBUG_MODE) 
		{ 
			System.out.println("Just generated this quadruple, using S" + jForPrintStatements + ":");
			System.out.println(newQuad);
		}

		int newCurvature = newQuad.quad()[j];

		// is this quadruple in the range? then add it to the circles list.
		if (newCurvature <= max && newCurvature >= min) 
		{

			if (PRIMES_ONLY) // this means we're only counting prime curvatures
			{
				if (isPrime(newCurvature))
				{
					if (max <= CirclePackingGenerator.DRAWING_THRESHOLD)
					{
						LabeledCircle temp = new LabeledCircle(newQuad, j);
						addToList(circles, temp, true);
					}
					if (output != null)
					{
						printCircleInfo(newQuad, j);
					}
				}
			}
			else // this means we're counting all curvatures in the packing
			{
				if (max <= CirclePackingGenerator.DRAWING_THRESHOLD)
				{
					LabeledCircle temp = new LabeledCircle(newQuad, j);
					addToList(circles, temp, true);
				}
				if (output != null)
				{
					printCircleInfo(newQuad, j);
				}
			}

		}

		// might this quadruple spawn another one in the range we care about?  ...then push it back onto the stack
		if (newCurvature < max)
		{
			quadStack.push(newQuad);	

			if (DEBUG_MODE) System.out.println("....and, we PUSHED this quadruple to quadStack. ^ \n");

		} // end of if statement: newCurvature < max
	} 
	
	public void printCircleInfo(Quadruple quad, int i)
	{
		int curv = quad.quad()[i];
		Complex combo = quad.combos()[i];
		output.println(curv + ",   " + combo.re() + ",   " + combo.im());
	}

	public void generateAllCurvaturesPC(Quadruple q) 
	{	
		// curretQuad is just a copy of the quad that was passed to the method
		Quadruple currentQuad = q.clone();

		if (DEBUG_MODE)
		{
			System.out.println("We're starting with this quadruple, which should be the PCR quadruple: ");
			System.out.println(currentQuad);
		}

		//  apply 3 Sj matrices to the pcr Quadruple 
		// the offLimits attribute of currentQuad will tell you which one to skip
		generateNewQuadPC(currentQuad, 0, false); // APPLY S1 (if possible)
		generateNewQuadPC(currentQuad, 1, false); // APPLY S2 (if possible)
		generateNewQuadPC(currentQuad, 2, false); // APPLY S3 (if possible)
		generateNewQuadPC(currentQuad, 3, false); // APPLY S4 (if possible)

		// while the stack of quadruples is not empty...
		while (!quadStack.isEmpty()) 
		{
			// pop the quadruple on top (most recently added)
			currentQuad = quadStack.pop();

			if (DEBUG_MODE) 
			{
				System.out.println("Just popped this quadruple: ");
				System.out.println(currentQuad);
				System.out.println("We are in the while loop. The stack has " + quadStack.size() + " elements.");
			}

			// generate child quadruples from the quadruple we just popped...

			// if the jth element is not the largest member of the current quadruple, apply Sj
			//***************************************
			for (int i = 0; i < 4; i++)
			{
				if (currentQuad.isNotLargest(i))
				{
					generateNewQuadPC(currentQuad, i, false);
				}
			} 
		}
		if (DEBUG_MODE)
		{
			System.out.println("We finished the while loop.");
		}
		
		if (thickened)
		{
			generateCurvaturesFromExtraStack();
		}
	}


	/**
	 * Generates a new quadruple (newQuad) from q.
	 * Stores the newest circle in the list of LabeledCircles
	 * If newQuad is in range, pushes newQuad to either quadStack or extraStack
	 * 
	 * @param q - the quadruple you're about to apply an Sj to
	 * @param j - tells you which of the Sj's to apply.  j = 0 --> S1,  j = 1 --> S2, etc
	 * @param isExtra - if true: the newQuad is a candidate for extraStack.
	 *                  if false: the newQuad is a candidate for quadStack (the main stack)
	 */
	public void generateNewQuadPC(Quadruple q, int j, boolean isExtra)
	{
		// if j is 0, we'll print S1, because that's the first of the Sj's 
		int jForPrintStatements = j + 1;

		// declare & construct the Quadruple that will hold the new quadruple
		Quadruple newQuad = new Quadruple();

		// if the Sj we're supposed to apply is off limits, return.
		if (j == q.offLimits())
		{
			return;
		}

		// ------------ now we know the Sj we're supposed to apply is NOT off limits ------------

		// generate new quadruple, using the Sj specified by the input parameter
		if (j == 0) newQuad = applyS1(q);
		else if (j == 1) newQuad = applyS2(q);
		else if (j == 2) newQuad = applyS3(q);
		else if (j == 3) newQuad = applyS4(q);
		else System.out.println("ERROR!!! You can only use S1, S2, S3, or S4.");

		if (DEBUG_MODE) 
		{ 
			System.out.println("Just generated this quadruple, using S" + jForPrintStatements + ":");
			System.out.println(newQuad);
		}

		int newCurvature = newQuad.quad()[j];

		// is this quadruple in the range? then add to circles list
		if (newCurvature <= max && newCurvature >= min) 
		{
			LabeledCircle temp = new LabeledCircle(newQuad, j);
			if (PRIMES_ONLY) // this means we're only counting prime curvatures
			{
				if (isPrime(newCurvature))
				{
					// if isExtra is true, then we're worried about duplicates
					addToList(circles, temp, isExtra); 
				}
			}
			else // this means we're counting all curvatures in the component
			{
				addToList(circles, temp, isExtra);
			}

		}

		// might this quadruple spawn another one in the range we care about?  ...then push it back onto the stack
		if (newCurvature < max)
		{
			if (!isExtra)
			{
				quadStack.push(newQuad);	
				if (DEBUG_MODE)
				{
					System.out.println("....and, we PUSHED this quadruple to quadStack. ^ \n");
				}
			}
			else // we're working with the extraStack right now
			{
				extraStack.push(newQuad);

				if (DEBUG_MODE)
				{
					System.out.println("....and, we PUSHED this quadruple to extraStack. ^ \n");
				}

			}

			// if we're doing the WHOLE prime component
			if (!TWO_GEN) 
			{
				// is the newest curvature prime?  if so, it spawns another branch of the prime component
				if (isPrime(newCurvature))
				{
					// first, put this quadruple on the extraStack, to be handled later
					Quadruple newNewQuad = newQuad.clone();
					newNewQuad.setOffLimits(j);
					extraStack.push(newNewQuad);

					if (DEBUG_MODE)
					{
						System.out.println("This quadruple is about to spawn a new branch of the component.");
						System.out.println("A clone of this quadruple was pushed onto the extra stack.");
						System.out.println("The clone looks like this: \n" + newNewQuad);
					}

				}

			}
			else // WE ONLY WANT 2 GENERATIONS
			{
				// is the newest curvature prime?  if so, it spawns another branch of the prime component
				// BUT we only want to spawn that branch if....
				//       **this quadruple is not already in the extraStack**

				if (isPrime(newCurvature))
				{
					if (!isExtra)
					{
						// first, put this quadruple on the extraStack, to be handled later
						Quadruple newNewQuad = newQuad.clone();
						newNewQuad.setOffLimits(j);
						extraStack.push(newNewQuad);

						if (DEBUG_MODE)
						{
							System.out.println("This quadruple is about to spawn a new branch of the component.");
							System.out.println("A clone of this quadruple was pushed onto the extra stack.");
							System.out.println("The clone looks like this: \n" + newNewQuad);
						}
					}
					else // the newCurvature is prime, but already on the extraStack
					{
						if (DEBUG_MODE)
						{
							System.out.println("This quadruple WOULD HAVE spawned a new branch of the component.");
							System.out.println("HOWEVER, since it was already on the extraStack, spawning a new");
							System.out.println("component would mean finding circles that aren't tangent to the PCR");
							System.out.println("or to a prime that's tangent to the PCR.");
							System.out.println("-------- c'est la vie, if we only want 2 GENERATIONS!! --------");
						}

					}

				}

			} // end of else statement: not 2GEN

		} // end of if statement: newCurvature < max
	} 
	
	public void generateCurvaturesFromExtraStack()
	{

		if (DEBUG_MODE) System.out.println("EXTRASTACK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		

		// while the extraStack is NOT empty
		while (!extraStack.isEmpty())
		{
			Quadruple currentQuad = extraStack.pop();

			if (DEBUG_MODE) 
			{
				System.out.println("Just popped this quadruple from extraStack: ");
				System.out.println(currentQuad);
				System.out.println("We are in the while loop. The extraStack has " + extraStack.size() + " elements.");
			}

			// generate child quadruples from the quadruple we just popped...

			// if the jth element is not the largest member of the current quadruple, apply Sj
			//***************************************
			for (int i = 0; i < 4; i++)
			{
				if (isNotLargest(i, currentQuad.quad()))
				{
					generateNewQuadPC(currentQuad, i, true);
				}
			} 

		
		}

	}
	
	public boolean addToList(ArrayList<LabeledCircle> list, LabeledCircle cur, boolean worriedAboutDuplicates)
	{
	
		if (worriedAboutDuplicates) 
		{
			if (list.contains(cur))
			{
				//System.out.println("DUPLICATE ENCOUNTERED");
				return false;
			}
			else // we haven't seen this circle yet
			{
				list.add(cur);
				return true;
			}
		}
		else
		{
			list.add(cur);
			return true;
		}
		
	}
	
	public static boolean isNotLargest(int index, int[] quad)
	{
		// count keeps track of how many entries are bigger than quad[index]
		int maxIndex = 0;

		// compare quad[index] each other member of the quadruple
		for (int i = 0; i < quad.length; i++)
		{
			if (quad[i] > quad[maxIndex])
			{
				maxIndex = i;
			}
		}

		// if quad[index] is smaller than at least one other entry, return true.
		if (maxIndex != index)  return true;
		return false;
	}

	//  Sj functions***********************************

	// for curvatures, a' = 2(b + c + d) - a;
	// for centers a'*z_1' = 2(b*z_2 + c*z_3 + d*z_4) - a*z_1;

	public static Quadruple applyS1(Quadruple q) 
	{ 
		// CURVATURES
		int[] before = q.quad();
		int n = -1 * before[0] + 2 * (before[1] + before[2] + before[3]);
		int[] answer = new int[4];
		answer[0] = n;
		answer[1] = before[1];
		answer[2] = before[2];
		answer[3] = before[3];

		// CENTERS
		Complex[] beforeC = q.combos();
		Complex sumOfOthers = beforeC[1].plus(beforeC[2]).plus(beforeC[3]);
		Complex newC = beforeC[0].scale(-1,1).plus(sumOfOthers.scale(2,1));

		Complex[] answerC = new Complex[4];
		answerC[0] = newC;
		answerC[1] = beforeC[1];
		answerC[2] = beforeC[2];
		answerC[3] = beforeC[3];

		Quadruple newQuad = new Quadruple(answer, answerC, q.offLimits());
		return newQuad;
	}

	public static Quadruple applyS2(Quadruple q) 
	{
		// CURVATURES
		int[] before = q.quad();
		int n = -1 * before[1] + 2 * (before[0] + before[2] + before[3]);
		int[] answer =  new int[4];
		answer[0] = before[0];
		answer[1] = n;
		answer[2] = before[2];
		answer[3] = before[3];
		
		// CENTERS
		Complex[] beforeC = q.combos();
		Complex sumOfOthers = beforeC[0].plus(beforeC[2]).plus(beforeC[3]);
		Complex newC = beforeC[1].scale(-1,1).plus(sumOfOthers.scale(2,1));

		Complex[] answerC = new Complex[4];
		answerC[0] = beforeC[0];
		answerC[1] = newC;
		answerC[2] = beforeC[2];
		answerC[3] = beforeC[3];

		Quadruple newQuad = new Quadruple(answer, answerC, q.offLimits());
		return newQuad;
	}

	public static Quadruple applyS3(Quadruple q) 
	{
		// CURVATURES
		int[] before = q.quad();
		int n = -1 * before[2] + 2 * (before[0] + before[1] + before[3]);
		int[] answer = new int[4];
		answer[0] = before[0];
		answer[1] = before[1];
		answer[2] = n;
		answer[3] = before[3];
		
		// CENTERS
		Complex[] beforeC = q.combos();
		Complex sumOfOthers = beforeC[0].plus(beforeC[1]).plus(beforeC[3]);
		Complex newC = beforeC[2].scale(-1,1).plus(sumOfOthers.scale(2,1));

		Complex[] answerC = new Complex[4];
		answerC[0] = beforeC[0];
		answerC[1] = beforeC[1];
		answerC[2] = newC;
		answerC[3] = beforeC[3];
		

		Quadruple newQuad = new Quadruple(answer, answerC, q.offLimits());
		return newQuad;
	}

	public static Quadruple applyS4(Quadruple q) 
	{
		
		// CURVATURES
		int[] before = q.quad();
		int n = -1 * before[3] + 2 * (before[0] + before[1] + before[2]);
		int[] answer = new int[4];
		answer[0] = before[0];
		answer[1] = before[1];
		answer[2] = before[2];
		answer[3] = n;

		// CENTERS
		Complex[] beforeC = q.combos();
		Complex sumOfOthers = beforeC[0].plus(beforeC[1]).plus(beforeC[2]);
		Complex newC = beforeC[3].scale(-1,1).plus(sumOfOthers.scale(2,1));

		Complex[] answerC = new Complex[4];
		answerC[0] = beforeC[0];
		answerC[1] = beforeC[1];
		answerC[2] = beforeC[2];
		answerC[3] = newC;
		

		Quadruple newQuad = new Quadruple(answer, answerC, q.offLimits());
		return newQuad;
	}

	//*************************************************

	public static boolean isPrime(long n)
	{
		if (n < 2)
		{
			return false;
		}

		for (long factor = 2; factor*factor <= n; factor++)
		{
			// if a factor divides n with no remainder, n is not prime
			if (n % factor == 0)
			{
				return false;
			}
		}

		// we only reach this statement if n >= 2 and no factors were able to divide n
		return true;
	}









}
