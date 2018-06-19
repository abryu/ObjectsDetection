package objectsdetection.videostreamingsplitting;

public interface I_VideoStreamingAndSplitting {

  String generateCommand();

  void streamingVideo();

  void killSteamingVideo();

}
