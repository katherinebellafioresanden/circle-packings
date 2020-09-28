import java.util.HashMap;
import java.util.Objects;

public class Rational implements Comparable<Rational>
{
	private final long num;
	private final long denom;
	private final double decimal;
	
	public Rational(long numIn, long denomIn)
	{
		long gcf = gcf(numIn, denomIn);
		num = numIn / gcf;
		denom = denomIn / gcf;
		decimal = 1.0 * num / denom;
	}
	
	// constructs a 0
	public Rational()
	{
		num = 0;
		denom = 1;
		decimal = 0;
	}
	

	
	// GETTERS
	public long num() {return num;}
	public long denom() {return denom;}
	public double val() {return decimal;}
	
	public Rational plus(Rational b)
	{
		long comDenom = lcm(denom, b.denom());
		long newNum = num * (comDenom / denom) + b.num() * (comDenom / b.denom());

		return new Rational(newNum, comDenom);

	}
	
	public Rational minus(Rational b)
	{
		Rational toAdd = new Rational(-b.num(), b.denom());
		return this.plus(toAdd);

	}
	
	public Rational times(Rational b)
	{
		long newNum = num * b.num();
		long newDenom = denom * b.denom();

		return new Rational(newNum, newDenom);
	
	}
	
	public Rational divide(Rational b)
	{
		Rational toMult = new Rational(b.denom(), b.num());
		return this.times(toMult);
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
	
	public static long gcf(long numerator, long denominator) 
	{
		if (numerator % denominator == 0) 
		{
			return denominator;
		}
		return gcf(denominator, numerator % denominator);
	}
	
	public boolean isPositive()
	{
		return (num > 0 && denom > 0) || (num < 0 && denom < 0);
	}
	
	public boolean isNegative()
	{
		return !isPositive() && num != 0;
	}
	
	public Rational neg()
	{
		return new Rational(-num, denom);
	}
	


	@Override
	public int compareTo(Rational o) 
	{
		double thisVal = decimal;
		double thatVal = o.val();
		
		if (thisVal < thatVal)
		{
			return -1;
		}
		else if (thisVal > thatVal)
		{
			return 1;
		}
		else // equal
		{
			return 0;
		}
	}
	
	public String toString()
	{
		if (denom == 1)
		{
			return "" + num;
		}
		else
		{
			return num + " / " + denom;
		}
		
	}
	
	@Override
	public boolean equals(Object x)
	{
		if (x == null)
			return false;
		if (this.getClass() != x.getClass())
			return false;

		Rational that = (Rational) x;
		return num == that.num() && denom == that.denom();
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(num, denom);
	}
	
	public static void main(String[] args)
	{
		Rational a = new Rational(1,3);
		Rational b = new Rational(1,2);
		
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		
		a = a.plus(b);
		b = b.minus(new Rational(1, 16));
		
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		
		a = a.neg();
		System.out.println("a is negative? " + a.isNegative());
		System.out.println("b is positive? " + b.isPositive());
		System.out.println("0 is negative? " + new Rational(0, 1).isNegative());
		
		HashMap<Rational, Integer> freqs = new HashMap<Rational, Integer>();
		freqs.put(a, 3);
		freqs.put(b, 1);

		System.out.println("Is a in the hashmap? " + freqs.containsKey(a));

	}
	


	
}
