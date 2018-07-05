Strictly speaking, it isn't possible to list the classes in a package. This is because a package is really nothing more than a namespace (eg com.epicapplications.foo.bar), and any jar-file in the classpath could potentially add classes into a package. Even worse, the classloader will load classes on demand, and part of the classpath might be on the other side of a network connection.

It is possible to solve a more restrictive problem. eg, all classes in a JAR file, or all classes that a JAR file defines within a particular package. This is the more common scenario anyways.

Unfortunately, there isn't any framework code to make this task easy. You have to scan the filesystem in a manner similar to how the ClassLoader would look for class definitions.

There are a lot of samples on the web for class files in plain-old-directories. Most of us these days work with JAR files.

To get things working with JAR files, try this simple library

When you run 'make' for this project, the following should print out:

<pre>
 Cleaning...
rm -rf build/
 Building...
javac -d build/classes src/pro/ddopson/ClassEnumerator.java src/test/ClassIShouldFindOne.java src/test/ClassIShouldFindTwo.java src/test/subpkg/ClassIShouldFindThree.java src/test/TestClassEnumeration.java
 Making JAR Files...
jar cf build/ClassEnumerator_test.jar -C build/classes/ . 
jar cf build/ClassEnumerator.jar -C build/classes/ pro
 Running Filesystem Classpath Test...
java -classpath build/classes test.TestClassEnumeration
ClassDiscovery: Package: 'test' becomes Resource: 'file:/Users/Dopson/work/other/java-class-enumeration/build/classes/test'
ClassDiscovery: Reading Directory '/Users/Dopson/work/other/java-class-enumeration/build/classes/test'
ClassDiscovery: FileName 'ClassIShouldFindOne.class'  =>  class 'test.ClassIShouldFindOne'
ClassDiscovery: FileName 'ClassIShouldFindTwo.class'  =>  class 'test.ClassIShouldFindTwo'
ClassDiscovery: FileName 'subpkg'  =>  class 'null'
ClassDiscovery: Reading Directory '/Users/Dopson/work/other/java-class-enumeration/build/classes/test/subpkg'
ClassDiscovery: FileName 'ClassIShouldFindThree.class'  =>  class 'test.subpkg.ClassIShouldFindThree'
ClassDiscovery: FileName 'TestClassEnumeration.class'  =>  class 'test.TestClassEnumeration'
 Running JAR Classpath Test...
java -classpath build/ClassEnumerator_test.jar  test.TestClassEnumeration
ClassDiscovery: Package: 'test' becomes Resource: 'jar:file:/Users/Dopson/work/other/java-class-enumeration/build/ClassEnumerator_test.jar!/test'
ClassDiscovery: Reading JAR file: '/Users/Dopson/work/other/java-class-enumeration/build/ClassEnumerator_test.jar'
ClassDiscovery: JarEntry 'META-INF/'  =>  class 'null'
ClassDiscovery: JarEntry 'META-INF/MANIFEST.MF'  =>  class 'null'
ClassDiscovery: JarEntry 'pro/'  =>  class 'null'
ClassDiscovery: JarEntry 'pro/ddopson/'  =>  class 'null'
ClassDiscovery: JarEntry 'pro/ddopson/ClassEnumerator.class'  =>  class 'null'
ClassDiscovery: JarEntry 'test/'  =>  class 'null'
ClassDiscovery: JarEntry 'test/ClassIShouldFindOne.class'  =>  class 'test.ClassIShouldFindOne'
ClassDiscovery: JarEntry 'test/ClassIShouldFindTwo.class'  =>  class 'test.ClassIShouldFindTwo'
ClassDiscovery: JarEntry 'test/subpkg/'  =>  class 'null'
ClassDiscovery: JarEntry 'test/subpkg/ClassIShouldFindThree.class'  =>  class 'test.subpkg.ClassIShouldFindThree'
ClassDiscovery: JarEntry 'test/TestClassEnumeration.class'  =>  class 'test.TestClassEnumeration'
 Tests Passed. 
</pre>


If you want to use this as a library, you can copy 'build/ClassEnumerator.jar' and do the following from your code

    ArrayList<Class<?>> discoveredClasses = pro.ddopson.ClassEnumerator.getClassesForPackage(MyCurrentClass.class.getPackage());
