package gov.nasa.jpf.symbc.realtime.rtsm.util;

public class BoundedBuffer {
	private int[] buf;
	private int in = 0;
	private int out = 0;
	private int count = 0;
	private int size = 16;
	private Object countLock;

	public BoundedBuffer() {
		buf = new int[this.size];
	}

	public void enqueue(int element) {
		buf[in] = element;
		synchronized (countLock) {
			++count;
		}
		
		in = (in + 1) & 15;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public int peek() {
		return buf[out];
	}

	public void dequeue() {
		synchronized (countLock) {
			--count;
		}
		out = (out + 1) & 15;
	}
}
