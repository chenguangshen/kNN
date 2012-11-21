package core;

public class DutchFlag {
	public void sort2(int[] array) {
		int i = 0;
		int j = array.length - 1;
		int k = 0;
		while (k <= j) {
			if (array[k] == 1) {
				int temp = array[k];
				array[k] = array[i];
				array[i] = temp;
				i++;
				k++;
			} else if (array[k] == 3) {
				int temp = array[k];
				array[k] = array[j];
				array[j] = temp;
				j--;
			} else {
				k++;
			}
		}
	}
	
	public static void main (String[] args) {
		int[] a = {3,2,3,2,1,2,1,3,2,3,2,1,2,3};
		new DutchFlag().sort2(a);
		for (int aa:a) {
			System.out.print(aa + " ");
		}
	}
}
