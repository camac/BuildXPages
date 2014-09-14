package com.gregorbyte.buildxpages;

public class TestNoteID {

	public static void main(String[] args) {
		
		int 	NOTE_ID_SPECIAL = 0xFFFF0000;
		int 	NOTE_CLASS_ICON = 0x00000010;
		
		long test1 = 4294901760L;
		long test2 = 16L;
		
		int result = (NOTE_ID_SPECIAL | NOTE_CLASS_ICON);				
		System.out.println(result);
		
		long result2 = (test1 | test2);
		System.out.println(result2);

	}
	
}
