public final class FlacLibrary {

  static {
    ExoPlayerLibraryInfo.registerModule("goog.exo.flac");
  }


  private FlacLibrary() {}

  /**
   * Override the names of the Flac native libraries. If an application wishes to call this method,
   * it must do so before calling any other method defined by this class, and before instantiating
   * any {@link LibflacAudioRenderer} and {@link FlacExtractor} instances.
   *
   * @param libraries The names of the Flac native libraries.
   */
  public static void setLibraries(String... libraries) {
  }

  /** Returns whether the underlying library is available, loading it if necessary. */
  public static boolean isAvailable() {
    return NativeLoader.loaded();
  }
}
