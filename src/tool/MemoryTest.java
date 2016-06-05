package tool;

public class MemoryTest {
	
	/**
	 * return KB
	 * @return
	 */
	public static double getUsedMemory() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory()) / (double)1024;
	}

	public static void main(String args[]) {
		long useMemory = (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) / 1024;
		System.out.println("已用内存1: " + useMemory + "KB");
		long[] use = new long[(74512 - ((int) useMemory)) / 8 * 1024];
		System.out.println("空闲内存: " + Runtime.getRuntime().freeMemory() / 1024
				+ "KB");
		System.out.println("已用内存2: "
				+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
						.freeMemory()) / 1024 + "KB");
		System.out.println("总内存: " + Runtime.getRuntime().totalMemory() / 1024
				+ "KB");
	}
}
