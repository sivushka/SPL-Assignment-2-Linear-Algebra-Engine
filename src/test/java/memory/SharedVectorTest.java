package memory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SharedVectorTest {

    private String arrayToString(double[] arr) {
        if (arr == null) return "null";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]).append(i == arr.length - 1 ? "" : ", ");
        }
        return sb.append("]").toString();
    }

    private void printTestHeader(String testName) {
        System.out.println("\n=== Running Test: " + testName + " ===");
    }

    @Test
    @DisplayName("Basic Operations: Constructor, Length, Get")
    void testBasicMethods() {
        printTestHeader("Basic Methods");
        double[] input = {1.5, 2.5, 3.5};
        System.out.println("Input Data: " + arrayToString(input));
        
        SharedVector v = new SharedVector(input, VectorOrientation.ROW_MAJOR);
        
        System.out.println("Expected Length: 3 | Actual: " + v.length());
        System.out.println("Expected Value at index 1: 2.5 | Actual: " + v.get(1));
        
        assertEquals(3, v.length());
        assertEquals(2.5, v.get(1), 0.0001);
    }

    @Test
    @DisplayName("Transpose: Switching Orientations")
    void testTranspose() {
        printTestHeader("Transpose Logic");
        SharedVector v = new SharedVector(new double[]{1, 1}, VectorOrientation.ROW_MAJOR);
        
        System.out.println("Initial Orientation: " + v.getOrientation());
        
        v.transpose();
        System.out.println("After 1st Transpose: " + v.getOrientation() + " (Expected: COLUMN_MAJOR)");
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
        
        v.transpose();
        System.out.println("After 2nd Transpose: " + v.getOrientation() + " (Expected: ROW_MAJOR)");
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    @DisplayName("Vector Addition: Normal and Edge Cases")
    void testAddComprehensive() {
        printTestHeader("Vector Addition");
        
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{10, 20}, VectorOrientation.ROW_MAJOR);
        System.out.println("Adding " + arrayToString(new double[]{1,2}) + " and " + arrayToString(new double[]{10,20}));
        
        v1.add(v2);
        double[] result = {v1.get(0), v1.get(1)};
        System.out.println("Result: " + arrayToString(result) + " (Expected: [11.0, 22.0])");
        assertArrayEquals(new double[]{11.0, 22.0}, result, 0.0001);

        System.out.println("Testing Mismatched Lengths (Edge Case)...");
        SharedVector shortVec = new SharedVector(new double[]{1}, VectorOrientation.ROW_MAJOR);
        SharedVector longVec = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        
        assertThrows(IllegalArgumentException.class, () -> {
            shortVec.add(longVec);
        }, "Should throw IllegalArgumentException when adding mismatched dimensions");
        
        System.out.println("Caught expected IllegalArgumentException for mismatched lengths.");
    }

    @Test
    @DisplayName("Negate: Checking signs")
    void testNegate() {
        printTestHeader("Negate Operation");
        double[] input = {5.0, -3.0, 0.0};
        SharedVector v = new SharedVector(input, VectorOrientation.ROW_MAJOR);
        System.out.println("Input: " + arrayToString(input));
        
        v.negate();
        double[] result = {v.get(0), v.get(1), v.get(2)};
        System.out.println("Result: " + arrayToString(result) + " (Expected: [-5.0, 3.0, 0.0])");
        
        assertArrayEquals(new double[]{-5.0, 3.0, 0.0}, result, 0.0001);
    }

    @Test
    @DisplayName("Dot Product: Basic Check")
    void testDotProduct() {
        printTestHeader("Dot Product");
        SharedVector v1 = new SharedVector(new double[]{1, 2, 3}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{4, 5, 6}, VectorOrientation.COLUMN_MAJOR);
        
        System.out.println("V1: [1,2,3], V2: [4,5,6]");
        double expected = (1*4) + (2*5) + (3*6); // 32.0
        double actual = v1.dot(v2);
        
        System.out.println("Expected: " + expected + " | Actual: " + actual);
        assertEquals(expected, actual, 0.0001);
    }

        
    @Test
    @DisplayName("Matrix-Vector Multiplication (vecMatMul)")
    void testVecMatMul() {
        printTestHeader("Vector-Matrix Multiplication");
        
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        
        double[][] matrixData = {{1, 0}, {0, 1}};
        SharedMatrix matrix = new SharedMatrix(matrixData);
        
        System.out.println("Vector: [1, 2] times Identity Matrix 2x2");
        
        v.vecMatMul(matrix);
        
        double[] result = {v.get(0), v.get(1)};
        System.out.println("Result: " + arrayToString(result) + " (Expected: [1.0, 2.0])");
        assertArrayEquals(new double[]{1.0, 2.0}, result, 0.0001);
    }
        
}
 
            
            