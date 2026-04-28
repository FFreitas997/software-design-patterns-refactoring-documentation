/**
 * Facade Pattern
 *
 * Intent: Provide a simplified interface to a complex subsystem.
 *
 * Example: A home theater system with many components that require
 *          many coordinated steps to operate. The HomeTheaterFacade
 *          simplifies common workflows like "watch movie" and "listen to music".
 *
 * Roles:
 *   Facade    -> HomeTheaterFacade
 *   Subsystems-> Amplifier, DVDPlayer, CDPlayer, Projector, Screen, Lights, PopcornMachine
 *   Client    -> main() (uses only the Facade for common tasks)
 */
public class Facade {

    // =========================================================================
    // Subsystem Classes
    // =========================================================================

    /** Controls the audio amplifier. */
    static class Amplifier {
        private String input;
        private int volume;
        private boolean on;

        public void on()  { on = true;  System.out.println("[Amplifier] ON"); }
        public void off() { on = false; System.out.println("[Amplifier] OFF"); }

        public void setDvd() {
            input = "DVD";
            System.out.println("[Amplifier] Input set to DVD");
        }

        public void setCd() {
            input = "CD";
            System.out.println("[Amplifier] Input set to CD");
        }

        public void setVolume(int level) {
            volume = level;
            System.out.println("[Amplifier] Volume set to " + level);
        }

        public void setSurroundSound() {
            System.out.println("[Amplifier] Surround sound enabled (5.1 channels)");
        }

        public void setStereoSound() {
            System.out.println("[Amplifier] Stereo sound enabled");
        }

        @Override
        public String toString() {
            return "Amplifier {input=" + input + ", volume=" + volume + ", on=" + on + "}";
        }
    }

    /** Controls the DVD player. */
    static class DVDPlayer {
        private String currentMovie;
        private boolean on;

        public void on()  { on = true;  System.out.println("[DVD Player] ON"); }
        public void off() { on = false; System.out.println("[DVD Player] OFF"); }

        public void play(String movie) {
            currentMovie = movie;
            System.out.println("[DVD Player] Playing: \"" + movie + "\"");
        }

        public void stop() {
            System.out.println("[DVD Player] Stopped: \"" + currentMovie + "\"");
            currentMovie = null;
        }

        public void pause()  { System.out.println("[DVD Player] Paused"); }
        public void resume() { System.out.println("[DVD Player] Resumed"); }

        public void setTwoChannelAudio() {
            System.out.println("[DVD Player] Set to 2-channel audio");
        }

        public void setSurroundAudio() {
            System.out.println("[DVD Player] Set to surround audio");
        }
    }

    /** Controls the CD player. */
    static class CDPlayer {
        private String currentAlbum;
        private boolean on;

        public void on()  { on = true;  System.out.println("[CD Player] ON"); }
        public void off() { on = false; System.out.println("[CD Player] OFF"); }

        public void play(String album) {
            currentAlbum = album;
            System.out.println("[CD Player] Playing album: \"" + album + "\"");
        }

        public void stop() {
            System.out.println("[CD Player] Stopped");
            currentAlbum = null;
        }

        public void eject() { System.out.println("[CD Player] Disc ejected"); }
    }

    /** Controls the projector. */
    static class Projector {
        private boolean on;

        public void on()  { on = true;  System.out.println("[Projector] ON"); }
        public void off() { on = false; System.out.println("[Projector] OFF"); }

        public void wideScreenMode() {
            System.out.println("[Projector] Widescreen mode (16:9)");
        }

        public void tvMode() {
            System.out.println("[Projector] TV mode (4:3)");
        }
    }

    /** Controls the projection screen. */
    static class Screen {
        public void up()   { System.out.println("[Screen] Going up"); }
        public void down() { System.out.println("[Screen] Going down"); }
    }

    /** Controls the room lights. */
    static class TheaterLights {
        private int brightness;

        public void on()  { brightness = 100; System.out.println("[Lights] ON (100%)"); }
        public void off() { brightness = 0;   System.out.println("[Lights] OFF"); }

        public void dim(int level) {
            brightness = level;
            System.out.println("[Lights] Dimmed to " + level + "%");
        }
    }

    /** Controls the popcorn machine. */
    static class PopcornPopper {
        private boolean on;

        public void on()  { on = true;  System.out.println("[Popcorn Machine] ON"); }
        public void off() { on = false; System.out.println("[Popcorn Machine] OFF"); }

        public void pop()  { System.out.println("[Popcorn Machine] Popping popcorn!"); }
        public void stop() { System.out.println("[Popcorn Machine] Popping stopped"); }
    }

    // =========================================================================
    // Facade
    // =========================================================================

    /**
     * The HomeTheaterFacade provides simplified high-level methods for
     * controlling the entire home theater. It coordinates all the subsystems.
     *
     * Clients don't need to know how many subsystems exist or their details —
     * they just call watchMovie(), endMovie(), etc.
     */
    static class HomeTheaterFacade {
        // References to all subsystem components
        private final Amplifier      amp;
        private final DVDPlayer      dvd;
        private final CDPlayer       cd;
        private final Projector      projector;
        private final Screen         screen;
        private final TheaterLights  lights;
        private final PopcornPopper  popcorn;

        HomeTheaterFacade(Amplifier amp, DVDPlayer dvd, CDPlayer cd,
                          Projector projector, Screen screen,
                          TheaterLights lights, PopcornPopper popcorn) {
            this.amp      = amp;
            this.dvd      = dvd;
            this.cd       = cd;
            this.projector = projector;
            this.screen   = screen;
            this.lights   = lights;
            this.popcorn  = popcorn;
        }

        /**
         * Simplified "watch movie" workflow — coordinates 12+ subsystem calls
         * in the right order so the client doesn't have to.
         */
        public void watchMovie(String movie) {
            System.out.println("\n🎬 Get ready to watch a movie...");
            popcorn.on();
            popcorn.pop();
            lights.dim(10);
            screen.down();
            projector.on();
            projector.wideScreenMode();
            amp.on();
            amp.setDvd();
            amp.setSurroundSound();
            amp.setVolume(5);
            dvd.on();
            dvd.setSurroundAudio();
            dvd.play(movie);
            System.out.println("🍿 Movie started: \"" + movie + "\"");
        }

        /**
         * Simplified "end movie" workflow — shuts everything down cleanly.
         */
        public void endMovie() {
            System.out.println("\n⏹  Shutting down movie...");
            popcorn.stop();
            popcorn.off();
            lights.on();
            screen.up();
            projector.off();
            amp.off();
            dvd.stop();
            dvd.off();
            System.out.println("Goodnight!");
        }

        /**
         * Simplified workflow for listening to a CD.
         */
        public void listenToCd(String album) {
            System.out.println("\n🎵 Get ready for music...");
            lights.dim(40);
            amp.on();
            amp.setCd();
            amp.setStereoSound();
            amp.setVolume(3);
            cd.on();
            cd.play(album);
            System.out.println("🎶 Now playing: \"" + album + "\"");
        }

        /** Stops CD playback and powers everything down. */
        public void endCd() {
            System.out.println("\n⏹  Ending CD session...");
            amp.off();
            cd.stop();
            cd.eject();
            cd.off();
            lights.on();
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Facade Pattern Demo — Home Theater ===");

        // Create all subsystem components
        Amplifier      amp      = new Amplifier();
        DVDPlayer      dvd      = new DVDPlayer();
        CDPlayer       cd       = new CDPlayer();
        Projector      projector = new Projector();
        Screen         screen   = new Screen();
        TheaterLights  lights   = new TheaterLights();
        PopcornPopper  popcorn  = new PopcornPopper();

        // Create the Facade — client only needs this
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(
                amp, dvd, cd, projector, screen, lights, popcorn);

        // --- Client uses simplified facade API ---
        homeTheater.watchMovie("Interstellar");

        System.out.println("\n... (movie plays) ...");

        homeTheater.endMovie();

        // --- Listen to music ---
        homeTheater.listenToCd("Dark Side of the Moon - Pink Floyd");

        System.out.println("\n... (music plays) ...");

        homeTheater.endCd();

        System.out.println("\n--- Advanced: Direct subsystem access still possible ---");
        // The facade doesn't prevent direct access when needed
        amp.setVolume(8);   // client directly adjusts volume
        System.out.println("Direct amplifier state: " + amp);
    }
}
