### Instructions
#### 1. On Pi side, follow the instructions from this resource [Start Video Streaming on Pi](https://raspberrypi.stackexchange.com/questions/23182/how-to-stream-video-from-raspberry-pi-camera-and-watch-it-live/23205#23205)
#### 2. Install VLC on your server. Open Terminal, navigate to the VLC directory.
#### 3. Run the command below to receive coming streaming video and split into frames (--scene-ratio sets to 1 is the maximum, approximately 25 f/sec if it is Pi Camera).
vlc.exe rtsp://IP-ADDRESS:8554/ --video-filter scene --scene-format jpg --scene-prefix test --scene-path C:\Users\wenbo\Desktop\frame --no-scene-replace --scene-ratio 1
#### 4. Configure config/config.properties
#### 5. Start Java main.

### Program Logic:

1. Main.java creates Controller and runs it.

2. Controller.java creates a synchronizedList which for holding the names of generated images from Instruction 3.
and starts a Producer thread and Consumer thread.

3. Producer.java loads the existing files in TARGET_IMAGE_DIR and put them into the synchronizedList.

4. Producer.java watches TARGET_IMAGE_DIR; If a new image generated, it put the image name into the synchronizedList.

5. Consumer.java creates and starts a FixedSizeThreadPool based on the number of threads configured in config.properties.

6. Once all threads are available, Consumer.java checks the synchronizedList size, divides the size into 

   N (N=Number_Of_Threads)parts. It generates a random number based on the index of the current part.
   
   Then runs a thread of Processor.java with the file name get from the synchronizedList's random index.
   
   Then it clears the synchronizedList and removes all image files based on the synchronizedList values.
   
7. Processor.java calls Watson Visual Recognition service to classify images, and parses the response.

8. The response could be formated to print out Classifiers only. Later could be integrated with Notification services (e.g. Twillio).