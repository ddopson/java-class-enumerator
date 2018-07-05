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
javac -d build/classes src/pro/ddopson/ClassEnumerator.java src/ppdd/ClassIShouldFindOne.java src/ppdd/ClassIShouldFindTwo.java src/ppdd/subpkg/ClassIShouldFindThree.java src/ppdd/TestClassEnumeration.java
 Making JAR Files...
jar cf build/ClassEnumerator_test.jar -C build/classes/ . 
jar cf build/ClassEnumerator.jar -C build/classes/ pro
 Running Filesystem Classpath Test...
java -classpath build/classes ppdd.TestClassEnumeration
ClassDiscovery: Package: 'ppdd' becomes Resource: 'file:/Users/Dopson/work/other/java-class-enumeration/build/classes/ppdd'
ClassDiscovery: Reading Directory '/Users/Dopson/work/other/java-class-enumeration/build/classes/ppdd'
ClassDiscovery: FileName 'ClassIShouldFindOne.class'  =>  class 'ppdd.ClassIShouldFindOne'
ClassDiscovery: FileName 'ClassIShouldFindTwo.class'  =>  class 'ppdd.ClassIShouldFindTwo'
ClassDiscovery: FileName 'subpkg'  =>  class 'null'
ClassDiscovery: Reading Directory '/Users/Dopson/work/other/java-class-enumeration/build/classes/ppdd/subpkg'
ClassDiscovery: FileName 'ClassIShouldFindThree.class'  =>  class 'ppdd.subpkg.ClassIShouldFindThree'
ClassDiscovery: FileName 'TestClassEnumeration.class'  =>  class 'ppdd.TestClassEnumeration'
 Running JAR Classpath Test...
java -classpath build/ClassEnumerator_test.jar  ppdd.TestClassEnumeration
ClassDiscovery: Package: 'ppdd' becomes Resource: 'jar:file:/Users/Dopson/work/other/java-class-enumeration/build/ClassEnumerator_test.jar!/ppdd'
ClassDiscovery: Reading JAR file: '/Users/Dopson/work/other/java-class-enumeration/build/ClassEnumerator_test.jar'
ClassDiscovery: JarEntry 'META-INF/'  =>  class 'null'
ClassDiscovery: JarEntry 'META-INF/MANIFEST.MF'  =>  class 'null'
ClassDiscovery: JarEntry 'pro/'  =>  class 'null'
ClassDiscovery: JarEntry 'pro/ddopson/'  =>  class 'null'
ClassDiscovery: JarEntry 'pro/ddopson/ClassEnumerator.class'  =>  class 'null'
ClassDiscovery: JarEntry 'ppdd/'  =>  class 'null'
ClassDiscovery: JarEntry 'ppdd/ClassIShouldFindOne.class'  =>  class 'ppdd.ClassIShouldFindOne'
ClassDiscovery: JarEntry 'ppdd/ClassIShouldFindTwo.class'  =>  class 'ppdd.ClassIShouldFindTwo'
ClassDiscovery: JarEntry 'ppdd/subpkg/'  =>  class 'null'
ClassDiscovery: JarEntry 'ppdd/subpkg/ClassIShouldFindThree.class'  =>  class 'ppdd.subpkg.ClassIShouldFindThree'
ClassDiscovery: JarEntry 'ppdd/TestClassEnumeration.class'  =>  class 'ppdd.TestClassEnumeration'
 Tests Passed. 
</pre>


If you want to use this as a library, you can copy 'build/ClassEnumerator.jar' and do the following from your code

    ArrayList<Class<?>> discoveredClasses = pro.ddopson.ClassEnumerator.getClassesForPackage(MyCurrentClass.class.getPackage());
