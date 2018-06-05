## Instructions
### 1. On Pi side, follow the instructions from this resource [Start Video Streaming on Pi](https://raspberrypi.stackexchange.com/questions/23182/how-to-stream-video-from-raspberry-pi-camera-and-watch-it-live/23205#23205)
### 2. Install VLC on your server. Open Terminal, navigate to the VLC directory.
### 3. Run the command below to receive coming streaming video and split into frames (--scene-ratio sets to 1 is the maximum, approximately 25 f/sec if it is Pi Camera).
vlc.exe rtsp://IP-ADDRESS:8554/ --video-filter scene --scene-format jpg --scene-prefix test --scene-path C:\Users\wenbo\Desktop\frame --no-scene-replace --scene-ratio 1
### 4. Configure config/config.properties
### 5. Start Java main.

## Java Program Logic:

1. The Main.java reads config.properties and stores it as a HashMap.

2. A BlockingQueue with QUEUE_CAP would be created. Threads would be created based on THREAD value.

3. Producer.java lists files in TARGET_IMAGE_DIR, does valid file name checking (avoid image.jpg.swp), put valid file names in the Queue.

4. Treads(Consumer.java) take the images name from the Queue, call Processor.java.

5. Processor.java creates FileInputStream, and call Watson (ImageClassification.java) for classification.

6. After classifying existing files, a Java WatchService monitons the directory.

7. If a new image created by Instructions 3, the WatchService valid file name and put the valid image file names into the Queue.

8. Treads(Consumer.java) take the images name from the Queue, call Processor.java.

9. Processor.java creates FileInputStream, and call Watson (ImageClassification.java) for classification.

10. The result could be formated to print out Classifiers only. Later could be integrated with Notification services (e.g. Twillio).