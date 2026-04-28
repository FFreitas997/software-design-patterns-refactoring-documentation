/**
 * Adapter Pattern (Object Adapter)
 *
 * Intent: Convert the interface of a class into another interface clients expect.
 *         Adapter lets classes work together that couldn't otherwise because of
 *         incompatible interfaces.
 *
 * Example: A media player that plays MP3 files wants to also play MP4 and VLC
 *          formats. Instead of changing the existing player or the third-party
 *          codec classes, we create Adapter classes.
 *
 * Roles:
 *   Target   -> MediaPlayer (interface the client expects)
 *   Adaptee  -> Mp4Player, VlcPlayer (existing classes with different interface)
 *   Adapter  -> Mp4Adapter, VlcAdapter (wrap Adaptees to match Target)
 *   Client   -> AudioPlayer (uses MediaPlayer interface)
 */
public class Adapter {

    // =========================================================================
    // Target Interface
    // =========================================================================

    /**
     * The interface that Client (AudioPlayer) expects all media players to have.
     */
    interface MediaPlayer {
        void play(String fileName);
        String getSupportedFormat();
    }

    // =========================================================================
    // Adaptees (existing third-party or legacy classes with different interfaces)
    // =========================================================================

    /**
     * An existing class that can play MP4 files.
     * It has a different method signature than MediaPlayer.
     */
    static class Mp4Player {
        /** Plays an MP4 video file. Note the different method name. */
        public void playMp4(String fileName) {
            System.out.println("[Mp4Player] Playing MP4 file: " + fileName
                    + " (H.264 video, AAC audio)");
        }

        public String getVersion() { return "Mp4Player v3.2"; }
    }

    /**
     * An existing class that can play VLC files.
     * Also has a different interface.
     */
    static class VlcPlayer {
        /** Plays a VLC/Ogg file. Different method name and signature. */
        public void playVlc(String fileName, boolean hardware) {
            System.out.println("[VlcPlayer] Playing VLC file: " + fileName
                    + " (hardware accel: " + hardware + ")");
        }

        public String getCodecInfo() { return "VlcPlayer - supports OGG, OGV, OGM"; }
    }

    /**
     * A streaming service client with yet another interface.
     */
    static class StreamingService {
        private final String serviceUrl;

        StreamingService(String serviceUrl) { this.serviceUrl = serviceUrl; }

        public void streamRemote(String resourceId, int qualityKbps) {
            System.out.println("[Streaming] Streaming '" + resourceId
                    + "' from " + serviceUrl + " at " + qualityKbps + " kbps");
        }
    }

    // =========================================================================
    // Adapters — wrap Adaptees to implement the MediaPlayer interface
    // =========================================================================

    /**
     * Adapts Mp4Player to the MediaPlayer interface.
     * Composition-based (Object Adapter): wraps an Mp4Player instance.
     */
    static class Mp4Adapter implements MediaPlayer {
        private final Mp4Player mp4Player;

        Mp4Adapter() {
            this.mp4Player = new Mp4Player();
        }

        /**
         * Translates the MediaPlayer.play() call into Mp4Player.playMp4().
         * The client never knows Mp4Player exists.
         */
        @Override
        public void play(String fileName) {
            if (!fileName.toLowerCase().endsWith(".mp4")) {
                System.out.println("[Mp4Adapter] Warning: file '" + fileName + "' is not MP4.");
            }
            mp4Player.playMp4(fileName);
        }

        @Override
        public String getSupportedFormat() {
            return "MP4 (" + mp4Player.getVersion() + ")";
        }
    }

    /**
     * Adapts VlcPlayer to the MediaPlayer interface.
     */
    static class VlcAdapter implements MediaPlayer {
        private final VlcPlayer vlcPlayer;
        private final boolean useHardwareAcceleration;

        VlcAdapter(boolean useHardwareAcceleration) {
            this.vlcPlayer = new VlcPlayer();
            this.useHardwareAcceleration = useHardwareAcceleration;
        }

        @Override
        public void play(String fileName) {
            vlcPlayer.playVlc(fileName, useHardwareAcceleration);
        }

        @Override
        public String getSupportedFormat() {
            return "VLC/OGG — " + vlcPlayer.getCodecInfo();
        }
    }

    /**
     * Adapts StreamingService to the MediaPlayer interface.
     * A more complex adapter that performs format translation.
     */
    static class StreamingAdapter implements MediaPlayer {
        private final StreamingService service;
        private static final int DEFAULT_QUALITY_KBPS = 1500;

        StreamingAdapter(String serviceUrl) {
            this.service = new StreamingService(serviceUrl);
        }

        @Override
        public void play(String fileName) {
            // Extract resource ID from filename (adaptation logic)
            String resourceId = fileName.replaceAll("\\.[^.]+$", "");
            service.streamRemote(resourceId, DEFAULT_QUALITY_KBPS);
        }

        @Override
        public String getSupportedFormat() {
            return "Streaming (all formats via network)";
        }
    }

    // =========================================================================
    // Client — AudioPlayer
    // =========================================================================

    /**
     * The AudioPlayer only knows about MediaPlayer. It selects the appropriate
     * adapter based on the file extension and uses it transparently.
     */
    static class AudioPlayer {

        /** Plays natively supported MP3 or delegates to an appropriate adapter. */
        public void play(String fileName) {
            String ext = getExtension(fileName).toLowerCase();

            MediaPlayer player = switch (ext) {
                case "mp3"      -> this::playMp3;          // native support (lambda as MediaPlayer)
                case "mp4"      -> new Mp4Adapter();
                case "vlc",
                     "ogg"      -> new VlcAdapter(true);
                case "stream"   -> new StreamingAdapter("https://stream.example.com");
                default         -> f -> System.out.println("[AudioPlayer] Format '." + ext + "' not supported.");
            };

            System.out.print("[AudioPlayer] Requested: " + fileName + " → ");
            player.play(fileName);
        }

        /** Direct MP3 playback — no adapter needed. */
        private void playMp3(String fileName) {
            System.out.println("[AudioPlayer] Playing MP3 natively: " + fileName);
        }

        private static String getExtension(String fileName) {
            int dot = fileName.lastIndexOf('.');
            return dot >= 0 ? fileName.substring(dot + 1) : "";
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demo ===\n");

        AudioPlayer player = new AudioPlayer();

        player.play("song.mp3");
        player.play("movie.mp4");
        player.play("podcast.vlc");
        player.play("music.ogg");
        player.play("live-concert.stream");
        player.play("unknown.xyz");

        System.out.println("\n--- Adapter capabilities ---");
        MediaPlayer[] adapters = { new Mp4Adapter(), new VlcAdapter(false), new StreamingAdapter("https://cdn.example.com") };
        for (MediaPlayer a : adapters) {
            System.out.println("  Supports: " + a.getSupportedFormat());
        }
    }
}
