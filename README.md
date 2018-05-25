##Instructions
### 1. On Pi side, follow the instructions from this resource [Start Video Streaming on Pi](https://raspberrypi.stackexchange.com/questions/23182/how-to-stream-video-from-raspberry-pi-camera-and-watch-it-live/23205#23205)
### 2. Install VLC on your server. Open CMD, navigate to the VLC directory.
### 3. Run 
vlc.exe rtsp://IP-ADDRESS:8554/ --video-filter scene --scene-format jpg --scene-prefix test --scene-path C:\Users\wenbo\Desktop\frame --no-scene-replace --scene-ratio 1
### 4. Configure config/config.properties
### 5. Start Java main




vlc rtsp://192.168.2.56:8554/ --video-filter scene --scene-format jpg --scene-prefix test --scene-path C:\Users\wenbo\Desktop\objectsdetection\Sample --no-scene-replace --scene-ratio 150
