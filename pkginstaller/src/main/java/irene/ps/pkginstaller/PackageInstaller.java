package irene.ps.pkginstaller;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageInstaller {
	private static DirectedGraph pkgs;
	
	public String installPackages(String[] inputlist) {
		if (inputlist == null) {
			return new String("null");
		}
		if (!validateListInput(inputlist)) {
			return new String ("Invalid Input");
		}
		String output = createPackageInstallOrder(inputlist);
		if (checkInputForCyclicDependencies() || !verifyPackageInstallOrder(output)) {
			return new String ("Invalid Input");
		}
		return output;
	}
	
	static boolean validateListInput(String[] pkglist) {
		if (pkglist.length == 0) {
			return true;
		}
		for (String each: pkglist) {
			if (!validatePackageListEntry(each)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validates a given string entry in the package list
	 * for the correct "package: dependency" input format.
	 * Acceptable package/dependency name characters: [A-Za-z0-9].
	 * (May modify this back later to accept dependency version characters, only alphanumeric characters for acceptable
	 * with the current implementation.)
	 * 
	 * If a list entry contains a dependency, handles invalid input case where a package is listed as its own dependency.
	 * @param listentry - Formatted string pair i.e.,"packagename: dependencyname"
	 * 
	 */
	static boolean validatePackageListEntry(String listentry) {
//		Pattern p = Pattern.compile("[A-Za-z0-9.-]+:{1}\\s{1}[A-Za-z0-9~>=<(-).\\s|^]*");
		Pattern p = Pattern.compile("[A-Za-z0-9]+:{1}\\s{1}[A-Za-z0-9]*");
		Matcher match = p.matcher(listentry);
		String[] entry = listentry.split(": ");
		return entry.length == 1 ? match.matches() : 
			entry.length == 2 ? match.matches() && !entry[0].equals(entry[1]) : false;
	}
	
	static String createPackageInstallOrder(String[] pkglist) {
		pkgs = new DirectedGraph(pkglist);
		pkgs.getPackageInstallOrder();
		return pkgs.getPackageInstallStringOutput();
	}
	
	static boolean checkInputForCyclicDependencies() {
		return pkgs.containsCycle() || pkgs.pkginstallorder.size() != pkgs.getVertices().size();
	}
	
	static boolean verifyPackageInstallOrder(String pkgorder) {
		if (pkgorder.length() == 0) {
			return pkgs.getVertices().isEmpty();
		}
		String[] list = pkgorder.split(", ");
		ArrayList<String> installed = new ArrayList<String>();
		assert pkgs != null;
		for (String p : list) {
			if (!installed.contains(p)) { 
				installed.add(p);
			}
			assert pkgs.lookupPackage(p) != null;	
			String dependency = pkgs.lookupPackage(p).getDependencyName();
			if (dependency.length() == 0) { //No dependencies, continue
				continue;
			}
			if (dependency.length() > 0) {
				if (!installed.contains(dependency)) { //Required dependency not install, terminate loop
					return false;
				}
			}
		}
		return true;
	}
}
