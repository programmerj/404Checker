package com.example.recursion;

public class Recursion {
	public static void main(String[] args) {
		recurse(5);
	}

	private static void recurse(int i) {
		if (i > 0) {
			System.out.println(i);
			recurse(i - 1);
			System.out.println(i);
		}
	}
}
