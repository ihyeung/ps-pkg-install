package irene.ps.pkginstaller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageInstaller {
   
	static boolean validListInput(String[] pkglist) {
		if (pkglist.length == 0) {
			return true;
		}
		for (String each: pkglist) {
			if (!validPackageInput(each)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validates a given string entry in the package list
	 * for the correct "package: dependency" input format.
	 * Acceptable package name characters: [A-Za-z0-9.-]
	 * Acceptable dependency name characters: [A-Za-z0-9~>=<(-).\\s|^]
	 * @param listentry
	 */
	static boolean validPackageInput(String listentry) {
		Pattern p = Pattern.compile("[A-Za-z0-9.-]+:{1}\\s{1}[A-Za-z0-9~>=<(-).\\s|^]*");
		Matcher match = p.matcher(listentry);
		return match.matches();
	}
	
	static boolean containsCycle() {
		return true;
	}
	
	static String createPackageInstallOrder(String[] pkglist) {
		return new String ("");
	}
	
	static boolean verifyPackageInstallOrder(String[] pkglist) {
		return false;
	}
}
