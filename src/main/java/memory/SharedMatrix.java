package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        this.vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        if (matrix == null || matrix.length == 0) {
            this.vectors = new SharedVector[0];
        } 
        else {
            loadRowMajor(matrix);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        //not sure we need this, already chacking in the constructor
        if (matrix == null || matrix.length == 0) {
            this.vectors = new SharedVector[0];
        }
        else {
        SharedVector[] newVectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            newVectors[i] = new SharedVector(matrix[i].clone(), VectorOrientation.ROW_MAJOR);
        }
        this.vectors = newVectors;
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        //not sure we need this, already chacking in the constructor
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            this.vectors = new SharedVector[0];
        }
        else {
            int numRows = matrix.length;
            int numCols = matrix[0].length;
            SharedVector[] newVectors = new SharedVector[numCols];

            for (int j = 0; j < numCols; j++) {
                double[] column = new double[numRows];
                for (int i = 0; i < numRows; i++) {
                    column[i] = matrix[i][j];
                }

                newVectors[j] = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
            }
            this.vectors = newVectors;
        }
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        SharedVector[] currentVecs = this.vectors;
        
        if (currentVecs == null || currentVecs.length == 0) {
            double[][] matrix = new double[0][0];
            return matrix ;
        }
        
        acquireAllVectorReadLocks(currentVecs);
        try {
            VectorOrientation currentOrientation = currentVecs[0].getOrientation();
            if (currentOrientation == VectorOrientation.ROW_MAJOR) {
                int numRows = currentVecs.length;
                int numCols = currentVecs[0].length();

                double[][] matrix = new double[numRows][numCols];
                for(int i = 0; i < numRows; i++) {
                    for(int j = 0; j < numCols; j++) {
                        matrix[i][j] = currentVecs[i].get(j);
                    }
                }
                return matrix;  
            }
            else {
                int numCols = currentVecs.length;
                int numRows = currentVecs[0].length();

                double[][] matrix = new double[numRows][numCols];
                for(int i = 0; i < numRows; i++) {
                    for(int j = 0; j < numCols; j++) {
                        matrix[i][j] = currentVecs[j].get(i);
                    }
                }
                return matrix;  
            }    
        }
        finally {
            releaseAllVectorReadLocks(currentVecs);
        }      
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        if (index < 0 || index >= vectors.length) {
            throw new IllegalArgumentException("Matrix get: Matrix index out of bounds" );
        }

        return this.vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return this.vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        // if length is zero; we can choose orientation as we wish
        if (vectors.length == 0) return VectorOrientation.ROW_MAJOR;
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        int len = vecs.length;
        for(int i = 0; i < len; i++) {
            if(vecs[i] != null) vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        int len = vecs.length;
        for(int i = len - 1; i >= 0; i--) {
            if(vecs[i] != null) vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        int len = vecs.length;
        for(int i = 0; i < len; i++) {
            if(vecs[i] != null) vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        int len = vecs.length;
        for(int i = len - 1; i >= 0; i--) {
            if(vecs[i] != null) vecs[i].writeUnlock();
        }
    }
}
