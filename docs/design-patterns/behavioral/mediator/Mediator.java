import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mediator Pattern
 *
 * Intent: Define an object that encapsulates how a set of objects interact.
 *         Promotes loose coupling by keeping objects from referring to each
 *         other explicitly.
 *
 * Example: A chat application with users and rooms.
 *   - Users communicate through a ChatRoom (Mediator), not directly
 *   - The mediator handles message routing, private messages, and broadcasts
 *   - Users can join/leave rooms
 *
 * Roles:
 *   Mediator         -> ChatMediator (interface)
 *   ConcreteMediator -> ChatRoom
 *   Colleague        -> ChatUser (abstract)
 *   ConcreteColleague-> RegularUser, BotUser
 */
public class Mediator {

    // =========================================================================
    // Mediator Interface
    // =========================================================================

    /**
     * The Mediator interface. Colleagues use this to send messages and
     * announce events without knowing who else is in the room.
     */
    interface ChatMediator {
        /** Sends a message from 'sender' to all users in the room. */
        void broadcastMessage(String message, ChatUser sender);

        /** Sends a private message from one user to another. */
        void sendPrivateMessage(String message, ChatUser sender, String recipientName);

        /** Registers a user with this mediator. */
        void addUser(ChatUser user);

        /** Removes a user from the mediator. */
        void removeUser(ChatUser user);

        /** Returns the room/mediator name. */
        String getName();
    }

    // =========================================================================
    // Concrete Mediator: ChatRoom
    // =========================================================================

    /**
     * The concrete Mediator. Knows all connected users and routes messages.
     * Users do NOT hold references to each other — only to this mediator.
     */
    static class ChatRoom implements ChatMediator {
        private final String name;
        private final Map<String, ChatUser> users = new HashMap<>();
        private final List<String> messageHistory = new ArrayList<>();

        ChatRoom(String name) {
            this.name = name;
            System.out.println("[ChatRoom] Room created: " + name);
        }

        @Override
        public void addUser(ChatUser user) {
            users.put(user.getName(), user);
            System.out.println("[ChatRoom:" + name + "] " + user.getName() + " joined.");
            broadcastMessage(user.getName() + " has joined the room.", user);
        }

        @Override
        public void removeUser(ChatUser user) {
            users.remove(user.getName());
            System.out.println("[ChatRoom:" + name + "] " + user.getName() + " left.");
            broadcastMessage(user.getName() + " has left the room.", user);
        }

        /**
         * Routes a message to ALL connected users except the sender.
         * This is the core mediation logic — users never call each other directly.
         */
        @Override
        public void broadcastMessage(String message, ChatUser sender) {
            String formatted = "[" + name + "] " + sender.getName() + ": " + message;
            messageHistory.add(formatted);
            System.out.println(formatted);

            // Deliver to all other users
            for (ChatUser user : users.values()) {
                if (user != sender) {
                    user.receive(message, sender.getName());
                }
            }
        }

        /**
         * Routes a private message to one specific user.
         */
        @Override
        public void sendPrivateMessage(String message, ChatUser sender, String recipientName) {
            ChatUser recipient = users.get(recipientName);
            if (recipient == null) {
                System.out.println("[ChatRoom:" + name + "] User '" + recipientName + "' not found.");
                return;
            }
            String formatted = "[PM:" + name + "] " + sender.getName()
                    + " -> " + recipientName + ": " + message;
            messageHistory.add(formatted);
            System.out.println(formatted);
            recipient.receive("[Private] " + message, sender.getName());
        }

        @Override
        public String getName() { return name; }

        public List<String> getHistory() { return List.copyOf(messageHistory); }

        public int getUserCount() { return users.size(); }
    }

    // =========================================================================
    // Colleague: ChatUser (abstract)
    // =========================================================================

    /**
     * The abstract Colleague class. Each user has a reference to the Mediator
     * (ChatRoom) but NO references to other users.
     */
    static abstract class ChatUser {
        protected final String name;
        protected ChatMediator mediator;
        protected final List<String> receivedMessages = new ArrayList<>();

        ChatUser(String name) {
            this.name = name;
        }

        public String getName() { return name; }

        /** Joins a chat room (sets the mediator). */
        public void joinRoom(ChatMediator mediator) {
            this.mediator = mediator;
            mediator.addUser(this);
        }

        /** Leaves the current chat room. */
        public void leaveRoom() {
            if (mediator != null) {
                mediator.removeUser(this);
                mediator = null;
            }
        }

        /** Sends a broadcast message to the room via the mediator. */
        public void sendMessage(String message) {
            if (mediator == null) {
                System.out.println("[" + name + "] Not in a room.");
                return;
            }
            mediator.broadcastMessage(message, this);
        }

        /** Sends a private message to a specific user via the mediator. */
        public void sendPrivateMessage(String message, String to) {
            if (mediator == null) {
                System.out.println("[" + name + "] Not in a room.");
                return;
            }
            mediator.sendPrivateMessage(message, this, to);
        }

        /**
         * Called by the Mediator to deliver a message to this user.
         * Subclasses can override to implement custom reaction behavior.
         */
        public void receive(String message, String from) {
            String entry = from + " says: " + message;
            receivedMessages.add(entry);
        }

        public List<String> getReceivedMessages() { return List.copyOf(receivedMessages); }
    }

    // =========================================================================
    // Concrete Colleagues
    // =========================================================================

    /** A regular human user. */
    static class RegularUser extends ChatUser {
        RegularUser(String name) { super(name); }

        @Override
        public void receive(String message, String from) {
            super.receive(message, from);
            // Regular users just see the message delivered to them
        }
    }

    /**
     * A bot user that auto-responds to certain keywords.
     * Shows how mediator enables diverse colleague behaviors.
     */
    static class BotUser extends ChatUser {
        BotUser(String name) { super(name); }

        @Override
        public void receive(String message, String from) {
            super.receive(message, from);

            // Auto-respond to greetings
            if (message.toLowerCase().contains("hello") || message.toLowerCase().contains("hi")) {
                sendMessage("Hi, " + from + "! I'm " + name + ", your friendly bot. 🤖");
            } else if (message.toLowerCase().contains("help")) {
                sendMessage("Need help? I can assist with commands: /status /users /time");
            } else if (message.toLowerCase().contains("/status")) {
                sendMessage("All systems operational! ✅");
            }
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Mediator Pattern Demo — Chat Room ===\n");

        // Create the mediator (chat room)
        ChatRoom generalRoom = new ChatRoom("General");

        // Create colleagues (users)
        ChatUser alice  = new RegularUser("Alice");
        ChatUser bob    = new RegularUser("Bob");
        ChatUser carol  = new RegularUser("Carol");
        ChatUser helpBot = new BotUser("HelpBot");

        System.out.println("\n--- Users join ---");
        alice.joinRoom(generalRoom);
        bob.joinRoom(generalRoom);
        carol.joinRoom(generalRoom);
        helpBot.joinRoom(generalRoom);

        System.out.println("\n--- Broadcast messages ---");
        alice.sendMessage("Hello everyone!");   // bot should respond
        bob.sendMessage("Hey Alice!");

        System.out.println("\n--- Private messages ---");
        alice.sendPrivateMessage("Are you free for a call later?", "Bob");
        bob.sendPrivateMessage("Sure, 3pm works!", "Alice");

        System.out.println("\n--- Bot interaction ---");
        carol.sendMessage("I need help with the system");
        carol.sendMessage("/status");

        System.out.println("\n--- User leaves ---");
        bob.leaveRoom();
        alice.sendMessage("Bob left, anyone else here?");

        System.out.println("\n--- Room statistics ---");
        System.out.println("Users still in room: " + generalRoom.getUserCount());
        System.out.println("Total messages: " + generalRoom.getHistory().size());

        System.out.println("\n--- Alice's received messages ---");
        alice.getReceivedMessages().forEach(m -> System.out.println("  " + m));
    }
}
