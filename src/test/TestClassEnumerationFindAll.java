package test;

import java.io.File;
import java.util.List;

import pro.ddopson.ClassEnumeratorFindAll;

public class TestClassEnumerationFindAll {
	public static void main(String[] args) {
		try{
		
		File directory = new File("/Users/bhaveshkumar/mygit/java-class-enumerator/folder");
		System.out.println("Test class location: " );
		List<Class<?>> classes = ClassEnumeratorFindAll.processDirectory(directory);
		System.out.println("Number of classes found in directory file: "+classes.size());

		Class<?>[] expected = new Class<?>[] {
			pro.ddopson.ClassEnumerator.class,
			pro.ddopson.ClassEnumeratorFindAll.class,
			test.ClassIShouldFindOne.class,
			test.ClassIShouldFindTwo.class,
			test.subpkg.ClassIShouldFindThree.class,
			TestClassEnumeration.class,
			TestClassEnumerationFindAll.class
		};
		for (Class<?> clazz : expected) {
			if (!classes.contains(clazz)) {
				System.out.println("FAIL: expected to find class '" + clazz.getName() + "'");
				System.exit(-1);
			}
		}
		if(classes.size() != expected.length) {
			System.out.println("FAIL: expected to find " + expected.length + " classes, but actually found " + classes.size());
			System.exit(-1);
		}
		}catch(Exception ex){
		System.out.println("Exception occured"+ex);
		}
	}
}
