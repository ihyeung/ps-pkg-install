package irene.ps.pkginstaller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Directed Graph class that represents individual packages to be installed
 * as graph vertices, with package dependencies represented as 
 * graph edges.
 *
 */
public class DirectedGraph {
	private ArrayList<Package> vertices;
	protected ArrayList<Package> pkginstallorder;
	private boolean isCyclic;
	
	DirectedGraph(String [] pkglist){
		isCyclic = false;
		vertices = new ArrayList<Package>();
		pkginstallorder = new ArrayList<Package>();
		for (String p : pkglist) {
			addPackageEntry(p);
		}
	}
	
	private Package addPackage(String pkgname) {
		Package p = lookupPackage(pkgname);
		if (p == null) {
			p = new Package(pkgname);
			vertices.add(p);
		}
		return p;
	}
	
	private void addPackageEntry(String entry) {
		String[] pair = entry.split(": ");
		assert pair.length == 1 || pair.length == 2;
		Package first = addPackage(pair[0]);
		if (pair.length == 1) {
			return;
		}
		Package second = addPackage(pair[1]);
		first.linkPackageDependency(second);
		second.outgoingnodes.add(first);
	}
	
	/**
	 * Uses a DFS to find a linear ordering of vertices (i.e., packages) such that
	 * for all edges contained in the graph's edges set, any dependency of a given package 
	 * precedes that package in the order of installation. 
	 *  
	 *  Loads list of package install order into pkginstallorder class member.
	 */
	private void topologicalSort() {
		Queue<Package> queue = new LinkedList<Package>();
		for (Package p : vertices) {
			if (p.prevNode == null) { //Add packages without dependencies first
				queue.add(p);
			}
		}
		while (!queue.isEmpty()) {
			Package currpkg = queue.remove();
			currpkg.visited = true;
			pkginstallorder.add(currpkg);
			for (Package next: currpkg.outgoingnodes) {
				if (next != null && next.visited) { //Found cycle, terminate early
					isCyclic = true; 
					return;
				}
				else if (next != null && !next.visited) {
					next.prevNode = null; //Remove edges between current and all packages requiring current as a dependency
					next.visited = true;
					queue.add(next); 
				}
			}		
		}
		if (pkginstallorder.size() < vertices.size()) { //If less than the total number of nodes were traversed, graph is cyclic
			isCyclic = true;
			//If graph contains a cycle, queue will eventually become empty (while loop will terminate)
		}
	}
	
	public String getPackageInstallStringOutput() {
		String s = "";
		if (pkginstallorder.size() == 0) {
			return s;
		}
		else if (pkginstallorder.size() == 1) {
			return pkginstallorder.get(0).name;
		}
		for (Package p: pkginstallorder) {
			s += p.name + ", ";
		}
		return s.substring(0, s.length()-2);
	}
	
	public ArrayList<Package> getVertices() {
		return vertices;
	}
	
	/**
	 * Checks if a package is already contained in the graph's list of vertices 
	 * to prevent package duplicates.
	 * @param name
	 * @return
	 */
	public Package lookupPackage(String name) {
		for (Package p : vertices) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}

	ArrayList<Package> getPackageInstallOrder() {
		topologicalSort();
		return this.pkginstallorder;
	}
	
	public boolean containsCycle() {
		return isCyclic;
	}

	
	/** 
	 * Inner node class representing each package entity to be installed.
	 * Specifications: 
	 * Any given node has at most one dependency.
	 */
	protected class Package {
		String name;
		boolean visited;
		Package prevNode; //Dependency
		String dependency; //Store dependency in string variable, prevNode may be modified to null during sort.
		List<Package> outgoingnodes; 
		
		private Package(String pkg) {
			name = pkg;
			visited = false;
			prevNode = null;
			dependency = null;
			outgoingnodes = new ArrayList<Package>();
		}
		String getDependencyName() {
			return dependency != null ? dependency : new String("");
		}
		private void linkPackageDependency(Package prev) {
			prevNode = prev;
			dependency = prev.name;
		}
	}
	

	
}
