package ch.sbs.utils.intervaltree;

/*
 * stolen from 
 * http://git.hashcollision.org/projects/interval_trees/src/org/arabidopsis/interval/
 * and simplified.
 */
/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

public class RbNode {
	public int key;
	public boolean color;
	public RbNode parent;
	public RbNode left;
	public RbNode right;

	public static boolean BLACK = false;
	public static boolean RED = true;

	private RbNode() {
		// Default constructor is only meant to be used for the
		// construction of the NIL node.
	}

	public RbNode(int theKey) {
		parent = NIL;
		left = NIL;
		right = NIL;
		key = theKey;
		color = RED;
	}

	static RbNode NIL;
	static {
		NIL = new RbNode();
		NIL.color = BLACK;
		NIL.parent = NIL;
		NIL.left = NIL;
		NIL.right = NIL;
	}

	public boolean isNull() {
		return this == NIL;
	}

	@Override
	public String toString() {
		if (this == NIL) {
			return "nil";
		}
		return "(" + key + " " + (color == RED ? "RED" : "BLACK") + " ("
				+ left.toString() + ", " + right.toString() + ")";
	}
}
