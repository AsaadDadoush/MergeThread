import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * @(#)MergeThread.java
 * the main function for this class is to
 * to implemt the thread concept by sorting an arrays
 * using merge sort
 * @author Asaad W. Daadouch
 * @version 1.0 4/10/2022.
 * {@link: https://github.com/AsaadDadoush/MergeThread.git}
 */
public class MergeThread implements Runnable
{
	static int[] A;									// Global Array Variable
    int start, end;									// Start and end indices used by each thread
	
    /** 
     * Constructor. Sets start and end global variables.
     * @param start is the start index of a sublist.
     * @param end is the end index of a sublist.
     * */
	public MergeThread(int start, int end) 
	{
		this.start = start;							// Start index of a sub-list
		this.end = end;								// End index of a sub-list
	}
	
	/** 
	 * MultiMerge_Sort method. Responsible for MergeSort process using a priority queue.
	 * This method will merge the (k) sorted sublists into one final sorted list and store it in array (B)
     * @param k is the number of sublists.
     * @param N is the size of the original array (A), or simply the total number of elements.
     * @param indices is an array that stores entities in the form of [start index, end index] for each sublist.
     * @param B is the array that will hold the final sorted list.
	 * */
	public static void MultiMerge_Sort(int k, int N, int[][] indices, int[] B) {
		/* Initialize a priority queue */
		PriorityQueue<int[]> pq = new PriorityQueue<int[]>(new PQ());
		
		/* remove the smallest element from each sublist and append it to the queue */
		for(int i = 0; i<k; i++)
		{
			int[] temp = new int[2];
			temp[0] = A[indices[i][0]]; 	// temp[0] is value
			temp[1] = i;					// temp[1] is the sub-list it came from
			pq.add(temp);					// Add the entity [value, location] to the priority queue
			indices[i][0] ++;				// increment the (start) index of that sub-list, since we took an item from it
		}
		
		/* Start adding values from A into pq and then from pq into B (sorting) */
		int i = 0;						// counter
		int pos;						// stores position
		int val;						// stores value
		do {
			int[] temp = pq.remove();	// Remove one [value, position] entity from the priority queue
										// Where (value) is the smallest in the queue.
			val = temp[0];				// The value to be inserted in B
			pos = temp[1];				// The sub-list from which the value was taken
			B[i] = val;					// Insert the value into B
			i++;						// Increment the counter
			
			if(indices[temp[1]][0] <= indices[temp[1]][1]) 		
			{	// If the sub-list isn't empty yet (start index is smaller or equal to end index)
				val = A[indices[pos][0]];				// The value to be removed from (A) and added to pq
				temp[0] = val;							// Update the [value, position] entity
				pq.add(temp);							// Add the new item into priority queue
				indices[temp[1]][0]++;					// Increment the (start) index.
			}
		}while(i<N);									// Keep looping until all (A) is sorted into (B)

	}
	
	@Override
	// Runs a thread, and then this thread will start sorting range [start, end] of array A.
	public void run() 
	{
		System.out.printf("THREAD #%d: sort the range {%d, %d} \n",
				Thread.currentThread().getId(), this.start, this.end);			// Print message
		// Sort the sub-list
		Arrays.sort(A, this.start, this.end + 1); 								// The +1 because it is exclusive
	}
	
	/** 
	 * The main method. Responsible for initialization, threads creation, and showing outputs to the user.
	 * This method will sort the values of (k) sorted sublists into one final sorted array (B)
     * @param args Command line arguments. Two must be passed: 
     * args[0] is N, it means the number of elements in the list to be sorted. 
     * args[1] is k, it means the number of threads used in sorting.
	 * */
	public static void main(String[] args) 
	{	
		/* Command line arguments */
		int N = 24;									// List size
		int k = 6; 									// Number of sub-lists (Number of threads)
		if(args.length < 2)
		{	/* Not enough arguments */
			System.out.println("ERROR: You didn't enter enough arguments. Please run the program again and pass 2 numeric values.");
			System.exit(0);
		}
		else 
		{	/* Enough arguments passed */
			try 
			{	/* Receive first and second inputs from the command line */
				N = Integer.parseInt(args[0]);
				k = Integer.parseInt(args[1]);
				if (N % k != 0)
				{		/* Check if N is divisible by k */
						System.out.println("ERROR: The size (N) must be divisible by (k)"+ "\nPlease try again.");
						System.exit(0);
				}
			}
			catch (NumberFormatException e) 
			{	/* Arguments were non-numeric */
			    System.err.println("ERROR: Failed trying to parse a non-numeric argument!");
			    System.exit(0);
			}
		}
		
		
		/* Starting message */
		System.out.println("MAIN: Program is starting!");
		
		/* Initializing */
		int p = N/k;								// Sub-list size
		A = new int[N];								// Initialize A
		int[] B = new int[N];						// Initialize B
		Random rnd = new Random(); 					// Random number generator
		ArrayList<Thread> sortThreads = new ArrayList<>(); // Create an ArrayList to store all the threads
		int[][] indices = new int[k][2];			// create k sets of {starting, ending} indices of A.

		
		/* Insert random numbers to the list between [0, 10*N] */
		for(int i = 0; i<N; i++) A[i] = rnd.nextInt(10 * N + 1);
		
		/* Print random generated array */
		System.out.println("MAIN: Original array A before sorting");
		for(int i = 0; i<N; i++) 
		{
			if(i%p == 0 && i!=0) System.out.print("\n");
			System.out.printf("%d  ",A[i]);
		}
		System.out.println("\n\n");
		

		/* Specify every start and end indices for every thread */
		for(int i = 0; i<k; i++) 
		{
			indices[i][0] = i * p;					// Start index
			indices[i][1] = (p * i) + (p - 1);		// End index
		}

		/* Start Timer */
		long startTime = System.currentTimeMillis();

		/* Create the threads and start them */
		for(int i = 0; i<k; i++) 
		{
			// Create and start the threads
			Runnable r = new MergeThread(indices[i][0], indices[i][1]);
			sortThreads.add(new Thread(r));
			System.out.printf("MAIN: A new thread is created with ID: %d, with indices: {%d, %d}\n", sortThreads.get(i).getId(), indices[i][0], indices[i][1]);
			sortThreads.get(i).start();
		}

		
		/* Wait for all threads to finish */
		for(int i = 0; i<k; i++) 
		{
			try {sortThreads.get(i).join();} 
			catch (InterruptedException e) { System.out.println("ERROR: A thread inturrept error occured!"); e.printStackTrace(); }
		}
		
		
		/* Print parallel-sorted array */
		System.out.println("\n\nMAINL All threads are done sorting! Array A became as follwing");
		for(int i = 0; i<N; i++) 
		{
			if(i%p == 0 && i!=0) System.out.print("\n");
			System.out.printf("%d  ",A[i]);
		}
		System.out.println();
		
		System.out.println("\n\nMAIN: Now, multimerge is starting");
		
		/* merge-sort A into B */		
		MultiMerge_Sort(k, N, indices, B);
		
		/* Stop the timer */
		long endTime = System.currentTimeMillis();
		
		long T = endTime - startTime;
		
		/* Show output */
		System.out.println("MAIN Merge-Sort is done, Array B became as following");
		for(int i = 0; i<N; i++) 
		{
			if(i%p == 0 && i!=0) System.out.print("\n");
			System.out.printf("%d  ",B[i]);
		}
		System.out.println();
		
		/* Needs to comment all print operations to calculate accurate timing. */
		System.out.printf("\n\nMAIN: The sorting took %d millie-seconds\n", T);
		
		System.out.println("Done!\n");
	}
}

/** 
 * A comparator to be used to compare entities in the form of [value, position] based on their value.
 * */
class PQ implements Comparator<int[]>{
	@Override
	public int compare(int[] o1, int[] o2) {
		if(o1[0] > o2[0]) return 1;
		else if(o1[0] < o2[0]) return -1;
		else return 0;
	}
}
