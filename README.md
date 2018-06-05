### Instructions
#### 1. On Pi side, follow the instructions from this resource [Start Video Streaming on Pi](https://raspberrypi.stackexchange.com/questions/23182/how-to-stream-video-from-raspberry-pi-camera-and-watch-it-live/23205#23205)
#### 2. Install VLC on your server. Open Terminal, navigate to the VLC directory.
#### 3. Run the command below to receive coming streaming video and split into frames (--scene-ratio sets to 1 is the maximum, approximately 25 f/sec if it is Pi Camera).
vlc.exe rtsp://IP-ADDRESS:8554/ --video-filter scene --scene-format jpg --scene-prefix test --scene-path C:\Users\wenbo\Desktop\frame --no-scene-replace --scene-ratio 1
#### 4. Configure config/config.properties
#### 5. Start Java main.

### Program Logic:

1. Main.java creates and runs Controller.java.

2. Controller.java creates a synchronizedList which for holding the names of generated images from Instruction 3;
and starts a Producer thread and Consumer thread.

3. Producer.java loads the existing files in TARGET_IMAGE_DIR and put them into the synchronizedList.

4. Producer.java watches TARGET_IMAGE_DIR; If a new image generated, it put the image name into the synchronizedList.

5. Consumer.java creates and starts a FixedSizeThreadPool based on the number of threads configured in config.properties.

6. Once all threads are available, Consumer.java checks the synchronizedList size, divides the size into 

   N (N=Number_Of_Threads)parts. Inside the FOR loop, tt generates a random number based on the index of the current part.
   
   Then runs a thread of Processor.java with the file name from the synchronizedList's random number of index.
   
   Then it clears the synchronizedList and removes all image files based on the synchronizedList's elements.
   
7. Processor.java calls Watson Visual Recognition service to classify images, and parses the response.

8. The response could be formatted to print out Classifiers only. Later could be integrated with Notification services (e.g. Twillio).


+ Main.java 
  + Creates and runs Controller.java
+ Controller.java
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
        + Thirdly, assigning ranges for generating random index number (e.g. TotalSize = 75, N = 3, Part 1: 0 - 24, Part 2: 25-49, Part 3 : 50-74)
        + Fourthly, get file name from the synchronizedList with the random index, start a new thread for classification with that index value (name)
        + Once threads triggered, clear the list from index 0 to TotalSize.
        + Lastly, remove image files based on the list(0,TotalSize)
+ Processor.java
  + Uses Watson Visual Recognition API, send InputStream and receives responses
  + The responses could be formatted and integrated with notification services later 