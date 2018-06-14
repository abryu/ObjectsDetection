### Instructions
 + Configure config/config.properties
 + Start Java main.

### Program Logic:

+ Main.java 
  + Creates and runs Controller.java
+ Controller.java
  + Check Pi to see if it's running video streaming. If not, start video streaming on Pi.
  + Open a local VLC channel to capture video streaming from Pi and split video into frames.
  + Creates a synchronizedList that shared by Producer and Consumer
    + The synchronizedList is for storing image file names (generated from Instruction 3)
  + Creates and runs Producer.java in a ThreadPool (one thread)
  + Creates and runs Consumer.java in a ThreadPool (one thread)
+ Producer.java
  + Loads existing images files in TARGET_IMAGE_DIR; stores the file names into the synchronizedList
  + Creates and runs a WatchService which monitors the TARGET_IMAGE_DIR; If a new image file generated, put the file name into the synchronizedList
+ Consumer.java
  + Creates a FixedSizeThreadPool for performing Classification task
    + The size based on the configured property in config.properties
    + A List<Future<Integer>> for tracking the status of threads (available or not)
  + Creates a FixedSizeThreadPool for removing unneeded images
  + A while loop always running:
    + Check the status of threads.
        + If all threads are available, start processing the list
        + Firstly, get the synchronizedList's size
        + Secondly, divide the size into N (N=number of threads) parts
        + Thirdly, assigning index ranges for generating random index numbers (e.g. TotalSize = 75, N = 3, Part 1: 0 - 24, Part 2: 25-49, Part 3 : 50-74)
        + Fourthly, get file name from the synchronizedList with the random index, start a new thread for classification with that index value (name)
        + Once threads triggered, clear the list from index 0 to TotalSize.
        + Lastly, remove image files based on the list(0,TotalSize)
+ Processor.java
  + Uses Watson Visual Recognition API, send InputStream and receives responses
  + The responses could be formatted and integrated with notification services later 