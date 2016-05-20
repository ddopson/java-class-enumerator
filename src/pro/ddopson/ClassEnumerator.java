package pro.ddopson;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassEnumerator {
	private static void log(String msg) {
		System.out.println("ClassDiscovery: " + msg);	
	}

	private static Class<?> loadClass(String className) {
		try {
			return Class.forName(className);
		} 
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
		}
	}
	
	/**
	 * Given a package name and a directory returns all classes within that directory
	 * @param directory
	 * @param pkgname
	 * @return Classes within Directory with package name
	 */
	public static List<Class<?>> processDirectory(File directory, String pkgname) {
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		 
		log("Reading Directory '" + directory + "'");
		
		// Get the list of the files contained in the package
		String[] files = directory.list();
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			String className = null;
			
			// we are only interested in .class files
			if (fileName.endsWith(".class")) {
				// removes the .class extension
				className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
			}
			
			log("FileName '" + fileName + "'  =>  class '" + className + "'");
			
			if (className != null) {
				classes.add(loadClass(className));
			}
			
			//If the file is a directory recursively class this method.
			File subdir = new File(directory, fileName);
			if (subdir.isDirectory()) {
				classes.addAll(processDirectory(subdir, pkgname + '.' + fileName));
			}
		}
		return classes;
	}

	/**
	 * Given a jar file's URL and a package name returns all classes within jar file.
	 * @param resource
	 * @param pkgname
	 */
	public static List<Class<?>> processJarfile(URL resource, String pkgname) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		//Turn package name to relative path to jar file
		String relPath = pkgname.replace('.', '/');
		log("relative path: "+relPath);
		String resPath = resource.getPath();
		log("resource path: "+resPath);
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		// jarPath = jarPath.replace(" ", "\\ ");
		log("Reading JAR file: '" + jarPath + "'");
		JarFile jarFile;
		
		try {
			jarFile = new JarFile(jarPath);         
		} 
		catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
		}
		
		//get contents of jar file and iterate through them
		Enumeration<JarEntry> entries = jarFile.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			
			//Get content name from jar file
			String entryName = entry.getName();
			String className = null;
			
			//If content is a class save class name.
			if(entryName.endsWith(".class") && entryName.startsWith(relPath) 
					&& entryName.length() > (relPath.length() + "/".length())) {
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
			}
			
			log("JarEntry '" + entryName + "'  =>  class '" + className + "'");
			
			//If content is a class add class to List
			if (className != null) {
				classes.add(loadClass(className));
			}
		}
		return classes;
	}
	/**
	 * Give a package this method returns all classes contained in that package
	 * @param pkg
	 */
	public static List<Class<?>> getClassesForPackage(Package pkg) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		//Get name of package and turn it to a relative path
		String pkgname = pkg.getName();
		String relPath = pkgname.replace('.', '/');
	
		// Get a File object for the package
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		
		//If we can't find the resource we throw an exception
		if (resource == null) {
			throw new RuntimeException("Unexpected problem: No resource for " + relPath);
		}
		
		log("Package: '" + pkgname + "' becomes Resource: '" + resource.toString() + "'");

		//If the resource is a jar get all classes from jar
		if(resource.toString().startsWith("jar:")) {
			classes.addAll(processJarfile(resource, pkgname));
		} 
		else {
			classes.addAll(processDirectory(new File(resource.getPath()), pkgname));
		}

		return classes;
	}
}
