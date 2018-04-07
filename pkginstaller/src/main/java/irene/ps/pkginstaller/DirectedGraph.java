package irene.ps.pkginstaller;

import java.util.ArrayList;

/**
 * Directed Graph class that represents individual packages to be installed
 * as graph vertices, with package dependencies represented as 
 * graph edges.
 *
 */
public class DirectedGraph {
	private ArrayList<Package> vertices;
	
	DirectedGraph(String [] pkglist){
	
	}
	
	/* 
	 * Inner node class representing each package entity to be installed.
	 * Specifications: 
	 * -Any given node has at most one dependency.
	 */
	private class Package {
	
	}
}
