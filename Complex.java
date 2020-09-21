/******************************************************************************
 *  Adapted by Katherine Bellafiore Sanden to include numerators & denominators 
 *  
 *  Source: Robert Sedgewick and Kevin Wayne, Princeton University, 
 *  Date: Oct-20-2017
 *  URL: https://introcs.cs.princeton.edu/java/97data/Complex.java
 *  
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im instance variables after
 *  they've been initialized.
 *
 *  % java Complex
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *  tan(a)       = -6.685231390246571E-6 + 1.0000103108981198i
 *
 ******************************************************************************/


public class Complex 
{
	private final Rational re;   // the real part
	private final Rational im;   // the imaginary part

	// create a new object with the given real and imaginary parts
	public Complex(long reNum, long reDenom, long imNum, long imDenom) 
	{	
		re = new Rational(reNum, reDenom);
		im = new Rational(imNum, imDenom);
	}
	
	// create a new object with the given real and imaginary parts
	public Complex(Rational real, Rational imag) 
	{	
		re = real;
		im = imag;
	}

	public Complex(double reIn, double imIn)
	{
		int accuracy = 1000; // how many digits of accuracy do you want?
		long reNum = (long) (reIn * accuracy);
		long reDenom = accuracy;
		long imNum = (long) (imIn * accuracy);
		long imDenom = accuracy;
		
		re = new Rational(reNum, reDenom);
		im = new Rational(imNum, imDenom); 
	}

	// return a string representation of the invoking Complex object
	public String toString() {
		//if (im == 0) return re + "";
		//if (re == 0) return im + "i";
		if (im.isNegative()) return re + " - " + (im.neg()) + "i";
		return "(" + re + ") + (" + im + ")i";
	}

	// return abs/modulus/magnitude
	public double abs() {
		return Math.hypot(re.val(), im.val());
	}

	// return angle/phase/argument, normalized to be between -pi and pi
	public double phase() {
		return Math.atan2(im.val(), re.val());
	}

	// return a new Complex object whose value is (this + b)
	public Complex plus(Complex b)
	{
		Rational real = re.plus(b.re());
		Rational imag = im.plus(b.im());
		return new Complex(real, imag);

	}

	public static long lcm(double a, double b)
	{
		if (a < 0) a = -a;
		if (b < 0) b = -b;

		if (a == b) return (long) a;


		long max = (long) a;
		long min = (long) b;
		if (a < b)
		{
			max = (long) b;
			min = (long) a;
		}

		long potential = max;

		while (potential % min != 0)
		{
			potential += max;
		}

		return potential;	
	}

	// return a new Complex object whose value is (this - b)
	public Complex minus(Complex b)
	{
		Rational real = re.minus(b.re());
		Rational imag = im.minus(b.im());
		return new Complex(real, imag);
	}

	// return a new Complex object whose value is (this * b)
	public Complex times(Complex b)
	{
		Rational real = re.times(b.re()).minus(im.times(b.im()));
		Rational imag = re.times(b.im()).plus(im.times(b.re()));
		return new Complex(real, imag);
	
	}

	public static long gcf(long numerator, long denominator) 
	{
		if (numerator % denominator == 0) 
		{
			return denominator;
		}
		return gcf(denominator, numerator % denominator);
	}

	// return a new object whose value is (this * alpha)
	public Complex scale(long scaleN, long scaleD)
	{
		Rational scalar = new Rational(scaleN, scaleD);
		return new Complex(re.times(scalar), im.times(scalar));
	}

	// return a new Complex object whose value is the conjugate of this
	public Complex conjugate() {
		return new Complex(re, im.neg());
	}
	

	// return the real or imaginary part
	public Rational re() { return re; }
	public Rational im() { return im; }
	public long reNum() {return re.num(); }
	public long reDenom() {return re.denom(); }
	public long imNum() {return im.num(); }
	public long imDenom() {return im.denom(); }

	// return a / b


	// a static version of plus
	public static Complex plus(Complex a, Complex b) {
		Rational real = a.re().plus(b.re());
		Rational imag = a.im().plus(b.im());
		Complex sum = new Complex(real, imag);
		return sum;
	}

//	
//	public boolean equals(Object x) {
//		if (x == null) return false;
//		if (this.getClass() != x.getClass()) return false;
//		Complex that = (Complex) x;
//		return (this.re == that.re) && (this.im == that.im);
//	}
	
	public boolean equals(Object x)
	{
		if (x == null) return false;
		if (this.getClass() != x.getClass()) return false;
		
		Complex that = (Complex) x;
		return this.re().equals(that.re()) && this.im().equals(that.im());
	}


	// sample client for testing
	public static void main(String[] args) {
		Complex a = new Complex(5.0, 6.0);
		Complex b = new Complex(-3.0, 4.0);

		System.out.println("a            = " + a);
		System.out.println("b            = " + b);
		System.out.println("Re(a)        = " + a.re());
		System.out.println("Im(a)        = " + a.im());
		System.out.println("b + a        = " + b.plus(a));
		System.out.println("a - b        = " + a.minus(b));
		System.out.println("a * b        = " + a.times(b));
		System.out.println("b * a        = " + b.times(a));
	//	System.out.println("a / b        = " + a.divides(b));
	//	System.out.println("(a / b) * b  = " + a.divides(b).times(b));
		System.out.println("conj(a)      = " + a.conjugate());
		System.out.println("|a|          = " + a.abs());
	}

}