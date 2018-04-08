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
	 * 4. Simplified to only permit alphanumeric input characters for package/dependency names.
	 * 
	 * --------------Removed for simplicity; may add below functionality back later -----------
	 * Special characters relating to the version of a dependency are 
	 * permitted (e.g. "npm: ~1.0.2" or "npm: >=1.0.2 <2.1.2" or "npm: ^5.8.0"
	 * are all valid inputs.)
	 * ----------------------------------------------------------------------------------------
	 */
	
	@Test
	public void testNullArrayInput() {
		assertEquals(PackageInstaller.installPackages(null), "null");
	}
	
	@Test
	public void testSimpleArrayInput() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: FraudStream", "CyberPortal: Ice"};
		assertTrue(PackageInstaller.validateListInput(list));
	}
	
	@Test
	public void testEmptyArrayInput() {
		list = new String[] {};
		assertTrue(PackageInstaller.validateListInput(list));
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String ("")));
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
		assertFalse(PackageInstaller.validateListInput(new String[] {"KittenService-Ice"}));
		assertFalse(PackageInstaller.validateListInput(new String[] {"KittenService Ice"}));
	}
	
	@Test
	public void testInputArrayWithInvalidPackageDependency() {
		assertFalse(PackageInstaller.validateListInput(new String[] {"KittenService: KittenService"}));
	}
	//Not implemented yet
//	@Test 
//	public void testPackageSpecificDependencyVersion() {
//		list = new String[] {"KittenService: <1.0.0 || >=2.3.1 <2.4.5", "Leetmeme: ~1.2.3"};
//		assertTrue(PackageInstaller.validateListInput(list));
//		assertTrue(true);
//	}
	
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

	}
	@Test
	public void testCyclicPackageListWithOneNodeWithoutDependencies() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Leetmeme: KittenService", "Ice: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s)); //Should pass installation order test despite output missing packages
		assertTrue(PackageInstaller.checkInputForCyclicDependencies());
	}

	@Test
	public void testCyclicDependencyWithMultipleNodesWithoutDependencies() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Leetmeme", "Leetmeme: KittenService", "Ice: ", "Fraudstream: ", "Cyberportal: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s)); //Should pass installation order test despite output missing packages
		assertTrue(PackageInstaller.checkInputForCyclicDependencies()); //
		
	}

	@Test
	public void testListOutputIncompleteMissingPackages() {
		list = new String[] {"KittenService: ", "CamelCaser: Leetmeme", "Leetmeme: KittenService", "Ice: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder(new String("Leetmeme, KittenService, Ice")));
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
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("Leetmeme, CamelCaser, KittenService, Ice")));
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
		assertFalse(PackageInstaller.verifyPackageInstallOrder(new String("KittenService, Fraudstream, CamelCaser, Leetmeme")));

	}
	@Test
	public void testOrderingListWithoutAnyPackageDependencies() {
		list = new String[] {"Leetmeme: ", "KittenService: ", "CamelCaser: ", "Fraudstream: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("CamelCaser, Fraudstream, KittenService, Leetmeme")));
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("CamelCaser, KittenService, Fraudstream, Leetmeme")));
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("Fraudstream, CamelCaser, KittenService, Leetmeme")));
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("CamelCaser, Fraudstream, Leetmeme, KittenService")));
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("Leetmeme, KittenService, Fraudstream, CamelCaser")));
	}
	
	
	@Test
	public void testOrderingListWithPackagesWithNoDependencies() {
		list = new String[] {"Leetmeme: Fraudstream", "KittenService: CamelCaser", "CamelCaser: ", "Fraudstream: "};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("CamelCaser, Fraudstream, KittenService, Leetmeme")));
		assertFalse(PackageInstaller.verifyPackageInstallOrder(new String("Leetmeme, KittenService, Fraudstream, CamelCaser")));
		assertTrue(PackageInstaller.verifyPackageInstallOrder(s));


	}

	@Test
	public void testOrderingListWithPackagesWithSameDependency() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: Fraudstream", "Fraudstream: Ice", "Leetmeme: Fraudstream"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("Ice, Fraudstream, Leetmeme, CamelCaser, KittenService")));
	}
	
	@Test
	public void testOrderingValidListWithCommonDependency() {
		list = new String[] {"KittenService: CamelCaser", "Fraudstream: CamelCaser", "Ice: CamelCaser", "Leetmeme: CamelCaser"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("CamelCaser, Ice, Fraudstream, Leetmeme, KittenService")));
	}
	
	@Test
	public void testOrderingInvalidListWithCommonDependency() {
		list = new String[] {"KittenService: CamelCaser", "Fraudstream: CamelCaser", "Ice: CamelCaser", "Leetmeme: CamelCaser"};
		String s = PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder(new String("Ice, CamelCaser, Fraudstream, Leetmeme, KittenService")));
	}
	
	@Test
	public void testValidOrderListAllPackagesLinked () {
		list = new String[] {"PackageA: PackageB", "PackageB: PackageC", "PackageC: PackageD", "PackageD: PackageE", "PackageE: PackageF", 
				"PackageF: PackageG", "PackageG: PackageH", "PackageH: PackageI", "PackageI: PackageJ", "PackageJ: PackageK", "PackageK: PackageL", 
				"PackageL: PackageM", "PackageM: PackageN", "PackageN: PackageO", "PackageO: PackageP", 
				"PackageP: PackageQ"};
		String s= PackageInstaller.createPackageInstallOrder(list);
		assertTrue(PackageInstaller.verifyPackageInstallOrder(new String("PackageQ, PackageP, PackageO, PackageN, PackageM, PackageL, PackageK, "
				+ "PackageJ, PackageI, PackageH, PackageG, PackageF, PackageE, PackageD, PackageC, PackageB, PackageA")));
	}
	@Test
	public void testInvalidOrderListAllPackagesLinked () {
		list = new String[] {"PackageA: PackageB", "PackageB: PackageC", "PackageC: PackageD", "PackageD: PackageE", "PackageE: PackageF", 
				"PackageF: PackageG", "PackageG: PackageH", "PackageH: PackageI", "PackageI: PackageJ", "PackageJ: PackageK", "PackageK: PackageL", 
				"PackageL: PackageM", "PackageM: PackageN", "PackageN: PackageO", "PackageO: PackageP", 
				"PackageP: PackageQ"};
		String s= PackageInstaller.createPackageInstallOrder(list);
		assertFalse(PackageInstaller.verifyPackageInstallOrder(new String("PackageP, PackageQ, PackageO, PackageN, PackageM, PackageL, PackageK, "
				+ "PackageJ, PackageI, PackageH, PackageG, PackageF, PackageE, PackageD, PackageC, PackageB, PackageA")));
		assertFalse(PackageInstaller.verifyPackageInstallOrder(new String("PackageA, PackageP, PackageO, PackageN, PackageM, PackageL, PackageK, "
				+ "PackageJ, PackageI, PackageH, PackageG, PackageF, PackageE, PackageD, PackageC, PackageB, PackageQ")));
	}
	
	
}
