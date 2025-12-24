package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        if (vector == null) {
        this.vector = new double[0];
        } 
        else {
            this.vector = vector;
        }
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        this.readLock();
        try { 
            // What are we doing if index is out of vector length?
            if( index >= vector.length) {
                throw new IllegalArgumentException("Vector get: index is out of vector length");
            }
            return vector[index]; 
        } 
        finally { this.readUnlock(); }
    }

    public int length() {
        // TODO: return vector length
        this.readLock();
        try { 
            return vector.length;
        }
        finally { this.readUnlock(); }
    }

    public VectorOrientation getOrientation() {
         // TODO: return vector orientation
        this.readLock();
        try { 
            return orientation;
        }
        finally { this.readUnlock(); }
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        lock.writeLock().lock(); 
        try {
            if (this.orientation == VectorOrientation.ROW_MAJOR)
            {
                this.orientation = VectorOrientation.COLUMN_MAJOR;
            } 
            else {
                this.orientation = VectorOrientation.ROW_MAJOR;
            }
        } 
        finally {
            lock.writeLock().unlock();
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        int thisId = System.identityHashCode(this);
        int otherId = System.identityHashCode(other);
        //avoiding deadlock
        if(thisId > otherId) { 
            other.readLock();
            this.writeLock();
        }
        else {
            this.writeLock();
            other.readLock();
        }

        try {
            if (this.vector.length != other.vector.length) 
                throw new IllegalArgumentException("Vector add: dimensions mismatch");
            // if same length but different orientation should we do transpose? Itay said no need
            for (int i = 0; i < vector.length; i++) {
                this.vector[i] += other.vector[i];
            }
        } 
        //avoiding deadlock
        finally {
            if(thisId > otherId) { 
                other.readUnlock();
                this.writeUnlock();
            }
            else {
                this.writeUnlock();
                other.readUnlock();
            }
        }
    }

    public void negate() {
        // TODO: negate vector
        this.writeLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                this.vector[i] = -this.vector[i];
            }
        } 
        finally {
            this.writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        int thisId = System.identityHashCode(this);
        int otherId = System.identityHashCode(other);
        
        if(thisId > otherId) { 
            other.readLock();
            this.readLock();
        }
        else {
            this.readLock();
            other.readLock();
        }

        try {
            if (this.vector.length != other.vector.length)
                throw new IllegalArgumentException("Dot product: dimensions mismatch");
            if (this.orientation == other.orientation) {
                throw new IllegalArgumentException("Dot product: dimensions mismatch");
            }
            double sum = 0;
            for (int i = 0; i < vector.length; i++) {
                sum += this.vector[i] * other.vector[i];
            }
            return sum;
        } 
        finally {
            if(thisId > otherId) { 
                other.readUnlock();
                this.readUnlock();
            }
            else {
                this.readUnlock();
                other.readUnlock();
            }
        }
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        
        this.writeLock();
        try {
            double[][] matrixData = matrix.readRowMajor();
            if (this.vector.length != matrixData.length) {
                throw new IllegalArgumentException("Vector-Matrix multiplication: dimension mismatch");
            }
            if(this.orientation != VectorOrientation.ROW_MAJOR) {
                throw new IllegalArgumentException("Vector-Matrix multiplication: cant multiply column-vector with matrix");
            }

            int numCols = matrixData[0].length;
            double[] result = new double[numCols];

            for (int i = 0; i < this.length(); i++) {
                double scalar = this.vector[i]; 
                double[] currentRow = matrixData[i]; 
            
                for (int j = 0; j < numCols; j++) {
                    result[j] += scalar * currentRow[j];
                }
            }
            this.vector = result;
        } 
        finally {
            this.writeUnlock();
        }
    }
}
