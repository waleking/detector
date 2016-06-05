package tool;

/**
 * 
 * MathUtil
 * 
 * huang weijing huang weijing 2013-4-6 下午9:05:38
 * 
 * @version 1.0.0
 * 
 */
public class MathUtil {
	/**
	 * array[0]+array[1]+... =log(exp(array[0]))+log(exp(array[1]))+...
	 * =b+log(exp(array[0]-b))+log(exp(array[1]-b))+... where
	 * b=max{array[0],array[1],...}
	 */
	public static double logSumExp(double[] logArray) {
		double result = 0.0;
		int M = logArray.length;
		double b = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < M; i++) {
			if (b < logArray[i]) {
				b = logArray[i];
			}
		}
		if (b == Double.NEGATIVE_INFINITY) {
			result = Double.NEGATIVE_INFINITY;
		} else {
			double exponentialSum = 0.0;
			for (int i = 0; i < M; i++) {
				exponentialSum += Math.exp(logArray[i] - b);
			}
			result = b + Math.log(exponentialSum);
		}
		return result;
	}

	/**
	 * a special case of logSumExp(array)
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double logSumExp(double a, double b) {
		double result = 0.0;
		double max = Double.NEGATIVE_INFINITY;
		// find out max one
		if (max < a) {
			max = a;
		}
		if (max < b) {
			max = b;
		}
		// System.out.println(max);
		// do adding
		if (max == Double.NEGATIVE_INFINITY) {
			result = Double.NEGATIVE_INFINITY;
		} else {
			double exponentialSum = 0.0;
			exponentialSum = Math.exp(a - max) + Math.exp(b - max);
			result = max + Math.log(exponentialSum);
		}
		return result;
	}

	/**
	 * sum the elements in the array
	 */
	public static double sum(double[] array) {
		double sum = 0.0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/**
	 * sum the elements in the array
	 */
	public static int sum(int[] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/**
	 * array=[a_1,a_2,...,a_N] exp(a_1)=exp(a_1)/ \sum_{n=1}^{N}exp(a_n) so,
	 * a_1=a_1-\log \sum_{n=1}^{N}exp(a_n)
	 */
	public static double[] logNormalize(double[] array) {
		double logDenominator = logSumExp(array);
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i] - logDenominator;
		}
		return array;
	}

	/**
	 * normalizing an array
	 * 
	 * @param args
	 */
	public static double[] normalize(double[] array) {
		try {
			double denominator = sum(array);
			for (int i = 0; i < array.length; i++) {
				array[i] = array[i] / denominator;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return (double[]) null;
		}
		return array;
	}

	/**
	 * argmax
	 * 
	 * @param args
	 */
	public static int argmax(double[] array) {
		double max = Double.NEGATIVE_INFINITY;
		int argmax = -1;
		for (int i = 0; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
				argmax = i;
			}
		}
		return argmax;
	}

	public static void main(String[] args) {
		// double[] array={Double.NEGATIVE_INFINITY,2.0,6.0};
		double[] array = { 0.29343991018776255, 0.12079831120263097,
				0.04154193280130773, 0.3890581852072659, 0.20705499426654253,
				0.04810666633449034 };
		System.out.println(MathUtil.argmax(array));
		// double[] array = { 0.1917723653391916, 0.09700950618267737,
		// 0.4802708065918582, 0.14236384168101773, 0.08842222282851299,
		// 1.612573767424817E-4 };
		// double[] array = { 10, 12, 1 };
		// // System.out.println(MathUtil.logSumExp(array));
		// // System.out.println(MathUtil.sum(array));
		// System.out.println(Arrays.toString(MathUtil.normalize(array)));
		// System.out.println(MathUtil.logSumExp(0, -1002));
	}

}
