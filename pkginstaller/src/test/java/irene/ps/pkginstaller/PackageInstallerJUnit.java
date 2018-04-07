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
	}
	
	/**
	 * Requirement 1: Input Validation
	 * 1. Input should be an array of strings, each of which should be
	 * up to two package names, separated with a colon and a single space. 
	 * 2. A package with no dependencies is permitted, in which case its string
	 * should contain one package name, followed by a colon and space.
	 * 3. A package has at most one dependency (for simplicity).
	 * 4. Special characters relating to the version of a dependency are 
	 * permitted (e.g. "npm: ~1.0.2" or "npm: >=1.0.2 <2.1.2" or "npm: ^5.8.0"
	 * are all valid inputs.)
	 */
	@Test
	public void testSimpleArrayInput() {
		list = new String[] {"KittenService: CamelCaser", "CamelCaser: FraudStream", "CyberPortal: Ice"};
		assertTrue(PackageInstaller.validListInput(list));
	}
	
	@Test
	public void testEmptyArrayInput() {
		list = new String[] {};
		assertTrue(PackageInstaller.validListInput(list));
	}
	@Test 
	public void testPackageWithNoDependencies() {
		list = new String[] {"KittenService: ", "Leetmeme: "};
		assertTrue(PackageInstaller.validListInput(list));
	}
	@Test 
	public void testPackageWithMultipleDependencies() {
		list = new String[] {"KittenService: Cyberportal, Ice, Hello", "Ice: "};
		assertFalse(PackageInstaller.validListInput(list));
	}
	@Test
	public void testInputArrayWithInvalidFormatOrCharacters() {
		assertFalse(PackageInstaller.validListInput(new String[] {"KittenService:Ice"}));
		assertFalse(PackageInstaller.validListInput(new String[] {"KittenService-Ice"}));
		assertFalse(PackageInstaller.validListInput(new String[] {"KittenService Ice"}));
	}
	@Test 
	public void testPackageSpecificDependencyVersion() {
		list = new String[] {"KittenService: <1.0.0 || >=2.3.1 <2.4.5", "Leetmeme: ~1.2.3"};
		assertTrue(PackageInstaller.validListInput(list));
	}
	
	/**
	 * Requirement 2: Reject Cyclic Dependencies
	 * Any package list input containing cyclic dependencies should
	 * be rejected as invalid.
	 */
	
	@Test
	public void testAcyclicPackageList() {
		
	}
	@Test
	public void testCyclicDependencyWithNoLeafNodes() {
		
	}
	@Test
	public void testCyclicDependencyContainingLeafNodes() {
		
	}
	
	/**
	 * Requirement 3: Package Installation Order Verification
	 * Verify the output of createPackageInstallOrder() returns 
	 * a list of packages ordered in such a way that a package's dependency
	 * will always precede that package.
	 * 
	 */
	@Test
	public void testOrderSimpleValidPackageList() {
		
	}
	@Test
	public void testOrderListWithPackagesWithNoDependencies() {
		
	}

	@Test
	public void testOrderListInvalidInstallOrder() {
		
	}
	
	@Test
	public void testOrderListAllPackagesLinked () {
		
	}
	
	
}
