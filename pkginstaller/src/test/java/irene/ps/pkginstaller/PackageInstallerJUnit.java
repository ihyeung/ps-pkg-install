package irene.ps.pkginstaller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageInstallerJUnit {
	String[] list;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		list = null;
	}

	/**
	 * 
	 * Package Installer Requirement #1: Valid Input Format
	 * 
	 * 1. Input should be an array of strings, each of which should be
	 * up to two package names, separated with a colon and a single space. 
	 * 2. A package with no dependencies is permitted, in which case its string
	 * should contain one package name, followed by a colon and space.
	 * 3. A package has at most one dependency (for simplicity).
	 * 4. Simplified to only permit uppercase and lowercase alphabetic characters for package/dependency names.
	 * 
	 */

	@Test
	public void testNullArrayInput() {
		assertEquals(PackageInstaller.installPackages(null), "null");
	}

	@Test
	public void testEmptyArrayInput() {
		list = new String[] {};
		assertEquals(PackageInstaller.installPackages(list), "");
	}

	@Test
	public void testSinglePackageArrayInput() {
		list = new String[] {"KittenService: "};
		assertEquals(PackageInstaller.installPackages(list), "KittenService");
	}

	@Test
	public void testSingleEntryArrayInput() {
		list = new String[] {"CamelCaser: KittenService"};
		assertTrue(PackageInstaller.verifyPackageInstallOrder(PackageInstaller.createPackageInstallOrder(list)));
	}

	@Test
	public void testSingleDependencyArrayInput() {
		list = new String[] {"CamelCaser: KittenService", "KittenService: "};
		assertEquals(PackageInstaller.installPackages(list), "KittenService, CamelCaser");
	}

	@Test
	public void testSimpleArrayInput() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Fraudstream", "Cyberportal: Ice"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.validateListInput(list));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Ice, Fraudstream, Cyberportal, CamelCaser, KittenService"));
	}

	@Test 
	public void testPackageWithNoDependencies() {
		list = new String[] {"KittenService: ", "Leetmeme: "};
		assertTrue(PackageInstaller.validateListInput(list));
	}

	@Test 
	public void testPackageWithMultipleDependencies() {
		list = new String[] {"KittenService: Cyberportal, Ice, Hello", "Ice: "};
		assertFalse(PackageInstaller.validateListInput(list));
	}

	@Test
	public void testInputArrayWithInvalidFormatOrCharacters() {
		assertFalse(PackageInstaller.validateListInput(new String[] {"KittenService:Ice"}));
		assertFalse(PackageInstaller.validateListInput(new String[] {"KittenService Ice"}));
		assertFalse(PackageInstaller.validateListInput(new String[] {"Kitten Service: Ice"}));
		assertFalse(PackageInstaller.validateListInput(new String[] {"KittenService2: Ice"}));
		list = new String[] {"KittenService-2: ^2.0.1"};
		assertEquals(PackageInstaller.installPackages(list), "Invalid Input");
	}

	@Test
	public void testInputArrayWithInvalidPackageDependency() {
		list = new String[] {"KittenService: KittenService"};
		assertEquals(PackageInstaller.installPackages(list), "Invalid Input");
	}

	/**
	 * Package Installer Requirement #2: Handling Cyclic Dependencies
	 * 
	 * Any package list input containing cyclic dependencies should
	 * be rejected as invalid.
	 * 
	 */

	@Test
	public void testAcyclicPackageList() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Leetmeme: Fraudstream", "Ice: KittenService"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s));
		assertFalse(PackageInstaller.checkInputForCyclicDependencies());		
	}

	@Test
	public void testSimpleCyclicPackageList() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Leetmeme: KittenService"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder(s));
		assertTrue(PackageInstaller.checkInputForCyclicDependencies());
		assertEquals(PackageInstaller.installPackages(list), "Invalid Input");
	}

	@Test
	public void testCyclicPackageListWithOneNodeWithoutDependencies() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Leetmeme: KittenService", "Ice: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s)); //Should pass installation order test despite output missing packages
		assertTrue(PackageInstaller.checkInputForCyclicDependencies());
		assertEquals(PackageInstaller.installPackages(list), "Invalid Input");
	}

	@Test
	public void testCyclicDependencyWithMultipleNodesWithoutDependencies() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Leetmeme: KittenService", "Ice: ", "Fraudstream: ", "Cyberportal: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s)); //Should pass installation order test despite output missing packages
		assertTrue(PackageInstaller.checkInputForCyclicDependencies()); 
		assertEquals(PackageInstaller.installPackages(list), "Invalid Input");
	}

	@Test
	public void testListOutputIncompleteMissingPackages() {
		list = new String[] {"KittenService: ", "CamelCaser: Leetmeme", "Leetmeme: KittenService", "Ice: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder("Leetmeme, KittenService, Ice"));
	}


	/**
	 * Package Installer Requirement #3: Package Install Order Valid
	 * 
	 * Verify that if a valid program output exists, the package installer returns a list of the packages 
	 * ordered in such a way that any given package with a dependency
	 * will always be preceded in installation order by that package's dependency.
	 * If no valid output exists, a cyclic dependency exists.
	 * 
	 */

	@Test
	public void testOrderingSimpleValidPackageList() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Ice: Leetmeme"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Leetmeme, CamelCaser, KittenService, Ice"));
	}

	@Test
	public void testOrderingSimpleValidPackageListUsingSort() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Fraudstream: Leetmeme"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s));
	}

	@Test
	public void testOrderingSimpleInvalidPackageList() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Fraudstream: Leetmeme"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder("KittenService, Fraudstream, CamelCaser, Leetmeme"));
	}

	@Test
	public void testOrderingListWithoutAnyPackageDependencies() {
		list = new String[] {"Leetmeme: ", "KittenService: ", "CamelCaser: ", "Fraudstream: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder("CamelCaser, Fraudstream, KittenService, Leetmeme"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("CamelCaser, KittenService, Fraudstream, Leetmeme"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Fraudstream, CamelCaser, KittenService, Leetmeme"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("CamelCaser, Fraudstream, Leetmeme, KittenService"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Leetmeme, KittenService, Fraudstream, CamelCaser"));
	}

	@Test
	public void testOrderingListWithPackagesWithNoDependencies() {
		list = new String[] {"Leetmeme: Fraudstream", "KittenService: CamelCaser", "CamelCaser: ", "Fraudstream: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder("CamelCaser, Fraudstream, KittenService, Leetmeme"));
		assertFalse(PackageInstaller.verifyPackageInstallOrder("Leetmeme, KittenService, Fraudstream, CamelCaser"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s));
	}

	@Test
	public void testOrderingListWithPackagesWithSameDependency() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Fraudstream", "Fraudstream: Ice", "Leetmeme: Fraudstream"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Ice, Fraudstream, Leetmeme, CamelCaser, KittenService"));
	}

	@Test
	public void testOrderingValidListWithSingleCommonDependency() {
		list = new String[] {"KittenService: CamelCaser", "Fraudstream: CamelCaser", "Ice: CamelCaser", "Leetmeme: CamelCaser"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder("CamelCaser, Ice, Fraudstream, Leetmeme, KittenService"));
	}

	@Test
	public void testOrderingInvalidListWithSingleCommonDependency() {
		list = new String[] {"KittenService: CamelCaser", "Fraudstream: CamelCaser", "Ice: CamelCaser", "Leetmeme: CamelCaser"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder("Ice, CamelCaser, Fraudstream, Leetmeme, KittenService"));
	}

	@Test
	public void testOrderingValidListWithMultipleValidOrderings() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Fraudstream", "Cyberportal: Ice"};
		PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Ice, Fraudstream, Cyberportal, CamelCaser, KittenService"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Ice, Cyberportal, Fraudstream, CamelCaser, KittenService"));
		assertTrue(PackageInstaller.verifyPackageInstallOrder("Fraudstream, Ice, CamelCaser, Cyberportal, KittenService"));
	}

	@Test
	public void testValidOrderListAllPackagesLinked () {
		list = new String[] {"PackageA: PackageB", "PackageB: PackageC", "PackageC: PackageD", "PackageD: PackageE", "PackageE: PackageF", 
				"PackageF: PackageG", "PackageG: PackageH", "PackageH: PackageI", "PackageI: PackageJ", "PackageJ: PackageK", "PackageK: PackageL", 
				"PackageL: PackageM", "PackageM: PackageN", "PackageN: PackageO", "PackageO: PackageP", 
		"PackageP: PackageQ"};
		assertEquals(PackageInstaller.installPackages(list), "PackageQ, PackageP, PackageO, PackageN, PackageM, PackageL, PackageK, " + 
				"PackageJ, PackageI, PackageH, PackageG, PackageF, PackageE, PackageD, PackageC, PackageB, PackageA");
	}
}
