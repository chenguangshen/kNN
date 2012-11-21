package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Test {
	public static void main(String[] args){
		HashMap<Integer, Double> mp = new HashMap<Integer, Double>();
		mp.put(1, 2.3);
		mp.put(2, 3.6);
		mp.put(3, 1.1);
		mp.put(4, 0.3);
		List<Double> sum1 = new ArrayList<Double>(mp.values());

	    Collections.sort(sum1, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				// TODO Auto-generated method stub
				return o2 < o1 ? 0 : 1;
			}	     
	    });
	    for (Double p : sum1) {
	        System.out.println(p);
	    }
	    
	    JTextArea textArea = new JTextArea("Insert your Text here");
	    JScrollPane scrollPane = new JScrollPane(textArea);  
	    textArea.setLineWrap(true);  
	    textArea.setWrapStyleWord(true);  
	    JOptionPane.showMessageDialog(null, scrollPane, "dialog test with textarea",  
	                                           JOptionPane.YES_NO_OPTION);
	    
	    //JOptionPane.showInputDialog(null,"Please input the searching terms:\n");  
	}
	
}
