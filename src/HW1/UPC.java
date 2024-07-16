package HW1;

// This is the starting version of the UPC-A scanner
//   that needs to be filled in for the homework

public class UPC {
	
	//--------------------------------------------
	// Scan in the bit pattern from the image
	// Takes the filename of the image
	// Returns an int array of the 95 scanned bits
	//--------------------------------------------
	public static int[] scanImage(String filename) {
		
		// YOUR CODE HERE....
		
		DUImage barCodeImage = new DUImage(filename);
	    int[] bits = extractBits(barCodeImage);
	    
	    return bits;
	}

	static DUImage initializeBarcodeImage(String fileName) {
	    return new DUImage(fileName);
	}

	static int[] extractBits(DUImage barCodeImage) {
	    int[] bits = new int[95];
	    int j = 5;
	    
	    for (int i = 0; i < bits.length; i++) {
	        int pixValue = getPixel(barCodeImage, j);
	        
	        bits[i] = bitToPix(pixValue);
	        
	        j = updatePosition(j);
	    }

	    return bits;
	}

	static int getPixel(DUImage image, int position) {
	    return image.getGreen(position, 50);
	}

	static int bitToPix(int pixelValue) {
	    if (pixelValue == 0) {
	        return 1;
	    } else if (pixelValue == 255) {
	        return 0;
	    }
	    return -1;
	}

	static int updatePosition(int currentPosition) {
	    return currentPosition + 2;
	}
	    
		
	
	//--------------------------------------------
	// Finds the matching digit for the given pattern
	// This is a helper method for decodeScan
	// Takes the full 95 scanned pattern as well as
	//   a starting location in that pattern where we
	//   want to look
	// Also takes in a boolean to indicate if this is a
	//   left or right pattern
	// Returns an int indicating which digit matches
	//   Any pattern that doesn't match anything will be -1
	//--------------------------------------------
	public static int matchPattern(int[] scanPattern, int startIndex, boolean left) {
		
		int[][] digitPat = {{0,0,0,1,1,0,1},
				            {0,0,1,1,0,0,1},	
				            {0,0,1,0,0,1,1},
				            {0,1,1,1,1,0,1},
				            {0,1,0,0,0,1,1},
				            {0,1,1,0,0,0,1},
				            {0,1,0,1,1,1,1},
				            {0,1,1,1,0,1,1},
				            {0,1,1,0,1,1,1},
				            {0,0,0,1,0,1,1}};
		
		// YOUR CODE HERE....
		for (int i = 0; i < digitPat.length; i++) {
		    if (match(digitPat[i], scanPattern, startIndex, left)) {
		        return i; 
		    }
		}
		return -1; 

		}

		static boolean match(int[] pattern, int[] scanPattern, int startIndex, boolean left) {
		    for (int j = 0; j < pattern.length; j++) {
		        int expectedBit = expected(pattern[j], left);
		        if (scanPattern[startIndex + j] != expectedBit) {
		            return false;
		        }
		    }
		    return true;
		}

		static int expected(int bit, boolean left) {
		    return left ? bit : 1 - bit;
		}
	
	//--------------------------------------------
	// Performs a full scan decode that turns all 95 bits
	//   into 12 digits
	// Takes the full 95 bit scanned pattern
	// Returns an int array of 12 digits
	//   If any digit scanned incorrectly it is returned as a -1
	// If the start, middle, or end patterns are incorrect
	//   it provides an error and exits
	//--------------------------------------------
	public static int[] decodeScan(int[] scanPattern) {
	    int[] digits = new int[12];

		// YOUR CODE HERE...
	    if(!(scanPattern[0] == 1 && scanPattern[1] == 0 && scanPattern[2] == 1 && 
	            scanPattern[45] == 0 && scanPattern[46] == 1 && scanPattern[47] == 0 && scanPattern[48] == 1 && 
	            scanPattern[92] == 1 && scanPattern[93] == 0 && scanPattern[94] == 1)) {
	           System.out.println("Error in start, middle or end patterns");
	           System.exit(1);
	       }
	    for(int i=3, j=0; i<45 && j<6; i+=7, j++) {
	        digits[j] = matchPattern(scanPattern, i , true);
	    }
	    for(int i=50, j=6; i<92 && j<12; i+=7, j++) {
	        digits[j] = matchPattern(scanPattern, i , false);
	    }
	    
	    return digits;
	}
	
	//--------------------------------------------
	// Do the checksum of the digits here
	// All digits are assumed to be in range 0..9
	// Returns true if check digit is correct and false otherwise
	//--------------------------------------------
	public static boolean verifyCode(int[] digits) {
	    
	    // In the UPC-A system, the check digit is calculated as follows:
	    // 1. Add the digits in the even-numbered positions (zeroth, second, fourth, sixth, etc.) together and multiply by three.
	    // 2. Add the digits in the even-numbered positions (first, third, fifth, etc.) to the result.
	    // 3. Find the result modulo 10 (i.e. the remainder when divided by 10).
	    // 4. If the result is not zero, subtract the result from ten.
	    
	    // Note that what the UPC standard calls 'odd' are our evens since we are zero-based and they are one-based
	    
	    if (digits.length != 12) {
	        throw new IllegalArgumentException("Expected digits to be of length 12");
	    }
	    
	    int checkDigit = 0;
	    int evens = 0;
	    int odds = 0;
	    
	    for (int i = 0; i < 11; i++) {
	        if (i % 2 == 0) {
	            evens += digits[i];
	        } else {
	            odds += digits[i];
	        }
	    }
	    
	    int checkSum = (3 * evens + odds) % 10;
	    
	    if (checkSum != 0) {
	        checkDigit = 10 - checkSum;
	    }
	    
	    return checkDigit == digits[11];
	}

	//--------------------------------------------
	// The main method scans the image, decodes it,
	//   and then validates it
	//--------------------------------------------	
	public static void main(String[] args) {
	        // file name to process.
	        // Note: change this to other files for testing
	        String barcodeFileName = "barcode1.png";

	        // optionally get file name from command-line args
	        if(args.length == 1){
		    barcodeFileName = args[0];
		}
		
		// scanPattern is an array of 95 ints (0..1)
		int[] scanPattern = scanImage(barcodeFileName);

		// Display the bit pattern scanned from the image
		System.out.println("Original scan");
		for (int i=0; i<scanPattern.length; i++) {
			System.out.print(scanPattern[i]);
		}
		System.out.println(""); // the \n
				
		
		// digits is an array of 12 ints (0..9)
		int[] digits = decodeScan(scanPattern);
		
		// YOUR CODE HERE TO HANDLE UPSIDE-DOWN SCANS
		if (digits[0] == -1) {
		    int[] reverse = new int[95];
		    for (int i = 0, b = 94; i < 95; i++, b--) {
		        reverse[b] = scanPattern[i];
		    }
		    digits = decodeScan(reverse);
		}		
		
		// Display the digits and check for scan errors
		boolean scanError = false;
		System.out.println("Digits");
		for (int i=0; i<12; i++) {
			System.out.print(digits[i] + " ");
			if (digits[i] == -1) {
				scanError = true;
			}
		}
		System.out.println("");
				
		if (scanError) {
			System.out.println("Scan error");
			
		} else { // Scanned in correctly - look at checksum
		
			if (verifyCode(digits)) {
				System.out.println("Passed Checksum");
			} else {
				System.out.println("Failed Checksum");
			}
		}
	}
}

