package core;

public class Combination {
	private static void comb(int len, String prefix, String s) {
		if (s.length() > 0) {
			if (prefix.length() == (len - 1)) {
				System.out.print("[");
				for (int i = 0; i < prefix.length(); i++) {
					System.out.print(prefix.charAt(i) + ",");
				}
				System.out.println(s.charAt(0) + "]");
			}
			comb(len, prefix + s.charAt(0), s.substring(1));
			comb(len, prefix, s.substring(1));
		}
	}

	public static void main(String[] args) {
		int n = 4, k = 2;
		String str = "";
		for (int i = 0; i < n; i++) {
			str += (i + 1);
		}
		comb(k, "", str);
	}
}