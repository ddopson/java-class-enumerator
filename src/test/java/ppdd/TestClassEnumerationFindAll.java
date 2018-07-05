package ppdd;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import ppdd.subpkg.ClassIShouldFindThree;
import pro.ddopson.ClassEnumeratorFindAll;


public class TestClassEnumerationFindAll {
	
	@Test
	public void main() {
		try{
		
		File directory = new File("folder");
		System.out.println("Test class location: " );
		List<Class<?>> classes = ClassEnumeratorFindAll.processDirectory(directory);
		System.out.println("Number of classes found in directory file: "+classes.size());

		Class<?>[] expected = new Class<?>[] {
			pro.ddopson.ClassEnumerator.class,
			ClassEnumeratorFindAll.class,
			ppdd.ClassIShouldFindOne.class,
			ppdd.ClassIShouldFindTwo.class,
			ClassIShouldFindThree.class,
			ppdd.TestClassEnumeration.class,
			TestClassEnumerationFindAll.class
		};
		for (Class<?> clazz : expected) {
			if (!classes.contains(clazz)) {
				fail("FAIL: expected to find class '" + clazz.getName() + "'");
			}
		}
		if(classes.size() != expected.length) {
			fail("FAIL: expected to find " + expected.length + " classes, but actually found " + classes.size());
		}
		}catch(Exception ex){
			fail("Exception occured"+ex);
		}
	}
}
