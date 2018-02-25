package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Test {
	
	private void sortTest(List<TestPo> tests){
		ValueCmp comparator = new ValueCmp();
		Collections.sort(tests, Collections.reverseOrder(comparator));
	}
	
	public static void main(String[] args) {
		List<TestPo> tests = new ArrayList<TestPo>();
		TestPo testPo1 = new TestPo();
		testPo1.setKey("A");
		testPo1.setValue(5.3);
		TestPo testPo2 = new TestPo();
		testPo2.setKey("B");
		testPo2.setValue(5.1);
		TestPo testPo3 = new TestPo();
		testPo3.setKey("C");
		testPo3.setValue(5.6);
		tests.add(testPo1);
		tests.add(testPo2);
		tests.add(testPo3);
		
		Test test = new Test();
 		test.sortTest(tests);
		//Collections.sort(tests);
		for(int i=0; i<tests.size(); i++){
			System.out.println(tests.get(i).getKey());
		}
	}
	
	class ValueCmp implements Comparator{
		public int compare(Object arg0, Object arg1) {
			TestPo detail1=(TestPo)arg0;
			TestPo detail2=(TestPo)arg1;
			if(detail1.getValue() > detail2.getValue())
				return 1;
			else
				return -1;
		}
	}
}
