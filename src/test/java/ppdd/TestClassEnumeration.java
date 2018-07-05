package ppdd;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import pro.ddopson.ClassEnumerator;

public class TestClassEnumeration {
	@Test
	public void main() {
		System.out.println("Test class package name: "+TestClassEnumeration.class.getPackage());
		List<Class<?>> classes = ClassEnumerator.getClassesForPackage(TestClassEnumeration.class.getPackage());
		Class<?>[] expected = new Class<?>[] {
			ppdd.ClassIShouldFindOne.class,
			ppdd.ClassIShouldFindTwo.class,
			ppdd.subpkg.ClassIShouldFindThree.class,
			TestClassEnumeration.class,
			ppdd.TestClassEnumerationFindAll.class
		};
		for (Class<?> clazz : expected) {
			if (!classes.contains(clazz)) {
				fail("FAIL: expected to find class '" + clazz.getName() + "'");
			}
		}
		if(classes.size() != expected.length) {
			fail("FAIL: expected to find " + expected.length + " classes, but actually found " + classes.size());
		}
	}
}
