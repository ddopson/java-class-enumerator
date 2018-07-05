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
	
	private static final String CLASS_SUFFIX = ".class";

	private static Class<?> loadClass(String cls) {
		try {
			return Class.forName(cls);
		} 
		catch (ClassNotFoundException e) {
			String err = "Unexpected ClassNotFoundException loading class [%s]";
			throw new ClassEnumException(String.format(err, cls), e);
		}
	}
	
	/**
	 * Given a package name and a directory returns all classes within that
	 * directory
	 * 
	 * @param dir
	 * @param pkgname
	 * @return Classes within Directory with package name
	 */
	public static List<Class<?>> processDirectory(File dir, String pkgname) {
		List<Class<?>> classes = new ArrayList<>();
		for(String file : dir.list()) {
			String cls = null;
			// we are only interested in .class files
			if (file.endsWith(CLASS_SUFFIX)) {
				// removes the .class extension
				cls = pkgname + '.' + file.substring(0, file.length() - 6);
				classes.add(loadClass(cls));
			}
			// If the file is a directory recursively class this method.
			File subdir = new File(dir, file);
			if (subdir.isDirectory()) {
				classes.addAll(processDirectory(subdir, pkgname + '.' + file));
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
		List<Class<?>> classes = new ArrayList<>();
		// Turn package name to relative path to jar file
		String relPath = pkgname.replace('.', '/');
		String resPath = resource.getPath();
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");

		try (JarFile jarFile = new JarFile(jarPath)) {
			// attempt to load jar file

			// get contents of jar file and iterate through them
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();

				// Get content name from jar file
				String entryName = entry.getName();
				String className = null;

				// If content is a class save class name.
				if (entryName.endsWith(CLASS_SUFFIX) && entryName.startsWith(relPath)
						&& entryName.length() > (relPath.length() + "/".length())) {
					className = entryName.replace('/', '.').replace('\\', '.').replace(CLASS_SUFFIX, "");
				}

				// If content is a class add class to List
				if (className != null) {
					classes.add(loadClass(className));
				}
			}
		} catch (IOException e) {
			String err = "Unexpected IOException reading JAR File [%s]";
			throw new ClassEnumException(String.format(err, jarPath), e);
		}
		return classes;
	}
	/**
	 * Give a package this method returns all classes contained in that package
	 * @param pkg
	 */
	public static List<Class<?>> getClassesForPackage(Package pkg) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		
		//Get name of package and turn it to a relative path
		String pkgname = pkg.getName();
		String relPath = pkgname.replace('.', '/');
	
		// Get a File object for the package
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		
		//If we can't find the resource we throw an exception
		if (resource == null) {
			String err = "Unexpected problem: No resource for " + relPath;
			throw new ClassEnumException(err);
		}
		
		//If the resource is a jar get all classes from jar
		if(resource.toString().startsWith("jar:")) {
			classes.addAll(processJarfile(resource, pkgname));
		} 
		else {
			classes.addAll(processDirectory(new File(resource.getPath()), pkgname));
		}

		return classes;
	}
	
	private ClassEnumerator() {
		
	}
	

	public static class ClassEnumException extends RuntimeException {

		private static final long serialVersionUID = 909384213793458361L;

		public ClassEnumException() {
			super();
		}

		public ClassEnumException(String message) {
			super(message);
		}

		public ClassEnumException(String message, Throwable cause) {
			super(message, cause);
		}

		public ClassEnumException(Throwable cause) {
			super(cause);
		}
	}
}
