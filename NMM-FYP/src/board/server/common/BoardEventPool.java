package board.server.common;

import java.util.ArrayList;
import java.util.List;

public class BoardEventPool<T> {

	private final List<T> eventPool;
	private int tail;
	private int head;
	private int count;

	public BoardEventPool(int count) {
		this.eventPool = new ArrayList<T>(count);
		this.head = 0;
		this.tail = 0;
		this.count = 0;
	}

	public synchronized void put(T event) throws InterruptedException {
		while (count > eventPool.size()) {
			wait();
		}
		eventPool.add(tail, event);
		tail = (tail + 1) % eventPool.size();
		count++;
		notifyAll();
	}

	public synchronized T get() throws InterruptedException {
		while (count <= 0) {
			wait();
		}
		T event = eventPool.get(head);
		head = (head + 1) % eventPool.size();
		count--;
		notifyAll();
		return event;
	}

}
