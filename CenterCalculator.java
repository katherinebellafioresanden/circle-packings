
public class CenterCalculator 
{
	// Si matrices
	private static double[][] S1 = {{-1, 2, 2, 2}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
	private static double[][] S2 = {{1, 0, 0, 0}, {2, -1, 2, 2}, {0, 0, 1, 0}, {0, 0, 0, 1}};
	private static double[][] S3 = {{1, 0, 0, 0}, {0, 1, 0, 0}, {2, 2, -1, 2}, {0, 0, 0, 1}};
	private static double[][] S4 = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {2, 2, 2, -1}};

	// transpose Si matrices
	private static double[][] S1T = {{-1, 0, 0, 0}, {2, 1, 0, 0}, {2, 0, 1, 0}, {2, 0, 0, 1}};
	private static double[][] S2T = {{1, 2, 0, 0}, {0, -1, 0, 0}, {0, 2, 1, 0}, {0, 2, 0, 1}};
	private static double[][] S3T = {{1, 0, 2, 0}, {0, 1, 2, 0}, {0, 0, -1, 0}, {0, 0, 2, 1}};
	private static double[][] S4T = {{1, 0, 0, 2}, {0, 1, 0, 2}, {0, 0, 1, 2}, {0, 0, 0, -1}};

	// -1 2 2 3 packing
	private static double[][] bugeye = {
			{-1, 0, 0}, 
			{2, -0.5, 0}, 
			{2, 0.5, 0}, 
			{3, 0, 2.0/3}};


	public static void main(String[] args)
	{
		double[] bugeyeCurvatures = getCol(0, bugeye);

		multiplyColumn(1, bugeye, bugeyeCurvatures);
		multiplyColumn(2, bugeye, bugeyeCurvatures);

		double[][] bugeyeCenters = getCenters(bugeye, bugeyeCurvatures);

		System.out.println("bugeye: \n");
		display(bugeye);

		System.out.println("bugeye centers: \n");
		display(bugeyeCenters);

		// -6 11 14, 15 packing
		double[][] middleStage = multiplyMatrices(S2, bugeye);
		double[][] people = multiplyMatrices(S2T, middleStage);

//		System.out.println("S2 * bugeye: \n");
//		display(middleStage);
//
//		System.out.println("S2 * bugeye centers: \n");
//		display(getCenters(middleStage, getCol(0, middleStage)));

		System.out.println("people: \n");
		display(people);

		double[] peopleCurvatures = getCol(0, people);
		double[][] peopleCenters = getCenters(people, peopleCurvatures);

		System.out.println("people centers: \n");
		display(peopleCenters);

		// -11, 21, 24, 28 packing
		double[][] secondMiddleStage = multiplyMatrices(S4, middleStage);
		double[][] coins = multiplyMatrices(S4T, secondMiddleStage);
		
		System.out.println("coins: \n");
		display(coins);
		
		double[] coinCurvatures = getCol(0, coins);
		double[][] coinCenters = getCenters(coins, coinCurvatures);

		System.out.println("coin centers: \n");
		display(coinCenters);

	}


	public static double[][] multiplyMatrices(double[][] Si, double[][] rq)
	{
		double[][] quad = new double[4][3];

		for (int r = 0; r < quad.length; r++)
		{
			for (int c = 0; c < quad[0].length; c++)
			{
				double[] column = {rq[0][c], rq[1][c], rq[2][c], rq[3][c]};
				quad[r][c] = dotProduct(Si[r],  column);
			}
		}	
		return quad;
	}

	public static double dotProduct(double[] a, double[] b)
	{
		if (a.length != b.length)
		{
			System.out.println("dotProduct(): arrays must have the same length");
		}
		double result = 0;
		for (int i = 0; i < a.length; i++)
		{
			result += a[i] * b[i];
		}
		return result;
	}

	public static double[][] getCenters(double[][] centerCombos, double[] curvatures)
	{
		double[][] centers = copy(centerCombos);
		double[] recips = {1.0 / curvatures[0], 1.0 / curvatures[1], 1.0 / curvatures[2], 1.0 / curvatures[3]};
		multiplyColumn(1, centers, recips);
		multiplyColumn(2, centers, recips);

		return centers;

	}

	public static void multiplyColumn(int col, double[][] matrix, double[] multiplier)
	{
		// bad inputs
		if (col > matrix[0].length || multiplier.length != matrix.length) return;

		// perform column multiplication
		for (int i = 0; i < matrix.length; i++)
		{
			matrix[i][col] = matrix[i][col] * multiplier[i];
		}	
	}

	public static void addToColumn(int col, double[][] matrix, double[] addOn)
	{
		// bad inputs
		if (col > matrix[0].length || addOn.length != matrix.length) return;

		// perform column addition
		for (int i = 0; i < matrix.length; i++)
		{
			matrix[i][col] = matrix[i][col] + addOn[i];
		}	
	}

	public static double[] getCol(int col, double[][] matrix)
	{
		double[] answer = new double[matrix.length];
		for (int i = 0; i < matrix.length; i++)
		{
			answer[i] = matrix[i][col];
		}
		return answer;
	}

	public static double[][] copy(double[][] orig)
	{
		double[][] copy = new double[orig.length][orig[0].length];
		for (int r = 0; r < orig.length; r++)
		{
			for (int c = 0; c < orig[0].length; c++)
			{
				copy[r][c] = orig[r][c];
			}
		}
		return copy;
	}

	public static void display(double[][] matrix)
	{
		for (int r = 0; r < matrix.length; r++)
		{
			System.out.print("[");
			for (int c = 0; c < matrix[0].length  - 1; c++)
			{
				System.out.print(matrix[r][c] + "    ");
			}
			System.out.println(matrix[r][matrix[0].length - 1] + "]");
		}
		System.out.println();
	}

	public static double[][] shiftCenters(double[][] combos, double xShift, double yShift)
	{
		double[][] newCombos = copy(combos);

		// create addOn columns for x and y
		double[] addOnX = new double[combos.length];
		double[] addOnY = new double[combos.length];
		for (int i = 0; i < combos.length; i++)
		{
			addOnX[i] = combos[i][0] * xShift;
			addOnY[i] = combos[i][0] * yShift;	
		}

		addToColumn(1, newCombos, addOnX);
		addToColumn(2, newCombos, addOnY);

		return newCombos;
	}



}
