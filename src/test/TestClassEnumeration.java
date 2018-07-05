package test;

import pro.ddopson.ClassEnumerator;
import test.subpkg.ClassIShouldFindThree;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class TestClassEnumeration {
	@Test
	public void main() {
		System.out.println("Test class package name: "+TestClassEnumeration.class.getPackage());
		List<Class<?>> classes = ClassEnumerator.getClassesForPackage(TestClassEnumeration.class.getPackage());
		Class<?>[] expected = new Class<?>[] {
			ClassIShouldFindOne.class,
			ClassIShouldFindTwo.class,
			ClassIShouldFindThree.class,
			TestClassEnumeration.class,
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
	}
}
